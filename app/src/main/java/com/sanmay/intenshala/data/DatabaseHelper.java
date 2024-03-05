package com.sanmay.intenshala.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, "Userdata.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("create Table Userdetails(email TEXT, title TEXT, description TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int ii) {
        DB.execSQL("drop Table if exists Userdetails");
        onCreate(DB);
    }

    public long insertUserData(String email, String title, String description) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("title", title);
        contentValues.put("description", description);
        return DB.insert("Userdetails", null, contentValues);
    }

    public Boolean updateUserData(String email, String title, String description) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("description", description);
        int result = DB.update("Userdetails", contentValues, "email=? AND title=?", new String[]{email, title});
        return result != -1;
    }

    public Boolean deleteData(String email, String title) {
        SQLiteDatabase DB = this.getWritableDatabase();
        int result = DB.delete("Userdetails", "email=? AND title=?", new String[]{email, title});
        return result != -1;
    }

    public Boolean deleteAllData() {
        SQLiteDatabase DB = this.getWritableDatabase();
        int result = DB.delete("Userdetails", null, null);
        return result != 0;
    }

    public int getCountOfNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM Userdetails", null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }
    public List<String> getAllTitles(String email) {
        List<String> titlesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT title FROM Userdetails WHERE email=?", new String[]{email});
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int titleIndex = cursor.getColumnIndex("title");
                    if (titleIndex != -1) {
                        do {
                            String title = cursor.getString(titleIndex);
                            titlesList.add(title);
                        } while (cursor.moveToNext());
                    } else {
                        // "title" column not found
//                        Log.e("DatabaseHelper", "Column 'title' not found in the result set");
                    }
                } else {
                    // No records found for the given email
//                    Log.e("DatabaseHelper", "No records found for email: " + email);
                }
            } else {
                // Cursor is null
//                Log.e("DatabaseHelper", "Cursor is null");
            }
        } catch (Exception e) {
            // Exception occurred
//            Log.e("DatabaseHelper", "Error retrieving titles", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return titlesList;
    }

    public Cursor getData(String email) {
        SQLiteDatabase DB = this.getWritableDatabase();
        return DB.rawQuery("SELECT * FROM Userdetails WHERE email=?", new String[]{email});
    }
}
