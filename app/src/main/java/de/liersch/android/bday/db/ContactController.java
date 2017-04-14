package de.liersch.android.bday.db;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.liersch.android.bday.beans.Contact;

public class ContactController {
  
  private final DatabaseManager mDatabaseManager;
  private final Context mApplicationContext;
  private final ContactUtil mContactUtil;
  
  public ContactController(Context applicationContext) {
    mApplicationContext = applicationContext;
    mDatabaseManager = DatabaseManager.getInstance(applicationContext);
    mContactUtil = ContactUtil.getInstance();
  }
  
  public void refresh() {
    final List<Contact> birthdayContacts = getContacts();
    
    List<Contact> systemContacts = ContactFactory.getInstance().createSystemContacts(mApplicationContext);
    
    // first time using the app
    if (birthdayContacts.size() == 0) {
      writeAllContactsWithAlerts(systemContacts);
    } else if (birthdayContacts.size() < systemContacts.size()) {
      addContact(birthdayContacts, systemContacts);
    } else if (birthdayContacts.size() > systemContacts.size()) {
      deleteContact(birthdayContacts, systemContacts);
    } else {
      updateContact(birthdayContacts, systemContacts);
    }
  }
  
  public List<Contact> getContacts() {
    final Cursor cursor = mDatabaseManager.getAllContacts();
    List<Contact> contacts = new ArrayList<Contact>();
    while (cursor.moveToNext()) {
      contacts.add(createContact(cursor));
    }
    cursor.close();
    return contacts;
  }
  
  public Contact getContact(long userId) {
    final Cursor cursor = mDatabaseManager.getContact(userId);
    cursor.moveToNext();
    final Contact contact = createContact(cursor);
    cursor.close();
    return contact;
  }
  
  public List<Contact> getSortedContacts(Calendar date) {
    return mContactUtil.sortContacts(getContacts(), date);
  }
  
  public List<Contact> getNextBirthdayContacts(Calendar date) {
    return mContactUtil.getNextBirthdayContacts(getContacts(), date);
  }
  
  public void setNotified(long userId, boolean isNotified) {
    Contact contact = getContact(userId);
    contact.notified = isNotified;
    mDatabaseManager.updateContact(contact);
  }
  
  public void setEnabledFirstAlert(long userId, boolean isAlertEnable) {
    Contact contact = getContact(userId);
    contact.firstAlert = isAlertEnable;
    mDatabaseManager.updateContact(contact);
  }
  
  public void setEnabledSecondAlert(long userId, boolean isAlertEnable) {
    Contact contact = getContact(userId);
    contact.secondAlert = isAlertEnable;
    mDatabaseManager.updateContact(contact);
  }
  
  private void addContact(List<Contact> birthdayContacts, List<Contact> systemContacts) {
    // TODO: update the position is complicated. at this time all contacts will replaced
    for (Contact systemContact : systemContacts) {
      boolean exist = false;
      for (Contact birthdayContact : birthdayContacts) {
        if (systemContact.userID == birthdayContact.userID) {
          exist = true;
          systemContact.firstAlert = birthdayContact.firstAlert;
          systemContact.secondAlert = birthdayContact.secondAlert;
          systemContact.notified = birthdayContact.notified;
          break;
        }
      }
      if (systemContact.name.equals("Baz")) {
        System.out.println("foo");
      }
      if (!exist) {
        systemContact.firstAlert = true;
        systemContact.secondAlert = true;
      }
    }
    writeAllContacts(systemContacts);
//    for (Contact systemContact : systemContacts) {
//      boolean found = false;
//      for (Contact birthdayContact : birthdayContacts) {
//        if (systemContact.userID == birthdayContact.userID) {
//          found = true;
//          break;
//        }
//      }
//      if (!found) {
//        mDatabaseManager.addContact(systemContact);
//      }
//    }
  }
  
  private void updateContact(List<Contact> birthdayContacts, List<Contact> systemContacts) {
    for (Contact systemContact : systemContacts) {
      for (Contact birthdayContact : birthdayContacts) {
        if (systemContact.userID == birthdayContact.userID) {
          systemContact.firstAlert = birthdayContact.firstAlert;
          systemContact.secondAlert = birthdayContact.secondAlert;
          systemContact.notified = birthdayContact.notified;
          ContactCompare result = compare(systemContact, birthdayContact);
          if (result != ContactCompare.EQUALS) {
            if (result == ContactCompare.BDAY) {
              // TODO: update the position is complicated. at this time all contacts will replaced
              writeAllContacts(systemContacts);
            } else {
              mDatabaseManager.updateContact(systemContact);
            }
          }
        }
      }
    }
  }
  
  private void deleteContact(List<Contact> birthdayContacts, List<Contact> systemContacts) {
    for (Contact birthdayContact : birthdayContacts) {
      boolean found = false;
      for (Contact systemContact : systemContacts) {
        if (systemContact.userID == birthdayContact.userID) {
          found = true;
          break;
        }
      }
      if (!found) {
        mDatabaseManager.deleteContact(birthdayContact);
        break;
      }
    }
  }
  
  private void writeAllContactsWithAlerts(List<Contact> contacts) {
    for (Contact contact : contacts) {
      contact.firstAlert = true;
      contact.secondAlert = true;
    }
    writeAllContacts(contacts);
  }
  
  private void writeAllContacts(List<Contact> contacts) {
    mDatabaseManager.deleteAllContacts();
    contacts = mContactUtil.sortContacts(contacts, Calendar.getInstance());
    for (Contact contact : contacts) {
      mDatabaseManager.addContact(contact);
    }
  }
  
  private Contact createContact(Cursor cursorBirthday) {
    return new Contact(
        cursorBirthday.getLong(0),
        cursorBirthday.getString(1),
        cursorBirthday.getString(2),
        cursorBirthday.getInt(3) != 0,
        cursorBirthday.getInt(4) != 0,
        cursorBirthday.getInt(5) != 0
    );
  }
  
  private ContactCompare compare(Contact currentContact, Contact savedContact) {
    if (currentContact.userID != savedContact.userID) {
      return ContactCompare.USER_ID;
    }
    if (!currentContact.name.equals(savedContact.name)) {
      return ContactCompare.NAME;
    }
    if (!currentContact.bday.equals(savedContact.bday)) {
      return ContactCompare.BDAY;
    }
    if (currentContact.notified != savedContact.notified) {
      return ContactCompare.NOTIFIED;
    }
    return ContactCompare.EQUALS;
  }
  
  private enum ContactCompare {
    EQUALS, USER_ID, NAME, BDAY, NOTIFIED
  }
}
