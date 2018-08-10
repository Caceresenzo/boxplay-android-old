package caceresenzo.apps.boxplay.managers;

import java.lang.reflect.Method;

import android.Manifest;
import android.os.Build;
import android.os.StrictMode;
import caceresenzo.apps.boxplay.activities.BoxPlayActivity;
import caceresenzo.apps.boxplay.application.BoxPlayApplication;
import caceresenzo.apps.boxplay.managers.XManagers.AbstractManager;

public class PermissionManager extends AbstractManager {
	
	@Override
	protected void initialize() {
		askPermission();
		hackSecureMode();
	}
	
	private void askPermission() {
		if (Build.VERSION.SDK_INT >= 23) {
			try {
				String[] permissions = { Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.REQUEST_INSTALL_PACKAGES };
				BoxPlayActivity.getBoxPlayActivity().requestPermissions(permissions, BoxPlayApplication.REQUEST_ID_PERMISSION);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}
	
	private void hackSecureMode() {
		if (Build.VERSION.SDK_INT >= 24) {
			try {
				Method method = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
				method.invoke(null);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}
	
}