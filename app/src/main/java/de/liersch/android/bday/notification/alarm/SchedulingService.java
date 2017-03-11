package de.liersch.android.bday.notification.alarm;

import android.app.IntentService;
import android.content.Intent;

import de.liersch.android.bday.notification.Notifier;

/**
 * This {@code IntentService} does the app's actual work.
 * {@code AlarmReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class SchedulingService extends IntentService {
  
  public static final String TAG = "SchedulingService";
  
  public SchedulingService() {
    super("SchedulingService");
  }
  
  @Override
  protected void onHandleIntent(Intent intent) {
    new Notifier(getApplicationContext()).notifyBirthdays();
    //new LoggingNotificationBuilder().showNotification(getApplicationContext(), TAG + " onHandleIntent");
  }
}
