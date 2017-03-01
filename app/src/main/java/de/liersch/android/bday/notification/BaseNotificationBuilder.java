package de.liersch.android.bday.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class BaseNotificationBuilder extends NotificationBuilder {

  protected PendingIntent getDeleteIntent(Context context, long userID) {
    Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
    intent.setAction("notification_cancelled");
    intent.putExtra("userID", userID);
    return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
  }

  protected PendingIntent getDeleteIntent(Context context, long[] userID) {
    Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
    intent.setAction("notification_cancelled");
    intent.putExtra("userID", userID);
    return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
  }
}
