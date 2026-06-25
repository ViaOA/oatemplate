package com.template.util;

import java.awt.Desktop;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.UIDefaults;
import javax.swing.UIManager;

import com.template.resource.Resource;
import com.viaoa.comm.multiplexer.OAMultiplexerClient;
import com.viaoa.comm.multiplexer.OAMultiplexerServer;
import com.viaoa.datasource.OADataSource;
import com.viaoa.datasource.jdbc.OADataSourceJDBC;
import com.viaoa.remote.multiplexer.OARemoteMultiplexerClient;
import com.viaoa.remote.multiplexer.OARemoteMultiplexerServer;
import com.viaoa.runtime.OARuntime;
import com.viaoa.sync.OASyncClient;
import com.viaoa.sync.OASyncServer;

public class Util {
	private static Logger LOG = Logger.getLogger(Util.class.getName());

	public static void showLookAndFeels() {
		UIManager.LookAndFeelInfo[] lfs = UIManager.getInstalledLookAndFeels();
		for (int i = 0; lfs != null && i < lfs.length; i++) {
			System.out.println(i + ") " + lfs[i].getName());
		}
	}

	public static void showLookAndFeelDefaults() {
		UIDefaults uid = UIManager.getLookAndFeel().getDefaults();
		Enumeration keys = uid.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			Object value = uid.get(key);
			System.out.println(key + " = " + value);
		}
	}

	public static void showSystemInfo() {
		Properties props = System.getProperties();
		Enumeration en = props.keys();
		for (int i = 0; en.hasMoreElements(); i++) {
			Object key = en.nextElement();
			if (key instanceof String) {
				System.out.println(i + ") " + key + " = " + props.getProperty((String) key));
			}
			;
		}
	}

	public static void lauchBrowser(String url) throws Exception {
		Desktop.getDesktop().browse(new URI(url));
	}

	public static List<String> getInfo() {
		final List<String> al = new ArrayList<String>();

		System.gc();
		al.add("Memory ============================");
		al.add(" Total: " + String.format("%,d", Runtime.getRuntime().totalMemory(), "#,###"));
		al.add("  Free: " + String.format("%,d", Runtime.getRuntime().freeMemory(), "#,###"));
		al.add("   Max: " + String.format("%,d", Runtime.getRuntime().maxMemory(), "#,###"));

		al.add("Object Cache =======================");
		OARuntime.graph().internal().objects().cache().getInfo(al);

		al.add("Triggers =======================");
		//qqqqqqq vecAll.add("total: " + OAObjectInfo.getTotalTriggers());

		al.add("DataSource =========================");
		OADataSource[] oadss = OARuntime.datasource().getAll();
		for (int i = 0; oadss != null && i < oadss.length; i++) {
			OADataSource oads = oadss[i];
			OADataSourceJDBC ds = null;
			if (oads instanceof OADataSourceJDBC) {
				ds = (OADataSourceJDBC) oads;
				ds.getInfo(al);
			}
		}

		OASyncClient sc = OARuntime.defaultGraph().internal().sync().getClient();
		if (sc != null) {
			al.add("OASync Client ======================");
			OARemoteMultiplexerClient rmc = sc.getRemoteMultiplexerClient();
			al.add(" remote methods called: " + String.format("%,d", rmc.getMethodCallCount(), "#,###"));
			al.add("   received: " + String.format("%,d", rmc.getReceivedMethodCount(), "#,###"));

			OAMultiplexerClient mc = rmc.getMultiplexerClient();
			al.add(" vsockets live: " + String.format("%,d", mc.getLiveSocketCount(), "#,###"));
			al.add("   created: " + String.format("%,d", mc.getCreatedSocketCount(), "#,###"));
			al.add(" read count: " + String.format("%,d", mc.getReadCount(), "#,###"));
			al.add("   size: " + String.format("%,d", mc.getReadSize(), "#,###"));
			al.add(" write count: " + String.format("%,d", mc.getWriteCount(), "#,###"));
			al.add("   size: " + String.format("%,d", mc.getWriteSize(), "#,###"));

		}

		OASyncServer ss = OARuntime.defaultGraph().internal().sync().getServer();
		if (ss != null) {
			al.add("OASync Server ======================");

			OARemoteMultiplexerServer rms = ss.getRemoteMultiplexerServer();
			al.add(" remote methods called: " + String.format("%,d", rms.getMethodCallCount(), "#,###"));
			al.add("   received: " + String.format("%,d", rms.getReceivedMethodCount(), "#,###"));

			al.add(" queue position: " + String.format("%,d", rms.getQueueHeadPos(), "#,###"));

			OAMultiplexerServer ms = rms.getMultiplexerServer();
			al.add(" connections live: " + String.format("%,d", ms.getLiveConnectionCount(), "#,###"));
			al.add("   created: " + String.format("%,d", ms.getCreatedConnectionCount(), "#,###"));

			al.add(" read count: " + String.format("%,d", ms.getReadCount(), "#,###"));
			al.add("   size: " + String.format("%,d", ms.getReadSize(), "#,###"));
			al.add(" write count: " + String.format("%,d", ms.getWriteCount(), "#,###"));
			al.add("   size: " + String.format("%,d", ms.getWriteSize(), "#,###"));
		}
		al.add(" ");

		Vector vec;
		Enumeration enumx;

		al.add("================== Resource properties ==================");
		vec = new Vector();
		enumx = Resource.getBundleProperties().keys();
		for (; enumx.hasMoreElements();) {
			String key = (String) enumx.nextElement();
			vec.add(key + " = " + convertValue(key, Resource.getValue(key)));
		}
		Collections.sort(vec);
		al.addAll(vec);

		al.add("================== Runtime arguments ==================");
		vec = new Vector();
		enumx = Resource.getRuntimeProperties().keys();
		for (; enumx.hasMoreElements();) {
			String key = (String) enumx.nextElement();
			vec.add(key + " = " + convertValue(key, Resource.getValue(key)));
		}
		Collections.sort(vec);
		al.addAll(vec);

		al.add("================== server.ini properties ==================");
		vec = new Vector();
		enumx = Resource.getServerProperties().keys();
		for (; enumx.hasMoreElements();) {
			String key = (String) enumx.nextElement();
			vec.add(key + " = " + convertValue(key, Resource.getValue(key)));
		}
		Collections.sort(vec);
		al.addAll(vec);

		al.add("================== client.ini properties ==================");
		vec = new Vector();
		enumx = Resource.getClientProperties().keys();
		for (; enumx.hasMoreElements();) {
			String key = (String) enumx.nextElement();
			vec.add(key + " = " + convertValue(key, Resource.getValue(key)));
		}
		Collections.sort(vec);
		al.addAll(vec);

		al.add("================== single.ini properties ==================");
		vec = new Vector();
		enumx = Resource.getSingleProperties().keys();
		for (; enumx.hasMoreElements();) {
			String key = (String) enumx.nextElement();
			vec.add(key + " = " + convertValue(key, Resource.getValue(key)));
		}
		Collections.sort(vec);
		al.addAll(vec);

		al.add("================== System properties ==================");
		vec = new Vector();
		Properties props = System.getProperties();
		enumx = props.keys();
		for (; enumx.hasMoreElements();) {
			String key = (String) enumx.nextElement();
			vec.add(key + ": " + props.getProperty(key));
		}
		Collections.sort(vec);
		al.addAll(vec);

		return al;
	}

	protected static String convertValue(String key, String val) {
		if (key == null || val == null) {
			return "";
		}
		key = key.toLowerCase();
		if (key.indexOf("password") >= 0) {
			val = "********";
		} else if (key.indexOf("pw") >= 0) {
			val = "*****";
		} else if (key.indexOf("secret") >= 0) {
			val = "*****";
		}
		return val;
	}

	/**
	 * Allows leading spaces for padding.
	 */
	public static String convertToValidPhoneNumber(String phone) {
		if (phone == null) {
			return null;
		}
		int x = phone.length();
		if (x == 0) {
			return phone;
		}
		StringBuilder sb = new StringBuilder(x);
		boolean b = false;
		for (int i = 0; i < x; i++) {
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
		for (int i = x; i < 10; i++) {
			b = true;
			sb.insert(0, ' ');
		}
		if (b) {
			phone = sb.toString();
		}
		return phone;
	}
}
