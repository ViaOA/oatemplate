package com.template.control.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.template.model.oa.*;
import com.template.resource.Resource;
import com.viaoa.comm.multiplexer.io.VirtualSocket;
import com.viaoa.sync.OASyncServer;
import com.viaoa.sync.model.ClientInfo;
import com.viaoa.util.OAConv;
import com.viaoa.util.OADateTime;

public abstract class RemoteServerController {

    private static Logger LOG = Logger.getLogger(RemoteServerController.class.getName());
    private int port;
    private OASyncServer syncServer;

    public RemoteServerController(int port) {
        LOG.config("OAServer using port="+port);
        this.port = port;
    }

    public OASyncServer getSyncServer() {
        if (syncServer == null) {
            Package p = AppServer.class.getPackage();
            syncServer = new OASyncServer(p, port) {
                @Override
                protected String getLogFileName() {
                    return RemoteServerController.this.getLogFileName();
                }
                @Override
                protected void onClientConnect(Socket socket, int connectionId) {
                    super.onClientConnect(socket, connectionId);
                    RemoteServerController.this.onClientConnect(socket, connectionId);
                }
                @Override
                protected void onClientDisconnect(int connectionId) {
                    super.onClientDisconnect(connectionId);
                    RemoteServerController.this.onClientDisconnect(connectionId);
                }
                @Override
                public void onUpdate(ClientInfo ci) {
                    super.onUpdate(ci);
                    RemoteServerController.this.onUpdate(ci);
                }
                @Override
                protected void onClientException(ClientInfo ci, String msg, Throwable ex) {
                    //super.onClientException(ci, msg, ex);
                    RemoteServerController.this.onClientException(ci, msg, ex);
                }
            };
            
            ClientInfo ci = syncServer.getClientInfo();
            ci.setUserId("");
            ci.setUserName(System.getProperty("user.name"));
            try {
                InetAddress localHost = InetAddress.getLocalHost();
                ci.setHostName(localHost.getHostName());
                ci.setIpAddress(localHost.getHostAddress());
            }
            catch (Exception e) {
            }
            ci.setLocation("");
            int release = OAConv.toInt(Resource.getValue(Resource.APP_Release));
            ci.setVersion(""+release);
            onUpdate(ci);
        }
        return syncServer;
    }
    
    public abstract void onUpdate(ClientInfo ci);
    
    protected abstract void onClientException(ClientInfo ci, String msg, Throwable ex);
    
    public void stop() throws Exception {
        if (syncServer != null) {
            syncServer.stop();
        }
    }
    
    public void start() throws Exception {
        String msg = Resource.getValue(Resource.APP_ApplicationName);
        msg += ", version="+Resource.getValue(Resource.APP_Version);
        msg += ", release="+Resource.getValue(Resource.APP_Release);
        msg += ", started=" + (new OADateTime());
        getSyncServer().setInvalidConnectionMessage(msg);
        getSyncServer().start();
        
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runUpdateClientSoftware();
                }
                catch (Exception e) {
                    LOG.log(Level.WARNING, "updateClientSoftware() exception", e);
                }
            }
        }, "UpdateClientSoftwareServerSocket");
        t.start();
        LOG.fine("Started thread, name="+t.getName());
        
        
        int x = Resource.getInt(Resource.APP_AppUpdateInterval);
        if (x > 0) {
            getSyncServer().startUpdateThread(x);
        }
        LOG.config("Started remote server, "+msg+", update interval="+x+" seconds");
    }

    protected void runUpdateClientSoftware() throws Exception {
        ServerSocket ss = getSyncServer().getMultiplexerServer().createServerSocket("getJars");
        for ( ;; ) {
            final Socket socket = ss.accept();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        updateClientSoftware(socket);
                    }
                    catch (Exception e) {
                        // TODO: handle exception
                        LOG.log(Level.WARNING, "updateClientSoftware exception", e);
                    }
                }
            }, "UpdateClientSoftwareSocket").start();
        }
    }
    
    protected void updateClientSoftware(Socket socket) throws Exception {
        // see ClientController.onUpdateSoftwareForWindows
        // send app jar first
        String s = Resource.getValue(Resource.APP_JarFileName);
        if (s.toLowerCase().endsWith(".jar")) s = s.substring(0, s.length()-4);
        String[] fnames = (s +", oa-core, oa-jfc, itext, jh").split(", ");
        final byte[] bs = new byte[8196];

        final BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
        final ObjectOutputStream oos = new ObjectOutputStream(bos);
        
        for (String fn : fnames) {
            LOG.fine("sending file to client, fn="+fn+", clientId="+((VirtualSocket) socket).getConnectionId());
            File f = new File("lib");
            File[] files = f.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.toLowerCase().indexOf(fn.toLowerCase()) == 0) return true;
                    return false;
                }
            });
            if (files == null || files.length == 0) continue;
            
            oos.writeObject(fn);
            
            final InputStream is = new FileInputStream(files[0]);
            final BufferedInputStream bis = new BufferedInputStream(is);
        
            for (int i=0;;i++) {
                int x = bis.read(bs, 0, bs.length);
                oos.writeInt(Math.max(x, 0));
                if (x <= 0) break;
                oos.write(bs, 0, x);
            }
            is.close();
        }
        oos.writeObject(""); // end of fileNames
        oos.flush();
        socket.getInputStream().read();
    }
    
    
    protected String getLogFileName() {
        return null;
    }
    protected void onClientConnect(Socket socket, int connectionId) {
    }
    protected void onClientDisconnect(int connectionId) {
    }

}
