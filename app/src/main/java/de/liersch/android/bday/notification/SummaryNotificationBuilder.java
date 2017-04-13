package de.liersch.android.bday.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import java.util.List;

import de.liersch.android.bday.R;
import de.liersch.android.bday.beans.Contact;

public class SummaryNotificationBuilder extends BaseNotificationBuilder {

  private static int NOTIFICATION_ID = 2;
  private static int MAX_ENTRIES = 3;

  public void createNotification(List<Contact> contacts, List<Integer> days, Context applicationContext) {
    int size = contacts.size();
    long[] userID = new long[contacts.size()];
    for (int i = 0; i < contacts.size(); i++) {
      userID[i] = contacts.get(i).userID;
    }

    PendingIntent pendingIntent = getOpenActivityIntent(applicationContext);
    final Resources resources = applicationContext.getResources();
    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext);
    final int first =
        Integer.parseInt(sharedPref.getString(resources.getString(R.string.preference_first_alert_key), "10"));
    String tickerText = resources.getString(R.string.notification_summary_content_title, size, first);
    String contentText = "";
    for (Contact contact : contacts) {
      contentText += contact.name + ", ";
    }
    contentText = contentText.substring(0, contentText.length() - 2);

    final NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
    int count = size < MAX_ENTRIES ? size : MAX_ENTRIES;

    for (int i = 0; i < count; i++) {
      inboxStyle.addLine(resources.getString(R.string.notification_single_content_title, contacts.get(i).name, days.get(i)));
    }
    if (size > count) {
      String summaryText = "+" + (size - count) + " more";
      inboxStyle.setSummaryText(summaryText);
    }

    NotificationCompat.Builder builder = new NotificationCompat.Builder(applicationContext);
    builder
        // TODO need api level 21
        .setCategory(Notification.CATEGORY_EVENT)
        .setContentIntent(pendingIntent)
        .setTicker(tickerText)
        .setSmallIcon(R.drawable.ic_stautsbar_icon)
        .setContentTitle(tickerText)
        .setContentText(contentText)
        .setAutoCancel(true)
        .setStyle(inboxStyle)
        .setDeleteIntent(getDeleteIntent(applicationContext, userID));

    Notification notification = builder.build();


    // Use the NotificationManager to show the notification
    NotificationManager nm = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
    nm.notify(NOTIFICATION_ID, notification);
  }
}
