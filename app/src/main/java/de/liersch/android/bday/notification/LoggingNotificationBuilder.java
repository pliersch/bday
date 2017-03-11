package de.liersch.android.bday.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import de.liersch.android.bday.R;

public class LoggingNotificationBuilder extends NotificationBuilder {
  
  private static int NOTIFICATION_ID = 12;
  
  public LoggingNotificationBuilder() {
  }
  
  public void showNotification(Context context, String msg) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
    builder
        .setTicker("Date Checker")
        .setSmallIcon(R.drawable.ic_stautsbar_icon)
        .setContentTitle("Logging")
        .setContentText(msg)
        .setAutoCancel(true)
        //.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
    ;
    Notification notification = builder.build();
    NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    nm.notify(NOTIFICATION_ID, notification);
  
  }
}
