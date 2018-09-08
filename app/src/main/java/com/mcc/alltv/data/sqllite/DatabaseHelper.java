package com.mcc.alltv.data.sqllite;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper DatabaseHelper = null;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "tv_channel_db";


    public static DatabaseHelper getInstance(Context context) {
        if (DatabaseHelper == null) {
            DatabaseHelper = new DatabaseHelper(context);
        }
        return DatabaseHelper;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DbConstants.SQL_CREATE_FAVOURITE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DbConstants.SQL_DELETE_FAVOURITE_ENTRIES);


        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


}

