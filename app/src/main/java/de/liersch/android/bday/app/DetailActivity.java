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
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

import de.liersch.android.bday.R;
import de.liersch.android.bday.beans.Contact;
import de.liersch.android.bday.db.ContactController;
import de.liersch.android.bday.db.ContactUtil;
import de.liersch.android.bday.ui.contacts.ContactListFragment;
import de.liersch.android.bday.util.CalendarUtil;

public class DetailActivity extends AppCompatActivity {

  private static final String EXTRA_IMAGE = "com.antonioleiva.materializeyourapp.extraImage";
  private Contact mContact;

  @Override
  protected void onResume() {
    super.onResume();
    updateView(true);
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initActivityTransitions();
    setContentView(R.layout.activity_detail);

    long userID = Long.parseLong(getIntent().getStringExtra(ContactListFragment.CONTACT_ID));
    mContact = new ContactController(getApplicationContext()).getContact(userID);

    ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), EXTRA_IMAGE);
    supportPostponeEnterTransition();

    setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
    collapsingToolbarLayout.setTitle(mContact.name);
    //collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

    updateView(false);
  }

  private void updateView(boolean resume) {
    if (resume) {
      mContact = new ContactController(getApplicationContext()).getContact(mContact.userID);
    }
    final ImageView imageView = (ImageView) findViewById(R.id.image);
    final ContentResolver contentResolver = getApplicationContext().getContentResolver();
    Bitmap bitmap = ContactUtil.getInstance().loadContactPhoto(contentResolver, mContact.userID);
    if (bitmap != null) {
      imageView.setImageBitmap(bitmap);
      imageView.setPadding(0,0,0,0);
    }

    TextView textView;

    textView = (TextView) findViewById(R.id.textViewBirthday);
    String string = getResources().getString(R.string.birthday) + " " + mContact.bday;
    textView.setText(string);

    textView = (TextView) findViewById(R.id.textViewAge);

    final CalendarUtil calendarUtil = CalendarUtil.getInstance();
    final Calendar calendar = calendarUtil.toCalendar(mContact.bday);
    final Calendar today = Calendar.getInstance();
    // TODO: Provide age via ContactController
    final int age = today.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
    string = getResources().getString(R.string.age) + age;
    textView.setText(string);

    textView = (TextView) findViewById(R.id.textViewDaysLeft);
    // TODO: Provide daysLeft via ContactController
    Calendar birthday = calendarUtil.toCalendar(mContact.bday);
    birthday = calendarUtil.computeNextPossibleEvent(birthday, today);
    int daysLeft = calendarUtil.getDaysLeft(today, birthday);
    string = getResources().getString(R.string.daysLeft) + daysLeft;
    textView.setText(string);
  }

  private void initActivityTransitions() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Slide transition = new Slide();
      transition.excludeTarget(android.R.id.statusBarBackground, true);
      getWindow().setEnterTransition(transition);
      getWindow().setReturnTransition(transition);
    }
  }

  public void onCheckboxClicked(View view) {
    CheckBox checkBox = (CheckBox) view;
    boolean checked = checkBox.isChecked();
    switch (view.getId()) {
      case R.id.checkBoxFirst:
    }

  }
}
