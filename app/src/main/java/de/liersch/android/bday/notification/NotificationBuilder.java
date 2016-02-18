package de.liersch.android.bday.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import de.liersch.android.bday.R;
import de.liersch.android.bday.activity.MainActivity;
import de.liersch.android.bday.db.ContactUtil;

public class NotificationBuilder {

  public void createNotification(long userID, String name, int daysLeft, Context applicationContext) {
    System.out.println("NotificationBuilder#createNotification for: " + name);
    NotificationCompat.Builder builder = new NotificationCompat.Builder(applicationContext);

    Intent intent = new Intent(applicationContext, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    String tickerText = buildTickerText(name, daysLeft, applicationContext);


    builder
        .setContentIntent(pendingIntent)
        .setTicker(tickerText)
        .setSmallIcon(R.drawable.ic_stautsbar_icon)
        .setLargeIcon(BitmapFactory.decodeResource(applicationContext.getResources(),R.drawable.ic_menu_share))
        .setAutoCancel(true);

    Notification notification = builder.build();

    // Inflate the notification layout as RemoteViews
    RemoteViews contentView = new RemoteViews(applicationContext.getPackageName(), R.layout.notification);

    // Set text on a TextView in the RemoteViews programmatically.
    final String time = DateFormat.getTimeInstance().format(new Date());
    //final String text = applicationContext.getResources().getString(R.string.collapsed, time);
    contentView.setTextViewText(R.id.textView, tickerText);
    final Bitmap bitmap = ContactUtil.getInstance().loadContactPhoto(applicationContext.getContentResolver(), userID);
    if (bitmap != null) {
      contentView.setImageViewBitmap(R.id.imageView, bitmap);
    }

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
      final RemoteViews bigContentView = new RemoteViews(applicationContext.getPackageName(), R.layout.notification_expanded);
      if (bitmap != null) {
        bigContentView.setImageViewBitmap(R.id.imageView, bitmap);
      }
      notification.bigContentView = bigContentView;
    }

    // Use the NotificationManager to show the notification
    NotificationManager nm = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
    int notificationId = (int) Calendar.getInstance().getTimeInMillis();
    nm.notify(notificationId, notification);
  }

  private String buildTickerText(String name, int daysLeft, Context applicationContext) {
    String tickerText = name + applicationContext.getResources().getString(R.string.custom_notification, daysLeft);
    tickerText += "\n";
    return tickerText;
  }
}
