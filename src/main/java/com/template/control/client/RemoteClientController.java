package com.template.control.client;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import com.template.datasource.DataSource;
import com.template.model.oa.*;
import com.template.resource.Resource;
import com.viaoa.ds.cs.OADataSourceClient;
import com.viaoa.ds.jdbc.db.Column;
import com.viaoa.ds.jdbc.db.Database;
import com.viaoa.ds.jdbc.db.Table;
import com.viaoa.hub.Hub;
import com.viaoa.object.OAObject;
import com.viaoa.object.OASiblingHelper;
import com.viaoa.object.OAThreadLocalDelegate;
import com.viaoa.sync.OASyncClient;
import com.viaoa.util.OAFile;
import com.viaoa.util.OAFilter;
import com.viaoa.util.OAString;
import com.viaoa.sync.OASync;

/**
	Connection to RemoteServer, that allows distributed method calls. 
	@see ServerDelegate#
*/
public abstract class RemoteClientController {

	private static final Logger LOG = Logger.getLogger(RemoteClientController.class.getName());
	
    private String serverName;
	private int port;
    private OASyncClient syncClient;
    private final ArrayList<OASiblingHelper> alSiblingHelperAWTThreadCache = new ArrayList<>();

    public OASyncClient getSyncClient() {
        return syncClient;
    }
    
