package de.liersch.android.bday.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import de.liersch.android.bday.R;

public class ContactsObserver extends ContentObserver {

  private AppWidgetManager mAppWidgetManager;
  private ComponentName mComponentName;

  public ContactsObserver(AppWidgetManager mgr, ComponentName cn, Handler h) {
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

