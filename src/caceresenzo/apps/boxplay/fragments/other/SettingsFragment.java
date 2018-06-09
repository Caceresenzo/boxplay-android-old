package caceresenzo.apps.boxplay.fragments.other;

import java.util.Iterator;
import java.util.Set;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v14.preference.MultiSelectListPreference;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceFragmentCompat;
import caceresenzo.apps.boxplay.R;
import caceresenzo.apps.boxplay.activities.BoxPlayActivity;
import caceresenzo.apps.boxplay.application.BoxPlayApplication;
import caceresenzo.apps.boxplay.models.music.enums.MusicGenre;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
	
	public static boolean PREF_MENU_EXPAND_COLLAPSE_BACK_BUTTON_ENABLE = true;
	
	private SharedPreferences sharedPreferences;
	
	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		setPreferencesFromResource(R.xml.preferences, rootKey);
		
		sharedPreferences = BoxPlayActivity.getManagers().getPreferences();
		
		onSharedPreferenceChanged(sharedPreferences, getString(R.string.boxplay_other_settings_store_music_pref_my_genre_key));
		onSharedPreferenceChanged(sharedPreferences, getString(R.string.boxplay_other_settings_boxplay_pref_background_service_key));
		onSharedPreferenceChanged(sharedPreferences, getString(R.string.boxplay_other_settings_boxplay_pref_force_factory_key));
		onSharedPreferenceChanged(sharedPreferences, getString(R.string.boxplay_other_settings_menu_pref_drawer_extend_collapse_back_button_key));
		onSharedPreferenceChanged(sharedPreferences, getString(R.string.boxplay_other_settings_application_pref_language_key));
		onSharedPreferenceChanged(sharedPreferences, getString(R.string.boxplay_other_settings_application_pref_crash_reporter_key));
		
		initializeButton();
	}
	
	private void initializeButton() {
		final Preference resetTutorialsProgressButton = findPreference(getString(R.string.boxplay_other_settings_boxplay_pref_reset_tutorials_key));
		resetTutorialsProgressButton.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				try {
					BoxPlayActivity.getManagers().getTutorialManager().resetTutorials();
					preference.setSummary(getString(R.string.boxplay_other_settings_boxplay_pref_reset_tutorials_summary_done));
				} catch (Exception exception) {
					preference.setSummary(getString(R.string.boxplay_other_settings_boxplay_pref_reset_tutorials_summary_error, exception.getLocalizedMessage()));
				}
				return false;
			}
		});
		
		final Preference clearImageCacheButton = findPreference(getString(R.string.boxplay_other_settings_application_pref_clear_image_cache_key));
		clearImageCacheButton.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				try {
					BoxPlayActivity.getViewHelper().clearImageCache();
					preference.setSummary(getString(R.string.boxplay_other_settings_application_pref_clear_image_cache_summary_done));
				} catch (Exception exception) {
					preference.setSummary(getString(R.string.boxplay_other_settings_application_pref_clear_image_cache_summary_error, exception.getLocalizedMessage()));
				}
				return false;
			}
		});
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Preference preference = findPreference(key);
		
		if (preference == null) {
			return;
		}
		
		if (preference instanceof SwitchPreference) {
			SwitchPreference switchPreference = (SwitchPreference) preference;
			
			if (key == getString(R.string.boxplay_other_settings_boxplay_pref_background_service_key)) {
				if (switchPreference.isChecked()) {
					preference.setSummary(R.string.boxplay_other_settings_boxplay_pref_background_service_summary_enabled);
				} else {
					preference.setSummary(R.string.boxplay_other_settings_boxplay_pref_background_service_summary_disabled);
				}
			} else if (key == getString(R.string.boxplay_other_settings_application_pref_crash_reporter_key)) {
				if (switchPreference.isChecked()) {
					preference.setSummary(R.string.boxplay_other_settings_application_pref_crash_reporter_summary_enabled);
				} else {
					preference.setSummary(R.string.boxplay_other_settings_application_pref_crash_reporter_summary_disabled);
				}
			}
		} else if (preference instanceof CheckBoxPreference) {
			CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
			
			if (key == getString(R.string.boxplay_other_settings_menu_pref_drawer_extend_collapse_back_button_key)) {
				if (checkBoxPreference.isChecked()) {
					checkBoxPreference.setSummary(R.string.boxplay_other_settings_menu_pref_drawer_extend_collapse_back_button_summary_enabled);
				} else {
					checkBoxPreference.setSummary(R.string.boxplay_other_settings_menu_pref_drawer_extend_collapse_back_button_summary_disabled);
				}
			} else if (key == getString(R.string.boxplay_other_settings_boxplay_pref_force_factory_key)) {
				if (checkBoxPreference.isChecked()) {
					checkBoxPreference.setSummary(R.string.boxplay_other_settings_boxplay_pref_force_factory_summary_enabled);
				} else {
					checkBoxPreference.setSummary(R.string.boxplay_other_settings_boxplay_pref_force_factory_summary_disabled);
				}
			}
		} else if (preference instanceof ListPreference) {
			ListPreference listPreference = (ListPreference) preference;
			
			int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(key, ""));
			if (prefIndex >= 0) {
				preference.setSummary(getString(R.string.boxplay_other_settings_application_pref_language_summary, listPreference.getEntries()[prefIndex]));
			}
			
			if (key == getString(R.string.boxplay_other_settings_application_pref_language_key)) {
				BoxPlayApplication.getBoxPlayApplication().setLocale(true);
			}
		} else if (preference instanceof MultiSelectListPreference) {
			MultiSelectListPreference multiSelectListPreference = (MultiSelectListPreference) preference;
			
			Set<String> values = multiSelectListPreference.getValues();
			if (values.size() >= 1) {
				String string = "";
				Iterator<String> iterator = values.iterator();
				while (iterator.hasNext()) {
					MusicGenre genre = MusicGenre.fromString(iterator.next());
					
					if (genre != MusicGenre.UNKNOWN) {
						string += BoxPlayActivity.getViewHelper().enumToStringCacheTranslation(genre) + (iterator.hasNext() ? ", " : "");
					}
				}
				
				preference.setSummary(getString(R.string.boxplay_other_settings_store_music_pref_my_genre_summary, string));
			} else {
				preference.setSummary(getString(R.string.boxplay_other_settings_store_music_pref_my_genre_summary_empty));
			}
			
			if (key == getString(R.string.boxplay_other_settings_application_pref_language_key)) {
				BoxPlayApplication.getBoxPlayApplication().setLocale(true);
			}
		} else {
			try {
				preference.setSummary(sharedPreferences.getString(key, ""));
			} catch (Exception exception) {
				preference.setSummary(exception.getLocalizedMessage());
			}
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
}