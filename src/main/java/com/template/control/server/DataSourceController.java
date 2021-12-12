// Copied from OATemplate project by OABuilder 02/13/19 10:11 AM
package com.template.control.server;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.*;
import java.util.zip.*;

import com.template.datasource.DataSource;
import com.template.model.oa.*;
import com.template.model.oa.propertypath.*;
import com.template.model.oa.filter.*;
import com.template.model.oa.cs.*;
import com.template.resource.Resource;
import com.viaoa.annotation.OAClass;
import com.viaoa.comm.io.OAObjectInputStream;
import com.viaoa.concurrent.OAExecutorService;
import com.viaoa.datasource.jdbc.OADataSourceJDBC;
import com.viaoa.datasource.objectcache.OADataSourceObjectCache;
import com.viaoa.hub.*;
import com.viaoa.json.OAJson;
import com.viaoa.object.*;
import com.viaoa.sync.OASyncDelegate;
import com.viaoa.sync.OASyncServer;
import com.viaoa.transaction.OATransaction;
import com.viaoa.util.*;
import com.viaoa.xml.*;

/**
 * Used to manage object persistence, includes serialization and OADataSource support.
 **/
public class DataSourceController {
	private static Logger LOG = OALogger.getLogger(DataSourceController.class);
	private DataSource dataSource;
	private OADataSourceObjectCache dsObjectCache;
	private boolean bIsUsingDatabase;

	private ServerRoot serverRoot;
	private Hub<ClientRoot> hubClientRoot;

	private OADate dateLastSerialize;
	private OATime timeLastSerialize;
	private String cacheFileName;
	private OADate dateLastXml;
	private OATime timeLastXml;
	private OADate dateLastJson;
	private OATime timeLastJson;

	private OAExecutorService executorService;
	private final AtomicInteger aiExecutor = new AtomicInteger();
	private CopyOnWriteArrayList<String> alExecutorService = new CopyOnWriteArrayList<String>();
	private CopyOnWriteArrayList<String> alSelectError = new CopyOnWriteArrayList<String>();

	public DataSourceController(ServerRoot serverRoot, Hub<ClientRoot> hubClientRoot) throws Exception {
		this.serverRoot = serverRoot;
		this.hubClientRoot = hubClientRoot;

		cacheFileName = Resource.getValue(Resource.DB_CacheFileName);
		String driver = Resource.getValue(Resource.DB_JDBC_Driver);

		bIsUsingDatabase = Resource.getBoolean(Resource.DB_Enabled, false);
		if (!bIsUsingDatabase) {
			String msg = "NOTE: DataSourceController ********* not using database **********";
			LOG.warning(msg);
			for (int i = 0; i < 10; i++) {
				System.out.println(msg);
			}
		} else {
			// put "non-db ready" classes into another DS
			String packageName = AppServer.class.getPackage().getName(); // oa model classes
			String[] fnames = OAReflect.getClasses(packageName);
			Class[] classes = null;
			for (String fn : fnames) {
				Class c = Class.forName(packageName + "." + fn);
				OAClass cx = (OAClass) c.getAnnotation(OAClass.class);
				if (cx == null) {
					continue;
				}
				if (cx.useDataSource()) {
					continue;
				}
				classes = (Class[]) OAArray.add(Class.class, classes, c);
			}

			final Class[] csNotUsingDatabase = classes;
			if (csNotUsingDatabase != null && csNotUsingDatabase.length > 0) {
				for (Class c : csNotUsingDatabase) {
					LOG.warning(String.format("Class %s is not in using database", c.getName()));
				}
				new OADataSourceObjectCache() {
					@Override
					public boolean isClassSupported(Class clazz, OAFilter filter) {
						for (Class c : csNotUsingDatabase) {
							if (c.equals(clazz)) {
								return true;
							}
						}
						return false;
					}
				};
			}

			if (!Resource.getBoolean(Resource.INI_SaveDataToDatabase, true)) {
				LOG.warning("NOT saving to Database, " + Resource.INI_SaveDataToDatabase + " is false");
				for (int i = 0; i < 20; i++) {
					System.out
							.println("DataSourceController. is NOT! saving to Database, " + Resource.INI_SaveDataToDatabase + " is false");
				}
			}

			dataSource = new DataSource();
			dataSource.open();
			dataSource.getOADataSource().setAssignIdOnCreate(true);
		}

		dsObjectCache = new OADataSourceObjectCache(); // for non-DB objects
		dsObjectCache.setAssignIdOnCreate(true);

		if (!OAString.isEmpty(cacheFileName)) {
			cacheFileName = OAFile.convertFileName(cacheFileName);
			File file = new File(cacheFileName);
			if (file.exists()) {
				LOG.config("OAObjectEmptyHubDelegate.load(" + cacheFileName + ");");
				try {
					OAObjectEmptyHubDelegate.load(file);
				} catch (Exception e) {
					LOG.log(Level.WARNING, "error loading " + cacheFileName + "", e);
				}
				file.delete();
			}
		}
	}

