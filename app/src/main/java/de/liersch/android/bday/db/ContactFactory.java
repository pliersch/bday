package de.liersch.android.bday.db;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import de.liersch.android.bday.beans.Contact;
import de.liersch.android.bday.util.DateParser;

public class ContactFactory {
  private static ContactFactory ourInstance = new ContactFactory();

  public static ContactFactory getInstance() {
    return ourInstance;
  }

  private ContactFactory() {
  }

  public List<Contact> createSystemContacts(Context applicationContext) {
    String contactID;
    String bday;
    List<Contact> contacts = new ArrayList<Contact>();
    Cursor cursorBDay = SystemContactsQuery.getInstance().queryBirthdayContacts(applicationContext);
    Cursor cursorContacts = SystemContactsQuery.getInstance().queryVisibleContacts(applicationContext);
    while (cursorContacts.moveToNext()) {
      contactID = cursorContacts.getString(0);
      long l = Long.parseLong(contactID);
      cursorBDay.moveToPosition(-1);
      while (cursorBDay.moveToNext()) {
        if (cursorBDay.getString(0).equals(contactID)) {
          bday = cursorBDay.getString(2);
          // TODO: ignoring german format (used by skype contacts)
          if(bday.matches(DateParser.ENGLISH_FORMAT)) {
            String name = cursorContacts.getString(1);
            // TODO: implement boolean notified (current hard coded with false)
            Contact contact = new Contact(l, name, bday, false);
            contacts.add(contact);
          }
        }
      }
    }
    cursorContacts.close();
    cursorBDay.close();
    return contacts;
  }

//  public List<Contact> createBirthdayContacts(Context applicationContext) {
//    Cursor cursorBirthday = DatabaseManager.getInstance(applicationContext).getAllContacts();
//    List<Contact> contacts = new ArrayList<Contact>();
//    while (cursorBirthday.moveToNext()) {
//      contacts.add(createContact(cursorBirthday));
//    }
//    cursorBirthday.close();
//    return contacts;
//  }

  // TODO: same method exists in ContactController
//  private Contact createContact(Cursor cursorBirthday) {
//    return new Contact(
//        cursorBirthday.getLong(0),
//        cursorBirthday.getString(1),
//        cursorBirthday.getString(2),
//        cursorBirthday.getInt(3) != 0
//    );
//  }
}
