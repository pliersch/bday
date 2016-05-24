package de.liersch.android.bday.widget.provider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import de.liersch.android.bday.R;
import de.liersch.android.bday.app.OldMainActivity;
import de.liersch.android.bday.db.DatabaseManager;
import de.liersch.android.bday.widget.service.ListWidgetService;


public class ListWidgetProvider extends BaseWidgetProvider {

  public static String REFRESH_ACTION = "com.example.android.weatherlistwidget.REFRESH";
  public static String RESET_DATABASE_ACTION = "com.example.android.weatherlistwidget.RESET_DATABASE";


  public ListWidgetProvider() {
    super();
    TAG = ListWidgetProvider.class.getSimpleName();
  }

  @Override
  protected String getThreadName() {
    return "ListWidgetProvider-worker";
  }

  @Override
  public void onReceive(Context ctx, Intent intent) {

    final String action = intent.getAction();
    if (action.equals(REFRESH_ACTION) || action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {

      // BroadcastReceivers have a limited amount of time to do work, so for this sample, we
      // are triggering an addContact of the data on another thread.  In practice, this addContact
      // can be triggered from a background service, or perhaps as a result of user actions
      // inside the main application.
      final Context context = ctx;
      sWorkerQueue.removeMessages(0);
      sWorkerQueue.post(new Runnable() {
        @Override
        public void run() {
          final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
          final ComponentName cn = new ComponentName(context, ListWidgetProvider.class);
          mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.weather_list);
        }
      });

      final int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    } else if (action.equals(CLICK_ACTION)) {
      // Show a toast
      final int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    } else if (action.equals(RESET_DATABASE_ACTION)) {
      DatabaseManager.getInstance(ctx).reset();
    }
    super.onReceive(ctx, intent);
  }

  @Override
  protected RemoteViews buildLayout(Context context, int appWidgetId, boolean largeLayout) {
    RemoteViews rv;
    if (largeLayout) {
      rv = new RemoteViews(context.getPackageName(), R.layout.widget_list_layout);
      rv.setEmptyView(R.id.weather_list, R.id.empty_view);

      // Specify the service to provide data for the collection widget.  Note that we need to
      // embed the appWidgetId via the data otherwise it will be ignored.
      final Intent intent = new Intent(context, ListWidgetService.class);
      intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

      final Bundle extras = new Bundle();
      extras.putInt(PROVIDER_ID, 0);
      intent.putExtras(extras);

      intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
      rv.setRemoteAdapter(R.id.weather_list, intent);

      // Set the empty view to be displayed if the collection is empty.  It must be a sibling
      // view of the collection view.

      // Bind a click listener template for the contents of the weather list.  Note that we
      // need to addContact the intent's data if we set an extra, since the extras will be
      // ignored otherwise.
      final Intent onClickIntent = new Intent(context, ListWidgetProvider.class);
      onClickIntent.setAction(CLICK_ACTION);
      onClickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
      onClickIntent.setData(Uri.parse(onClickIntent.toUri(Intent.URI_INTENT_SCHEME)));

      final PendingIntent onClickPendingIntent = PendingIntent.getBroadcast(context, 0,
          onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
      rv.setPendingIntentTemplate(R.id.weather_list, onClickPendingIntent);

      // Bind the click intent for the refresh button on the widget
      final Intent refreshIntent = new Intent(context, ListWidgetProvider.class);
      refreshIntent.setAction(REFRESH_ACTION);
      final PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0,
          refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
      rv.setOnClickPendingIntent(R.id.btn_refresh, refreshPendingIntent);

      final Intent onClickDbIntent = new Intent(context, ListWidgetProvider.class);
      onClickDbIntent.setAction(RESET_DATABASE_ACTION);
      onClickDbIntent.setData(Uri.parse(onClickDbIntent.toUri(Intent.URI_INTENT_SCHEME)));
      PendingIntent dbPendingIntent = PendingIntent.getBroadcast(context, 0, onClickDbIntent, PendingIntent.FLAG_CANCEL_CURRENT);
      rv.setOnClickPendingIntent(R.id.btn_widget_db_reset, dbPendingIntent);

      final Intent activityIntent = new Intent(context, OldMainActivity.class);
      PendingIntent activityPendingIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_CANCEL_CURRENT);
      rv.setOnClickPendingIntent(R.id.btn_widget_options, activityPendingIntent);
    } else {
      rv = new RemoteViews(context.getPackageName(), R.layout.widget_small_layout);
      // TODO: not implements
    }
    return rv;
  }
}
