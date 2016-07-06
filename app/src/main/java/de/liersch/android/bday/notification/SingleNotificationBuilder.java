package de.liersch.android.bday.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import de.liersch.android.bday.R;
import de.liersch.android.bday.beans.Contact;

public class SingleNotificationBuilder extends NotificationBuilder {

  private static int NOTIFICATION_ID = 1;

  public void createNotification(Contact contact, int daysLeft, Context applicationContext) {
    final String name = contact.name;
    final long userID = contact.userID;
    System.out.println("SummaryNotificationBuilder#createSingleNotification for: " + name);



    PendingIntent pendingIntent = getOpenActivityIntent(applicationContext);
    String tickerText = applicationContext.getResources().getString(R.string.notification_single_content_title, name, daysLeft);
    Bitmap bitmap = getIcon(userID, applicationContext);

    NotificationCompat.Builder builder = new NotificationCompat.Builder(applicationContext);
    builder
        // TODO need api level 21
        //.setCategory(Notification.CATEGORY_EVENT)
        .setContentIntent(pendingIntent)
        .setTicker(tickerText)
        .setSmallIcon(R.drawable.ic_stautsbar_icon)
        .setLargeIcon(bitmap)
        .setContentTitle(name)
        .setContentText(tickerText)
        .setAutoCancel(true);

    Notification notification = builder.build();

//    if (Build.VERSION.SDK_INT >= 16) {
//      final RemoteViews bigContentView = new RemoteViews(applicationContext.getPackageName(), R.layout.notification_expanded);
//      if (bitmap != null) {
//        bigContentView.setImageViewBitmap(R.id.imageView, bitmap);
//      }
//      notification.bigContentView = bigContentView;
//    }

    // Use the NotificationManager to show the notification
    NotificationManager nm = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
    nm.notify(NOTIFICATION_ID, notification);
  }

  private PendingIntent getCallPhoneIntent(Context applicationContext, String phoneNumber) {
    Uri call = Uri.parse("tel:" + phoneNumber);
    Intent intent = new Intent(Intent.ACTION_CALL, call);
    // TODO: try "this" too
    //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    return PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
  }

}
