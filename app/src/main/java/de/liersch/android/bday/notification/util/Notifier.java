package de.liersch.android.bday.notification.util;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Calendar;

import de.liersch.android.bday.db.DatabaseManager;
import de.liersch.android.bday.notification.SummaryNotificationBuilder;
import de.liersch.android.bday.notification.SingleNotificationBuilder;
import de.liersch.android.bday.util.CalendarUtil;

public class Notifier {

  public static int TIME_RANGE = 200;
  private final Context mApplicationContext;

  public Notifier(Context applicationContext) {
    mApplicationContext = applicationContext;
  }

  // TODO: remove or update old notifications
  // SEE: http://developer.android.com/training/notify-user/managing.html
  public void notifyBirthdays() {
// TODO maybe closed db
    final Cursor cursor = DatabaseManager.getInstance(mApplicationContext).read();
    if (cursor != null) {
      ArrayList<Long> ids = new ArrayList<Long>();
      ArrayList<String> names = new ArrayList<String>();
      ArrayList<Integer> days = new ArrayList<Integer>();
      cursor.moveToPosition(-1);
      while (cursor.moveToNext()) {
        final String userID = cursor.getString(0);
        final String name = cursor.getString(1);
        final String bday = cursor.getString(2);
        final String send = cursor.getString(3);
        System.out.println("Notifier#notifyBirthdays db entries: " + userID + "," + name + "," + bday + "," + send);

        final int daysLeft = computeDaysLeft(bday);

        if (daysLeft < 20) {
          new SingleNotificationBuilder().createNotification(Long.parseLong(userID), name, daysLeft, mApplicationContext);
        } else if (daysLeft <= TIME_RANGE) {
          ids.add(Long.parseLong(userID));
          names.add(name);
          days.add(daysLeft);
        }
      }
      if (ids.size() > 1) {
        new SummaryNotificationBuilder().createNotification(ids, names, days, mApplicationContext);
      } else {
        new SingleNotificationBuilder().createNotification(ids.get(0), names.get(0), days.get(0), mApplicationContext);
      }
    }
  }

  private int computeDaysLeft(String bday) {
    Calendar now = Calendar.getInstance();
    final CalendarUtil calendarUtil = CalendarUtil.getInstance();
    Calendar birthday = calendarUtil.toCalendar(bday);
    birthday = calendarUtil.computeNextPossibleEvent(birthday, now);
    return calendarUtil.getDaysLeft(now, birthday);
  }

  public void destroy() {
    DatabaseManager.getInstance(mApplicationContext).close();
  }
}
