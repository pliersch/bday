package de.liersch.android.bday.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import de.liersch.android.bday.R;
import de.liersch.android.bday.activity.MainActivity;
import de.liersch.android.bday.db.ContactUtil;

public abstract class NotificationBuilder {

  protected PendingIntent getOpenActivityIntent(Context applicationContext) {
    Intent intent = new Intent(applicationContext, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    return PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
  }

  protected Bitmap getIcon(long userID, Context applicationContext) {
    Bitmap bitmap = ContactUtil.getInstance().loadContactPhoto(applicationContext.getContentResolver(), userID);
    if (bitmap == null) {
      // TODO: why share icon not shown
      bitmap = BitmapFactory.decodeResource(applicationContext.getResources(), R.drawable.ic_menu_share);
    }
    return bitmap;
  }
}
