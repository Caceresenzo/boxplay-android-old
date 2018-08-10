package caceresenzo.apps.boxplay.managers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.preference.PreferenceManager;
import caceresenzo.apps.boxplay.activities.BoxPlayActivity;
import caceresenzo.apps.boxplay.application.BoxPlayApplication;
import caceresenzo.apps.boxplay.helper.ViewHelper;

public class XManagers {
	
	protected BoxPlayActivity boxPlayActivity;
	
	protected PermissionManager permissionManager;
	protected DataManager dataManager;
	protected VideoManager videoManager;
	protected MusicManager musicManager;
	protected ServerManager serverManager;
	protected UpdateManager updateManager;
	protected TutorialManager tutorialManager;
	protected PremiumManager premiumManager;
	protected SearchAndGoManager searchAndGoManager;
	
	protected final File baseApplicationDirectory;
	protected final File baseDataDirectory;
	
	protected SharedPreferences preferences;
	
	private List<AbstractManager> managers;
	
	public XManagers() {
		baseApplicationDirectory = new File("/sdcard" + "/" + "BoxPlay" + "/"); // TODO: getString(R.string.application_name) and Environment.getExternalStorageDirectory()
		baseDataDirectory = new File(baseApplicationDirectory, "data/");
	}
	
	public XManagers initialize(BoxPlayActivity boxPlayActivity) {
		this.boxPlayActivity = boxPlayActivity;
		
		managers = new ArrayList<>();
		
		// Config
		preferences = PreferenceManager.getDefaultSharedPreferences(BoxPlayApplication.getBoxPlayApplication());
		
		// Permission
		managers.add(permissionManager = new PermissionManager());
		// permissionManager.initialize();
		
		// Data
		managers.add(dataManager = new DataManager());
		// dataManager.initialize();
		
		// Elements
		managers.add(videoManager = new VideoManager());
		// videoManager.initialize();
		managers.add(musicManager = new MusicManager());
		// musicManager.initialize();
		
		managers.add(serverManager = new ServerManager());
		// serverManager.initialize();
		
		// Update
		managers.add(updateManager = new UpdateManager());
		// updateManager.initialize();
		
		// Tutorials
		managers.add(tutorialManager = new TutorialManager());
		// tutorialManager.initialize();
		
		// Premium
		managers.add(premiumManager = new PremiumManager());
		// premiumManager.initialize();
		
		// Search n' Go
		managers.add(searchAndGoManager = new SearchAndGoManager());
		// searchAndGoManager.initialize();
		
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
		return permissionManager;
	}
	
	public DataManager getDataManager() {
		return dataManager;
	}
	
	public VideoManager getVideoManager() {
		return videoManager;
	}
	
	public MusicManager getMusicManager() {
		return musicManager;
	}
	
	public ServerManager getServerManager() {
		return serverManager;
	}
	
	public UpdateManager getUpdateManager() {
		return updateManager;
	}
	
	public TutorialManager getTutorialManager() {
		return tutorialManager;
	}
	
	public PremiumManager getPremiumManager() {
		return premiumManager;
	}
	
	public SearchAndGoManager getSearchAndGoManager() {
		return searchAndGoManager;
	}
	
	protected static abstract class AbstractManager {
		protected BoxPlayActivity boxPlayActivity = BoxPlayActivity.getBoxPlayActivity();
		protected Handler boxPlayHandler = BoxPlayActivity.getHandler();
		protected ViewHelper viewHelper = BoxPlayActivity.getViewHelper();
		
		protected void initialize() {
			;
		}
		
		protected void destroy() {
			;
		}
		
		protected XManagers getManagers() {
			return BoxPlayActivity.getManagers();
		}
		
		protected String getString(int ressourceId, Object... args) {
			return BoxPlayActivity.getBoxPlayActivity().getString(ressourceId, args);
		}
	}
	
	protected static abstract class SubManager {
		protected void initialize() {
			;
		}
	}
	
	protected String getString(int ressourceId, Object... args) {
		return BoxPlayActivity.getBoxPlayActivity().getString(ressourceId, args);
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