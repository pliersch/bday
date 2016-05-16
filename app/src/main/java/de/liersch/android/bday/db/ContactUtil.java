package de.liersch.android.bday.db;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.liersch.android.bday.beans.Contact;
import de.liersch.android.bday.util.DateParser;

public class ContactUtil {

  private static ContactUtil mInstance;

  public static ContactUtil getInstance() {
    if (mInstance == null) {
      mInstance = new ContactUtil();
    }
    return mInstance;
  }

  public Bitmap loadContactPhoto(ContentResolver cr, long  id) {
    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri, true);
    if (input == null) {
      return null;
    }
    return BitmapFactory.decodeStream(input);
  }

  public List<Contact> sortContacts(List<Contact> contacts, Calendar date) {
    Collections.sort(contacts, new CustomComparator());
    final DateParser dateParser = new DateParser();
    final int monthToday = date.get(Calendar.MONTH) + 1;

    int i;
    for (i = 0; i < contacts.size(); i++) {
      final String bday = contacts.get(i).bday;
      final int monthBirthday = dateParser.getMonth(bday);

      if(monthBirthday >= monthToday) {
        if(monthBirthday > monthToday) {
          break;
        }
        final int dayToday = date.get(Calendar.DATE);
        final int dayBirthday = dateParser.getDay(bday);
        if(dayBirthday >= dayToday) {
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

  public List<Contact> getNextBirthdayContacts(List<Contact> contacts, Calendar date) {
    final List<Contact> sortedContacts = sortContacts(contacts, date);
    List<Contact> next = new ArrayList<Contact>();
    final Contact firstContact = sortedContacts.remove(0);
    next.add(firstContact);
    final DateParser dateParser = new DateParser();
    final int monthFirst = dateParser.getMonth(firstContact.bday);
    final int dayFirst = dateParser.getDay(firstContact.bday);

    for (Contact contact : sortedContacts) {
      final int monthCurrent = dateParser.getMonth(contact.bday);
      final int dayCurrent = dateParser.getDay(contact.bday);
      if (monthFirst == monthCurrent && dayFirst == dayCurrent) {
        next.add(contact);
      } else {
        break;
      }
    }
    return next;
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
