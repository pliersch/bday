package de.liersch.android.bday.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.HashMap;

import de.liersch.android.bday.R;
import de.liersch.android.bday.beans.Contact;
import de.liersch.android.bday.common.logger.Log;
import de.liersch.android.bday.db.ContactController;
import de.liersch.android.bday.db.ContactService;
import de.liersch.android.bday.notification.alarm.AlarmReceiver;
import de.liersch.android.bday.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity implements ContactListFragment.OnContactSelectedListener {
  
  public static final String TAG = "MainActivity";
  public static final int DEBUG_MODE = -1;
  public static final int RELEASE_MODE = 1;
  public static int CURRENT_DEV_MODE = -1;
  
  private Contact mCurrentContact;
  
  // Request code for READ_CONTACTS. It can be any number > 0.
  private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
  private boolean mIsDualPane;
  private ContactListFragment mContactListFragment;
  private DetailFragment mDetailFragment;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
//    CURRENT_DEV_MODE = DEBUG_MODE;
    CURRENT_DEV_MODE = RELEASE_MODE;
    super.onCreate(savedInstanceState);
    //DatabaseManager.getInstance(getApplicationContext()).reset();
    
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
      //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
    } else {
      new ContactController(getApplicationContext()).refresh();
    }
    
    new AlarmReceiver().setAlarm(this);
    startService(new Intent(this, ContactService.class));
    setContentView(R.layout.activity_main);

  
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    
    mCurrentContact = new ContactController(this).getSortedContacts().get(0);

//    if (CURRENT_DEV_MODE == RELEASE_MODE) {
//
//      final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.main_linear_layout);
//      final FrameLayout child = (FrameLayout) findViewById(fragment_container2);
//      linearLayout.removeView(child);

//      final ActionBar actionBar = getSupportActionBar();
//      if (actionBar != null) {
//        actionBar.setDisplayHomeAsUpEnabled(false);
//        actionBar.setHomeButtonEnabled(false);
//      }
//      toolbar.setNavigationIcon(null);

//      final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.fragment_container2);
//      LinearLayout.LayoutParams layoutParams =
//          new LinearLayout.LayoutParams(
//              LinearLayout.LayoutParams.MATCH_PARENT,
//              0, 1);
//
//      frameLayout.setLayoutParams(layoutParams);

//    }
    
//    if (savedInstanceState == null) {
//      final ContactListFragment contactListFragment = new ContactListFragment();
//      getSupportFragmentManager().beginTransaction()
//          .add(R.id.fragment_container, contactListFragment)
//          .commit();
//      mCurrentFragmentId = contactListFragment.getId();
//    } else {
//      mCurrentFragmentId = savedInstanceState.getInt(FRAGMENT_ID, R.id.fragment_container);
//    }
  }
  
  @Override
  public void onSaveInstanceState(Bundle outState) {
    //outState.putInt(FRAGMENT_ID, mCurrentFragmentId);
    super.onSaveInstanceState(outState);
  }

//  @Override
//  protected void onDestroy() {
//    // TODO: killing service? really?
//    stopService(new Intent(this, ContactService.class));
//    super.onDestroy();
//  }
  
  @Override
  public void onBackPressed() {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_settings) {
      Intent intent = new Intent(this, SettingsActivity.class);
      startActivity(intent);
      return true;
    }
    
    return super.onOptionsItemSelected(item);
  }
  
  @Override
  public void onAttachFragment(Fragment fragment) {
    super.onAttachFragment(fragment);
    if (fragment instanceof ContactListFragment) {
      mContactListFragment = (ContactListFragment) fragment;
    } else if (fragment instanceof DetailFragment){
      mDetailFragment = (DetailFragment) fragment;
      mIsDualPane = true;
      Bundle args = new Bundle();
      args.putBoolean("isDualPane", true);
    }
    if (mDetailFragment != null && mContactListFragment != null) {
      mContactListFragment.setDualPane(true);
    }
  }
  
  @Override
  protected void onResumeFragments() {
    super.onResumeFragments();
    if (mDetailFragment != null) {
      mDetailFragment.updateView(mCurrentContact);
    }
  }
  
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        // Permission is granted
        new ContactController(getApplicationContext()).refresh();
      } else {
        Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
      }
    }
  }
  
//  public void initializeLogging() {
//    // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
//    // Wraps Android's native log framework
//    LogWrapper logWrapper = new LogWrapper();
//    Log.setLogNode(logWrapper);
//
//    // Filter strips out everything except the message text.
//    // MessageOnlyLogNode msgFilter = new MessageOnlyLogNode();
//    // logWrapper.setNext(msgFilter);
//    LogNode logNode = new LogNode();
//    logWrapper.setNext(logNode);
//
//    // On screen logging via a fragment with a TextView.
//    LogFragment logFragment = (LogFragment) getSupportFragmentManager().findFragmentById(log_fragment);
//    logNode.setNext(logFragment.getLogView());
//  }
  
  @Override
  protected void onStart() {
    super.onStart();
//    if (CURRENT_DEV_MODE == DEBUG_MODE) {
//      initializeLogging();
//    }
    Log.i(TAG, "Logging Ready");
  }
  
  @Override
  public void onContactSelected(HashMap<String, String> hashMap) {
    final long userId = Long.parseLong((hashMap.get(ContactListFragment.CONTACT_ID)));
    final Contact contact = new ContactController(getApplicationContext()).getContact(userId);
    mDetailFragment.updateView(contact);
  }
}
