package de.liersch.android.bday.ui.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.liersch.android.bday.R;
import de.liersch.android.bday.app.DetailActivity;
import de.liersch.android.bday.beans.Contact;
import de.liersch.android.bday.db.ContactController;
import de.liersch.android.bday.util.CalendarUtil;

public class ContactListFragment extends ListFragment implements AdapterView.OnItemClickListener {

  private CalendarUtil mCalendarUtil;
  private ArrayList<HashMap<String, String>> mContactList;

  public static final String NAME = "de.liersch.android.name";
  public static final String CONTACT_ID = "de.liersch.android.contactid";
  public static final String BDAY = "de.liersch.android.bday";
  public static final String DAYS_LEFT = "de.liersch.android.daysleft";

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    // If activity recreated (such as from screen rotate), restore
    // the previous article selection set by onSaveInstanceState().
    // This is primarily necessary when in the two-pane layout.
    if (savedInstanceState != null) {
      // mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
    }
    mCalendarUtil = CalendarUtil.getInstance();
    return inflater.inflate(R.layout.fragment_contact_list_layout, container, false);
  }

  @Override
  public void onStart() {
    super.onStart();
    Bundle args = getArguments();
    if (args != null) {
      // Set article based on argument passed in
      //updateArticleView(args.getInt(ARG_POSITION));
    } else {
      // Set article based on saved instance state defined during onCreateView
      //updateArticleView(mCurrentPosition);
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    updateView();
  }

  private void updateView() {
    final List<Contact> contacts = new ContactController(getContext()).getSortedContacts(Calendar.getInstance());

    mContactList = new ArrayList<>();
    for (Contact contact: contacts) {

      Calendar today = Calendar.getInstance();
      Calendar birthday = mCalendarUtil.toCalendar(contact.bday);
      birthday = mCalendarUtil.computeNextPossibleEvent(birthday, today);
      final int daysLeft = mCalendarUtil.getDaysLeft(today, birthday);
      String msg;
      if (daysLeft == 0) {
        msg = "today";
      } else if (daysLeft == 1) {
        msg = Integer.toString(daysLeft) + " day";
      } else {
        msg = Integer.toString(daysLeft) + " days";
      }

      HashMap<String, String> hashMap = new HashMap<>();
      hashMap.put(NAME, contact.name);
      hashMap.put(DAYS_LEFT, msg);
      hashMap.put(CONTACT_ID, Long.toString(contact.userID));
      hashMap.put(BDAY, contact.bday);
      mContactList.add(hashMap);
    }

    String[] from = {NAME, DAYS_LEFT};
    int[] to = {R.id.textViewName, R.id.textViewDays};
    ContactsAdapter simpleAdapter = new ContactsAdapter(getContext(), mContactList, R.layout.listview_contacts_item, from, to);
    setListAdapter(simpleAdapter);
    getListView().setOnItemClickListener(this);
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    final Intent intent = new Intent(this.getActivity(), DetailActivity.class);
    final HashMap<String, String> contactData = mContactList.get(position);
    intent.putExtra(NAME, contactData.get(NAME));
    intent.putExtra(CONTACT_ID, contactData.get(CONTACT_ID));
    intent.putExtra(DAYS_LEFT, contactData.get(DAYS_LEFT));
    intent.putExtra(BDAY, contactData.get(BDAY));
    startActivity(intent);
  }
}
