package de.liersch.android.bday.util;

import java.util.Calendar;

public class CalendarUtil {

  private static CalendarUtil mInstance;

  public static CalendarUtil getInstance() {
    if (mInstance == null) {
      mInstance = new CalendarUtil();
    }
    return mInstance;
  }

  public int getDaysLeft(Calendar from, Calendar to) {
    final int i = from.get(Calendar.DAY_OF_YEAR);
    final int j = to.get(Calendar.DAY_OF_YEAR);
    return j - i;
  }

  public int[] getMonthAndDaysLeft(Calendar from, Calendar to) {
    int diffYear = to.get(Calendar.YEAR) - from.get(Calendar.YEAR);
    int monthLeft = diffYear * 12 + to.get(Calendar.MONTH) - from.get(Calendar.MONTH) - 1;
    int maxDaysFromMonth = from.getActualMaximum(Calendar.DATE);
    int daysFrom =  maxDaysFromMonth - from.get(Calendar.DATE);
    int daysTo = to.get(Calendar.DATE);
    int daysLeft = daysFrom + daysTo;

    if(daysLeft > maxDaysFromMonth) {
      monthLeft += 1;
      daysLeft -= maxDaysFromMonth;
    }

    int[] result = new int[2];
    result[0] = monthLeft;
    result[1] = daysLeft;
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
    Calendar.getInstance().clear();
    Calendar calendar = Calendar.getInstance();
    calendar.set(year, month - 1, day, 0, 0);
    return calendar;
  }
}
