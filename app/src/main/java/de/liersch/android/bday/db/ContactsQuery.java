package de.liersch.android.bday.db;

import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.content.CursorLoader;

public class ContactsQuery {

	public CursorLoader queryVisibleContacts(Context context) {
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ?";
		String[] selectionArgs = new String[] {"1"};
		String[] projection = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME };
		// Returns a new CursorLoader
		CursorLoader cursorLoader = new CursorLoader(
				context,
				uri,
				projection,
				selection,
				selectionArgs,
				null // Default sort order
		);
		return cursorLoader;
	}

	public CursorLoader queryBirthdaysContacts(Context context) {
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
		CursorLoader cursorLoader = new CursorLoader(context,
				uri,
				projection,
				selection,
				selectionArgs,
				null
		);
		return cursorLoader;
	}

}
