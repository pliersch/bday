package de.liersch.android.bday.db;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class ContactsQuery {

	private static ContactsQuery mInstance;

	public static ContactsQuery getInstance(){
		if (mInstance == null) {
			mInstance = new ContactsQuery();
		}
		return mInstance;
	}

	private ContactsQuery() {

	}

	public Cursor queryVisibleContacts(Context context) {
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ?";
		String[] selectionArgs = new String[] {"1"};
		String[] projection = new String[] {
				ContactsContract.Contacts._ID,
				ContactsContract.Contacts.DISPLAY_NAME };

		Cursor cursor = context.getContentResolver().query(
        uri,
				projection,
				selection,
				selectionArgs,
				null // Default sort order
		);
		return cursor;
	}

	public Cursor queryBirthdaysContacts(Context context) {
		Uri uri = ContactsContract.Data.CONTENT_URI;
		String[] projection = new String[] {
				ContactsContract.Data.CONTACT_ID,
				ContactsContract.CommonDataKinds.Event.TYPE,
				ContactsContract.CommonDataKinds.Event.START_DATE,
				ContactsContract.CommonDataKinds.Event.LABEL };
		String selection = ContactsContract.Data.MIMETYPE + " = ? AND "
				+ ContactsContract.CommonDataKinds.Event.TYPE + " = ?";
		String[] selectionArgs = new String[] {
				ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
				String.valueOf(ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)
		};
		Cursor cursor = context.getContentResolver().query(
				uri,
				projection,
				selection,
				selectionArgs,
        null // Default sort order
		);
		return cursor;
	}

}
