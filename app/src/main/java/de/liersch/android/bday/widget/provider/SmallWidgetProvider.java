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
import de.liersch.android.bday.activity.MainActivity;
import de.liersch.android.bday.widget.service.SmallWidgetService;


public class SmallWidgetProvider extends BaseWidgetProvider {

  public SmallWidgetProvider() {
    super();
    TAG = SmallWidgetProvider.class.getSimpleName();
  }

  @Override
  protected String getThreadName() {
    return "SmallWidgetProvider-worker";
  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    for (int i = 0; i < appWidgetIds.length; ++i) {
      System.out.println("SmallWidgetProvider#onUpdate for : " + appWidgetIds[i]);

      // Here we setup the intent which points to the StackViewService which will
      // provide the views for this collection.
      Intent intent = new Intent(context, SmallWidgetService.class);
      intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
      // When intents are compared, the extras are ignored, so we need to embed the extras
      // into the data so that the extras will not be ignored.
      intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
      RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_small_layout);
      rv.setRemoteAdapter(R.id.stack_view, intent);

      // The empty view is displayed when the collection has no items. It should be a sibling
      // of the collection view.
      rv.setEmptyView(R.id.stack_view, R.id.empty_view);
/*
      // Here we setup the a pending intent template. Individuals items of a collection
      // cannot setup their own pending intents, instead, the collection as a whole can
      // setup a pending intent template, and the individual items can set a fillInIntent
      // to create unique before on an item to item basis.
      Intent toastIntent = new Intent(context, StackWidgetProvider.class);
      toastIntent.setAction(StackWidgetProvider.TOAST_ACTION);
      toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
      intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
      PendingIntent toastPendingIntent =
          PendingIntent.getBroadcast(context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
      rv.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent);
    */
      //appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
    }
    super.onUpdate(context, appWidgetManager, appWidgetIds);
  }

  @Override
  public void onReceive(Context ctx, Intent intent) {
    final String action = intent.getAction();
    // TODO
    if (action.equals(CLICK_ACTION)) {
      final Context context = ctx;
      sWorkerQueue.removeMessages(0);
      sWorkerQueue.post(new Runnable() {
        @Override
        public void run() {
          final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
          final ComponentName cn = new ComponentName(context, SmallWidgetProvider.class);
          mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.small_stack_view);
        }
      });
    }
    super.onReceive(ctx, intent);
  }

  @Override
  protected RemoteViews buildLayout(Context context, int appWidgetId, boolean largeLayout) {
    RemoteViews rv;
    final Intent intent = new Intent(context, SmallWidgetService.class);
    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
    final Bundle extras = new Bundle();
    extras.putInt(PROVIDER_ID, 1);
    intent.putExtras(extras);
    intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
    rv = new RemoteViews(context.getPackageName(), R.layout.widget_small_layout);
    rv.setRemoteAdapter(R.id.small_stack_view, intent);

    // Set the empty view to be displayed if the collection is empty.  It must be a sibling
    // view of the collection view.
    rv.setEmptyView(R.id.small_stack_view, R.id.small_empty_view);

    final Intent activityIntent = new Intent(context, MainActivity.class);
    PendingIntent activityPendingIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);
    rv.setPendingIntentTemplate(R.id.small_stack_view, activityPendingIntent);
    return rv;
  }
}
