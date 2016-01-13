package de.liersch.android.bday.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import de.liersch.android.bday.beans.Contact;

public class DatabaseManager {

  private static DatabaseManager dbManager;
  private final DatabaseHelper db;
  private SQLiteDatabase mDatabase;

  private static final String DB_NAME = "bday.db";
  private static final String TABLE_NOTIFICATIONS = "notifications";
  private static final int DB_VERSION = 1;
  private static final String DROP = "DROP TABLE IF EXISTS ".concat(TABLE_NOTIFICATIONS);
  private static final String CREATE =
      "CREATE TABLE ".concat(TABLE_NOTIFICATIONS).concat(" (" +
          "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
          "user_id INTEGER NOT NULL, " +
          "name STRING NOT NULL, " +
          "bday STRING NOT NULL, " +
          "notified INTEGER NOT NULL" +
          ")");

  // context seems to be the application context
  public static DatabaseManager getInstance(Context applicationContext) {
    if (dbManager == null) {
      dbManager = new DatabaseManager(applicationContext);
    }
    return dbManager;
  }

  private DatabaseManager(Context context) {
    db = new DatabaseHelper(context);
  }

  public Cursor read() {
    Cursor cursor = null;
    // TODO read is calling but db isn't open
    if (mDatabase != null && mDatabase.isOpen()) {
      cursor = mDatabase.query(
          TABLE_NOTIFICATIONS,
          new String[]{"user_id", "name", "bday", "notified"},
          null, null, null, null, "_id");
    }
    return cursor;
  }

  public void close() {
    db.getReadableDatabase().close();
  }

  public void addContact(Contact contact) {
    // TODO read is calling but db isn't open
    if(mDatabase != null && mDatabase.isOpen()) {
      final long primaryKey = mDatabase.insert(TABLE_NOTIFICATIONS, null, createValues(contact));
      System.out.println("DatabaseManager#addContact: " + "primaryKey: " + primaryKey + " | userID: " + contact.userID + " | birthday: " + contact.bday);
    }
  }

  public void updateContact(Contact contact) {
    // TODO read is calling but db isn't open
    if(mDatabase != null && mDatabase.isOpen()) {
      String where = "user_id = ?";
      String[] whereArgs = new String[]{ Long.toString(contact.userID) };
      mDatabase.update(TABLE_NOTIFICATIONS, createValues(contact), where, whereArgs);
    }
  }

  public void deleteAllContacts() {
    mDatabase.delete(TABLE_NOTIFICATIONS, null, null);
  }

  public void deleteContact(Contact contact) {
    String where = "user_id = ?";
    String[] whereArgs = new String[]{ Long.toString(contact.userID) };
    mDatabase.delete(TABLE_NOTIFICATIONS, where, whereArgs);
  }

  public void reset() {
    mDatabase.execSQL(DROP);
    mDatabase.execSQL(CREATE);
  }

  private ContentValues createValues(Contact contact) {
    ContentValues values = new ContentValues();
    values.put("user_id", contact.userID);
    values.put("name", contact.name);
    values.put("bday", contact.bday);
    values.put("notified", contact.notified ? 1 : 0);
    return values;
  }

/*  private void exportDB() {
    File sd = Environment.getExternalStorageDirectory();
    File data = Environment.getDataDirectory();
    FileChannel source;
    FileChannel destination;
    String currentDBPath = "/data/de.liersch.android.bday/databases/"+DB_NAME;
    String backupDBPath = DB_NAME;
    File currentDB = new File(data, currentDBPath);
    File backupDB = new File(sd, backupDBPath);
    try {
      source = new FileInputStream(currentDB).getChannel();
      destination = new FileOutputStream(backupDB).getChannel();
      destination.transferFrom(source, 0, source.size());
      source.close();
      destination.close();
    } catch(IOException e) {
      e.printStackTrace();
    }
  }*/

  class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context applicationContext) {
      super(applicationContext, DB_NAME, null, DB_VERSION);
      mDatabase = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL(CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      db.execSQL(DROP);
      onCreate(db);
    }
  }
}
