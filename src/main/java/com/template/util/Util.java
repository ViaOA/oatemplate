package com.template.util;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.io.*;

import javax.swing.*;

import java.lang.reflect.*;
import java.net.URI;
import java.net.URL;

import com.template.resource.Resource;
import com.viaoa.comm.multiplexer.MultiplexerClient;
import com.viaoa.comm.multiplexer.MultiplexerServer;
import com.viaoa.ds.OADataSource;
import com.viaoa.ds.jdbc.OADataSourceJDBC;
import com.viaoa.hub.*;
import com.viaoa.jfc.*;
import com.viaoa.object.OAObjectCacheDelegate;
import com.viaoa.object.OAObjectInfo;
import com.viaoa.remote.multiplexer.RemoteMultiplexerClient;
import com.viaoa.remote.multiplexer.RemoteMultiplexerServer;
import com.viaoa.sync.OASyncClient;
import com.viaoa.sync.OASyncDelegate;
import com.viaoa.sync.OASyncServer;
import com.viaoa.util.OAFile;
import com.viaoa.util.OAString;

public class Util {
    private static Logger LOG = Logger.getLogger(Util.class.getName());
	
	
	public static void showLookAndFeels() {
		UIManager.LookAndFeelInfo[] lfs = UIManager.getInstalledLookAndFeels();
		for (int i=0; lfs != null && i < lfs.length; i++) {
			System.out.println(i + ") " + lfs[i].getName());
		}
	}
	public static void showLookAndFeelDefaults() {
		UIDefaults uid = UIManager.getLookAndFeel().getDefaults();
		Enumeration keys  = uid.keys();
		while (keys.hasMoreElements()) { 
		    String key   = (String) keys.nextElement(); 
		    Object value = uid.get(key); 
		    System.out.println (key + " = " + value); 
		  } 
	}

	public static void showSystemInfo() {
		Properties props = System.getProperties();
		Enumeration en = props.keys();
		for (int i=0; en.hasMoreElements(); i++) {
			Object key = en.nextElement();
			if (key instanceof String) System.out.println(i+") " + key + " = " + props.getProperty((String)key));;
		}
	}

	public static void lauchBrowser(String url) throws Exception {
        Desktop.getDesktop().browse(new URI(url));
	}


