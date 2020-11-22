package com.template.control.single;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.template.control.HelpController;
import com.template.control.LogController;
import com.template.control.client.ClientFrameController;
import com.template.control.server.DataSourceController;
import com.template.control.server.ObjectController;
import com.template.control.server.ServerSpellCheckController;
import com.template.delegate.CronDelegate;
import com.template.delegate.ExecutorServiceDelegate;
import com.template.delegate.LogDelegate;
import com.template.delegate.ModelDelegate;
import com.template.delegate.ScheduledExecutorServiceDelegate;
import com.template.model.oa.AppServer;
import com.template.model.oa.AppUser;
import com.template.model.oa.AppUserLogin;
import com.template.model.oa.cs.ClientRoot;
import com.template.model.oa.cs.ServerRoot;
import com.template.resource.Resource;
import com.viaoa.context.OAContext;
import com.viaoa.hub.Hub;
import com.viaoa.jfc.OAJfcUtil;
import com.viaoa.jfc.text.spellcheck.SpellChecker;
import com.viaoa.object.OAObject;
import com.viaoa.object.OAObjectInfoDelegate;
import com.viaoa.process.OACronProcessor;
import com.viaoa.util.OAConv;
import com.viaoa.util.OADate;
import com.viaoa.util.OADateTime;
import com.viaoa.util.OAFile;
import com.viaoa.util.OAReflect;
import com.viaoa.util.OAString;
import com.viaoa.util.OATime;

/**
 * Main controller for starting in Single User mode.
 */
public abstract class SingleController {
	private static Logger LOG = Logger.getLogger(SingleController.class.getName());

	private DataSourceController controlDataSource;
	private ObjectController controlObject;
	private LogController controlLog;
	private HelpController controlHelp;
	private volatile ClientFrameController controlFrame;
	private JFrame frmDummy;

	private boolean bExitCalled;
	private ServerRoot serverRoot;
	private ClientRoot clientRoot;
	private Hub<ClientRoot> hubClientRoot;

	private final Object LOCKSave = new Object();
	private volatile boolean bClosed;
	private AppUserLogin appUserLogin;

	protected ServerSpellCheckController controlServerSpellCheck;
	private SpellChecker spellChecker;

	public SingleController(JFrame frmDummy) {
		this.frmDummy = frmDummy;
		Resource.setLocale(null);

		String s = Resource.getValue(Resource.APP_SingleApplicationName);
		Resource.setRunTimeName(s);
	}

	public ServerRoot getServerRoot() {
		if (serverRoot == null) {
			serverRoot = new ServerRoot();
		}
		return serverRoot;
	}

	public Hub<ClientRoot> getClientRootHub() {
		if (hubClientRoot == null) {
			hubClientRoot = new Hub<>(ClientRoot.class);
			hubClientRoot.add(getClientRoot());
		}
		return hubClientRoot;
	}

	public ClientRoot getClientRoot() {
		if (clientRoot == null) {
			clientRoot = new ClientRoot();
			clientRoot.setId(1);
			clientRoot.save();
		}
		return clientRoot;
	}

	public boolean start() throws Exception {
		getLogController();

		String packageName = "com.template.model.oa";
		String[] cnames = OAReflect.getClasses(packageName);
		for (String fn : cnames) {
			Class c = Class.forName(packageName + "." + fn);
			OAObjectInfoDelegate.getObjectInfo(c);
		}

		boolean b = _start();

		long ms = System.currentTimeMillis();
		getFrameController().getFrame();
		long ms2 = System.currentTimeMillis();
		LOG.fine("frame load time " + (ms2 - ms) + "ms");
		getFrameController().getFrame().setVisible(true);
		setProcessing(false);

		int x = Resource.getInt(Resource.CLIENT_CheckAWTThreadMinutes);
		if (x > 0) {
			runCheckAWTThread(x);
		}

		LOG.config(Resource.getValue(Resource.APP_Welcome));
		return b;
	}

