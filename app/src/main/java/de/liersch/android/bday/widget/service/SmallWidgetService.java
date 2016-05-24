package de.liersch.android.bday.widget.service;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.Calendar;
import java.util.List;

import de.liersch.android.bday.R;
import de.liersch.android.bday.beans.Contact;
import de.liersch.android.bday.db.ContactController;
import de.liersch.android.bday.db.ContactUtil;
import de.liersch.android.bday.util.CalendarUtil;
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
  private Context mApplicationContext;
  private CalendarUtil mCalendarUtil;
  private static int sAvailableWidgets = 0;

  public SmallRemoteViewsFactory(Context context, Intent intent) {
    System.out.println("SmallWidgetService StackRemoteViewsFactory#constructor");
    mApplicationContext = context;
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
    final ContactController contactController = new ContactController(mApplicationContext);
    final int size = contactController.getNextBirthdayContacts(Calendar.getInstance()).size();
    System.out.println("SmallWidgetService#getCount " + size);
    return size;
    //return 1;
  }

  public RemoteViews getViewAt(int position) {
    System.out.println("SmallWidgetService#getViewAt: " + position);

    final Calendar today = Calendar.getInstance();
    final List<Contact> nextBirthdayContacts = new ContactController(mApplicationContext).getNextBirthdayContacts(today);

    long contactID;
    int daysLeftToBDay;

    final int size = nextBirthdayContacts.size();
    final int hour = today.get(Calendar.HOUR_OF_DAY);
    final int current = hour % size;
    final Contact contact = nextBirthdayContacts.get(current);
    contactID = contact.userID;
    Calendar birthday = mCalendarUtil.toCalendar(contact.bday);
    birthday = mCalendarUtil.computeNextPossibleEvent(birthday, today);
    daysLeftToBDay = mCalendarUtil.getDaysLeft(today, birthday);
    RemoteViews rv = new RemoteViews(mApplicationContext.getPackageName(), R.layout.widget_small_item);
    rv.setTextViewText(R.id.widget_item_small, contact.name.concat(Integer.toString(daysLeftToBDay)));

    Bitmap bitmap = ContactUtil.getInstance().loadContactPhoto(mApplicationContext.getContentResolver(), contactID);
    if (bitmap != null) {
      rv.setImageViewBitmap(R.id.widget_card_image_view, bitmap);
    }

    final Intent fillInIntent = new Intent();
    final Bundle extras = new Bundle();
    // TODO try without casting
    extras.putString(ListWidgetProvider.EXTRA_DAY_ID, String.valueOf(contactID));
    fillInIntent.putExtras(extras);
    rv.setOnClickFillInIntent(R.id.widget_card_image_view, fillInIntent);
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
    System.out.println("SmallWidgetService#onDataSetChanged");
  }
}
