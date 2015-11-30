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
import de.liersch.android.bday.app.MainActivity;
import de.liersch.android.bday.widget.service.WidgetService;


public class SmallWidgetProvider extends BaseWidgetProvider {

  public SmallWidgetProvider() {
    super();
  }

  @Override
  protected String getThreadName() {
    return "SmallWidgetProvider-worker";
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
          mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.stack_view);
        }
      });

      final int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    } else if (action.equals(CLICK_ACTION)) {
      // Show a toast
      final int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    super.onReceive(ctx, intent);
  }

  @Override
  protected RemoteViews buildLayout(Context context, int appWidgetId, boolean largeLayout) {
    RemoteViews rv;
    if (largeLayout) {
      // Specify the service to provide data for the collection widget.  Note that we need to
      // embed the appWidgetId via the data otherwise it will be ignored.
      final Intent intent = new Intent(context, WidgetService.class);
      intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
      final Bundle extras = new Bundle();
      extras.putInt(BaseWidgetProvider.PROVIDER_ID, 1);
      intent.putExtras(extras);
      intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
      rv = new RemoteViews(context.getPackageName(), R.layout.widget_card_layout);
      rv.setRemoteAdapter(R.id.stack_view, intent);

      // Set the empty view to be displayed if the collection is empty.  It must be a sibling
      // view of the collection view.
      rv.setEmptyView(R.id.stack_view, R.id.empty_view);

      final Intent activityIntent = new Intent(context, MainActivity.class);
      PendingIntent activityPendingIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);
      rv.setOnClickPendingIntent(R.id.imageView2, activityPendingIntent);

//      // Bind a click listener template for the contents of the weather list.  Note that we
//      // need to update the intent's data if we set an extra, since the extras will be
//      // ignored otherwise.
//      final Intent onClickIntent = new Intent(context, LargeWidgetProvider.class);
//      onClickIntent.setAction(LargeWidgetProvider.CLICK_ACTION);
//      onClickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
//      onClickIntent.setData(Uri.parse(onClickIntent.toUri(Intent.URI_INTENT_SCHEME)));
//      final PendingIntent onClickPendingIntent = PendingIntent.getBroadcast(context, 0,
//          onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//      rv.setPendingIntentTemplate(R.id.stack_view, onClickPendingIntent);

      // Restore the minimal header
      rv.setTextViewText(R.id.city_name, context.getString(R.string.city_name));
    } else {
      rv = new RemoteViews(context.getPackageName(), R.layout.widget_card_layout);
      // TODO: not implements
    }
    return rv;
  }
}
