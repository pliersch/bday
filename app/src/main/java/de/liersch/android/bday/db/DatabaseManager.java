package de.liersch.android.bday.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager {

  private static de.liersch.android.bday.db.DatabaseManager dbManager;
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
          "send BOOLEAN NOT NULL," +
          "CONSTRAINT user_id_unique UNIQUE (user_id)" +
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

  public void close() {
    db.getReadableDatabase().close();
  }

  public Cursor read() {
    Cursor cursor = null;
    // TODO read is calling but db isn't open
    if (mDatabase != null && mDatabase.isOpen()) {
      cursor = mDatabase.query(
          TABLE_NOTIFICATIONS,
          new String[]{"_id", "user_id", "send"},
          null, null, null, null,
          "user_id");
    }
    return cursor;
  }

  public void update(long userID) {
    // TODO read is calling but db isn't open
    if(mDatabase != null && mDatabase.isOpen()) {
      ContentValues values = new ContentValues();
      values.put("user_id", userID);
      values.put("send", true);
      mDatabase.insert(TABLE_NOTIFICATIONS, null, values);
    }
  }

  public void reset() {
    mDatabase.execSQL(DROP);
    mDatabase.execSQL(CREATE);
  }

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
