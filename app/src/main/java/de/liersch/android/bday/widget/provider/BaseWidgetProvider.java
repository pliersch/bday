package de.liersch.android.bday.widget.provider;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.RemoteViews;

public abstract class BaseWidgetProvider extends AppWidgetProvider {
  public static String CLICK_ACTION = "com.example.android.weatherlistwidget.CLICK";
  public static String EXTRA_DAY_ID = "com.example.android.weatherlistwidget.day";
  public static String PROVIDER_ID = "de.liersch.android.bday.provider";

  protected static HandlerThread sWorkerThread;
  protected static Handler sWorkerQueue;

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

  // TODO: clear the worker queue if we are destroyed?


  protected abstract RemoteViews buildLayout(Context context, int appWidgetId, boolean largeLayout);

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
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
