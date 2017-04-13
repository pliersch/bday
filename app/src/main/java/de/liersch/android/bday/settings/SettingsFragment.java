package de.liersch.android.bday.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import de.liersch.android.bday.R;
import de.liersch.android.bday.notification.Notifier;

import static de.liersch.android.bday.R.xml.preferences;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    // Load the preferences from an XML resource
    addPreferencesFromResource(preferences);
    
    Preference preference;
    
    preference = findPreference(getString(R.string.preference_first_alert_key));
    preference.setOnPreferenceChangeListener(this);
    
    preference = findPreference(getString(R.string.preference_second_alert_key));
    preference.setOnPreferenceChangeListener(this);
  }
  
  @Override
  public boolean onPreferenceChange(Preference preference, Object newValue) {
    if (preference.getKey().equals(getString(R.string.preference_first_alert_key))) {
      final String msg = getActivity().getResources().getString(R.string.preference_first_alert_summary, newValue);
      preference.setSummary(msg);
      new Notifier(getActivity().getApplicationContext()).notifyBirthdays();
    }
    
    if (preference.getKey().equals(getString(R.string.preference_second_alert_key))) {
      final String msg = getActivity().getResources().getString(R.string.preference_second_alert_summary, newValue);
      preference.setSummary(msg);
      new Notifier(getActivity().getApplicationContext()).notifyBirthdays();
    }
    return true;
  }
}
