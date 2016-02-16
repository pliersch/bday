package de.liersch.android.bday.notification.util;

import android.content.Context;
import android.database.Cursor;

import java.util.Calendar;

import de.liersch.android.bday.db.DatabaseManager;
import de.liersch.android.bday.notification.NotificationBuilder;
import de.liersch.android.bday.util.CalendarUtil;

public class Notifier {

  public static int TIME_RANGE = 40;
  private final Context mApplicationContext;

  public Notifier(Context applicationContext) {
    mApplicationContext = applicationContext;
  }

  public void notifyBirthdays() {
// TODO maybe closed db
    final Cursor cursor = DatabaseManager.getInstance(mApplicationContext).read();
    if (cursor != null) {
      cursor.moveToPosition(-1);
      while (cursor.moveToNext()) {
        final String userID = cursor.getString(0);
        final String name = cursor.getString(1);
        final String bday = cursor.getString(2);
        final String send = cursor.getString(3);
        System.out.println("Notifier#notifyBirthdays db entries: " + userID + "," + name + ","  + bday + "," + send);

        final int daysLeft = computeDaysLeft(bday);
        if (daysLeft <= TIME_RANGE) {
          new NotificationBuilder().createNotification(Long.parseLong(userID), name, daysLeft, mApplicationContext);
        }
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
