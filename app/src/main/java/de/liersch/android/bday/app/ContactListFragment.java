package de.liersch.android.bday.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.liersch.android.bday.R;
import de.liersch.android.bday.app.util.ContactsAdapter;
import de.liersch.android.bday.beans.Contact;
import de.liersch.android.bday.db.ContactController;
import de.liersch.android.bday.util.CalendarUtil;

public class ContactListFragment extends ListFragment implements AdapterView.OnItemClickListener {
  
  interface OnContactSelectedListener {
    void onContactSelected(HashMap<String, String> hashMap);
  }
  
  private CalendarUtil mCalendarUtil;
  private ArrayList<HashMap<String, String>> mContactList;
  private OnContactSelectedListener mListener;
  
  public static final String NAME = "de.liersch.android.name";
  public static final String CONTACT_ID = "de.liersch.android.contactid";
  public static final String BDAY = "de.liersch.android.bday";
  public static final String DAYS_LEFT = "de.liersch.android.daysleft";
  
  private static final String POSITION = "pos";
  
  private boolean mIsDualPane;
  private int mPosition = -1;
  private TextView mCurrentTextView;
  
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    Activity activity = (Activity) context;
    try {
      mListener = (OnContactSelectedListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString() + " must implement OnContactSelectedListener");
    }
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    
    // If activity recreated (such as from screen rotate), restore
    // the previous article selection set by onSaveInstanceState().
    // This is primarily necessary when in the two-pane layout.
    if (savedInstanceState != null) {
      mPosition = savedInstanceState.getInt(POSITION);
    }
    mCalendarUtil = CalendarUtil.getInstance();
    return inflater.inflate(R.layout.fragment_contact_list_layout, container, false);
  }
  
  @Override
  public void onResume() {
    super.onResume();
    if (mIsDualPane && mPosition >= 0) {
      
      final View childAt = getListView().getChildAt(mPosition);
      //getListView().getChildAt(mPosition).setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_light));
      //getListView().setItemChecked(mPosition, true);
    }
    updateView();
  }
  
  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(POSITION, mPosition);
  }
  
  public void setDualPane(boolean isDualPane){
    mIsDualPane = isDualPane;
  }
  
  // TODO refactor simple view vs image view (portrait vs landscape )
  
  private void updateView() {
    final List<Contact> contacts = new ContactController(getContext()).getSortedContacts();
    
    mContactList = new ArrayList<>();
    
    
    for (Contact contact : contacts) {
      
      Calendar today = Calendar.getInstance();
      Calendar birthday = mCalendarUtil.toCalendar(contact.bday);
      birthday = mCalendarUtil.computeNextPossibleEvent(birthday, today);
      final int daysLeft = mCalendarUtil.getDaysLeft(today, birthday);
      String msg;
      final Resources resources = getActivity().getApplicationContext().getResources();
      if (daysLeft == 0) {
        msg = resources.getString(R.string.today);
      } else if (daysLeft == 1) {
        msg = resources.getString(R.string.day, "1");
      } else {
        msg = resources.getString(R.string.days, Integer.toString(daysLeft));
      }
      
      HashMap<String, String> hashMap = new HashMap<>();
      HashMap<String, String> simpleHashMap = new HashMap<>();
      ArrayList<Object> simpleContactList = new ArrayList<>();
      
      hashMap.put(NAME, contact.name);
      hashMap.put(DAYS_LEFT, msg);
      hashMap.put(CONTACT_ID, Long.toString(contact.userID));
      hashMap.put(BDAY, contact.bday);
      mContactList.add(hashMap);
      if (mIsDualPane) {
        simpleHashMap.put(NAME, contact.name);
        simpleContactList.add(simpleHashMap);
      }
    }
    ContactsAdapter simpleAdapter;
    if (mIsDualPane) {
      String[] from = {NAME};
      int[] to = {R.id.textViewName};
      simpleAdapter = new ContactsAdapter(getContext(), mContactList, R.layout.listview_contacts_item, from, to);
    } else {
      String[] from = {NAME, DAYS_LEFT};
      int[] to = {R.id.textViewName, R.id.textViewDays};
      simpleAdapter = new ContactsAdapter(getContext(), mContactList, R.layout.listview_contacts_item, from, to);
    }
    setListAdapter(simpleAdapter);
    getListView().setOnItemClickListener(this);
  }
  
  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    mPosition = position;
    view.setSelected(true);
    final HashMap<String, String> contactData = mContactList.get(position);
    if (mIsDualPane) {
//      if (mCurrentTextView != null) {
//        mCurrentTextView.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
//      }
//      final TextView textView = (TextView) view.findViewById(R.id.textViewName);
//      mCurrentTextView = textView;
//      textView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
      mListener.onContactSelected(contactData);
    } else {
      final Intent intent = new Intent(this.getActivity(), DetailActivity.class);
      intent.putExtra(CONTACT_ID, contactData.get(CONTACT_ID));
      startActivity(intent);
    }
  }
}
