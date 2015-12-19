package de.liersch.android.bday.db;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;

public class ContactService extends Service {

  private ContactsObserver mContactsObserver;
  private Handler mHandler;
  private HandlerThread mWorkerThread;
  private ContentResolver resolver;

  private void registerContentObserver(Context context) {
    resolver = context.getContentResolver();
    mContactsObserver = new ContactsObserver(mHandler);
    resolver.registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, mContactsObserver);
  }

  protected void initThread() {
    mWorkerThread = new HandlerThread("contacts_thread");
    mWorkerThread.start();
    mHandler = new Handler(mWorkerThread.getLooper());
  }

  @Override
  public void onCreate() {
    super.onCreate();
    // TODO: check if possible to move to onStartCommand
    initThread();
  }

  @Override
  public void onDestroy() {
    resolver.unregisterContentObserver(mContactsObserver);
    super.onDestroy();
  }

  @Override
  public void sendBroadcast(Intent intent) {
    // TODO
    super.sendBroadcast(intent);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    registerContentObserver(getApplicationContext());
    return super.onStartCommand(intent, flags, startId);
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  public class ContactsObserver extends ContentObserver {

    public ContactsObserver(Handler h) {
      super(h);
    }

    // Implement the onChange(boolean) method to delegate the change notification to
    // the onChange(boolean, Uri) method to ensure correct operation on older versions
    // of the framework that did not have the onChange(boolean, Uri) method.
    @Override
    public void onChange(boolean selfChange) {
      onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
      System.out.println("DataProvider#onChange");
      // The data has changed, so notify the widget that the collection view needs to be updated.
      // In response, the factory's onDataSetChanged() will be called which will requery the
      // cursor for the new data.
      //mAppWidgetManager.notifyAppWidgetViewDataChanged(
      //mAppWidgetManager.getAppWidgetIds(mComponentName), R.id.weather_list);
    }
  }
}
