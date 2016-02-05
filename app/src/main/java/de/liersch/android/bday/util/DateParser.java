package de.liersch.android.bday.util;

public class DateParser {

  public static final String ENGLISH_FORMAT = "\\d{4}-\\d{2}-\\d{2}";
  public static final String GERMAN_FORMAT = "\\d{2}\\.\\d{2}\\.\\d{4}";

  public int getMonth(String date) {
    int result = -1;
    if (date.matches(ENGLISH_FORMAT)) {
      result = parseSubstring(date, 5, 7);
    } else if (date.matches(GERMAN_FORMAT)) {
      result = parseSubstring(date, 3, 5);
    }
    return result;
  }

  public int getDay(String date) {
    int result = -1;
    if (date.matches(ENGLISH_FORMAT)) {
      result = parseSubstring(date, 8, 10);
    } else if (date.matches(GERMAN_FORMAT)) {
      result = parseSubstring(date, 0, 2);
    }
    return result;
  }

  private int parseSubstring(String string, int start, int end) {
    int result = -1;
    // TODO create logger and add if failed!
    try {
      result = Integer.parseInt(string.substring(start, end));
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    return result;
  }
}
