package de.liersch.android.bday.notification;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

import de.liersch.android.bday.common.logger.Log;

public class DateService extends Service {

  public static final String TAG = "DateService";
  private ServiceBinder mBinder = new ServiceBinder();
  private BroadcastReceiver mReceiver;

  @Override
  public IBinder onBind(Intent intent) {
    Log.i(TAG, "onBind ");
    return mBinder;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Log.i(TAG, "onCreate ");
    new LoggingNotificationBuilder().showNotification(getApplicationContext(), "Create Date Service");
    mReceiver  = createReceiver();
  }

  @Override
  public void onDestroy() {
    Log.i(TAG, "onDestroy ");
    new LoggingNotificationBuilder().showNotification(getApplicationContext(), "Destroy Date Service");
    unregisterReceiver(mReceiver);
    super.onDestroy();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.i(TAG, "onStartCommand ");
    final IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
    registerReceiver(mReceiver, intentFilter);
    return super.onStartCommand(intent, flags, startId);
  }

  private BroadcastReceiver createReceiver() {
    return new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        new Notifier(getApplicationContext()).notifyBirthdays();
        new LoggingNotificationBuilder().showNotification(context, "Date change");
      }
    };
  }

  private class ServiceBinder extends Binder {
    // Schnittstellenmethoden für den Service
  }
}
