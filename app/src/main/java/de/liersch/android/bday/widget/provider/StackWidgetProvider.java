package de.liersch.android.bday.widget.provider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import de.liersch.android.bday.R;
import de.liersch.android.bday.app.OldMainActivity;
import de.liersch.android.bday.widget.service.ListWidgetService;


public class StackWidgetProvider extends BaseWidgetProvider {

  public StackWidgetProvider() {
    super();
  }

  @Override
  protected String getThreadName() {
    return "StackWidgetProvider-worker";
  }

  @Override
  public void onReceive(Context ctx, Intent intent) {
    final String action = intent.getAction();
    System.out.println("Provider#onReceive: " + ctx.toString() + action);
    if (action.equals(CLICK_ACTION)) {
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
      final Intent intent = new Intent(context, ListWidgetService.class);
      intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
      final Bundle extras = new Bundle();
      extras.putInt(PROVIDER_ID, 1);
      intent.putExtras(extras);
      intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
      rv = new RemoteViews(context.getPackageName(), R.layout.widget_card_layout);
      rv.setRemoteAdapter(R.id.stack_view, intent);

      // Set the empty view to be displayed if the collection is empty.  It must be a sibling
      // view of the collection view.
      rv.setEmptyView(R.id.stack_view, R.id.empty_view);

      final Intent activityIntent = new Intent(context, OldMainActivity.class);
      PendingIntent activityPendingIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);
      rv.setOnClickPendingIntent(R.id.widget_card_image_view, activityPendingIntent);

//      // Restore the minimal header
//      rv.setTextViewText(R.id.city_name, context.getString(R.string.city_name));
    } else {
      rv = new RemoteViews(context.getPackageName(), R.layout.widget_card_layout);
      // TODO: not implements
    }
    return rv;
  }
}
