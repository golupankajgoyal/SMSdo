package com.example.android.smsdo.Database;

import android.content.ContentResolver;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import com.example.android.smsdo.Database.PhoneContract.ItemEntry;

import androidx.annotation.Nullable;

public class PhoneDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "phoneNos.db";

    /**
     * Database version, If you need to change the Database schema, then you must increase the DATABASE_VERSION
     */
    private static final int DATABASE_VERSION = 1;


    public PhoneDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_ITEM_TABLE = " CREATE TABLE " + ItemEntry.TABLE_NAME + " ( "
                + ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ItemEntry.COLUMN_PHONE_NO
                + " TEXT NOT NULL " + " );";

        db.execSQL(SQL_CREATE_ITEM_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
