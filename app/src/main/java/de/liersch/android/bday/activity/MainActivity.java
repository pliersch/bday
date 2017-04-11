package de.liersch.android.bday.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import de.liersch.android.bday.R;
import de.liersch.android.bday.common.logger.Log;
import de.liersch.android.bday.common.logger.LogFragment;
import de.liersch.android.bday.common.logger.LogNode;
import de.liersch.android.bday.common.logger.LogWrapper;
import de.liersch.android.bday.db.ContactController;
import de.liersch.android.bday.db.ContactService;
import de.liersch.android.bday.fragments.DevFragment;
import de.liersch.android.bday.notification.alarm.AlarmReceiver;
import de.liersch.android.bday.settings.SettingsActivity;
import de.liersch.android.bday.ui.contacts.ContactListFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

  public static final String TAG = "MainActivity";
  private int mCurrentFragmentId;
  private final String FRAGMENT_ID = "FRAGMENT_ID";
  // Request code for READ_CONTACTS. It can be any number > 0.
  private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //DatabaseManager.getInstance(getApplicationContext()).reset();
  
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
      //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
    }
    
    new AlarmReceiver().setAlarm(this);

    startService(new Intent(this, ContactService.class));

    setContentView(R.layout.activity_main);


    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.setDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);

    if (savedInstanceState == null) {
      final ContactListFragment contactListFragment = new ContactListFragment();
      getSupportFragmentManager().beginTransaction()
          .add(R.id.fragment_container, contactListFragment)
          .commit();
      mCurrentFragmentId = contactListFragment.getId();
    } else {
      mCurrentFragmentId = savedInstanceState.getInt(FRAGMENT_ID, R.id.fragment_container);
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putInt(FRAGMENT_ID, mCurrentFragmentId);
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
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    if (id == R.id.action_settings) {
      Intent intent = new Intent(this, SettingsActivity.class);
      startActivity(intent);
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onNavigationItemSelected(MenuItem item) {

    int id = item.getItemId();

    switch (id) {
      case R.id.nav_camera:
        replaceFragment(new ContactListFragment());
        break;
      case R.id.nav_gallery:

        break;
      case R.id.nav_slideshow:

        break;
      case R.id.nav_manage:

        break;
      case R.id.nav_dev_actions:
        replaceFragment(new DevFragment());
        break;
      case R.id.nav_logging:
        break;
      default:
        System.out.println("MainActivity#onNavigationItemSelected: no valid id");
        break;
    }

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  private void replaceFragment(Fragment fragment) {
    getSupportFragmentManager().beginTransaction()
        .replace(mCurrentFragmentId, fragment)
        .commit();
    mCurrentFragmentId = fragment.getId();
  }
  
  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                         int[] grantResults) {
    if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        // Permission is granted
        new ContactController(getApplicationContext()).refresh();
      } else {
        Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
      }
    }
  }

  /** Set up targets to receive log data */
  public void initializeLogging() {
    // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
    // Wraps Android's native log framework
    LogWrapper logWrapper = new LogWrapper();
    Log.setLogNode(logWrapper);

    // Filter strips out everything except the message text.
    // MessageOnlyLogNode msgFilter = new MessageOnlyLogNode();
    // logWrapper.setNext(msgFilter);
    LogNode logNode = new LogNode();
    logWrapper.setNext(logNode);

    // On screen logging via a fragment with a TextView.
    LogFragment logFragment = (LogFragment) getSupportFragmentManager()
        .findFragmentById(R.id.log_fragment);
    logNode.setNext(logFragment.getLogView());
  }

  @Override
  protected void onStart() {
    super.onStart();
    initializeLogging();
    Log.i(TAG, "Logging Ready");
  }
}
