package de.liersch.android.bday.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DateParserTest {

  private DateParser mDateParser;

  @Before
  public void setUp() throws Exception {
    mDateParser = new DateParser();
  }

  //----------------------------------
  //
  // getMonth
  //
  //----------------------------------

  @Test
  public void test_getMonth_german() throws Exception {
    assertEquals(10, mDateParser.getMonth("01.10.2020"));
    assertEquals(-1, mDateParser.getMonth("2020.10.01"));
    assertEquals(-1, mDateParser.getMonth("2020810801"));
  }

  @Test
  public void test_getMonth_english() throws Exception {
    assertEquals(10, mDateParser.getMonth("2020-10-01"));
    assertEquals(-1, mDateParser.getMonth("01-10-2020"));
    assertEquals(-1, mDateParser.getMonth("2020810801"));
  }

  //----------------------------------
  //
  // getDay
  //
  //----------------------------------

  @Test
  public void test_getDay_german() throws Exception {
    assertEquals(1,  mDateParser.getDay("01.10.2020"));
    assertEquals(-1, mDateParser.getDay("2020.10.01"));
    assertEquals(-1, mDateParser.getDay("2020810801"));
  }
  @Test
  public void test_getDay_english() throws Exception {
    assertEquals(1,  mDateParser.getDay("2020-10-01"));
    assertEquals(-1, mDateParser.getDay("01-10-2020"));
    assertEquals(-1, mDateParser.getDay("2020810801"));
  }
}
