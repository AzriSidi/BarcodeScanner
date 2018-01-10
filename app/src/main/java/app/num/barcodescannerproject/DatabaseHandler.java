package app.num.barcodescannerproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "db_scanner";
    private static final String TABLE_CONTACTS = "show_result";
    private static final String KEY_ID = "id";
    private static final String KEY_RESULT = "result";
    private static final String KEY_TYPE = "type";
    private static final String KEY_DATE_TIME = "date_time";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_RESULT + " TEXT, "
                +KEY_TYPE + " TEXT," + KEY_DATE_TIME +
                " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    private String getDateTime() {
        Long tsLong = System.currentTimeMillis();
        return tsLong.toString();
    }

    public ArrayList<String> getAllResults() {
        ArrayList<String> contactList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS +
                " ORDER BY " + KEY_DATE_TIME +" DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String name = cursor.getString(cursor.getColumnIndex(KEY_RESULT));
                contactList.add(name);
                cursor.moveToNext();
            }
        }
        return contactList;
    }

    public ArrayList<String> getAllType() {
        ArrayList<String> contactList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS +
                " ORDER BY " + KEY_DATE_TIME +" DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String name = cursor.getString(cursor.getColumnIndex(KEY_TYPE));
                contactList.add(name);
                cursor.moveToNext();
            }
        }
        return contactList;
    }

    public ArrayList<String> getAllTime() {
        ArrayList<String> contactList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS +
                " ORDER BY " + KEY_DATE_TIME +" DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String name = cursor.getString(cursor.getColumnIndex(KEY_DATE_TIME));
                contactList.add(name);
                cursor.moveToNext();
            }
        }
        return contactList;
    }

    public void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_RESULT, contact.getResult());
        values.put(KEY_TYPE, contact.getType());
        values.put(KEY_DATE_TIME, getDateTime());
        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }

    public void updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE "+TABLE_CONTACTS+" SET "
                +KEY_TYPE+"='"+contact.getType()+"' ,"
                +KEY_DATE_TIME+"='"+getDateTime()+"' WHERE "
                +KEY_RESULT+"='"+contact.getResult()+"'";
        db.execSQL(updateQuery);
    }

    public boolean checkContact(String result){
        boolean lastnamePresent = false;
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        while(cursor.moveToNext()){
            String record=cursor.getString(cursor.getColumnIndex(KEY_RESULT));
            if(record.equals(result)){
                lastnamePresent = true;
                break;
            }
        }
        return lastnamePresent;
    }

    public void deleteAllContact() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_CONTACTS);
        db.close();
    }

    public void deleteContact(String result) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_RESULT + " = ?",
                new String[] { String.valueOf(result) });
        db.close();
    }

    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        return cursor.getCount();
    }
}
