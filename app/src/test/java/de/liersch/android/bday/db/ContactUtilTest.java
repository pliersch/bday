package de.liersch.android.bday.db;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.liersch.android.bday.beans.Contact;

import static org.junit.Assert.assertEquals;

public class ContactUtilTest {

  private ContactUtil mUtil;

  @Before
  public void setUp() throws Exception {
    mUtil = ContactUtil.getInstance();
  }

  @Test
  public void test_sortContacts() throws Exception {
    List<Contact> contacts = createContactsMock();
    final Calendar calendar = Calendar.getInstance();
    calendar.set(2015, 2, 1);
    final List<Contact> sortedContacts = mUtil.sortContacts(contacts, calendar);
    assertEquals("1990-03-03", sortedContacts.get(0).bday);
  }

  private List<Contact> createContactsMock() {
    List<Contact> contacts = new ArrayList<Contact>();
    contacts.add(new Contact(4,"d","1990-04-02",false));
    contacts.add(new Contact(2,"b","1990-02-02",false));
    contacts.add(new Contact(1,"a","1990-01-02",false));
    contacts.add(new Contact(3,"c","1990-03-03",false));
    return contacts;
  }

}
