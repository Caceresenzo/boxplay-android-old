package caceresenzo.apps.boxplay.managers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.preference.PreferenceManager;
import caceresenzo.apps.boxplay.application.BoxPlayApplication;
import caceresenzo.apps.boxplay.helper.ViewHelper;

public class XManagers {
	
	protected BoxPlayApplication boxPlayApplication;
	
	protected PermissionManager permissionManager;
	protected DataManager dataManager;
	protected VideoManager videoManager;
	protected MusicManager musicManager;
	protected ServerManager serverManager;
	protected UpdateManager updateManager;
	protected TutorialManager tutorialManager;
	protected PremiumManager premiumManager;
	protected SearchAndGoManager searchAndGoManager;
	protected DebugManager debugManager;
	
	protected final File baseApplicationDirectory;
	protected final File baseDataDirectory;
	
	protected SharedPreferences preferences;
	
	private List<AbstractManager> managers;
	
	public XManagers() {
		baseApplicationDirectory = new File("/sdcard" + "/" + "BoxPlay" + "/"); // TODO: getString(R.string.application_name) and Environment.getExternalStorageDirectory()
		baseDataDirectory = new File(baseApplicationDirectory, "data/");
	}
	
	public XManagers initialize(BoxPlayApplication boxPlayApplication) {
		this.boxPlayApplication = boxPlayApplication;
		
		managers = new ArrayList<>();
		
		// Config
		preferences = PreferenceManager.getDefaultSharedPreferences(BoxPlayApplication.getBoxPlayApplication());
		
		if (permissionManager == null) {
			// Permission
			managers.add(permissionManager = new PermissionManager());
			// permissionManager.initialize();
		}
		
		if (dataManager == null) {
			// Data
			managers.add(dataManager = new DataManager());
			// dataManager.initialize();
		}
		
		if (videoManager == null) {
			// Elements
			managers.add(videoManager = new VideoManager());
			// videoManager.initialize();
		}
		
		if (musicManager == null) {
			managers.add(musicManager = new MusicManager());
			// musicManager.initialize();
		}
		
		if (serverManager == null) {
			managers.add(serverManager = new ServerManager());
			// serverManager.initialize();
		}
		
		if (updateManager == null) {
			// Update
			managers.add(updateManager = new UpdateManager());
			// updateManager.initialize();
			
		}
		if (tutorialManager == null) {
			// Tutorials
			managers.add(tutorialManager = new TutorialManager());
			// tutorialManager.initialize();
		}
		
		if (premiumManager == null) {
			// Premium
			managers.add(premiumManager = new PremiumManager());
			// premiumManager.initialize();
		}
		
		if (searchAndGoManager == null) {
			// Search n' Go
			managers.add(searchAndGoManager = new SearchAndGoManager());
			// searchAndGoManager.initialize();
		}
		
		if (debugManager == null) {
			// Extraction
			managers.add(debugManager = new DebugManager());
		}
		
		for (AbstractManager manager : managers) {
			manager.initialize();
		}
		
		return this;
	}
	
	public void destroy() {
		for (AbstractManager manager : managers) {
			manager.destroy();
		}
	}
	
	public void checkAndRecreate() {
		if (managers == null) {
			managers = new ArrayList<>();
			
			if (BoxPlayApplication.getBoxPlayApplication() != null) {
				initialize(BoxPlayApplication.getBoxPlayApplication());
			}
		}
	}
	
	public File getBaseApplicationDirectory() {
		return baseApplicationDirectory;
	}
	
	public File getBaseDataDirectory() {
		return baseDataDirectory;
	}
	
	public SharedPreferences getPreferences() {
		return preferences;
	}
	
	public PermissionManager getPermissionManager() {
		checkAndRecreate();
		return permissionManager;
	}
	
	public DataManager getDataManager() {
		checkAndRecreate();
		return dataManager;
	}
	
	public VideoManager getVideoManager() {
		checkAndRecreate();
		return videoManager;
	}
	
	public MusicManager getMusicManager() {
		checkAndRecreate();
		return musicManager;
	}
	
	public ServerManager getServerManager() {
		checkAndRecreate();
		return serverManager;
	}
	
	public UpdateManager getUpdateManager() {
		checkAndRecreate();
		return updateManager;
	}
	
	public TutorialManager getTutorialManager() {
		checkAndRecreate();
		return tutorialManager;
	}
	
	public PremiumManager getPremiumManager() {
		checkAndRecreate();
		return premiumManager;
	}
	
	public SearchAndGoManager getSearchAndGoManager() {
		checkAndRecreate();
		return searchAndGoManager;
	}
	
	public DebugManager getDebugManager() {
		checkAndRecreate();
		return debugManager;
	}
	
	protected abstract static class AbstractManager {
		protected BoxPlayApplication boxPlayApplication = BoxPlayApplication.getBoxPlayApplication();
		protected Handler boxPlayHandler = BoxPlayApplication.getHandler();
		protected ViewHelper viewHelper = BoxPlayApplication.getViewHelper();
		
		protected void initialize() {
			;
		}
		
		protected void destroy() {
			;
		}
		
		protected XManagers getManagers() {
			return BoxPlayApplication.getManagers();
		}
		
		protected String getString(int ressourceId, Object... args) {
			return BoxPlayApplication.getBoxPlayApplication().getString(ressourceId, args);
		}
	}
	
	protected abstract static class SubManager {
		protected void initialize() {
			;
		}
	}
	
	protected String getString(int ressourceId, Object... args) {
		return BoxPlayApplication.getBoxPlayApplication().getString(ressourceId, args);
	}
	
	public void writeLocalFile(File file, String string) throws IOException {
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		FileOutputStream outputStream = new FileOutputStream(file);
		outputStream.write(string.getBytes());
		outputStream.close();
	}
	
}