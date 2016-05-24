package de.liersch.android.bday.widget.provider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
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
    super.onUpdate(context, appWidgetManager, appWidgetIds);
  }

  @Override
  public void onReceive(Context ctx, Intent intent) {
    final String action = intent.getAction();
    // TODO
    if (action.equals(CLICK_ACTION)) {

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
