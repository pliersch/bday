package de.liersch.android.bday;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.ContactsContract;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * data observer notifies an update for all widgets when it detects a change.
 */
class ContactsObserver extends ContentObserver {
  private AppWidgetManager mAppWidgetManager;
  private ComponentName mComponentName;

  ContactsObserver(AppWidgetManager mgr, ComponentName cn, Handler h) {
    super(h);
    mAppWidgetManager = mgr;
    mComponentName = cn;
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
    mAppWidgetManager.notifyAppWidgetViewDataChanged(
        mAppWidgetManager.getAppWidgetIds(mComponentName), R.id.weather_list);
  }

}

public class LargeWidgetProvider extends AppWidgetProvider {
  public static String CLICK_ACTION = "com.example.android.weatherlistwidget.CLICK";
  public static String REFRESH_ACTION = "com.example.android.weatherlistwidget.REFRESH";
  public static String EXTRA_DAY_ID = "com.example.android.weatherlistwidget.day";

  private static HandlerThread sWorkerThread;
  private static Handler sWorkerQueue;
  private static ContactsObserver contactsObserver;

  private boolean mIsLargeLayout = true;

  public LargeWidgetProvider() {
    // Start the worker thread
    sWorkerThread = new HandlerThread("LargeWidgetProvider-worker");
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

  private void registerContentObserver(Context context) {
    final ContentResolver r = context.getContentResolver();
    final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
    final ComponentName cn = new ComponentName(context, LargeWidgetProvider.class);
    contactsObserver = new ContactsObserver(mgr, cn, sWorkerQueue);
    r.registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, contactsObserver);
  }

  @Override
  public void onReceive(Context ctx, Intent intent) {
    final String action = intent.getAction();
    System.out.println("Provider#onReceive: " + action);
    if (action.equals(REFRESH_ACTION)) {
      if (contactsObserver == null) {
        registerContentObserver(ctx);
      }
      // BroadcastReceivers have a limited amount of time to do work, so for this sample, we
      // are triggering an update of the data on another thread.  In practice, this update
      // can be triggered from a background service, or perhaps as a result of user actions
      // inside the main application.
      final Context context = ctx;
      sWorkerQueue.removeMessages(0);
      sWorkerQueue.post(new Runnable() {
        @Override
        public void run() {
          final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
          final ComponentName cn = new ComponentName(context, LargeWidgetProvider.class);
          mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.weather_list);
        }
      });

      final int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    } else if (action.equals(CLICK_ACTION)) {
      // Show a toast
      final int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
      final String day = intent.getStringExtra(EXTRA_DAY_ID);
      final String formatStr = ctx.getResources().getString(R.string.toast_format_string);
      Toast.makeText(ctx, String.format(formatStr, day), Toast.LENGTH_SHORT).show();
    }

    super.onReceive(ctx, intent);
  }

  private RemoteViews buildLayout(Context context, int appWidgetId, boolean largeLayout) {
    RemoteViews rv;
    if (largeLayout) {
      // Specify the service to provide data for the collection widget.  Note that we need to
      // embed the appWidgetId via the data otherwise it will be ignored.
      final Intent intent = new Intent(context, LargeWidgetService.class);
      intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
      intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
      rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
      rv.setRemoteAdapter(R.id.weather_list, intent);

      // Set the empty view to be displayed if the collection is empty.  It must be a sibling
      // view of the collection view.
      rv.setEmptyView(R.id.weather_list, R.id.empty_view);

      // Bind a click listener template for the contents of the weather list.  Note that we
      // need to update the intent's data if we set an extra, since the extras will be
      // ignored otherwise.
      final Intent onClickIntent = new Intent(context, LargeWidgetProvider.class);
      onClickIntent.setAction(LargeWidgetProvider.CLICK_ACTION);
      onClickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
      onClickIntent.setData(Uri.parse(onClickIntent.toUri(Intent.URI_INTENT_SCHEME)));
      final PendingIntent onClickPendingIntent = PendingIntent.getBroadcast(context, 0,
          onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
      rv.setPendingIntentTemplate(R.id.weather_list, onClickPendingIntent);

      // Bind the click intent for the refresh button on the widget
      final Intent refreshIntent = new Intent(context, LargeWidgetProvider.class);
      refreshIntent.setAction(LargeWidgetProvider.REFRESH_ACTION);
      final PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0,
          refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
      rv.setOnClickPendingIntent(R.id.refresh, refreshPendingIntent);

      // Restore the minimal header
      rv.setTextViewText(R.id.city_name, context.getString(R.string.city_name));
    } else {
      rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout_small);
      // TODO: not implements
    }
    return rv;
  }

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
