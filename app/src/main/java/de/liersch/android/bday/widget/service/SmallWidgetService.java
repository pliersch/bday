package de.liersch.android.bday.widget.service;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.io.InputStream;
import java.util.Calendar;

import de.liersch.android.bday.R;
import de.liersch.android.bday.db.DatabaseManager;
import de.liersch.android.bday.util.CalendarUtil;
import de.liersch.android.bday.widget.provider.BaseWidgetProvider;
import de.liersch.android.bday.widget.provider.ListWidgetProvider;

/**
 * This is the service that provides the factory to be bound to the collection service.
 */
public class SmallWidgetService extends RemoteViewsService {
  @Override
  public RemoteViewsFactory onGetViewFactory(Intent intent) {
    return new SmallRemoteViewsFactory(this.getApplicationContext(), intent);
  }
}

/**
 * This is the factory that will provide data to the collection widget.
 */
class SmallRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
  private final int mProviderId;
  private Context mApplicationContext;
  private Cursor mCursorBirthday;
  private CalendarUtil mCalendarUtil;
  private static int sAvailableWidgets = 0;

  public SmallRemoteViewsFactory(Context context, Intent intent) {
    System.out.println("StackRemoteViewsFactory#constructor context: " + context.toString());
    mApplicationContext = context;
    mProviderId = intent.getIntExtra(BaseWidgetProvider.PROVIDER_ID, 0);
    mCalendarUtil = CalendarUtil.getInstance();
  }

  public void onCreate() {
    sAvailableWidgets++;
    // Since we reload the cursor in onDataSetChanged() which gets called immediately after
    // onCreate(), we do nothing here.
  }

  public void onDestroy() {
    // TODO
    if(--sAvailableWidgets == 0) {
//      mCursorBirthday.close();
    }
  }

  public int getCount() {
    return 1;
  }

  public RemoteViews getViewAt(int position) {
    String contactID = "";
    int daysLeftToBDay = 0;
    if (mCursorBirthday.moveToPosition(position)) {
      contactID = mCursorBirthday.getString(position);
      System.out.println("Service#getViewAt: " + position + " | provider " + mProviderId);
      Calendar today = Calendar.getInstance();
      Calendar birthday = mCalendarUtil.toCalendar(mCursorBirthday.getString(2));
      birthday = mCalendarUtil.computeNextPossibleEvent(birthday, today);
      daysLeftToBDay = mCalendarUtil.getDaysLeft(today, birthday);
    }

    int layoutId = R.layout.widget_small_item;
    int itemId = R.id.widget_item_small;

    RemoteViews rv = new RemoteViews(mApplicationContext.getPackageName(), layoutId);
    rv.setTextViewText(itemId, mCursorBirthday.getString(1).concat(Integer.toString(daysLeftToBDay)));

    Bitmap bitmap = loadContactPhoto(mApplicationContext.getContentResolver(), Long.parseLong(contactID));
    if (bitmap != null) {
      rv.setImageViewBitmap(R.id.imageView2, bitmap);
    }

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
    return 1;
  }

  public long getItemId(int position) {
    return position;
  }

  public boolean hasStableIds() {
    return true;
  }

  public void onDataSetChanged() {
    System.out.println("Service#onDataSetChanged");
    // Refresh the cursor
    if (mCursorBirthday != null) {
      mCursorBirthday.close();
    }
    mCursorBirthday = DatabaseManager.getInstance(mApplicationContext).read();
  }

  private Bitmap loadContactPhoto(ContentResolver cr, long  id) {
    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri, true);
    if (input == null) {
      return null;
    }
    return BitmapFactory.decodeStream(input);
  }

}
