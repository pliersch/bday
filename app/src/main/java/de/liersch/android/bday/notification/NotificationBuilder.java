package de.liersch.android.bday.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.util.Date;

import de.liersch.android.bday.R;
import de.liersch.android.bday.app.MainActivity;

public class NotificationBuilder {

  public void createNotification(String name, int daysLeft, Context mApplicationContext) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(mApplicationContext);

    //Create Intent to launch this Activity again if the notification is clicked.
    Intent i = new Intent(mApplicationContext, MainActivity.class);
    i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent intent = PendingIntent.getActivity(mApplicationContext, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
    builder.setContentIntent(intent);

    // Sets the ticker text
    builder.setTicker(mApplicationContext.getResources().getString(R.string.custom_notification));

    // Sets the small icon for the ticker
    builder.setSmallIcon(R.drawable.ic_stat_custom);

    // Cancel the notification when clicked
    builder.setAutoCancel(true);

    // Build the notification
    Notification notification = builder.build();

    // Inflate the notification layout as RemoteViews
    RemoteViews contentView = new RemoteViews(mApplicationContext.getPackageName(), R.layout.notification);

    // Set text on a TextView in the RemoteViews programmatically.
    final String time = DateFormat.getTimeInstance().format(new Date());
    final String text = mApplicationContext.getResources().getString(R.string.collapsed, time);
    contentView.setTextViewText(R.id.textView, text);

        /* Workaround: Need to set the content view here directly on the notification.
         * NotificationCompatBuilder contains a bug that prevents this from working on platform
         * versions HoneyComb.
         * See https://code.google.com/p/android/issues/detail?id=30495
         */
    notification.contentView = contentView;

    // Add a big content view to the notification if supported.
    // Support for expanded notifications was added in API level 16.
    // (The normal contentView is shown when the notification is collapsed, when expanded the
    // big content view set here is displayed.)
    if (Build.VERSION.SDK_INT >= 16) {
      // Inflate and set the layout for the expanded notification view
      notification.bigContentView = new RemoteViews(mApplicationContext.getPackageName(), R.layout.notification_expanded);
    }

    // Use the NotificationManager to show the notification
    NotificationManager nm = (NotificationManager) mApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
    nm.notify(0, notification);
  }
}