    public static Vector<String> getInfo() {
        Vector<String> vecAll = new Vector<String>();

        System.gc();
        vecAll.addElement("Memory ============================");
        vecAll.addElement(" Total: "+ String.format("%,d", Runtime.getRuntime().totalMemory(), "#,###"));
        vecAll.addElement("  Free: "+ String.format("%,d", Runtime.getRuntime().freeMemory(), "#,###"));
        vecAll.addElement("   Max: "+ String.format("%,d", Runtime.getRuntime().maxMemory(), "#,###"));

        vecAll.addElement("Object Cache =======================");
        OAObjectCacheDelegate.getInfo(vecAll);

        vecAll.addElement("Triggers =======================");
        vecAll.addElement("total: "+OAObjectInfo.getTotalTriggers());
        
        vecAll.addElement("DataSource =========================");
        OADataSource[] oadss = OADataSource.getDataSources();
        for (int i=0; oadss != null && i < oadss.length; i++) {
            OADataSource oads = oadss[i]; 
            OADataSourceJDBC ds = null;
            if (oads instanceof OADataSourceJDBC) {
                ds = (OADataSourceJDBC) oads;
                ds.getInfo(vecAll);
            }
        }
        
        OASyncClient sc = OASyncDelegate.getSyncClient();
        if (sc != null) { 
            vecAll.addElement("OASync Client ======================");
            RemoteMultiplexerClient rmc = sc.getRemoteMultiplexerClient();
            vecAll.addElement(" remote methods called: "+ String.format("%,d", rmc.getMethodCallCount(), "#,###"));
            vecAll.addElement("   received: "+ String.format("%,d", rmc.getReceivedMethodCount(), "#,###"));
            
            MultiplexerClient mc = rmc.getMultiplexerClient();
            vecAll.addElement(" vsockets live: "+ String.format("%,d", mc.getLiveSocketCount(), "#,###"));
            vecAll.addElement("   created: "+ String.format("%,d", mc.getCreatedSocketCount(), "#,###"));
            vecAll.addElement(" read count: "+ String.format("%,d", mc.getReadCount(), "#,###"));
            vecAll.addElement("   size: "+ String.format("%,d", mc.getReadSize(), "#,###"));
            vecAll.addElement(" write count: "+ String.format("%,d", mc.getWriteCount(), "#,###"));
            vecAll.addElement("   size: "+ String.format("%,d", mc.getWriteSize(), "#,###"));

        }
        
        OASyncServer ss = OASyncDelegate.getSyncServer();
        if (ss != null) { 
            vecAll.addElement("OASync Server ======================");
            
            RemoteMultiplexerServer rms = ss.getRemoteMultiplexerServer();
            vecAll.addElement(" remote methods called: "+ String.format("%,d", rms.getMethodCallCount(), "#,###"));
            vecAll.addElement("   received: "+ String.format("%,d", rms.getReceivedMethodCount(), "#,###"));
            
            vecAll.addElement(" queue position: "+ String.format("%,d", rms.getQueueHeadPos(), "#,###"));
            
            MultiplexerServer ms = rms.getMultiplexerServer();
            vecAll.addElement(" connections live: "+ String.format("%,d", ms.getLiveConnectionCount(), "#,###"));
            vecAll.addElement("   created: "+ String.format("%,d", ms.getCreatedConnectionCount(), "#,###"));

            vecAll.addElement(" read count: "+ String.format("%,d", ms.getReadCount(), "#,###"));
            vecAll.addElement("   size: "+ String.format("%,d", ms.getReadSize(), "#,###"));
            vecAll.addElement(" write count: "+ String.format("%,d", ms.getWriteCount(), "#,###"));
            vecAll.addElement("   size: "+ String.format("%,d", ms.getWriteSize(), "#,###"));
        }
        vecAll.addElement(" ");
        
        Vector vec;
        Enumeration enumx;
        
        vecAll.add("================== Resource properties ==================");
        vec = new Vector();
        enumx = Resource.getBundleProperties().keys();
        for ( ;enumx.hasMoreElements();) {
            String key = (String) enumx.nextElement();
            vec.addElement(key + " = " + convertValue(Resource.getValue(key)));
        }
        Collections.sort(vec);
        vecAll.addAll(vec);
        
        vecAll.add("================== Runtime arguments ==================");
        vec = new Vector();
        enumx = Resource.getRuntimeProperties().keys();
        for ( ;enumx.hasMoreElements();) {
            String key = (String) enumx.nextElement();
            vec.addElement(key + " = " + convertValue(Resource.getValue(key)));
        }
        Collections.sort(vec);
        vecAll.addAll(vec);
        
        vecAll.add("================== server.ini properties ==================");
        vec = new Vector();
        enumx = Resource.getServerProperties().keys();
        for ( ;enumx.hasMoreElements();) {
            String key = (String) enumx.nextElement();
            vec.addElement(key + " = " + convertValue(Resource.getValue(key)));
        }
        Collections.sort(vec);
        vecAll.addAll(vec);
        
        vecAll.add("================== client.ini properties ==================");
        vec = new Vector();
        enumx = Resource.getClientProperties().keys();
        for ( ;enumx.hasMoreElements();) {
            String key = (String) enumx.nextElement();
            vec.addElement(key + " = " + convertValue(Resource.getValue(key)));
        }
        Collections.sort(vec);
        vecAll.addAll(vec);
        
        vecAll.add("================== System properties ==================");
        vec = new Vector();
        Properties props = System.getProperties();
        enumx = props.keys();
        for ( ;enumx.hasMoreElements();) {
            String key = (String) enumx.nextElement();
            vec.addElement(key + ": " + props.getProperty(key));
        }
        Collections.sort(vec);
        vecAll.addAll(vec);
        
        return vecAll;
    }

