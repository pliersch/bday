package de.liersch.android.bday.widget.service;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class CalendarUtil {

  private static CalendarUtil mInstance;

  public static CalendarUtil getInstance() {
    if (mInstance == null) {
      mInstance = new CalendarUtil();
    }
    return mInstance;
  }

  public int getDaysLeft(Calendar from, Calendar to) {
    long diff = to.getTime().getTime() - from.getTime().getTime();
    long convert = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    return (int) convert;
  }

  public int[] getMonthAndDaysLeft(Calendar from, Calendar to) {

    int diffYear = to.get(Calendar.YEAR) - from.get(Calendar.YEAR);
    int diffMonth = diffYear * 12 + to.get(Calendar.MONTH) - from.get(Calendar.MONTH);

    int fullMonth = diffMonth > 0 ? diffMonth - 1 : 0;

    int numDaysThisMonth = from.getActualMaximum(Calendar.DATE);
    numDaysThisMonth -= from.get(Calendar.DATE);
    int numDaysThisBirthdayMonth = to.get(Calendar.DATE);
    int numDaysLeft = numDaysThisMonth + numDaysThisBirthdayMonth;

    int months = Math.ceil(numDaysLeft / 30.5);
    int days = numDaysLeft - 30;

    months += fullMonth;

    int[] result = new int[2];
    result[0] = months;
    result[1] = days;
    return result;
  }

  public Calendar computeNextPossibleEvent(Calendar event, Calendar from) {
    int year = from.get(Calendar.YEAR);
    if(from.get(Calendar.MONTH) > event.get(Calendar.MONTH)) {
      year++;
    } else if(from.get(Calendar.MONTH) == event.get(Calendar.MONTH)) {
      if(from.get(Calendar.DATE) > event.get(Calendar.DATE)) {
        year++;
      }
    }
    event.set(Calendar.YEAR, year);
    return event;
  }

  public Calendar toCalendar(String string) {
    String[] split = string.split("-");
    int year = Integer.parseInt(split[0]);
    int month = Integer.parseInt(split[1]);
    int day = Integer.parseInt(split[2]);
    Calendar calendar = Calendar.getInstance();
    calendar.set(year, month, day, 0, 0);
    return calendar;
  }

}
