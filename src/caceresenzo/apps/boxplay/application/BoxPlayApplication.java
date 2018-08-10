package caceresenzo.apps.boxplay.application;

import java.util.Locale;

import com.rohitss.uceh.UCEHandler;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import caceresenzo.apps.boxplay.R;
import caceresenzo.apps.boxplay.activities.BoxPlayActivity;
import caceresenzo.apps.boxplay.helper.LocaleHelper;
import caceresenzo.libs.comparator.Version;
import caceresenzo.libs.comparator.VersionType;

public class BoxPlayApplication extends Application {
	
	/**
	 * Android request id
	 */
	public static final int REQUEST_ID_UPDATE = 20;
	public static final int REQUEST_ID_VLC_VIDEO = 40;
	public static final int REQUEST_ID_VLC_VIDEO_URL = 41;
	public static final int REQUEST_ID_VLC_AUDIO = 42;
	public static final int REQUEST_ID_PERMISSION = 100;
	
	/**
	 * File provider
	 */
	public static final String FILEPROVIDER_AUTHORITY = "caceresenzo.apps.boxplay.provider";
	
	private static final Version VERSION = new Version("3.0.7", VersionType.BETA);
	
	private static BoxPlayApplication APPLICATION;
	private SharedPreferences sharedPreferences;
	
	@Override
	public void onCreate() {
		super.onCreate();
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		APPLICATION = this;
		setLocale();
		
		new UCEHandler.Builder(getApplicationContext()) //
				.setUCEHEnabled(sharedPreferences.getBoolean(getString(R.string.boxplay_other_settings_application_pref_crash_reporter_key), true)) //
				.setTrackActivitiesEnabled(true) //
				.setBackgroundModeEnabled(true) //
				.addCommaSeparatedEmailAddresses("caceresenzo1502@gmail.com") //
				.build() //
		;
	}
	
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(LocaleHelper.onAttach(base));
	}
	
	// @Override
	// public void onConfigurationChanged(Configuration newConfig) {
	// super.onConfigurationChanged(newConfig);
	// setLocale();
	// }
	
	public void setLocale() {
		setLocale(false);
	}
	
	@SuppressWarnings("deprecation")
	public void setLocale(boolean autoReCache) {
		final Resources resources = getResources();
		final Configuration configuration = resources.getConfiguration();
		final Locale locale = getLocale();
		if (!configuration.locale.equals(locale)) {
			configuration.setLocale(locale);
			resources.updateConfiguration(configuration, null);
			
			try {
				Configuration config = getBaseContext().getResources().getConfiguration();
				config.setLocale(locale);
				createConfigurationContext(config);
			} catch (Exception exception) {
				; // Unavailable in old API
			}
			
			if (BoxPlayActivity.getBoxPlayActivity() != null && autoReCache) {
				BoxPlayActivity.getViewHelper().recache();
			}
		}
	}
	
	private static Context updateResources(Context context, String language) {
		Locale locale = new Locale(language);
		Locale.setDefault(locale);
		
		Resources res = context.getResources();
		Configuration config = new Configuration(res.getConfiguration());
		if (Build.VERSION.SDK_INT >= 17) {
			config.setLocale(locale);
			context = context.createConfigurationContext(config);
		} else {
			config.locale = locale;
			res.updateConfiguration(config, res.getDisplayMetrics());
		}
		return context;
	}
	
	public String getLocaleString() {
		return sharedPreferences.getString(getString(R.string.boxplay_other_settings_application_pref_language_key), getString(R.string.boxplay_other_settings_application_pref_language_default_value)).toLowerCase();
	}
	
	public Locale getLocale() {
		return new Locale(getLocaleString());
	}
	
	public SharedPreferences getPreferences() {
		return sharedPreferences;
	}
	
	/**
	 * Get BoxPlay version
	 */
	public static Version getVersion() {
		return VERSION;
	}
	
	public static BoxPlayApplication getBoxPlayApplication() {
		return APPLICATION;
	}
}