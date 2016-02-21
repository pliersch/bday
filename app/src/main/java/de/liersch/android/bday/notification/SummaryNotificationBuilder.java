package de.liersch.android.bday.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Calendar;

import de.liersch.android.bday.R;
import de.liersch.android.bday.notification.util.Notifier;

public class SummaryNotificationBuilder extends NotificationBuilder {

  public void createNotification(ArrayList<Long> ids, ArrayList<String> names, ArrayList<Integer> days, Context applicationContext) {
    PendingIntent pendingIntent = getPendingIntent(applicationContext);
    final Resources resources = applicationContext.getResources();
    String tickerText = resources.getString(R.string.notification_summary_content_title, names.size(), Notifier.TIME_RANGE);
    String contentText = "";
    for (String name : names) {
      contentText += name + ", ";
    }
    contentText = contentText.substring(0, contentText.length() - 2);

    final NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
    int size = names.size();
    int count = size < 2 ? size : 2;

    for (int i = 0; i < count; i++) {
      inboxStyle.addLine(resources.getString(R.string.notification_single_content_title, names.get(i), days.get(i)));
    }
    if (size > count) {
      String summaryText = "+" + (size-count) + " more";
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
        .setStyle(inboxStyle);

    Notification notification = builder.build();

    // Use the NotificationManager to show the notification
    NotificationManager nm = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
    int notificationId = (int) Calendar.getInstance().getTimeInMillis();
    nm.notify(notificationId, notification);
  }
}
