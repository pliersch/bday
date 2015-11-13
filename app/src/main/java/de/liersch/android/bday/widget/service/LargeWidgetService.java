package de.liersch.android.bday.widget.service;


import android.appwidget.AppWidgetManager;
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

import de.liersch.android.bday.R;
import de.liersch.android.bday.db.ContactsQuery;
import de.liersch.android.bday.widget.provider.BaseWidgetProvider;
import de.liersch.android.bday.widget.provider.LargeWidgetProvider;

/**
 * This is the service that provides the factory to be bound to the collection service.
 */
public class LargeWidgetService extends RemoteViewsService {
  @Override
  public RemoteViewsFactory onGetViewFactory(Intent intent) {
    return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
  }
}

/**
 * This is the factory that will provide data to the collection widget.
 */
class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
  private final int mProviderId;
  private Context mContext;
  private Cursor mCursorContacts;
  private Cursor mCursorBDay;
  private Cursor mCursorPhoto;
  private int mAppWidgetId;

  public StackRemoteViewsFactory(Context context, Intent intent) {
    System.out.println("StackRemoteViewsFactory#constructor");
    mContext = context;
    mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID);
    mProviderId = intent.getIntExtra(BaseWidgetProvider.PROVIDER_ID, 0);
  }

  public void onCreate() {
    // Since we reload the cursor in onDataSetChanged() which gets called immediately after
    // onCreate(), we do nothing here.
  }

  public void onDestroy() {
    if (mCursorContacts != null) {
      mCursorContacts.close();
    }
    if (mCursorBDay != null) {
      mCursorBDay.close();
    }
    if (mCursorPhoto != null) {
      mCursorPhoto.close();
    }
  }

  public int getCount() {
    System.out.println("Service#getCount: " + mCursorContacts.getCount());
    return mCursorContacts.getCount();
  }

  public RemoteViews getViewAt(int position) {
    System.out.println("Service#getViewAt: " + position + " for provider " + mProviderId);


    String contactID = "";
    String bday = "?";
    long l = 0;
    //InputStream inputStream = null;
    if (mCursorContacts.moveToPosition(position)) {
      contactID = mCursorContacts.getString(0);
      String szId = mCursorContacts.getString(mCursorContacts.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
      l = Long.parseLong(szId);

      mCursorBDay.moveToPosition(-1);
      while (mCursorBDay.moveToNext()) {
        if (mCursorBDay.getString(0).equals(contactID)) {
          bday = mCursorBDay.getString(2);
        }
      }
    }

    // Return a proper item with the proper day and temperature
    final String formatStr = mContext.getResources().getString(R.string.item_format_string);
    int layoutId = R.layout.widget_item_with_date;
    int itemId = R.id.widget_item_week;

    if(bday.equals("?")) {
      layoutId = R.layout.widget_item_without_date;
      itemId = R.id.widget_item_today;
    }

    if(mProviderId == 1) {
      layoutId = R.layout.widget_card_item_new;
      itemId = R.id.widget_card_item_new;
    }
    RemoteViews rv = new RemoteViews(mContext.getPackageName(), layoutId);
    rv.setTextViewText(itemId, mCursorContacts.getString(1).concat(bday));

    if(mProviderId == 1) {
      Bitmap bitmap = loadContactPhoto(mContext.getContentResolver(), l);
      if(bitmap != null) {
        System.out.println("foobar" + bitmap.getHeight());
        rv.setImageViewBitmap(R.id.imageView2, bitmap);
      }
    }

    // Set the click intent so that we can handle it and show a toast message
    final Intent fillInIntent = new Intent();
    final Bundle extras = new Bundle();
    extras.putString(LargeWidgetProvider.EXTRA_DAY_ID, contactID);
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
    System.out.println("Service#onDataSetChanged");
    // Refresh the cursor
    if (mCursorContacts != null) {
      mCursorContacts.close();
    }
    mCursorContacts = ContactsQuery.getInstance().queryVisibleContacts(mContext);

    if (mCursorBDay != null) {
      mCursorBDay.close();
    }
    mCursorBDay = ContactsQuery.getInstance().queryBirthdaysContacts(mContext);

    if (mCursorPhoto != null) {
      mCursorPhoto.close();
    }
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
