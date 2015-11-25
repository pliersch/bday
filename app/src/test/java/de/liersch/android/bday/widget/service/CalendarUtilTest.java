package de.liersch.android.bday.widget.service;

import org.junit.Before;
import org.junit.Test;

import java.lang.Exception;
import java.util.Calendar;

import static org.junit.Assert.*;

public class CalendarUtilTest {

  private CalendarUtil mUtil;

  @Before
  public void setUp() throws Exception {
    mUtil = CalendarUtil.getInstance();
  }

  //----------------------------------
  //
  // toCalendar
  //
  //----------------------------------

  @Test
  public void test_toCalendar() throws Exception {
    Calendar result = mUtil.toCalendar("2000-01-01");
    assertEquals(2000, result.get(Calendar.YEAR));
    assertEquals(0, result.get(Calendar.MONTH));
    assertEquals(1, result.get(Calendar.DATE));
  }

  //----------------------------------
  //
  // computeNextPossibleEvent
  //
  //----------------------------------

  @Test
  public void test_computeNextPossibleEvent_next_month() throws Exception {
    Calendar event = getCalendar(1977, 3, 1);
    Calendar from = getCalendar(2000, 2, 1);
    Calendar result = mUtil.computeNextPossibleEvent(event, from);
    assertEquals(2000, result.get(Calendar.YEAR));
    assertEquals(3, result.get(Calendar.MONTH));
    assertEquals(1, result.get(Calendar.DATE));
  }

  @Test
  public void test_computeNextPossibleEvent_same_month_day_before() throws Exception {
    Calendar event = getCalendar(1977, 3, 2);
    Calendar from = getCalendar(2000, 3, 1);
    Calendar result = mUtil.computeNextPossibleEvent(event, from);
    assertEquals(2000, result.get(Calendar.YEAR));
    assertEquals(3, result.get(Calendar.MONTH));
    assertEquals(2, result.get(Calendar.DATE));
  }

  @Test
  public void test_computeNextPossibleEvent_same_month_day_after() throws Exception {
    Calendar event = getCalendar(1977, 3, 1);
    Calendar from = getCalendar(2000, 3, 2);
    Calendar result = mUtil.computeNextPossibleEvent(event, from);
    assertEquals(2001, result.get(Calendar.YEAR));
    assertEquals(3, result.get(Calendar.MONTH));
    assertEquals(1, result.get(Calendar.DATE));
  }

  @Test
  public void test_computeNextPossibleEvent_same_month_same_day() throws Exception {
    Calendar event = getCalendar(1977, 3, 1);
    Calendar from = getCalendar(2000, 3, 1);
    Calendar result = mUtil.computeNextPossibleEvent(event, from);
    assertEquals(2000, result.get(Calendar.YEAR));
    assertEquals(3, result.get(Calendar.MONTH));
    assertEquals(1, result.get(Calendar.DATE));
  }

  //----------------------------------
  //
  // getDaysLeft
  //
  //----------------------------------

  @Test
  public void test_getDaysLeft_first_to_last_day_of_year() throws Exception {
    Calendar from = getCalendar(2015, 0, 1);
    Calendar to = getCalendar(2015, 11, 31);
    int daysLeft = mUtil.getDaysLeft(from, to);
    int days = to.get(Calendar.DAY_OF_YEAR);
    assertEquals(days - 1, daysLeft);
  }

  @Test
  public void test_getDaysLeft_month_to_next_month() throws Exception {
    Calendar from = getCalendar(2015, 0, 1);
    Calendar to = getCalendar(2015, 1, 1);
    int daysLeft = mUtil.getDaysLeft(from, to);
    assertEquals(31, daysLeft);
  }

  //----------------------------------
  //
  // getMonthAndDaysLeft
  //
  //----------------------------------

  @Test
  public void test_getMonthAndDaysLeft_first_to_last_day_of_year() throws Exception {
    Calendar from = getCalendar(2015, 0, 1);
    Calendar to = getCalendar(2015, 11, 31);
    int[] result = mUtil.getMonthAndDaysLeft(from, to);
    assertEquals(11, result[0]);
    assertEquals(30, result[1]);
  }

  @Test
  public void test_getMonthAndDaysLeft_with_february() throws Exception {
    Calendar from = getCalendar(2015, 1, 15);
    Calendar to = getCalendar(2015, 2, 15);
    int[] result = mUtil.getMonthAndDaysLeft(from, to);
    assertEquals(0, result[0]);
    assertEquals(28, result[1]);
  }

  //----------------------------------
  //
  // HELPER
  //
  //----------------------------------

  private Calendar getCalendar(int year, int month, int day) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(year, month, day);
    return calendar;
  }

}
