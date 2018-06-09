package caceresenzo.apps.boxplay.managers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import caceresenzo.apps.boxplay.activities.BoxPlayActivity;
import caceresenzo.apps.boxplay.application.BoxPlayApplication;

public class XManagers {
	
	protected BoxPlayActivity boxPlayActivity;
	
	protected PermissionManager permissionManager;
	protected DataManager dataManager;
	protected VideoManager videoManager;
	protected MusicManager musicManager;
	protected ServerManager serverManager;
	protected UpdateManager updateManager;
	protected TutorialManager tutorialManager;
	
	protected final File baseApplicationDirectory;
	protected final File baseDataDirectory;
	
	protected SharedPreferences preferences;
	
	public XManagers() {
		baseApplicationDirectory = new File("/sdcard" + "/" + "BoxPlay" + "/"); // TODO: getString(R.string.application_name) and Environment.getExternalStorageDirectory()
		baseDataDirectory = new File(baseApplicationDirectory, "data/");
	}
	
	public XManagers initialize(BoxPlayActivity boxPlayActivity) {
		this.boxPlayActivity = boxPlayActivity;
		return this;
	}
	
	public XManagers initializeConfig() {
		preferences = PreferenceManager.getDefaultSharedPreferences(BoxPlayApplication.getBoxPlayApplication());
		return this;
	}
	
	public XManagers initializePermission() {
		permissionManager = new PermissionManager();
		permissionManager.initialize();
		return this;
	}
	
	public XManagers initializeData() {
		dataManager = new DataManager();
		dataManager.initialize();
		return this;
	}
	
	public XManagers initializeElements() {
		videoManager = new VideoManager();
		videoManager.initialize();
		musicManager = new MusicManager();
		musicManager.initialize();
		
		serverManager = new ServerManager();
		serverManager.initialize();
		return this;
	}
	
	public XManagers initializeUpdate() {
		updateManager = new UpdateManager();
		updateManager.initialize();
		return this;
	}
	
	public XManagers initializeTutorial() {
		tutorialManager = new TutorialManager();
		tutorialManager.initialize();
		return this;
	}
	
	public void finish() {
		
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
	
	protected static abstract class AManager {
		protected BoxPlayActivity boxPlayActivity = BoxPlayActivity.getBoxPlayActivity();
		
		protected void initialize() {
			;
		}
		
		protected XManagers getManagers() {
			return BoxPlayActivity.getManagers();
		}
		
		protected String getString(int ressourceId, Object... args) {
			return BoxPlayActivity.getBoxPlayActivity().getString(ressourceId, args);
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