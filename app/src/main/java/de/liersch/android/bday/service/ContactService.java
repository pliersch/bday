package de.liersch.android.bday.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class ContactService extends Service {

  private BroadcastReceiver mReceiver;

  @Override
  public void onCreate() {
    super.onCreate();
    mReceiver  = createReceiver();
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    registerReceiver(mReceiver, createIntentFilter());
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public void onDestroy() {
    unregisterReceiver(mReceiver);
    super.onDestroy();
  }

  private BroadcastReceiver createReceiver() {
    final BroadcastReceiver receiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        System.out.println("bla");
      }
    };
    return receiver;
  }
  private IntentFilter createIntentFilter() {
    final IntentFilter s_intentFilter = new IntentFilter();
    //s_intentFilter.addAction(Intent.ACTION_TIME_TICK);
    s_intentFilter.addAction(Intent.ACTION_EDIT);
    return s_intentFilter;
  }
}