    protected static String convertValue(String val) {
        if (val == null) return "";
        if (val.toLowerCase().indexOf("password") >= 0) val = "password";
        return  val;
    }
    
    
	/**
	 * This should be ran in another thread, then call dlgProgress.setVisible(true);
	 * @param fileNameFrom
	 * @param fileNameTo
	 * @throws Exception
	 
	protected void copy(ProgressDialog dlgProgress, String fileNameFrom, String fileNameTo) throws Exception {
		if (dlgProgress != null) {
			dlgProgress.setTitle("Copy File");
			dlgProgress.setHeading("Copy file " + fileNameFrom + " to " + fileNameTo);
			dlgProgress.setValue(0);
		}
    	fileNameFrom = OAString.convertFileName(fileNameFrom);
        File fileFrom = new File(fileNameFrom);
        if (!fileFrom.exists()) throw new Exception("File " + fileNameFrom + " not found");

        fileNameTo = OAString.convertFileName(fileNameTo);
        File fileTo = new File(fileNameTo);
        fileTo.mkdirs();
        fileTo.delete();
        
        int max = (int) fileFrom.length();
		if (dlgProgress != null) {
			dlgProgress.setMinimum(0);
			dlgProgress.setValue(0);
			dlgProgress.setMaximum(max);
		}
        InputStream is = new FileInputStream(fileFrom);
        OutputStream os = new FileOutputStream(fileTo);
            
        int tot = 0;
        int bufferSize = 1024 * 8;
        
        byte[] bs = new byte[bufferSize];
        for (int i=0; ;i++) {
            int x = is.read(bs, 0, bufferSize);
            if (x < 0) break;
            tot += x;
    		if (dlgProgress != null) {
    			dlgProgress.setValue(Math.min(tot,max));
    		}
            os.write(bs, 0, x);
        }
        is.close();
        os.close();
    }
    */
    
	/**
	 * Allows leading spaces for padding.
	 * @param phone
	 * @return
	 */
    public static String convertToValidPhoneNumber(String phone) {
        if (phone == null) return null;
        int x = phone.length();
        if (x ==0) return phone;
        StringBuilder sb = new StringBuilder(x);
        boolean b = false;
        for (int i=0; i<x; i++) {
            char ch = phone.charAt(i);
            if (!Character.isDigit(ch)) {
                if (ch != ' ') {
                    b = true;
                    continue;
                }
            }
            sb.append(ch);
            
        }
        x = sb.length();
        for (int i=x; i<10; i++) {
            b = true;
            sb.insert(0,' ');
        }
        if (b) {
            phone = sb.toString();
        }
        return phone;
    }

    
    
    /** 
     *  Not used ... see verifyWebStartFile()
     */
    public static void createNewWebStartShortcuts() {
        /*
         * Create desktop links.
         * This takes window link files that I created on my desktop, and then stored in jar to be able to be used/installed automatically when 
         * program runs.
         * 
         * To recreate, look at the file properties to see how the link was set up.
         */
        try {
            String s = System.getProperty("user.home") + "/" + Resource.getValue(Resource.APP_JWSRootDirectory);

            // icon that the links use.
            OAFile.copyResourceToFile(com.template.resource.Resource.class, "desktop/icon.ico", s + "/icon.ico");           
            
            // desktop links
            s = System.getProperty("user.home") + "/Desktop/";
            File file = new File(s + "CDI Scheduler.lnk");
            if (file.exists()) {
                OAFile.copyResourceToFile(com.template.resource.Resource.class, "desktop/CDI Scheduler.lnk", s + "CDI Scheduler.lnk");           
            }
            // program menu links
            s = System.getProperty("user.home") + "/Start Menu/Programs/";
            OAFile.copyResourceToFile(com.template.resource.Resource.class, "desktop/CDI Scheduler.lnk", s + "CDI Scheduler/CDI Scheduler.lnk");
            LOG.config("JWS shortcuts have been set.");
        }
        catch (Exception e) {
            LOG.log(Level.WARNING, "errror setting JWS shortcuts",e);
        }
    }
    
    public static boolean verifyWebStartFile() {
        
        
        return true;
    }
    
    

