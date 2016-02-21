package de.liersch.android.bday.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;

import de.liersch.android.bday.R;
import de.liersch.android.bday.db.ContactUtil;

public class SingleNotificationBuilder extends NotificationBuilder {

  public void createNotification(long userID, String name, int daysLeft, Context applicationContext) {
    System.out.println("SummaryNotificationBuilder#createSingleNotification for: " + name);

    PendingIntent pendingIntent = getPendingIntent(applicationContext);
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
        .setAutoCancel(true)
        .addAction(R.drawable.ic_call_black_24dp, "CALL", pendingIntent);

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
    int notificationId = (int) Calendar.getInstance().getTimeInMillis();
    nm.notify(notificationId, notification);
  }

  private Bitmap getIcon(long userID, Context applicationContext) {
    Bitmap bitmap = ContactUtil.getInstance().loadContactPhoto(applicationContext.getContentResolver(), userID);
    if (bitmap == null) {
      bitmap = BitmapFactory.decodeResource(applicationContext.getResources(), R.drawable.ic_menu_share);
    }
    return bitmap;
  }

}
