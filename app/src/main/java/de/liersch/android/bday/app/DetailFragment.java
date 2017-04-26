package de.liersch.android.bday.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Calendar;

import de.liersch.android.bday.R;
import de.liersch.android.bday.beans.Contact;
import de.liersch.android.bday.db.ContactController;
import de.liersch.android.bday.util.CalendarUtil;


public class DetailFragment extends Fragment implements View.OnClickListener {
  
  private Contact mContact;
  private View view;
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.fragment_detail_content, container, false);
    return view;
  }
  
  public void updateView(Contact contact) {
    mContact = contact;
    Switch firstAlert = (Switch) view.findViewById(R.id.switchFirst);
    firstAlert.setOnClickListener(this);
    firstAlert.setChecked(mContact.firstAlert);
    Switch secondAlert = (Switch) view.findViewById(R.id.switchSecond);
    secondAlert.setOnClickListener(this);
    secondAlert.setChecked(mContact.secondAlert);
    
    TextView textView;
    
    textView = (TextView) view.findViewById(R.id.textViewBirthday);
    textView.setText(mContact.bday);
    
    textView = (TextView) view.findViewById(R.id.textViewAge);
    
    final CalendarUtil calendarUtil = CalendarUtil.getInstance();
    final Calendar calendar = calendarUtil.toCalendar(mContact.bday);
    final Calendar today = Calendar.getInstance();
    // TODO: Provide age via ContactController
    final String age = String.valueOf(today.get(Calendar.YEAR) - calendar.get(Calendar.YEAR));
    textView.setText(age);
    
    textView = (TextView) view.findViewById(R.id.textViewDaysLeft);
    // TODO: Provide daysLeft via ContactController
    Calendar birthday = calendarUtil.toCalendar(mContact.bday);
    birthday = calendarUtil.computeNextPossibleEvent(birthday, today);
    String daysLeft = String.valueOf(calendarUtil.getDaysLeft(today, birthday));
    textView.setText(daysLeft);
  }
  
  @Override
  public void onClick(View view) {
    Switch checkBox = (Switch) view;
    boolean checked = checkBox.isChecked();
    final ContactController contactController = new ContactController(getActivity().getApplicationContext());
    switch (view.getId()) {
      case R.id.switchFirst:
        contactController.setEnabledFirstAlert(mContact.userID, checked);
        break;
      case R.id.switchSecond:
        contactController.setEnabledSecondAlert(mContact.userID, checked);
        break;
    }
  }
}
