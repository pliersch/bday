package de.liersch.android.bday.widget.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AdapterViewFlipper;

public class CustomAdapterViewFlipper extends AdapterViewFlipper {

  public CustomAdapterViewFlipper(Context context) {
    super(context);
  }

  public CustomAdapterViewFlipper(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean performClick () {
    System.out.println("Foo");
    return true;
  }
}
