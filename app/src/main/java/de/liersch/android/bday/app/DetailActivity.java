package de.liersch.android.bday.app;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

import de.liersch.android.bday.R;
import de.liersch.android.bday.db.ContactUtil;
import de.liersch.android.bday.ui.contacts.ContactListFragment;
import de.liersch.android.bday.util.CalendarUtil;

public class DetailActivity extends AppCompatActivity {

  private static final String EXTRA_IMAGE = "com.antonioleiva.materializeyourapp.extraImage";
  private CollapsingToolbarLayout collapsingToolbarLayout;

  @SuppressWarnings("ConstantConditions")
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initActivityTransitions();
    setContentView(R.layout.activity_detail);

    ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), EXTRA_IMAGE);
    supportPostponeEnterTransition();

    setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    final ImageView imageView = (ImageView) findViewById(R.id.image);
    String name = getIntent().getStringExtra(ContactListFragment.NAME);
    long contactId = Long.parseLong(getIntent().getStringExtra(ContactListFragment.CONTACT_ID));
    final ContentResolver contentResolver = getApplicationContext().getContentResolver();
    Bitmap bitmap = ContactUtil.getInstance().loadContactPhoto(contentResolver, contactId);
    if (bitmap != null) {
      imageView.setImageBitmap(bitmap);
    }

    collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
    collapsingToolbarLayout.setTitle(name);
    //collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

    TextView textView;

    textView = (TextView) findViewById(R.id.textViewBirthday);
    String bday = getIntent().getStringExtra(ContactListFragment.BDAY);
    String string = getResources().getString(R.string.birthday) + bday;
    textView.setText(string);

    textView = (TextView) findViewById(R.id.textViewAge);

    final Calendar calendar = CalendarUtil.getInstance().toCalendar(bday);
    final Calendar today = Calendar.getInstance();
    final int age = today.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
    string = getResources().getString(R.string.age) + age;
    textView.setText(string);

    textView = (TextView) findViewById(R.id.textViewDaysLeft);
    String daysLeft = getIntent().getStringExtra(ContactListFragment.DAYS_LEFT);
    string = getResources().getString(R.string.daysLeft) + daysLeft;
    textView.setText(string);
  }

  @Override public boolean dispatchTouchEvent(MotionEvent motionEvent) {
    try {
      return super.dispatchTouchEvent(motionEvent);
    } catch (NullPointerException e) {
      return false;
    }
  }

  private void initActivityTransitions() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Slide transition = new Slide();
      transition.excludeTarget(android.R.id.statusBarBackground, true);
      getWindow().setEnterTransition(transition);
      getWindow().setReturnTransition(transition);
    }
  }
}