	private boolean _start() throws Exception {
		// needs to be set before datasource controller
		LOG.fine("create and start cron process");
		OACronProcessor cp = new OACronProcessor();
		CronDelegate.setCronProcessor(cp);
		CronDelegate.getCronProcessor().start();

		// DataSource
		boolean bCheckForDatasourceCorruption = Resource.getBoolean(Resource.DB_CheckForCorruption, true);

		// 1: verify that database exists and can be opened
		LOG.config("Starting Database");
		if (!getDataSourceController().isDataSourceReady()) {
			String msg = "DataSource can not be opened.";
			LOG.severe(msg); // this will cause program to exit
			throw new Exception(msg);
		}

		// 2: verify that database files are not corrupted.
		//    if they are damaged, then perform a forward restore from backup + log files.
		for (int i = 0; bCheckForDatasourceCorruption && i < 2; i++) {
			LOG.config("Verifying database files");
			if (!getDataSourceController().isDataSourceCorrupted()) {
				break;
			}

			String msg = "DataSource is not valid.";
			LOG.severe(msg); // this will cause program to exit
			if (i > 0) {
				throw new Exception(msg);
			}

			String dirName = Resource.getValue(Resource.DB_BackupDirectory, "dbbackup");

			LOG.config("performing a forward restore from directory " + dirName);
			getDataSourceController().restoreDataSource(dirName);
			LOG.config("successfully performed a forward restore.");
		}

		// 3: verify that the database structures: tables/columns/indexes/etc
		LOG.config("Verifying database");
		if (Resource.getBoolean(Resource.DB_Verify, true) && !getDataSourceController().verifyDataSource()) {
			String msg = "Can not verify Database, please check database settings in the \"server.ini\" file.";
			LOG.severe(msg); // this will cause program to exit
			throw new Exception(msg);
		}

		// 4: load the data
		LOG.config("Loading data for Server Root");
		if (!getDataSourceController().loadServerRoot()) {
			return false; // error loading data from DS
		}

		// set admin user for this server
		AppUser user = null;
		for (AppUser au : getServerRoot().getAppUsers()) {
			if (au.getAdmin()) {
				user = au;
				break;
			}
		}
		if (user == null) {
			user = new AppUser();
			user.setFirstName("Admin");
			user.setLastName("Admin");
			user.setAdmin(true);
			user.setLoginId("admin");
			user.setPassword(OAString.convertToSHAHash("admin"));
			user.save();
			getServerRoot().getAppUsers().add(user);
		}
		ModelDelegate.setLocalAppUser(user);

		LOG.config("Initializing OAContext ... as admin user");
		OAContext.setContextHub(null, ModelDelegate.getLocalAppUserHub());

		// initialize serverRoot, ModelDelegate
		ModelDelegate.initialize(getServerRoot(), getClientRoot());

		// must be after ModelDelegate is initialized
		LOG.config("Starting Object Controller");
		getObjectController().start();

		AppServer appServer = ModelDelegate.getAppServer();
		appServer.setStarted(null);
		appServer.setCreated(new OADateTime());
		appServer.setRelease("" + Resource.getInt(Resource.APP_Release));

		appUserLogin = new AppUserLogin();
		appUserLogin.setAppUser(user);
		appUserLogin.setConnectionId(0);
		appUserLogin.setLocation("App Server");
		appUserLogin.setComputerName(System.getProperty("user.name"));

		appUserLogin.setHostName(Resource.getValue(Resource.APP_HostName));
		appUserLogin.setIpAddress(Resource.getValue(Resource.APP_HostIPAddress));

		appServer.setAppUserLogin(appUserLogin);

		ModelDelegate.setLocalAppUserLogin(appUserLogin);

		LOG.fine("SpellCheck Controller");
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				String root = Resource.getValue(Resource.APP_RootDirectory) + "/";
				root = OAFile.convertFileName(root);
				String s = null;
				try {
					s = Resource.getValue(Resource.APP_DictionaryFileName);
					getSpellCheckController().loadDictionaryTextFile(root + s);
					s = Resource.getValue(Resource.APP_DictionaryFileName2);
					getSpellCheckController().loadDictionaryTextFile(root + s);
					s = Resource.getValue(Resource.APP_NewWordsFileName);
					getSpellCheckController().loadNewWordsTextFile(root + s);
				} catch (Exception e) {
					LOG.log(Level.WARNING, "Error loading SpellCheck file " + root + s + ", will continue.", e);
				}
			}
		});
		t.setName("SpellCheck.preload");
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();

		Resource.setSpellChecker(getSpellChecker());

		// Thread to save data every 5 minutes
		LOG.config("Starting DB thread");
		startDBThread();

		// remove old log files
		Runnable r = new Runnable() {
			@Override
			public void run() {
				int i1 = OAConv.toInt(Resource.getValue(Resource.APP_LogRegularDays, "14"));
				int i2 = OAConv.toInt(Resource.getValue(Resource.APP_LogErrorDays, "30"));
				LOG.config("removing regular log files > " + i1 + " days.");
				LOG.config("removing error log files > " + i2 + " days.");
				getLogController().removeOldLogFiles(i1, i2);
			}
		};
		ExecutorServiceDelegate.submit(r);
		ScheduledExecutorServiceDelegate.scheduleEvery(r, new OATime(0, 30, 0)); // run at 12:30am

		/*
		LOG.config("adding ShutdownHook to call close");
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					if (!bClosed) {
						LOG.fine("calling ShutdownHook");
						SingleController.this.close();
					}
				} catch (Exception e) {
					LOG.log(Level.WARNING, "Exception in shutDownHook", e);
				}
			}
		})
		*/;

		appServer.setStarted(new OADateTime());

		return true;
	}

	private volatile boolean bCheckingAWT;

	private void runCheckAWTThread(final int minutes) {
		LOG.fine("start thread");
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				_runCheckAWTThread(minutes);
			}
		}, "CheckAWTThread");
		t.setDaemon(true);
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
	}

	private void _runCheckAWTThread(int minutes) {
		// run AWT thread checker, will send stack traces to server if it is frozen
		int errorCount = 0;
		for (;;) {
			try {
				bCheckingAWT = true;
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						bCheckingAWT = false;
					}
				});
				Thread.sleep(((errorCount > 0) ? 1 : minutes) * 60 * 1000);
				if (bCheckingAWT && !Resource.getBoolean(Resource.INI_Debug)) {
					LOG.warning("AWTThread did not respond to invokeLater, dumping stack traces to log.");
					controlLog.dumpStackTrace();
					if (++errorCount == 3) {
						callExit();
					}
				} else {
					errorCount = 0;
				}
			} catch (Exception e) {
				LOG.log(Level.WARNING, "Error while running LogController.dumpStackTrace", e);
				break;
			}
		}
	}

	protected LogController getLogController() {
		if (controlLog != null) {
			return controlLog;
		}
		controlLog = new LogController(false) {
			@Override
			protected void onStatusMessage(String msg) {
				SingleController.this.setStatus(msg);
			}
		};
		LogDelegate.setLogController(controlLog);

		// Have Logger remove all log files
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				int i1 = OAConv.toInt(Resource.getValue(Resource.APP_LogRegularDays, "14"));
				int i2 = OAConv.toInt(Resource.getValue(Resource.APP_LogErrorDays, "30"));
				LOG.config("removing regular log files > " + i1 + " days.");
				LOG.config("removing error log files > " + i2 + " days.");
				getLogController().removeOldLogFiles(i1, i2);
			}
		}, "LogController.removeOldLogFiles");
		t.start();

		return controlLog;
	}

	public DataSourceController getDataSourceController() {
		if (controlDataSource == null) {
			try {
				controlDataSource = new DataSourceController(getServerRoot(), getClientRootHub());
			} catch (Exception e) {
				LOG.log(Level.WARNING, "DataSource Error", e);
				throw new RuntimeException("Exception getting datasource controller", e);
			}
		}
		return controlDataSource;
	}

	protected ObjectController getObjectController() {
		if (controlObject == null) {
			controlObject = new ObjectController();
		}
		return controlObject;
	}

	protected ServerSpellCheckController getSpellCheckController() {
		if (controlServerSpellCheck == null) {
			controlServerSpellCheck = new ServerSpellCheckController();
		}
		return controlServerSpellCheck;
	}

	private SpellChecker getSpellChecker() {
		if (spellChecker != null) {
			return spellChecker;
		}
		spellChecker = new SpellChecker() {
			@Override
			public boolean isWordFound(String word) {
				return getSpellCheckController().isWordFound(word);
			}

			@Override
			public String[] getMatches(String text) {
				return getSpellCheckController().getMatches(text, 50);
			}

			@Override
			public String[] getSoundexMatches(String text) {
				return getSpellCheckController().getSoundexMatches(text);
			}

			@Override
			public void addNewWord(String word) {
				getSpellCheckController().addNewWord(word);
			}
		};

		return spellChecker;
	}

	protected synchronized HelpController getHelpController() {
		if (controlHelp == null) {
			controlHelp = new HelpController();
		}
		return controlHelp;
	}

	public JFrame getFrame() {
		JFrame frm = null;
		if (controlFrame != null) {
			if (getFrameController().isFrameCreated()) {
				frm = getFrameController().getFrame();
			}
		}
		if (frm == null) {
			frm = frmDummy;
		}
		return frm;
	}

	public ClientFrameController getFrameController() {
		if (controlFrame != null) {
			return controlFrame;
		}

		synchronized (this) {
			if (controlFrame != null) {
				return controlFrame;
			}
			controlFrame = createFrameController();
		}
		return controlFrame;
	}

	private ClientFrameController createFrameController() {
		ClientFrameController fc = new ClientFrameController(getHelpController()) {
			@Override
			public void onExit() {
				SingleController.this.callExit();
			}

			@Override
			public void onSave() {
				SingleController.this.saveData();
			}

			@Override
			protected void setLookAndFeel(String laf) {
				SingleController.this.setLookAndFeel(laf);
			}
		};
		return fc;
	}

	boolean setStatus(String msg) {
		if (controlFrame != null) {
			if (controlFrame.isFrameCreated()) {
				getFrameController().setStatus(msg);
				return false;
			}
		}
		return false;
	}

	boolean setProcessing(boolean bVisible) {
		return setProcessing(bVisible, "");
	}

	boolean setProcessing(boolean bVisible, String msg) {
		if (controlFrame != null) {
			if (controlFrame.isFrameCreated()) {
				getFrameController().setProcessing(bVisible, msg);
				return true;
			}
		}
		return false;
	}

	public void setLookAndFeel(String laf) {
		LOG.fine("L&F=" + laf);
		boolean bWasNull = (laf == null);
		if (laf == null) {
			laf = Resource.getValue(Resource.APP_LookAndFeel, (String) null);
		}

		try {
			laf = OAJfcUtil.setLookAndFeel(laf);

			// SwingUtilities.updateComponentTreeUI(..)
			if (controlFrame != null) {
				getFrameController().updateUI();
			}
			if (controlHelp != null) {
				getHelpController().updateUI();
			}

			if (!bWasNull) {
				Resource.setValue(Resource.TYPE_Single, Resource.APP_LookAndFeel, laf);
				Resource.save();
			}
			LOG.fine("L&F set to " + laf);
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Error setting L&F to " + laf, e);
		} finally {
		}
	}

	private void callExit() {
		bExitCalled = true;
		onExit();
	}

	protected abstract void onExit();

	private final AtomicInteger aiSave = new AtomicInteger();

	public void saveData() {
		aiSave.getAndIncrement();
		synchronized (LOCKSave) {
			LOCKSave.notify();
		}
	}

	private void startDBThread() {
		LOG.fine("entering");
		Thread t = new Thread(new Runnable() {
			OADate dateLastBackup = new OADate(); // set today, so that it will run after midnight

			public void run() {
				for (int i = 0; !bClosed; i++) {
					int cntSave = aiSave.get();
					try {
						if (i > 0) {
							LOG.fine(i + " saving to database");
							_saveData();
						}
						if (dateLastBackup != null && !dateLastBackup.equals(new OADate())) {
							dateLastBackup = new OADate();
							SingleController.this.backupDatabase();
						}
					} catch (Exception e) {
						LOG.log(Level.WARNING, "DBThread Error", e);
					}
					try {
						if (cntSave != aiSave.get()) {
							continue;
						}
						synchronized (LOCKSave) {
							LOG.finer("waiting 5 minutes");
							LOCKSave.wait(1000 * 60 * 5);
						}
					} catch (Exception e) {
						LOG.log(Level.WARNING, "DBThread Error", e);
					}
				}
			}
		}, "DatabaseAutoSave");
		t.start();
	}

	private void _saveData() {
		LOG.fine("Saving data ...");

		boolean bSaveToDB = Resource.getBoolean(Resource.DB_Enabled, true);
		if (bSaveToDB) {
			bSaveToDB = Resource.getBoolean(Resource.INI_SaveDataToDatabase, true);
		}

		if (bSaveToDB) {
			getDataSourceController().saveData();
			LOG.fine("Data saved to database");
		} else {
			LOG.fine("NOT saving data to database");
		}

		boolean bSaveToFile = Resource.getBoolean(Resource.INI_SaveDataToFile, !bSaveToDB);
		try {
			if (bSaveToFile) {
				LOG.fine("Saving data as serialized object file");
				getDataSourceController().writeToSerializeFile(false);
			}
		} catch (Throwable e) {
			LOG.log(Level.WARNING, "Error while saving data to serialized file", e);
		}

		try {
			if (Resource.getBoolean(Resource.INI_SaveDataToXmlFile, false)) {
				LOG.fine("Saving data as XML object file");
				getDataSourceController().writeToXmlFile();
			}
		} catch (Throwable e) {
			LOG.log(Level.WARNING, "Error while saving data to json file", e);
		}

		try {
			if (Resource.getBoolean(Resource.INI_SaveDataToJsonFile, false)) {
				LOG.fine("Saving data as JSON object file");
				getDataSourceController().writeToJsonFile();
			}
		} catch (Throwable e) {
			LOG.log(Level.WARNING, "Error while saving data to json file", e);
		}

		if (!bSaveToDB) {
			serverRoot.save(OAObject.CASCADE_ALL_LINKS); // flag all as saved
		}
		LOG.fine("Data saved");

		String s = Resource.getValue(Resource.APP_NewWordsFileName);
		try {
			LOG.finer("Saving spellcheck new words data");
			getSpellCheckController().saveNewWordsTextFile(s);
		} catch (Throwable e) {
			LOG.log(Level.WARNING, "Error while saving to file " + s, e);
		}
	}

	public void close() throws Exception {
		LOG.config("Closing application ... please wait while shutdown completes ...");

		if (controlFrame != null) {
			controlFrame.close(); // saves window size/location to ini file and frm.setVisible(false)
		}

		bClosed = true;
		synchronized (LOCKSave) {
			LOCKSave.notify();
			_saveData();
		}

		if (controlObject != null) {
			LOG.config("Closing object controller");
			controlObject.stop();
		}

		LOG.config("Closing database");
		getDataSourceController().close();

		if (controlLog != null) {
			LOG.config("Closing log files ... " + Resource.getValue(Resource.APP_GoodBye));
			controlLog.close();
		}
	}

	private void backupDatabase() {
		String dirName = Resource.getValue(Resource.DB_BackupDirectory, "dbbackup");
		//dirName += "/" + (new OADate()).toString("yyyyMMdd");
		//dirName = OAString.convertFileName(dirName);
		OAFile.mkdirsForFile(dirName);

		try {
			LOG.config("Starting Database backup to " + dirName);
			getDataSourceController().backupDataSource(dirName);
			LOG.config("Completed Database backup to " + dirName);
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Database backup failed to directory " + dirName, e);
		}
	}

	public static void main(String[] args) {
		// Resource.setRootDir(".");
		Resource.loadArguments(args);
		Resource.setRunType(Resource.RUNTYPE_Single);
		SingleController sc = new SingleController(null) { // StartupController.RUNTYPE_Single
			@Override
			protected void onExit() {
				try {
					close();
					LOG.config("Looks Great! :) good bye");
					System.exit(0);
				} catch (Exception e) {
					System.out.println("Exception onExit, " + e);
					System.exit(1);
				}
			}
		};
		try {
			sc.start();
		} catch (Exception e) {
			System.out.println("Exception: " + e);
			e.printStackTrace();
			System.exit(1);
		}
	}
}
