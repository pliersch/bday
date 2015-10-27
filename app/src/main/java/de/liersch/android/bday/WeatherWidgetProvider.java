package de.liersch.android.bday;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.ContactsContract;
import android.widget.RemoteViews;
import android.widget.Toast;

import de.liersch.android.bday.db.ContactsQuery;

/**
 * Our data observer just notifies an update for all weather widgets when it detects a change.
 */
class WeatherDataProviderObserver extends ContentObserver {
  private AppWidgetManager mAppWidgetManager;
  private ComponentName mComponentName;

  WeatherDataProviderObserver(AppWidgetManager mgr, ComponentName cn, Handler h) {
    super(h);
    mAppWidgetManager = mgr;
    mComponentName = cn;
  }

  @Override
  public void onChange(boolean selfChange) {
    System.out.println("DataProvider#onChange");
    // The data has changed, so notify the widget that the collection view needs to be updated.
    // In response, the factory's onDataSetChanged() will be called which will requery the
    // cursor for the new data.
    mAppWidgetManager.notifyAppWidgetViewDataChanged(
        mAppWidgetManager.getAppWidgetIds(mComponentName), R.id.weather_list);
  }
}

public class WeatherWidgetProvider extends AppWidgetProvider {
  public static String CLICK_ACTION = "com.example.android.weatherlistwidget.CLICK";
  public static String REFRESH_ACTION = "com.example.android.weatherlistwidget.REFRESH";
  public static String EXTRA_DAY_ID = "com.example.android.weatherlistwidget.day";

  private static HandlerThread sWorkerThread;
  private static Handler sWorkerQueue;
  private static WeatherDataProviderObserver sDataObserver;

  private boolean mIsLargeLayout = true;

  public WeatherWidgetProvider() {
    // Start the worker thread
    sWorkerThread = new HandlerThread("WeatherWidgetProvider-worker");
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
    if (sDataObserver == null) {
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
    final ComponentName cn = new ComponentName(context, WeatherWidgetProvider.class);
    sDataObserver = new WeatherDataProviderObserver(mgr, cn, sWorkerQueue);
    r.registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, sDataObserver);
    //context.getApplicationContext().getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, sDataObserver);
  }

  @Override
  public void onReceive(Context ctx, Intent intent) {
    final String action = intent.getAction();
    System.out.println("Provider#onReceive: " + action);
    if (action.equals(REFRESH_ACTION)) {
      if (sDataObserver == null) {
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
          final ContentResolver r = context.getContentResolver();
//          Uri uri = ContactsContract.Contacts.CONTENT_URI;
//          String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ?";
//          String[] selectionArgs = new String[] {"1"};
//          String[] projection = new String[] {
//              ContactsContract.Contacts._ID,
//              ContactsContract.Contacts.DISPLAY_NAME };
//          final Cursor c = r.query(uri, projection, selection, selectionArgs, null);
//          final int count = c.getCount();
//
  //          // We disable the data changed observer temporarily since each of the updates
//          // will trigger an onChange() in our data observer.
//          r.unregisterContentObserver(sDataObserver);
//          for (int i = 0; i < count; ++i) {
//            final Uri uri = ContentUris.withAppendedId(WeatherDataProvider.CONTENT_URI, i);
//            final ContentValues values = new ContentValues();
//            values.put(WeatherDataProvider.Columns.TEMPERATURE,
//                new Random().nextInt(sMaxDegrees));
//            r.update(uri, values, null, null);
//          }
//          r.registerContentObserver(WeatherDataProvider.CONTENT_URI, true, sDataObserver);

          final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
          final ComponentName cn = new ComponentName(context, WeatherWidgetProvider.class);
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
      final Intent intent = new Intent(context, WeatherWidgetService.class);
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
      final Intent onClickIntent = new Intent(context, WeatherWidgetProvider.class);
      onClickIntent.setAction(WeatherWidgetProvider.CLICK_ACTION);
      onClickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
      onClickIntent.setData(Uri.parse(onClickIntent.toUri(Intent.URI_INTENT_SCHEME)));
      final PendingIntent onClickPendingIntent = PendingIntent.getBroadcast(context, 0,
          onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
      rv.setPendingIntentTemplate(R.id.weather_list, onClickPendingIntent);

      // Bind the click intent for the refresh button on the widget
      final Intent refreshIntent = new Intent(context, WeatherWidgetProvider.class);
      refreshIntent.setAction(WeatherWidgetProvider.REFRESH_ACTION);
      final PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0,
          refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
      rv.setOnClickPendingIntent(R.id.refresh, refreshPendingIntent);

      // Restore the minimal header
      rv.setTextViewText(R.id.city_name, context.getString(R.string.city_name));
    } else {
      rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout_small);
      Cursor c = ContactsQuery.getInstance().queryVisibleContacts(context);
      if (c.moveToPosition(0)) {
        int tempColIndex = c.getColumnIndex(WeatherDataProvider.Columns.TEMPERATURE);
        int temp = c.getInt(tempColIndex);
        String formatStr = context.getResources().getString(R.string.header_format_string);
        String header = String.format(formatStr, temp,  context.getString(R.string.city_name));
        rv.setTextViewText(R.id.city_name, header);
      }
      c.close();
    }
    return rv;
  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    System.out.println("Provider#onUpdate: " + appWidgetIds.length);
    if (sDataObserver == null) {
      registerContentObserver(context);
    }
    // Update each of the widgets with the remote adapter
    for (int i = 0; i < appWidgetIds.length; ++i) {
      RemoteViews layout = buildLayout(context, appWidgetIds[i], mIsLargeLayout);
      appWidgetManager.updateAppWidget(appWidgetIds[i], layout);
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