	public boolean isUsingDatabase() {
		return bIsUsingDatabase;
	}

	public OADataSourceObjectCache getObjectCacheDataSource() {
		return dsObjectCache;
	}

	public OADataSourceJDBC getOADataSourceJDBC() {
		if (this.dataSource == null) {
			return null;
		}
		return this.dataSource.getOADataSource();
	}

	private void select(final Hub hub) {
		select(hub, null, null);
	}

	private void select(final Hub hub, final String query, final String orderBy) {
		if (hub == null) {
			return;
		}
		aiExecutor.incrementAndGet();
		String s = "selecting " + hub.getObjectClass().getSimpleName() + ", query=" + query + ", orderBy=" + orderBy;
		LOG.fine(s);
		try {
			alExecutorService.add(s);
			hub.select(query, orderBy);
			hub.loadAllData();
			alExecutorService.remove(s);
		} finally {
			aiExecutor.decrementAndGet();
		}
	}

	public boolean loadServerRoot() throws Exception {
		// if (this.dataSource == null) return false;  // might need to use object cache filters
		LOG.log(Level.CONFIG, "selecting Server data");

		LOG.info("starting to select data");
		executorService = new OAExecutorService("DataSourceController.startup");

		if (!isUsingDatabase()) {
			if (!Resource.getBoolean(Resource.INI_SaveDataToFile, false) || !readFromSerializeFile()) {
				if (!Resource.getBoolean(Resource.INI_SaveDataToJsonFile, false) || !readFromJsonFile()) {
					if (!Resource.getBoolean(Resource.INI_SaveDataToXmlFile, false) || !readFromXmlFile()) {
						LOG.log(Level.WARNING, "No data loaded");
					}
				}
			}
			if (!readObjectCacheDataSource()) {
				OAObjectSaveDelegate.save(serverRoot, OAObject.CASCADE_ALL_LINKS, new OACascade() {
					@Override
					public boolean wasCascaded(OAObject oaObj, boolean bAdd) {
						boolean b = super.wasCascaded(oaObj, bAdd);
						if (!b && oaObj != serverRoot) {
							dsObjectCache.addToStorage(oaObj, true);
						}
						return b;
					}
				});
				writeObjectCacheDataSource();
			}
		}

		/*$$Start: DatasourceController.loadServerRoot $$*/
		aiExecutor.incrementAndGet();
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				String msg = "serverRoot.getCreateOneAppServerHub()";
				try {
					alExecutorService.add(msg);
					if (isUsingDatabase()) {
						select(serverRoot.getCreateOneAppServerHub(), "", null);
					}
					if (serverRoot.getCreateOneAppServerHub().getAt(0) == null) {
						// createOne=true
						serverRoot.getCreateOneAppServerHub().add(new AppServer());
					} else {
						serverRoot.getCreateOneAppServerHub().cancelSelect();
					}
				} catch (Exception e) {
					String s = "DataSourceController error selecting AppServers, exception=" + e;
					alSelectError.add(s);
					LOG.log(Level.WARNING, s, e);
				} finally {
					aiExecutor.decrementAndGet();
					alExecutorService.remove(msg);
				}
			}
		});
		aiExecutor.incrementAndGet();
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				String msg = "serverRoot.getAppUsers()";
				try {
					alExecutorService.add(msg);
					if (isUsingDatabase()) {
						select(serverRoot.getAppUsers(), "", null);
						serverRoot.getAppUsers().loadAllData();
					}
				} catch (Exception e) {
					String s = "DataSourceController error selecting AppUsers, exception=" + e;
					alSelectError.add(s);
					LOG.log(Level.WARNING, s, e);
				} finally {
					aiExecutor.decrementAndGet();
					alExecutorService.remove(msg);
				}
			}
		});

		/*$$End: DatasourceController.loadServerRoot $$*/

		int max = Resource.getInt(Resource.DB_MaxWaitForSelects, 300);
		for (int i = 0; i < max; i++) {
			if (aiExecutor.get() == 0) {
				break;
			}
			Thread.sleep(1000);
			LOG.fine(i + ") waiting for data to be selected, remaining=" + aiExecutor.get());
			if (i >= 15) {
				String s = "Open queries";
				for (String s2 : alExecutorService) {
					s += "\n" + s2;
				}
				LOG.fine(s);
			}
		}

		for (String s : alSelectError) {
			LOG.warning("Error during select: " + s);
		}

		// dont need to have these Hubs as selectAll in objectCache
		OAObjectCacheDelegate.removeSelectAllHub(serverRoot.getAppUsers());

		LOG.info("completed selecting data");
		executorService.close();
		return (alSelectError.size() == 0);
	}

	public void saveData() {
		// serverRoot.save(OAObject.CASCADE_ALL_LINKS);
		// hubClientRoot.saveAll(OAObject.CASCADE_ALL_LINKS);

		OACascade cascade = new OACascade();
		OAObjectSaveDelegate.save(serverRoot, OAObject.CASCADE_ALL_LINKS, cascade);

		HubSaveDelegate.saveAll(hubClientRoot, OAObject.CASCADE_ALL_LINKS, cascade);

		OASyncServer ss = OASyncDelegate.getSyncServer();
		if (ss != null) {
			OASyncDelegate.getSyncServer().saveCache(cascade, OAObject.CASCADE_ALL_LINKS);
			OASyncDelegate.getSyncServer().performDGC();
		}
	}

	public void writeToSerializeFile(boolean bErrorMode) throws Exception {
		final String dirName = Resource.getValue(Resource.APP_DataDirectory, "data");
		File f1 = new File(dirName);
		if (f1.exists()) {
			if (!f1.isDirectory()) {
				File f2;
				for (int i = 0;; i++) {
					f2 = new File(dirName + "_" + i);
					if (!f2.exists()) {
						break;
					}
				}
				f1.renameTo(f2);
			}
		} else {
			f1.mkdir();
		}

		LOG.fine("Saving data to file temp.bin, will rename when done");

		File fileTemp = new File(OAString.convertFileName(dirName + "/temp.bin"));
		_writeSerializeToFile(fileTemp);

		if (bErrorMode) {
			OADateTime dt = new OADateTime();
			String s = dt.toString("yyyyMMdd_HHmmss");
			File file = new File(OAFile.convertFileName(dirName + "/databaseDump_" + s + ".bin"));
			if (file.exists()) {
				file.delete();
			}
			fileTemp.renameTo(file);
			LOG.log(Level.CONFIG, "ErrorMode=true, Data has been saved to " + file.getName());
			return;
		}

		File dataFile = new File(OAString.convertFileName(dirName + "/data.bin"));

		if (dataFile.exists()) {
			String backupName = null;

			// save to daily/hourly/5minute
			OADate d = new OADate();
			if (dateLastSerialize == null || !d.equals(dateLastSerialize)) {
				dateLastSerialize = d;
				backupName = "data_daily_" + (d.toString("yyMMdd")) + ".bin";
				File f = new File(OAFile.convertFileName(dirName + "/" + backupName));
				if (f.exists()) {
					backupName = null;
				}
			}

			if (backupName == null || backupName.length() == 0) {
				OATime t = new OATime();
				if (timeLastSerialize == null || t.getHour() != timeLastSerialize.getHour()) {
					timeLastSerialize = t;
					backupName = "data_hourly_" + (t.toString("HH")) + ".bin";
				} else {
					// save to nearest 5 minutes, otherwise it could have 60 files over time.
					int m = t.getMinute();
					m = m == 0 ? 0 : ((m / 5) * 5);
					backupName = "data_min_" + OAString.format(m, "00") + ".bin";
				}
			}

			backupName = OAFile.convertFileName(dirName + "/" + backupName);
			File backupFile = new File(backupName);
			if (backupFile.exists()) {
				backupFile.delete();
			}
			dataFile.renameTo(backupFile);
			dataFile = new File(OAString.convertFileName(dirName + "/data.bin"));
		}

		fileTemp.renameTo(dataFile);
		LOG.fine("Saved data to serialized file " + dataFile);
	}

	protected void _writeSerializeToFile(File fileTemp) throws Exception {
		FileOutputStream fos = new FileOutputStream(fileTemp);

		Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
		DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(fos, deflater, 1024 * 5);

		/**
		 * Notes on serialization: The ObjectStream will make sure that objects are only saved/visited once. These 3 wrappers all use the
		 * same ObjectStream.
		 */
		ObjectOutputStream oos = new ObjectOutputStream(deflaterOutputStream);
		OAObjectSerializer wrap;

		wrap = new OAObjectSerializer(serverRoot, false, true);
		wrap.setIncludeBlobs(true);
		// wrap.setExcludedReferences(new Class[] {DeliveryDate.class});
		oos.writeObject(wrap);

		String s = Resource.getValue(Resource.APP_DataVersion);
		oos.writeObject(s);

		deflaterOutputStream.finish();
		oos.close();
		fos.close();
	}

	public boolean readFromSerializeFile() throws Exception {
		final String dirName = Resource.getValue(Resource.APP_DataDirectory, "data");
		LOG.log(Level.CONFIG, "Reading from file " + dirName + "/data.bin");
		File file = new File(OAFile.convertFileName(dirName + "/data.bin"));
		if (!file.exists()) {
			LOG.log(Level.CONFIG, "file " + dirName + "/data.bin does not exist");
			return false;
		}
		FileInputStream fis = new FileInputStream(file);

		Inflater inflater = new Inflater();
		InflaterInputStream inflaterInputStream = new InflaterInputStream(fis, inflater, 1024 * 3);

		OAObjectInputStream ois = new OAObjectInputStream(inflaterInputStream);

		OAObjectSerializer wrap = (OAObjectSerializer) ois.readObject();
		ServerRoot sr = (ServerRoot) wrap.getObject();
		//Note: sr will be the same as this.serverRoot, since it has the same Id.

		if (sr != this.serverRoot) {
			throw new Exception("Invalid ServerRoot, expected id=777");
		}

		try {
			String s = (String) ois.readObject();
			Resource.setValue(Resource.TYPE_Server, Resource.APP_DataVersion, s);
		} catch (EOFException e) {
		}

		ois.close();
		fis.close();
		LOG.log(Level.CONFIG, "reading from serialized file data.bin completed");

		return true;
	}

	public void close() {
		if (dataSource != null) {
			dataSource.close(); // this will call JavaDB shutdown, and remove datasource from list of available datasources
		}
		if (!OAString.isEmpty(cacheFileName)) {
			try {
				LOG.config("saving " + cacheFileName);
				OAFile.mkdirsForFile(cacheFileName);
				OAObjectEmptyHubDelegate.save(new File(cacheFileName));
				LOG.config("saved " + cacheFileName);
			} catch (Exception e) {
				LOG.log(Level.WARNING, "error while saving " + cacheFileName, e);
			}
		}
	}

	public boolean verifyDataSource() throws Exception {
		LOG.config("Verifying database structure");
		if (dataSource == null) {
			return true;
		}
		dataSource.getOADataSource().verify();
		return true;
	}

	public String getInfo() {
		Vector vec = new Vector();

		OADataSourceJDBC oads = getOADataSourceJDBC();
		vec.addElement("DataSource ============================");
		oads.getInfo(vec);

		StringBuffer sb = new StringBuffer(4096);
		int x = vec.size();
		for (int i = 0; i < x; i++) {
			String s = (String) vec.elementAt(i);
			sb.append(s + "\r\n");
		}
		return new String(sb);
	}

	public boolean isDataSourceReady() {
		if (dataSource == null) {
			return true;
		}
		return dataSource.getOADataSource().isAvailable();
	}

	public void disconnect() {
		LOG.fine("called");
		if (dataSource != null) {
			dataSource.close();
		}
	}

	public void reconnect() {
		LOG.fine("called");
		if (dataSource != null) {
			dataSource.getOADataSource().reopen(0);
		}
	}

	public void updateDataSource() {
		// todo
	}

	private int cacheCnt;

	public void insertAllObjectsToDatabase() throws Exception {
		LOG.config("Loading database");

		final HashMap<Class, Integer> hash = new HashMap<Class, Integer>();
		OAObjectCacheDelegate.callback(new OACallback() {
			@Override
			public boolean updateObject(Object obj) {
				cacheCnt++;
				OAObjectDelegate.setNew((OAObject) obj, true);
				Object objx = ((OAObject) obj).getProperty("id");
				if (objx != null && objx instanceof Number) {
					int id = ((Number) objx).intValue();
					Integer n = hash.get(objx.getClass());
					if (n == null || id > n) {
						hash.put(obj.getClass(), id);
					}
				}
				return true;
			}
		});

		OADataSourceJDBC oads = getOADataSourceJDBC();
		for (Entry<Class, Integer> entry : hash.entrySet()) {
			oads.setNextNumber(entry.getKey(), entry.getValue() + 1);
		}
		System.out.println(cacheCnt + " objects updated from Cache");

		OATransaction tran = new OATransaction(java.sql.Connection.TRANSACTION_READ_UNCOMMITTED);
		try {
			tran.start();
			serverRoot.save(OAObject.CASCADE_ALL_LINKS);
			tran.commit();
		} catch (Exception e) {
			System.out.println("Exception: " + e);
			e.printStackTrace();
			tran.rollback();
			throw e;
		}
	}

	/**
	 * Calls verifyDatabaseTables to check for table/index/file corruption.
	 *
	 * @return true if database is good, else false if there is corruption.
	 * @see #forwardRestoreBackupDatabase() to restore from backup and include log files for zero data loss.
	 * @see #backupDatabase(String) to perform database files, and include log files - which will enable forward restores.
	 */
	public boolean isDataSourceCorrupted() {
		boolean b;
		try {
			getOADataSourceJDBC().checkForCorruption();
			b = false;
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Error while checking datasource, will return false", e);
			b = true;
		}
		return b;
	}

	public boolean restoreDataSource(String backupDirectory) {
		boolean b;
		try {
			getOADataSourceJDBC().restore(backupDirectory);
			b = false;
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Error while restoring datasource, will return false", e);
			b = true;
		}
		return b;
	}

	public boolean backupDataSource(String backupDirectory) {
		if (getOADataSourceJDBC() == null) {
			return false;
		}
		boolean b;
		try {
			getOADataSourceJDBC().backup(backupDirectory);
			b = false;
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Error while backing up datasource, will return false", e);
			b = true;
		}
		return b;
	}

	public boolean readFromXmlFile() throws Exception {
		final String dirName = Resource.getValue(Resource.APP_DataDirectory, "data");
		LOG.log(Level.CONFIG, "Reading from file " + dirName + "/data.xml");
		File file = new File(OAFile.convertFileName(dirName + "/data.xml"));
		if (!file.exists()) {
			LOG.log(Level.CONFIG, "file " + dirName + "/data.xml does not exist");
			return false;
		}

		OAXMLReader r = new OAXMLReader();
		r.read(file);

		LOG.log(Level.CONFIG, "reading from file data.xml completed");
		return true;
	}

	public void writeToXmlFile() throws Exception {
		final String dirName = Resource.getValue(Resource.APP_DataDirectory, "data");
		File f1 = new File(dirName);
		if (f1.exists()) {
			if (!f1.isDirectory()) {
				File f2;
				for (int i = 0;; i++) {
					f2 = new File(dirName + "_" + i);
					if (!f2.exists()) {
						break;
					}
				}
				f1.renameTo(f2);
			}
		} else {
			f1.mkdir();
		}

		LOG.fine("Saving data to file temp.xml, will rename when done");

		File fileTemp = new File(OAString.convertFileName(dirName + "/temp.xml"));
		_writeToXmlFile(fileTemp);

		File dataFile = new File(OAString.convertFileName(dirName + "/data.xml"));

		if (dataFile.exists()) {
			String backupName = null;

			// save to daily/hourly/5minute
			OADate d = new OADate();
			if (dateLastXml == null || !d.equals(dateLastXml)) {
				dateLastXml = d;
				backupName = "data_daily_" + (d.toString("yyMMdd")) + ".xml";
				File f = new File(OAFile.convertFileName(dirName + "/" + backupName));
				if (f.exists()) {
					backupName = null;
				}
			}

			if (backupName == null || backupName.length() == 0) {
				OATime t = new OATime();
				if (timeLastXml == null || t.getHour() != timeLastXml.getHour()) {
					timeLastXml = t;
					backupName = "data_hourly_" + (t.toString("HH")) + ".xml";
				} else {
					// save to nearest 5 minutes, otherwise it could have 60 files over time.
					int m = t.getMinute();
					m = m == 0 ? 0 : ((m / 5) * 5);
					backupName = "data_min_" + OAString.format(m, "00") + ".xml";
				}
			}

			backupName = OAFile.convertFileName(dirName + "/" + backupName);
			File backupFile = new File(backupName);
			if (backupFile.exists()) {
				backupFile.delete();
			}
			dataFile.renameTo(backupFile);
			dataFile = new File(OAString.convertFileName(dirName + "/data.xml"));
		}

		fileTemp.renameTo(dataFile);
		LOG.fine("Saved data to file " + dataFile);
	}

	protected void _writeToXmlFile(final File file) throws Exception {
		OAXMLWriter w = new OAXMLWriter(file.getPath());

		w.setIndentAmount(2);
		w.write(serverRoot);
		w.close();
	}

	public boolean readFromJsonFile() throws Exception {
		final String dirName = Resource.getValue(Resource.APP_DataDirectory, "data");
		LOG.log(Level.CONFIG, "Reading from file " + dirName + "/data.json");
		File file = new File(OAFile.convertFileName(dirName + "/data.json"));
		if (!file.exists()) {
			LOG.log(Level.CONFIG, "file " + dirName + "/data.json does not exist");
			return false;
		}

		OAJson oaj = new OAJson();
		serverRoot = oaj.readObject(file, ServerRoot.class, false);

		LOG.log(Level.CONFIG, "reading from file data.json completed");

		return true;
	}

	public void writeToJsonFile() throws Exception {
		final String dirName = Resource.getValue(Resource.APP_DataDirectory, "data");
		File f1 = new File(dirName);
		if (f1.exists()) {
			if (!f1.isDirectory()) {
				File f2;
				for (int i = 0;; i++) {
					f2 = new File(dirName + "_" + i);
					if (!f2.exists()) {
						break;
					}
				}
				f1.renameTo(f2);
			}
		} else {
			f1.mkdir();
		}

		LOG.fine("Saving data to file temp.json, will rename when done");

		File fileTemp = new File(OAString.convertFileName(dirName + "/temp.json"));
		_writeToJsonFile(fileTemp);

		File dataFile = new File(OAString.convertFileName(dirName + "/data.json"));

		if (dataFile.exists()) {
			String backupName = null;

			// save to daily/hourly/5minute
			OADate d = new OADate();
			if (dateLastJson == null || !d.equals(dateLastJson)) {
				dateLastJson = d;
				backupName = "data_daily_" + (d.toString("yyMMdd")) + ".json";
				File f = new File(OAFile.convertFileName(dirName + "/" + backupName));
				if (f.exists()) {
					backupName = null;
				}
			}

			if (backupName == null || backupName.length() == 0) {
				OATime t = new OATime();
				if (timeLastJson == null || t.getHour() != timeLastJson.getHour()) {
					timeLastJson = t;
					backupName = "data_hourly_" + (t.toString("HH")) + ".json";
				} else {
					// save to nearest 5 minutes, otherwise it could have 60 files over time.
					int m = t.getMinute();
					m = m == 0 ? 0 : ((m / 5) * 5);
					backupName = "data_min_" + OAString.format(m, "00") + ".json";
				}
			}

			backupName = OAFile.convertFileName(dirName + "/" + backupName);
			File backupFile = new File(backupName);
			if (backupFile.exists()) {
				backupFile.delete();
			}
			dataFile.renameTo(backupFile);
			dataFile = new File(OAString.convertFileName(dirName + "/data.json"));
		}

		fileTemp.renameTo(dataFile);
		LOG.fine("Saved data to file " + dataFile);
	}

	protected void _writeToJsonFile(final File file) throws Exception {
		OAJson oaj = new OAJson();
		oaj.setIncludeAll(true);
		oaj.write(serverRoot, file);
	}

	public void writeObjectCacheDataSource() throws Exception {
		final String dirName = Resource.getValue(Resource.APP_DataDirectory, "data");
		LOG.log(Level.CONFIG, "Save to file " + dirName + "/ObjectCache.bin");
		File file = new File(OAFile.convertFileName(dirName + "/ObjectCache.bin"));
		OAFile.mkdirsForFile(file);
		getObjectCacheDataSource().saveToStorageFile(file);
	}

	public boolean readObjectCacheDataSource() throws Exception {
		final String dirName = Resource.getValue(Resource.APP_DataDirectory, "data");
		LOG.log(Level.CONFIG, "Reading from file " + dirName + "/ObjectCache.bin");
		File file = new File(OAFile.convertFileName(dirName + "/ObjectCache.bin"));
		return getObjectCacheDataSource().loadFromStorageFile(file);
	}

	/*
	public static void main(String[] args) throws Exception {
	    DataSourceController dsc = new DataSourceController();

	    dsc.loadServerRoot();

	    // dsc.backupDatabase("c:\\temp\\dbBackDerby");
	    dsc.isDataSourceReady();
	    dsc.isDatabaseCorrupted();

	    System.out.println("Done");
	}
	*/
}
