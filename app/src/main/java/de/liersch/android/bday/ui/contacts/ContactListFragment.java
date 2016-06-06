package de.liersch.android.bday.ui.contacts;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.liersch.android.bday.R;
import de.liersch.android.bday.beans.Contact;
import de.liersch.android.bday.db.ContactController;
import de.liersch.android.bday.util.CalendarUtil;

public class ContactListFragment extends ListFragment implements AdapterView.OnItemClickListener {

  private CalendarUtil mCalendarUtil;

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

    int[] fruitsImages = {R.drawable.robot, R.drawable.robot, R.drawable.robot, R.drawable.robot, R.drawable.robot};

    final List<Contact> contacts = new ContactController(getContext()).getSortedContacts(Calendar.getInstance());

    ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    for (Contact contact: contacts) {

      Calendar today = Calendar.getInstance();
      Calendar birthday = mCalendarUtil.toCalendar(contact.bday);
      birthday = mCalendarUtil.computeNextPossibleEvent(birthday, today);
      int daysLeftToBDay = mCalendarUtil.getDaysLeft(today, birthday);

      HashMap<String, String> hashMap = new HashMap<>();
      hashMap.put("name", contact.name);
      hashMap.put("daysLeft", Integer.toString(daysLeftToBDay));
      hashMap.put("image", fruitsImages[0] + "");
      arrayList.add(hashMap);
    }

    String[] from = {"name", "image", "daysLeft"};
    int[] to = {R.id.textViewName, R.id.imageView, R.id.textViewDays};
    ContactsAdapter simpleAdapter = new ContactsAdapter(getContext(), arrayList, R.layout.list_view_items, from, to);
    setListAdapter(simpleAdapter);
    getListView().setOnItemClickListener(this);
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT).show();
  }
}
