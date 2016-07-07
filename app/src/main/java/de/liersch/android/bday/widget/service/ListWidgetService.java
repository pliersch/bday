package de.liersch.android.bday.widget.service;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.Calendar;

import de.liersch.android.bday.R;
import de.liersch.android.bday.db.DatabaseManager;
import de.liersch.android.bday.util.CalendarUtil;
import de.liersch.android.bday.widget.provider.ListWidgetProvider;

/**
 * This is the service that provides the factory to be bound to the collection service.
 */
public class ListWidgetService extends RemoteViewsService {
  @Override
  public RemoteViewsFactory onGetViewFactory(Intent intent) {
    return new StackRemoteViewsFactory(this.getApplicationContext());
  }
}

/**
 * This is the factory that will provide data to the collection widget.
 */
class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
  private Context mApplicationContext;
  private Cursor mCursorBirthday;
  private CalendarUtil mCalendarUtil;

  public StackRemoteViewsFactory(Context context) {
    mApplicationContext = context;
    mCalendarUtil = CalendarUtil.getInstance();
  }

  public void onCreate() {
    // Since we reload the cursor in onDataSetChanged() which gets called immediately after
    // onCreate(), we do nothing here.
  }

  public void onDestroy() {
  }

  public int getCount() {
    final int count = mCursorBirthday.getCount();
    // TODO
    if(count == 0) {

    }
    return count;
  }

  public RemoteViews getViewAt(int position) {
    String contactID = "";
    int daysLeftToBDay = 0;
    if (mCursorBirthday.moveToPosition(position)) {
      contactID = mCursorBirthday.getString(0);
      Calendar today = Calendar.getInstance();
      Calendar birthday = mCalendarUtil.toCalendar(mCursorBirthday.getString(2));
      birthday = mCalendarUtil.computeNextPossibleEvent(birthday, today);
      daysLeftToBDay = mCalendarUtil.getDaysLeft(today, birthday);
    }

    int layoutId = R.layout.widget_list_item;
    int itemId = R.id.widget_item_list;


    RemoteViews rv = new RemoteViews(mApplicationContext.getPackageName(), layoutId);
    rv.setTextViewText(itemId, mCursorBirthday.getString(1).concat(Integer.toString(daysLeftToBDay)));

    final Intent fillInIntent = new Intent();
    final Bundle extras = new Bundle();
    extras.putString(ListWidgetProvider.EXTRA_DAY_ID, contactID);
    fillInIntent.putExtras(extras);
    rv.setOnClickFillInIntent(itemId, fillInIntent);

    return rv;
  }

  public RemoteViews getLoadingView() {
    // We aren't going to return a default loading view in this sample
    return null;
  }

  public int getViewTypeCount() {
    // Technically, we have two types of views (the dark and light background views)
    return 2;
  }

  public long getItemId(int position) {
    return position;
  }

  public boolean hasStableIds() {
    return true;
  }

  public void onDataSetChanged() {
    // Refresh the cursor
    if (mCursorBirthday != null) {
      mCursorBirthday.close();
    }
    mCursorBirthday = DatabaseManager.getInstance(mApplicationContext).getAllContacts();
  }
}
