package de.liersch.android.bday.db;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class SystemContactsQuery {

  private static SystemContactsQuery mInstance;

  public static SystemContactsQuery getInstance() {
    if (mInstance == null) {
      mInstance = new SystemContactsQuery();
    }
    return mInstance;
  }

  private SystemContactsQuery() {
  }

  public Cursor queryVisibleContacts(Context context) {
    Uri uri = ContactsContract.Contacts.CONTENT_URI;
    String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ?";
    String[] selectionArgs = new String[]{"1"}; // TODO: 1-> google contacts. same like in queryBirthdayContacts?
    String[] projection = new String[]{
        ContactsContract.Contacts._ID,
        ContactsContract.Contacts.DISPLAY_NAME};

    return context.getContentResolver().query(
        uri,
        projection,
        selection,
        selectionArgs,
        null // Default sort order
    );
  }

  public Cursor queryBirthdayContacts(Context context) {
    Uri uri = ContactsContract.Data.CONTENT_URI;
    String[] projection = new String[]{
        ContactsContract.Data.CONTACT_ID,
        ContactsContract.CommonDataKinds.Event.TYPE,
        ContactsContract.CommonDataKinds.Event.START_DATE,
        ContactsContract.CommonDataKinds.Event.LABEL};
    String selection = // TODO: here another solution
        //ContactsContract.RawContacts.ACCOUNT_TYPE + " = 'com.google'" +
        ContactsContract.Data.MIMETYPE + " = ? AND " +
        ContactsContract.CommonDataKinds.Event.TYPE + " = ?";
    String[] selectionArgs = new String[]{
        ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
        String.valueOf(ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)
    };
    return context.getContentResolver().query(
        uri,
        projection,
        selection,
        selectionArgs,
        null // Default sort order
    );
  }

  public Cursor queryPhoneNumber(Context context, long userID) {
    Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    String[] projection = new String[]{
        ContactsContract.Data.CONTACT_ID,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER,
        ContactsContract.CommonDataKinds.Phone.TYPE,
        ContactsContract.RawContacts.ACCOUNT_TYPE};
    //ContactsContract.RawContacts.ACCOUNT_TYPE};
    String selection = ContactsContract.Data.CONTACT_ID + " = " + userID; // TODO: or here
//    String[] selectionArgs = new String[]{
//        ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
//        String.valueOf(ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)
//    };

//    String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ?";
//    String[] selectionArgs = new String[]{"1"}; // TODO: 1-> google contacts. same like in queryBirthdayContacts?

    return context.getContentResolver().query(
        uri,
        projection,
        selection,
        null,
        null
    );

//    ContentResolver cr = context.getContentResolver();
//    // Read Contacts
//    Cursor c = cr.query(uri, projection, selection, selectionArgs, ContactsContract.Contacts.LOOKUP_KEY + " DESC");
//    if (c.getCount() > 0) {
//      while (c.moveToNext()) {
//        String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
//        String type = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
//        String Phone_number = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//        System.out.println(id + ": " + name + ": " + Phone_number + " : " + type);
//      }
//    }
//    return c;
  }

  public Cursor queryPhoneNumbers(Context context) {
    Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    String[] projection = new String[]{
        ContactsContract.Contacts._ID,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER,
        ContactsContract.CommonDataKinds.Phone.TYPE,
        ContactsContract.RawContacts.ACCOUNT_TYPE};
//    String selection = ContactsContract.RawContacts.ACCOUNT_TYPE + " = 'com.google'"; // TODO: or here
//    };

    String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ?";
    String[] selectionArgs = new String[]{"1"}; // TODO: 1-> google contacts. same like in queryBirthdayContacts?

//    return context.getContentResolver().query(
//        uri,
//        projection,
//        selection,
//        selectionArgs,
//        ContactsContract.Contacts.LOOKUP_KEY + " DESC"
//    );

    ContentResolver cr = context.getContentResolver();
    // Read Contacts
    Cursor c = cr.query(uri, projection, selection, selectionArgs, ContactsContract.Contacts.LOOKUP_KEY + " DESC");
    if (c.getCount() > 0) {
      while (c.moveToNext()) {
        String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
        String type = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
        String Phone_number = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        System.out.println(id + ": " + name + ": " + Phone_number + " : " + type);
      }
    }
    return c;
  }
}
