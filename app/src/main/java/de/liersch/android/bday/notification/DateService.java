package de.liersch.android.bday.notification;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

import java.util.List;

import de.liersch.android.bday.notification.util.Notifier;

public class DateService extends Service {

  public static final String ACTION_BROADCAST = "de.liersch.android.bday.DATE_SERVICE";
  private ServiceBinder mBinder = new ServiceBinder();
  private BroadcastReceiver mReceiver;
  private List<Long> userIdNextDays;
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
    mNotifier.destroy();
    super.onDestroy();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    registerReceiver(mReceiver, createIntentFilter());
    return super.onStartCommand(intent, flags, startId);
  }

  private IntentFilter createIntentFilter() {
    final IntentFilter s_intentFilter = new IntentFilter();
    //s_intentFilter.addAction(Intent.ACTION_TIME_TICK);
    s_intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
    return s_intentFilter;
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
