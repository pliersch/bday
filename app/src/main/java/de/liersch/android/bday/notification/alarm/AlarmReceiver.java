package de.liersch.android.bday.notification.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Calendar;

import de.liersch.android.bday.db.ContactFactory;
import de.liersch.android.bday.db.DatabaseManager;

/**
 * When the alarm fires, this WakefulBroadcastReceiver receives the broadcast Intent
 * and then starts the IntentService {@code SchedulingService} to do some work.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {
  
  public AlarmReceiver() {
  }
  
  @Override
  public void onReceive(Context context, Intent intent) {
    Intent service = new Intent(context, SchedulingService.class);
    startWakefulService(context, service);
  }
  
  public void setAlarm(Context context) {
    AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    Intent intent = new Intent(context, AlarmReceiver.class);
    PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(System.currentTimeMillis());
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.HOUR, 0);
    calendar.set(Calendar.AM_PM, Calendar.AM);
    calendar.add(Calendar.DAY_OF_MONTH, 1);
    alarmMgr.setRepeating(
        AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent
    );
    
//    alarmMgr.setInexactRepeating(
//        AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent
//    );
    
    // Enable {@code BootReceiver} to automatically restart the alarm when the
    // device is rebooted.
    ComponentName receiver = new ComponentName(context, BootReceiver.class);
    PackageManager pm = context.getPackageManager();
    
    pm.setComponentEnabledSetting(receiver,
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        PackageManager.DONT_KILL_APP);
  }
}
