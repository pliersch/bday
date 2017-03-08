package de.liersch.android.bday.beans;

public class Contact {

  public String bday;
  public String name;
  public long userID;
  public boolean notified;
  public boolean firstAlert;
  public boolean secondAlert;

  public Contact(long userID, String name, String bday, boolean notified, boolean firstAlert, boolean secondAlert) {
    this.userID = userID;
    this.name = name;
    this.bday = bday;
    this.notified = notified;
    this.firstAlert = firstAlert;
    this.secondAlert = secondAlert;
    
  }
}
