package de.liersch.android.bday.widget.service;

import java.util.Calendar;

public class CalendarUtil {

  private static CalendarUtil mInstance;

  public static CalendarUtil getInstance(){
    if (mInstance == null) {
      mInstance = new CalendarUtil();
    }
    return mInstance;
  }

  public int getDaysLeftToBDay(String date) {
    Calendar calendar = Calendar.getInstance();
    return 0;
  }

}
