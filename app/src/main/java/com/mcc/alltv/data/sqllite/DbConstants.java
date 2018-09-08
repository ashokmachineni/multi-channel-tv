package com.mcc.alltv.data.sqllite;

import android.provider.BaseColumns;

public class DbConstants implements BaseColumns {

    // commons
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    public static final String COLUMN_NAME_NULLABLE = null;


    // favourites
    public static final String FAVOURITE_TABLE_NAME = "favourite";

    public static final String CHANNEL_ID = "channel_id";
    public static final String CHANNEL_LOGO = "channel_logo_url";
    public static final String CHANNEL_NAME = "channel_name";
    public static final String CHANNEL_URL = "stream_url";
    public static final String IS_LIVE = "is_live";


    public static final String SQL_CREATE_FAVOURITE_ENTRIES =
            "CREATE TABLE " + FAVOURITE_TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    CHANNEL_ID + INTEGER_TYPE + COMMA_SEP +
                    CHANNEL_LOGO + TEXT_TYPE + COMMA_SEP +
                    CHANNEL_NAME + TEXT_TYPE + COMMA_SEP +
                    CHANNEL_URL + TEXT_TYPE + COMMA_SEP +
                    IS_LIVE + INTEGER_TYPE +" )";


    public static final String SQL_DELETE_FAVOURITE_ENTRIES =
            "DROP TABLE IF EXISTS " + FAVOURITE_TABLE_NAME;




}
