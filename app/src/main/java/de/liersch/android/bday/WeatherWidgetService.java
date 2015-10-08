/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.liersch.android.bday;


import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

/**
 * This is the service that provides the factory to be bound to the collection service.
 */
public class WeatherWidgetService extends RemoteViewsService {
  @Override
  public RemoteViewsFactory onGetViewFactory(Intent intent) {
    return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
  }
}

/**
 * This is the factory that will provide data to the collection widget.
 */
class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
  private Context mContext;
  private Cursor mCursorContacts;
  private Cursor mCursorBDay;
  private int mAppWidgetId;

  public StackRemoteViewsFactory(Context context, Intent intent) {
    System.out.println("StackRemoteViewsFactory#constructor");
    mContext = context;
    mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID);
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
  }

  public int getCount() {
    System.out.println("Service#getCount: " + mCursorContacts.getCount());
    return mCursorContacts.getCount();
  }

  public RemoteViews getViewAt(int position) {
    System.out.println("Service#getViewAt: " + position);

    String contactID;
    String bday = "?";
    // Get the data for this position from the content provider
    String day = "Unknown Day";
    if (mCursorContacts.moveToPosition(position)) {
      contactID = mCursorContacts.getString(0);
      mCursorBDay.moveToPosition(-1);
      while (mCursorBDay.moveToNext()) {
        if (mCursorBDay.getString(0).equals(contactID)) {
          bday = mCursorBDay.getString(2);
        }
      }
    }

    // Return a proper item with the proper day and temperature
    final String formatStr = mContext.getResources().getString(R.string.item_format_string);
    int layoutId = R.layout.widget_item_week;
    int itemId = R.id.widget_item_week;

    if(bday == "?") {
      layoutId = R.layout.widget_item_today;
      itemId = R.id.widget_item_today;
    }
    RemoteViews rv = new RemoteViews(mContext.getPackageName(), layoutId);
    rv.setTextViewText(itemId, mCursorContacts.getString(1).concat(bday));

    // Set the click intent so that we can handle it and show a toast message
    final Intent fillInIntent = new Intent();
    final Bundle extras = new Bundle();
    extras.putString(WeatherWidgetProvider.EXTRA_DAY_ID, day);
    fillInIntent.putExtras(extras);
    rv.setOnClickFillInIntent(layoutId, fillInIntent);

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
    Uri uri = ContactsContract.Contacts.CONTENT_URI;
    String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ?";
    String[] selectionArgs = new String[] {"1"};
    String[] projection = new String[] {
        ContactsContract.Contacts._ID,
        ContactsContract.Contacts.DISPLAY_NAME };
    mCursorContacts = mContext.getContentResolver().query(uri, projection, selection, selectionArgs, null);

    if (mCursorBDay != null) {
      mCursorBDay.close();
    }
    uri = ContactsContract.Data.CONTENT_URI;
    projection = new String[] {
        ContactsContract.Data.CONTACT_ID,
        ContactsContract.CommonDataKinds.Event.TYPE,
        ContactsContract.CommonDataKinds.Event.START_DATE,
        ContactsContract.CommonDataKinds.Event.LABEL };
    selection = ContactsContract.Data.MIMETYPE + " = ? AND "
        + ContactsContract.CommonDataKinds.Event.TYPE + " = ?";
    selectionArgs = new String[] {
        ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
        String.valueOf(ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)
    };
    mCursorBDay = mContext.getContentResolver().query(uri, projection, selection, selectionArgs, null);
  }
}
