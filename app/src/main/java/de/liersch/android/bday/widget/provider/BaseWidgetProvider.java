package de.liersch.android.bday.widget.provider;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.ContactsContract;
import android.widget.RemoteViews;

import de.liersch.android.bday.widget.ContactsObserver;

/**
 * data observer notifies an update for all widgets when it detects a change.
 */


public abstract class BaseWidgetProvider extends AppWidgetProvider {
  public static String CLICK_ACTION = "com.example.android.weatherlistwidget.CLICK";
  public static String REFRESH_ACTION = "com.example.android.weatherlistwidget.REFRESH";
  public static String EXTRA_DAY_ID = "com.example.android.weatherlistwidget.day";

  protected static HandlerThread sWorkerThread;
  protected static Handler sWorkerQueue;
  protected static ContactsObserver contactsObserver;

  private boolean mIsLargeLayout = true;

  public BaseWidgetProvider() {
    super();
    initThread(getThreadName());
  }

  protected abstract String getThreadName();

  protected void initThread(String threadName) {
    sWorkerThread = new HandlerThread(threadName);
    sWorkerThread.start();
    sWorkerQueue = new Handler(sWorkerThread.getLooper());
  }

  // XXX: clear the worker queue if we are destroyed?

  @Override
  public void onEnabled(Context context) {
    System.out.println("Provider#onEnabled");
    // Register for external updates to the data to trigger an update of the widget.  When using
    // content providers, the data is often updated via a background service, or in response to
    // user interaction in the main app.  To ensure that the widget always reflects the current
    // state of the data, we must listen for changes and update ourselves accordingly.
    if (contactsObserver == null) {
      registerContentObserver(context);
    }
  }

  @Override
  public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
    super.onRestored(context, oldWidgetIds, newWidgetIds);
    System.out.println("Provider#onRestored");
  }

  @Override
  public void onDeleted(Context context, int[] appWidgetIds) {
    super.onDeleted(context, appWidgetIds);
    System.out.println("Provider#onDeleted");
  }

  protected void registerContentObserver(Context context) {
    final ContentResolver r = context.getContentResolver();
    final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
    final ComponentName cn = new ComponentName(context, this.getClass());
    //final ComponentName cn = new ComponentName(context, LargeWidgetProvider.class);
    contactsObserver = new ContactsObserver(mgr, cn, sWorkerQueue);
    r.registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, contactsObserver);
  }

  protected abstract RemoteViews buildLayout(Context context, int appWidgetId, boolean largeLayout);

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    System.out.println("Provider#onUpdate: " + appWidgetIds.length);
    if (contactsObserver == null) {
      registerContentObserver(context);
    }
    // Update each of the widgets with the remote adapter
    for (int appWidgetId : appWidgetIds) {
      RemoteViews layout = buildLayout(context, appWidgetId, mIsLargeLayout);
      appWidgetManager.updateAppWidget(appWidgetId, layout);
    }
    super.onUpdate(context, appWidgetManager, appWidgetIds);
  }

  @Override
  public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetId, Bundle newOptions) {

//    int minWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
//    int maxWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
    int minHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
//    int maxHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);

    RemoteViews layout;
    mIsLargeLayout = minHeight >= 100;
    layout = buildLayout(context, appWidgetId, mIsLargeLayout);
    appWidgetManager.updateAppWidget(appWidgetId, layout);
  }
}
