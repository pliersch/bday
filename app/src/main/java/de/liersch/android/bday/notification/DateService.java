package de.liersch.android.bday.notification;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

import de.liersch.android.bday.notification.util.Notifier;

public class DateService extends Service {

  public static final String ACTION_BROADCAST = "de.liersch.android.bday.DATE_SERVICE";
  private ServiceBinder mBinder = new ServiceBinder();
  private BroadcastReceiver mReceiver;
  private Notifier mNotifier;

  // TODO

  @Override
  public IBinder onBind(Intent intent) {
    return mBinder;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    mReceiver  = createReceiver();
  }

  @Override
  public void onDestroy() {
    unregisterReceiver(mReceiver);
    if (mNotifier != null) {
      mNotifier.destroy();
    }
    super.onDestroy();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    final IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
    registerReceiver(mReceiver, intentFilter);
    return super.onStartCommand(intent, flags, startId);
  }

  private BroadcastReceiver createReceiver() {
    final BroadcastReceiver receiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        System.out.println("DateService#BroadcastReceiver#onReceive" + action);
        mNotifier = new Notifier(getApplicationContext());
        mNotifier.notifyBirthdays();
      }
    };
    return receiver;
  }

  public class ServiceBinder extends Binder {
    // Schnittstellenmethoden für den Service
  }
}
