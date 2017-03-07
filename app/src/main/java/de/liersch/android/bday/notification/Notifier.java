package de.liersch.android.bday.notification;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.liersch.android.bday.beans.Contact;
import de.liersch.android.bday.db.ContactController;
import de.liersch.android.bday.settings.SettingsActivity;
import de.liersch.android.bday.util.CalendarUtil;

public class Notifier {

  public static final String TAG = "Notifier";
  private final Context mApplicationContext;

  public Notifier(Context applicationContext) {
    mApplicationContext = applicationContext;
  }

  public void notifyBirthdays() {
    NotificationManager nm = (NotificationManager) mApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
    nm.cancelAll();
    final List<Contact> sortedContacts = new ContactController(mApplicationContext).getSortedContacts(Calendar.getInstance());

    if (sortedContacts.size() > 0) {
      List<Contact> contacts = new ArrayList<Contact>();
      ArrayList<Integer> days = new ArrayList<Integer>();
      SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mApplicationContext);
      final int first = Integer.parseInt(sharedPref.getString(SettingsActivity.FIRST_ALERT, "30"));
      final int second = Integer.parseInt(sharedPref.getString(SettingsActivity.SECOND_ALERT, "5"));

      for (Contact contact : sortedContacts) {
        final int daysLeft = computeDaysLeft(contact.bday);
        if (daysLeft == 0) {
          new BirthdayNotificationBuilder().createNotification(contact, mApplicationContext);
        } else if (daysLeft <= first && !contact.notified || daysLeft <= second ) {
          contacts.add(contact);
          days.add(daysLeft);
        }
      }
      if (days.size() > 1) {
        new SummaryNotificationBuilder().createNotification(contacts, days, mApplicationContext);
      } else if (days.size() > 0){
        new SingleNotificationBuilder().createNotification(contacts.get(0), days.get(0), mApplicationContext);
      }
    }
  }

  // TODO: here to. provide daysLeft via ContactController!
  private int computeDaysLeft(String bday) {
    final CalendarUtil calendarUtil = CalendarUtil.getInstance();
    Calendar now = Calendar.getInstance();
    Calendar birthday = calendarUtil.toCalendar(bday);
    birthday = calendarUtil.computeNextPossibleEvent(birthday, now);
    return calendarUtil.getDaysLeft(now, birthday);
  }

  public void destroy() {
    // TODO: delete obsolete ?
  }
}