    public void start(String serverName, int port) throws Exception {
    	this.serverName = serverName;
    	this.port = port;
        LOG.config("creating DataSourceClient");

        // Create OADataSource to be used by OASyncClient
        final OADataSourceClient dsClient = new OADataSourceClient() {
            DataSource ds;

            public boolean willCreatePropertyValue(OAObject object, String propertyName) {
                return ("id".equalsIgnoreCase(propertyName));
            }
            @Override
            public boolean isAvailable() {
                return true;
            }
            @Override
            public boolean isClassSupported(Class clazz, OAFilter filter) {
                Database db = getDataSource().getDatabase();
                Table table = db.getTable(clazz);
                return table != null;
            }
            @Override
            public int getMaxLength(Class clazz, String propertyName) {
                Database db = getDataSource().getDatabase();
                Table table = db.getTable(clazz);
                if (table == null) return -1;
                Column col = table.getColumn(null, propertyName);
                if (col == null) return -1;
                return col.maxLength;
            }

            DataSource getDataSource() {
                if (ds != null) return ds;
                ds = new DataSource();
                return ds;
            }
        };

        LOG.config("connecting to RemoteServer "+serverName+", on port="+port);
        Package p = AppUser.class.getPackage();
    	syncClient = new OASyncClient(p, serverName, port) {
            private AtomicInteger aiDetailCnt = new AtomicInteger();
            private AtomicInteger aiCursorCnt = new AtomicInteger();
            
            @Override
            public Object getDetail(final OAObject masterObject, final String propertyName) {
                int cnt = aiDetailCnt.incrementAndGet();
                //LOG.finer("OAClient.getDetail, masterObject=" + masterObject + ", propertyName=" + propertyName);

                boolean bUseSameThread = (getFrame() == null || !SwingUtilities.isEventDispatchThread());
                ArrayList<OASiblingHelper> alSiblingHelper = OAThreadLocalDelegate.getSiblingHelpers();

                if (!bUseSameThread && (alSiblingHelper != null)) {
                    for (OASiblingHelper sh : alSiblingHelper) {
                        if (bUseSameThread = sh.getUseSameThread()) break;
                    }
                }
                if (bUseSameThread) {
                    return super.getDetail(masterObject, propertyName);  // mostly called by background threads  
                }
                
                final int cntCursor = aiCursorCnt.incrementAndGet();
                getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                if (alSiblingHelper == null || alSiblingHelper.size() == 0) {
                    alSiblingHelper = alSiblingHelperAWTThreadCache;
                }
                else {
                    OASiblingHelper sh = alSiblingHelper.get(0);
                    if (!alSiblingHelperAWTThreadCache.contains(sh)) {
                        alSiblingHelperAWTThreadCache.add(0, sh);
                        if (alSiblingHelperAWTThreadCache.size() > 3) alSiblingHelperAWTThreadCache.remove(3);
                    }
                }

                final ArrayList<OASiblingHelper> alSiblingHelperX = alSiblingHelper;
                final boolean bForMerger = OAThreadLocalDelegate.isHubMergerChanging();
                
                SwingWorker<Object, Void> sw = new SwingWorker<Object, Void>() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        Object obj;
                        try {
                            if (alSiblingHelperX != null) {
                                for (OASiblingHelper sh : alSiblingHelperX) {
                                    OAThreadLocalDelegate.addSiblingHelper(sh);
                                }
                            }
                            if (bForMerger) OAThreadLocalDelegate.setHubMergerIsChanging(true);
                            obj = getDetail(masterObject, propertyName);
                        }
                        finally {
                            if (bForMerger) OAThreadLocalDelegate.setHubMergerIsChanging(false);
                            if (alSiblingHelperX != null) {
                                for (OASiblingHelper sh : alSiblingHelperX) {
                                    OAThreadLocalDelegate.removeSiblingHelper(sh);
                                }
                            }
                        }
                        
                        ActionListener al = new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (cntCursor == aiCursorCnt.get()) {
                                    getFrame().setCursor(null);
                                }
                            }
                        };
                        Timer timer = new Timer(150, al);  
                        timer.setRepeats(false);
                        timer.start();
                        return obj;
                    }
                };
                sw.execute();

                Object obj;
                for (;;) {
                    try {
                        obj = sw.get();  // wait for response
                        break;
                    }
                    catch (Exception e) { 
                        throw new RuntimeException("Error while getting Detail, Object="+masterObject.getClass().getSimpleName()+", property="+propertyName, e);
                    }
                }
                return obj;
            }
            @Override
            protected void onSocketClose(boolean bError) {
                super.onSocketClose(bError);
                onDisconnect(null);
            }
            @Override
            protected void onSocketException(Exception e) {
                super.onSocketException(e);
                onDisconnect(e);
            }
            @Override
            public OADataSourceClient getOADataSourceClient() {
                return dsClient;
            }
    	};
        LOG.config("Starting Client ...");
        syncClient.start();
        
        int x = Resource.getInt(Resource.APP_AppUpdateInterval);
        if (x > 0) {
            syncClient.startUpdateThread(x);
        }
        LOG.config("Client started, updateInterval set to "+x+" seconds");
    }
    
    public boolean isConnected() {
    	return (OASync.isConnected());
    }
    
	public void close() {
        try {
            if (syncClient != null) syncClient.stop();          
        }
        catch (Exception e) {
        }
	}

    protected void onUpdateSoftwareForWindows() throws Exception {
        // see: RemoteServerController.updateClientSoftware
        final Socket socket = getSyncClient().getRemoteMultiplexerClient().getMultiplexerClient().createSocket("getJars");
        final InputStream is = socket.getInputStream();
        final BufferedInputStream bis = new BufferedInputStream(is);
        final ObjectInputStream ois = new ObjectInputStream(bis);
        final byte[] bs = new byte[8196];
        
        // the javapackager creates a *.cfg file that needs to be changed for the new jar files
        String appName = Resource.getValue(Resource.APP_ApplicationName);
        String config = OAFile.readTextFile(appName + ".cfg", 2048);
        final boolean bUsePrefix = config.indexOf("app.mainjar=vx") < 0;
        if (bUsePrefix) config = OAString.convert(config, "app.mainjar=", "app.mainjar=vx");
        else config = OAString.convert(config, "app.mainjar=vx", "app.mainjar=");
        
        String newJars = "";
        for (int i=0;;i++) {
            String fn = (String) ois.readObject();
            if (fn.length() == 0) break;
            fn = fn + ".jar";
            
            if (bUsePrefix) fn = "vx" + fn;
            if (i > 0) {
                fn = "lib/" + fn;
                newJars = OAString.append(newJars, fn, ";");
            }

            File file = new File(fn);
            if (file.exists()) file.delete();
            file.createNewFile();
            
            OutputStream os = new FileOutputStream(file);
            for (;;) {
                int x = ois.readInt();
                if (x <= 0) break;
                ois.readFully(bs, 0, x);
                os.write(bs, 0, x);
            }
            os.close();
        }

        String s = "app.classpath=";
        int pos1 = config.indexOf(s);
        if (pos1 >= 0) {
            pos1 += s.length();
            s = "app.runtime=";
            int pos2 = config.indexOf(s);
            if (pos2 >= 0) {
                config = config.substring(0, pos1) + newJars + OAFile.NL + config.substring(pos2);
                OAFile.writeTextFile(appName + ".cfg", config);
            }
        }
        
        socket.getOutputStream().write(77); // done
        socket.close();
    }

	protected abstract void onDisconnect(Exception e);
    protected abstract JFrame getFrame();
}
