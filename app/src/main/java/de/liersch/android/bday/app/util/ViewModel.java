package de.liersch.android.bday.app.util;

public class ViewModel {
  private String text;
  private String image;

  public ViewModel(String text, String image) {
    this. text = text;
    this.image = image;
  }

  public String getText() {
    return text;
  }

  public String getImage() {
    return image;
  }
}
