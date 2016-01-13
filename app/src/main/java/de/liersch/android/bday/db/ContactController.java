package de.liersch.android.bday.db;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import de.liersch.android.bday.beans.Contact;

public class ContactController {

  private final DatabaseManager mDatabaseManager;
  private final Context mApplicationContext;

  public ContactController(Context applicationContext) {
    mApplicationContext = applicationContext;
    mDatabaseManager = DatabaseManager.getInstance(applicationContext);
  }

  public void refresh() {
    final List<Contact> birthdayContacts = getContacts();

    List<Contact> systemContacts = ContactFactory.getInstance().createSystemContacts(mApplicationContext);

    // first time using the app
    if (birthdayContacts.size() == 0) {
      writeAllContacts(systemContacts);
    } else if (birthdayContacts.size() < systemContacts.size()) {
      addContact(birthdayContacts, systemContacts);
    } else if (birthdayContacts.size() > systemContacts.size()) {
      deleteContact(birthdayContacts, systemContacts);
    } else {
      updateContact(birthdayContacts, systemContacts);
    }
  }

  public List<Contact> getContacts() {
    final Cursor cursor = mDatabaseManager.read();
    List<Contact> contacts = new ArrayList<Contact>();
    while (cursor.moveToNext()) {
      contacts.add(createContact(cursor));
    }
    return contacts;
  }

  private void addContact(List<Contact> birthdayContacts, List<Contact> systemContacts) {
    // TODO: update the position is complicated. at this time all contacts will replaced
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
    boolean found = false;
    for (Contact birthdayContact : birthdayContacts) {
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

  private void writeAllContacts(List<Contact> contacts) {
    mDatabaseManager.deleteAllContacts();
    contacts = ContactUtil.getInstance().sortContacts(contacts);
    for (Contact contact : contacts) {
      mDatabaseManager.addContact(contact);
    }
  }

  private Contact createContact(Cursor cursorBirthday) {
    return new Contact(
        cursorBirthday.getLong(0),
        cursorBirthday.getString(1),
        cursorBirthday.getString(2),
        cursorBirthday.getInt(3) != 0
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

  enum ContactCompare {
    EQUALS, USER_ID, NAME, BDAY, NOTIFIED
  }
}
