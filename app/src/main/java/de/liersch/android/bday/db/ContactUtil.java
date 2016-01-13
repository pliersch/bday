package de.liersch.android.bday.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.liersch.android.bday.beans.Contact;

public class ContactUtil {

  private static ContactUtil mInstance;

  public static ContactUtil getInstance() {
    if (mInstance == null) {
      mInstance = new ContactUtil();
    }
    return mInstance;
  }

  public List<Contact> sortContacts(List<Contact> contacts) {
    Collections.sort(contacts, new CustomComparator());
    Calendar today = Calendar.getInstance();
    final int monthToday = today.get(Calendar.MONTH) + 1;
    int i;
    for (i = 0; i < contacts.size(); i++) {
      Contact contact = contacts.get(i);
      final int monthBirthday = Integer.parseInt(contact.bday.substring(5, 7));
      if(monthBirthday >= monthToday) {
        if(monthBirthday > monthToday) {
          break;
        }
        final int dayToday = today.get(Calendar.DATE);
        final int dayBirthday = Integer.parseInt(contact.bday.substring(8, 10));
        if(dayBirthday >= dayToday) {
          System.out.println(i);
          break;
        }
      }
    }
    List<Contact> sorted = new ArrayList<Contact>();
    final List<Contact> contacts1 = contacts.subList(i, contacts.size());
    final List<Contact> contacts2 = contacts.subList(0, i);
    sorted.addAll(contacts1);
    sorted.addAll(contacts2);
    return sorted;
  }

  class CustomComparator implements Comparator<Contact> {
    @Override
    public int compare(Contact o1, Contact o2) {
      final String substring1 = o1.bday.substring(5, 10);
      final String substring2 = o2.bday.substring(5, 10);
      return substring1.compareTo(substring2);
    }
  }
}
