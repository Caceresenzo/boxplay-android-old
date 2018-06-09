package caceresenzo.apps.boxplay.application;

import java.util.Locale;

import com.rohitss.uceh.UCEHandler;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import caceresenzo.apps.boxplay.R;
import caceresenzo.apps.boxplay.activities.BoxPlayActivity;

public class BoxPlayApplication extends Application {
	
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
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setLocale();
	}
	
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
			
			if (BoxPlayActivity.getBoxPlayActivity() != null && autoReCache) {
				BoxPlayActivity.getViewHelper().recache();
			}
		}
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
	
	public static BoxPlayApplication getBoxPlayApplication() {
		return APPLICATION;
	}
}