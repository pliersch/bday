package de.liersch.android.bday.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import de.liersch.android.bday.R;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Load the preferences from an XML resource
    addPreferencesFromResource(R.xml.preferences);
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    System.out.println("SettingsFragment#onSharedPreferenceChanged");
//    if (key.equals(KEY_PREF_SYNC_CONN)) {
//      //Preference connectionPref = findPreference(key);
//      // Set summary to be the user-description for the selected value
//      connectionPref.setSummary(sharedPreferences.getString(key, ""));
//    }
  }

}
