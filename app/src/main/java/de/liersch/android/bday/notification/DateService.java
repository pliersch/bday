package de.liersch.android.bday.notification;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Binder;
import android.os.IBinder;

import java.util.List;

import de.liersch.android.bday.db.SystemContactsQuery;
import de.liersch.android.bday.db.DatabaseManager;

public class DateService extends Service {

  public static final String ACTION_BROADCAST = "de.liersch.android.bday.DATE_SERVICE";
  private ServiceBinder mBinder = new ServiceBinder();
  private DatabaseManager mDatabaseManager;
  private BroadcastReceiver mReceiver;
  private NotificationBuilder mNotificationBuilder;
  private Cursor mCursorContacts;
  private Cursor mCursorBDay;

  private List<Long> userIdNextDays;

  // TODO

  @Override
  public IBinder onBind(Intent intent) {
    return mBinder;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    mDatabaseManager = DatabaseManager.getInstance(getApplicationContext());
    mNotificationBuilder = new NotificationBuilder();
    mReceiver  = createReceiver();
  }

  @Override
  public void onDestroy() {
    unregisterReceiver(mReceiver);
    mDatabaseManager.close();
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
        System.out.println("Foobar" + action);

        refreshCursor();
        computeNextBirthdays();
        updateNextBirthdays();
        notifyToday();
        notifyNextDays();


        final Cursor contactCursor = SystemContactsQuery.getInstance().queryVisibleContacts(getApplicationContext());


        // TODO maybe closed db
        final Cursor read = mDatabaseManager.read();
        if(read != null) {
          read.moveToPosition(-1);
          while (read.moveToNext()) {
            final String id = read.getString(0);
            final String userID = read.getString(1);
            final String send = read.getString(2);
            System.out.println("db entries: " + id + "," + userID + "," + send);
            if(userID.equals("62")) {
              mNotificationBuilder.createNotification(userID, 100, getApplicationContext());
            }
          }
        }

        //mDatabaseManager.addContact(l);

      }

      private void refreshCursor() {
        if (mCursorContacts != null) {
          mCursorContacts.close();
        }
        mCursorContacts = SystemContactsQuery.getInstance().queryVisibleContacts(getApplicationContext());
        if (mCursorBDay != null) {
          mCursorBDay.close();
        }
        mCursorBDay = SystemContactsQuery.getInstance().queryBirthdaysContacts(getApplicationContext());
      }

      private void notifyNextDays() {

      }

      private void notifyToday() {
      }

      private void computeNextBirthdays() {
        //CalendarUtil.getInstance().
      }

      private void updateNextBirthdays() {
      }


    };
    return receiver;
  }

  public class ServiceBinder extends Binder {
    // Schnittstellenmethoden für den Service
  }

}
