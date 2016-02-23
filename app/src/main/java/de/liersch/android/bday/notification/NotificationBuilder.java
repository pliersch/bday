package de.liersch.android.bday.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import de.liersch.android.bday.activity.MainActivity;

public abstract class NotificationBuilder {

  protected PendingIntent getOpenActivityIntent(Context applicationContext) {
    Intent intent = new Intent(applicationContext, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    return PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
  }
}
