package de.liersch.android.bday.beans;

public class Contact {

  public int position;
  public String bday;
  public String name;
  public long userID;
  public boolean notified;

  public Contact(long userID, String name, String bday, boolean notified, int position) {
    this.userID = userID;
    this.name = name;
    this.bday = bday;
    this.notified = notified;
    this.position = position;
  }
}
