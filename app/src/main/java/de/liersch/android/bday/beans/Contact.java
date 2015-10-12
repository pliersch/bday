package de.liersch.android.bday.beans;

public class Contact {

  private String bday;
  private String name;
  public Contact(String name, String bday) {
    this.name = name;
    this.bday = bday;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getBday() {
    return bday;
  }

  public void setBday(String bday) {
    this.bday = bday;
  }

}
