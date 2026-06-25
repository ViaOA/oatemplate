package com.template.control.testclient;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

import com.template.control.client.RemoteClientController;
import com.template.delegate.*;
import com.template.model.oa.*;
import com.template.model.oa.cs.*;
import com.template.remote.RemoteAppInterface;
import com.viaoa.comm.multiplexer.OAMultiplexerClient;
import com.viaoa.config.OAProperties;
import com.viaoa.lang.OAString;
import com.viaoa.log.OALogUtil;
import com.viaoa.object.OAObject;
import com.viaoa.runtime.OARuntime;

public class TestClientController {
	private static Logger LOG = Logger.getLogger(RemoteClientController.class.getName());

	
    public void start(final String serverName, final int port, final String userId, final String passwordPlain) throws Exception {
        String s = String.format("Server=%s, port=%d, userId=%s", serverName, port, userId);
        LOG.config("client connection starting, "+s);
        
        RemoteClientController controlRemote = new RemoteClientController() {
            protected @Override void onDisconnect(Exception e) {
            }
            protected JFrame getFrame() {
                return null;
            }
        };
        
        // this will start OASync client, and create OADataSourceClient
        controlRemote.start(serverName, port);
        
        int connectionId = OARuntime.oa().internal().sync().getConnectionId();
        
        OAProperties serverProps = RemoteDelegate.getRemoteApp().getServerProperties();

        String password = OAString.convertToSHAHash(passwordPlain);
        AppUserLogin userLogin = RemoteDelegate.getRemoteApp().getUserLogin(connectionId, userId, password, "test", System.getProperty("user.name"));

        RemoteAppInterface remoteApp = RemoteDelegate.getRemoteApp();
        LOG.config("remoteApp.getRelease() = "+remoteApp.getRelease());
        
        ServerRoot rootServer = remoteApp.getServerRoot();
        ClientRoot rootClient = remoteApp.getClientRoot(connectionId);

        rootServer.loadReferences(1, 1, false, 500);
        
        ModelDelegate.initialize(rootServer, rootClient);
        LOG.config("client startup completed");
    }
    
    public void stop() throws Exception {
    	OARuntime.oa().sync().stop();
    }

    public void custom() throws Exception {
        // CUSTOM code here
    }
    
    public static void main(String[] args) throws Exception {
        OALogUtil.consoleOnly(Level.FINE);
        OAObject.setDebugMode(true);

        TestClientController tc = new TestClientController();

        tc.start("localhost", 1099, "admin", "starts123");

        tc.custom();
        
        Thread.sleep(2000);
        
        tc.stop();
    }
}
