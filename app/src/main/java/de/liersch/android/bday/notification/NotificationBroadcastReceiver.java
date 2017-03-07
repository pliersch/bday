package de.liersch.android.bday.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.liersch.android.bday.db.ContactController;

public class NotificationBroadcastReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent)
  {
    String action = intent.getAction();
    if(action.equals("notification_cancelled"))
    {
      long userID = intent.getLongExtra("userID", -1);
      if (userID != -1) {
        setUserNotified(userID, context.getApplicationContext());
      } else {
        setUsersNotified(intent.getLongArrayExtra("userID"), context.getApplicationContext());
      }
    }
  }
  
  private void setUserNotified(long userID, Context applicationContext) {
    new ContactController(applicationContext).setNotified(userID, true);
  }
  
  private void setUsersNotified(long[] userIDs, Context applicationContext) {
    for (long userID:userIDs) {
      setUserNotified(userID, applicationContext.getApplicationContext());
    }
  }
}
