package de.liersch.android.bday.notification.util;

import android.content.Context;
import android.database.Cursor;

import de.liersch.android.bday.db.DatabaseManager;
import de.liersch.android.bday.notification.NotificationBuilder;

public class Notifier {

  private final Context mApplicationContext;

  public Notifier(Context applicationContext) {
    mApplicationContext = applicationContext;
  }

  public void notifyBirthdays() {
// TODO maybe closed db
    final Cursor read = DatabaseManager.getInstance(mApplicationContext).read();
    if (read != null) {
      NotificationBuilder notificationBuilder = new NotificationBuilder();
      read.moveToPosition(-1);
      while (read.moveToNext()) {
        final String userID = read.getString(0);
        final String name = read.getString(1);
        final String bday = read.getString(2);
        final String send = read.getString(3);
        System.out.println("Notifier#notifyBirthdays db entries: " + userID + "," + name + "," + send);
        //if (userID.equals("62")) {
          notificationBuilder.createNotification(name, 100, mApplicationContext);
        //}
      }
    }
  }

  public void notifyNextDays() {

  }

  public void notifyToday() {
  }

  public void computeNextBirthdays() {
    //CalendarUtil.getInstance().
  }

  public void updateNextBirthdays() {
  }

  public void destroy() {
    DatabaseManager.getInstance(mApplicationContext).close();
  }


}