    /** HACK! for Java Web Start - since the local downloaded JNLP file does not always exist. 
     * Read the desktop link file to find the filename that 
     * see: "http://www.wotsit.org/" for windows lnk file format 
     */ 
    public static String readWebstartJNLPCacheFileName(String windowsLinkFileName) throws Exception { 
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(windowsLinkFileName)); 
        
        // header
        for (int j=0; j<0x52; j++) { 
            byte ch = (byte) in.read();
        }        
        
        byte loc[] = new byte[1024];
        
        String s = "-localfile ";
        char[] match = s.toCharArray();
        int matchMax = s.length();
        int matchPos = 0;
 
        int pos = 0;
        for (int j=0; j<2500; j++) { 
            byte ch = (byte) in.read();
            if (ch == -1) break;

            if (matchPos > matchMax) {
                if (ch > 31 && ch < 127) {
                    if (ch == '"') break;
                    else loc[pos++] = ch; 
                }
            }
            else if (matchPos == matchMax) {
                if (ch == '"') matchPos++;
            }
            else {
                if (ch == 0);  // chars are in double bytes, skip the 0
                else if (ch == match[matchPos]) {
                    matchPos++;
                }
                else {
                    matchPos = 0;
                }
            }            
        } 
        if (matchPos < 5) return null;
        s = new String(loc);
        LOG.fine("===> "+s);
        return s;
     } 


    /**
     * "Hack" to fix a bug with Java Web Start, where the desktop and start menu Links do not point to a valid jnlp in the jws cache directory.
     * 
     * This reads the Windows actual file for the desktop short cut icon, and will parse it 
     * to find out which file is being called, which should exist in the JWS cache.
     * If it does not exist, it will be created usig the jnlp file from the website.
     * 
     * @throws Exception
     */
    public static boolean fixWindowsLinksForWebStart() throws Exception {
        LOG.fine("verifying and fixing windows links");
        boolean b = false;;

        String userHome = System.getProperty("user.home");
        
        // Sales
        URL url = new URL("http://www.viaoa.com/jnlp/scheduler/sales.jnlp");
        String s = userHome + "/Desktop/CDI Sales.lnk";
        if (fixWindowsLinkForWebStart(s, url)) b = true;
        s = userHome + "/Start Menu/Programs/CDI Sales/CDI Sales.lnk";
        if (fixWindowsLinkForWebStart(s, url)) b = true;

        // Scheduler
        url = new URL("http://www.viaoa.com/jnlp/scheduler/scheduler.jnlp");
        s = userHome + "/Desktop/CDI Scheduler.lnk";
        if (fixWindowsLinkForWebStart(s, url)) b = true;
        s = userHome + "/Start Menu/Programs/CDI Scheduler/CDI Scheduler.lnk";
        if (fixWindowsLinkForWebStart(s, url)) b = true;
        
        return b;
    }
    
    private static boolean fixWindowsLinkForWebStart(String linkFileName, URL urlJnlp) throws Exception {
        LOG.fine("linkFileName="+linkFileName);

        linkFileName = OAString.convertFileName(linkFileName);
        
        File file = new File(linkFileName);
        if (!file.exists()) {
            LOG.fine("linkFileName="+linkFileName+" does not exist");
            return false;
        }
        
        // read the jnlp fileName that is being used by the desktop link.  It is one of the program arguments for the short cut program.
        String jnlpFileName = readWebstartJNLPCacheFileName(linkFileName);
        LOG.fine("jnlpFileName="+jnlpFileName);
        if (jnlpFileName == null) return false;
        
        file = new File(jnlpFileName);
        if (file.exists()) {
            return false;
        }
        LOG.warning("linkFileName="+linkFileName+" does not exist, will create and copy from URL="+urlJnlp.toString()+", to file="+jnlpFileName);
        
        BufferedReader in = new BufferedReader(new InputStreamReader(urlJnlp.openStream()));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file))); 
        
        String str;    
        while ((str = in.readLine()) != null) {
            out.println(str);
        }
        in.close();
        out.close();
        return true;
    }
}

