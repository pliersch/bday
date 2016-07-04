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

    // During startup, check if there are arguments passed to the fragment.
    // onStart is a good place to do this because the layout has already been
    // applied to the fragment at this point so we can safely call the method
    // below that sets the article text.
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
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    final List<Contact> contacts = new ContactController(getContext()).getSortedContacts(Calendar.getInstance());

    mContactList = new ArrayList<>();
    for (Contact contact: contacts) {

      Calendar today = Calendar.getInstance();
      today = mCalendarUtil.getFullDayCalendar(today);
      Calendar birthday = mCalendarUtil.toCalendar(contact.bday);
      birthday = mCalendarUtil.computeNextPossibleEvent(birthday, today);
      int daysLeftToBDay = mCalendarUtil.getDaysLeft(today, birthday);

      HashMap<String, String> hashMap = new HashMap<>();
      hashMap.put(NAME, contact.name);
      hashMap.put(DAYS_LEFT, Integer.toString(daysLeftToBDay));
      hashMap.put("image", Integer.toString(R.drawable.ic_account_circle_white_48dp));
      hashMap.put(CONTACT_ID, Long.toString(contact.userID));
      hashMap.put(BDAY, contact.bday);
      mContactList.add(hashMap);
    }

    String[] from = {NAME, "image", DAYS_LEFT};
    int[] to = {R.id.textViewName, R.id.imageView, R.id.textViewDays};
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
