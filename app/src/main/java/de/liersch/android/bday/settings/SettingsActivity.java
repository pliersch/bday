package de.liersch.android.bday.settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import de.liersch.android.bday.notification.Notifier;

public class SettingsActivity extends Activity {

  private SharedPreferences.OnSharedPreferenceChangeListener listener;
  private SharedPreferences preferences;

  public static String FIRST_ALTER = "first";
  public static String SECOND_ALTER = "second";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Display the fragment as the main content.
    getFragmentManager().beginTransaction()
        .replace(android.R.id.content, new SettingsFragment())
        .commit();
  }

  @Override
  public void onResume() {
    super.onResume();
    preferences = getSharedPreferences(getPackageName() + "_preferences", MODE_PRIVATE);
    final String first = preferences.getString("first", "10");
    listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
      public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        new Notifier(getApplicationContext()).notifyBirthdays();
      }
    };
    preferences.registerOnSharedPreferenceChangeListener(listener);
  }

  @Override
  public void onPause() {
    super.onPause();
    // TODO
    //if (listener != null && preferences != null) {
      preferences.unregisterOnSharedPreferenceChangeListener(listener);
    //}
  }
}
