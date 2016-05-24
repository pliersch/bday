package de.liersch.android.bday.widget.provider;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.RemoteViews;

public abstract class BaseWidgetProvider extends AppWidgetProvider {
  public static String CLICK_ACTION = "com.example.android.weatherlistwidget.CLICK";
  public static String EXTRA_DAY_ID = "com.example.android.weatherlistwidget.day";
  public static String PROVIDER_ID = "de.liersch.android.bday.provider";

  protected String TAG = "not set";


  protected static HandlerThread sWorkerThread;
  protected static Handler sWorkerQueue;

  private boolean mIsLargeLayout = true;

  public BaseWidgetProvider() {
    super();
    System.out.println("BaseWidgetProvider#constructor");
    initThread(getThreadName());
  }

  protected abstract String getThreadName();

  protected void initThread(String threadName) {
    sWorkerThread = new HandlerThread(threadName);
    sWorkerThread.start();
    sWorkerQueue = new Handler(sWorkerThread.getLooper());
  }

  // TODO: clear the worker queue if we are destroyed?

  @Override
  public void onEnabled(Context context) {
    System.out.println(TAG + "#onEnabled");
  }

  @Override
  public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
    System.out.println(TAG + "#onRestored");
    super.onRestored(context, oldWidgetIds, newWidgetIds);
  }

  @Override
  public void onDeleted(Context context, int[] appWidgetIds) {
    System.out.println(TAG + "#onDeleted");
    super.onDeleted(context, appWidgetIds);
  }

  protected abstract RemoteViews buildLayout(Context context, int appWidgetId, boolean largeLayout);

  @Override
  public void onReceive(Context context, Intent intent) {
    System.out.println(TAG + "#onReceive: " + context.toString() + intent);
    super.onReceive(context, intent);
  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    System.out.println(TAG + "#onUpdate");

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
