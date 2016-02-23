package de.liersch.android.bday.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Calendar;

import de.liersch.android.bday.R;
import de.liersch.android.bday.db.ContactUtil;
import de.liersch.android.bday.db.SystemContactsQuery;

public class SingleNotificationBuilder extends NotificationBuilder {

  public void createNotification(long userID, String name, int daysLeft, Context applicationContext) {
    System.out.println("SummaryNotificationBuilder#createSingleNotification for: " + name);

    ArrayList<String> numbers = readPhoneNumbers(applicationContext, userID);
    PendingIntent phoneMainIntent = null;
    PendingIntent phoneMobileIntent = null;
    if (numbers.get(0) != null) {
      phoneMainIntent = getCallPhoneIntent(applicationContext, numbers.get(0));
    }
    if (numbers.get(1) != null) {
      phoneMobileIntent = getCallPhoneIntent(applicationContext, numbers.get(1));
    }

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

    if (phoneMainIntent != null) {
      builder.addAction(R.drawable.ic_call_black_24dp, "CALL", phoneMainIntent);
    }

    if (phoneMobileIntent != null) {
      builder.addAction(R.drawable.ic_call_black_24dp, "CALL", phoneMobileIntent);
    }

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

  private ArrayList<String> readPhoneNumbers(Context applicationContext, long userID) {
    Cursor c = SystemContactsQuery.getInstance().queryPhoneNumber(applicationContext, userID);
    SystemContactsQuery.getInstance().queryPhoneNumbers(applicationContext);
    final ArrayList<String> numbers = new ArrayList<>(2);
    boolean mainFounded = false;
    boolean mobileFounded = false;
    if (c.getCount() > 0) {
      while (c.moveToNext()) {
        final int phoneType = Integer.parseInt(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)));
        if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_MAIN && !mainFounded) {
          mainFounded = true;
          numbers.add(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
        }
        if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE && !mobileFounded) {
          mobileFounded = true;
          numbers.add(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
        }
      }
    }
    c.close();
    return numbers;
  }

  private Bitmap getIcon(long userID, Context applicationContext) {
    Bitmap bitmap = ContactUtil.getInstance().loadContactPhoto(applicationContext.getContentResolver(), userID);
    if (bitmap == null) {
      // TODO: why share icon not shown
      bitmap = BitmapFactory.decodeResource(applicationContext.getResources(), R.drawable.ic_menu_share);
    }
    return bitmap;
  }

  private PendingIntent getCallPhoneIntent(Context applicationContext, String phoneNumber) {
    Uri call = Uri.parse("tel:" + phoneNumber);
    Intent intent = new Intent(Intent.ACTION_CALL, call);
    // TODO: try "this" too
    //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    return PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
  }

}
