package com.mcc.alltv.data.sqllite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mcc.alltv.model.Channel;

import java.util.ArrayList;


public class FavouriteDbController {

    private SQLiteDatabase database;

    public FavouriteDbController(Context context) {
        database = DatabaseHelper.getInstance(context).getWritableDatabase();
    }

    public int insertData(String channelId, String channelLogo, String channelName,String streamUrl, int isLive) {

        ContentValues values = new ContentValues();
        values.put(DbConstants.CHANNEL_ID, channelId);
        values.put(DbConstants.CHANNEL_LOGO, channelLogo);
        values.put(DbConstants.CHANNEL_NAME, channelName);
        values.put(DbConstants.CHANNEL_URL,streamUrl);
        values.put(DbConstants.IS_LIVE,isLive);

        // Insert the new row, returning the primary key value of the new row
        return (int) database.insert(DbConstants.FAVOURITE_TABLE_NAME, DbConstants.COLUMN_NAME_NULLABLE, values);
    }

    public ArrayList<Channel> getAllData() {

        String[] projection = {
                DbConstants._ID,
                DbConstants.CHANNEL_ID,
                DbConstants.CHANNEL_LOGO,
                DbConstants.CHANNEL_NAME,
                DbConstants.CHANNEL_URL,
                DbConstants.IS_LIVE,
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = DbConstants._ID + " DESC";

        Cursor c = database.query(
                DbConstants.FAVOURITE_TABLE_NAME,  // The table name to query
                projection,                        // The columns to return
                null,                     // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                      // don't group the rows
                null,
                sortOrder                           // The sort order
        );

        return fetchData(c);
    }

    private ArrayList<Channel> fetchData(Cursor c) {
        ArrayList<Channel> favDataArray = new ArrayList<>();

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    // get  the  data into array,or class variable
                    int categoryID = c.getInt(c.getColumnIndexOrThrow(DbConstants._ID));
                    int channelId = c.getInt(c.getColumnIndexOrThrow(DbConstants.CHANNEL_ID));
                    String channelLogo = c.getString(c.getColumnIndexOrThrow(DbConstants.CHANNEL_LOGO));
                    String channelName = c.getString(c.getColumnIndexOrThrow(DbConstants.CHANNEL_NAME));
                    String streamUrl =  c.getString(c.getColumnIndexOrThrow(DbConstants.CHANNEL_URL));
                    int isLive =  c.getInt(c.getColumnIndexOrThrow(DbConstants.IS_LIVE));

                    // wrap up data list and return
                    Channel channelData = new Channel(channelId, categoryID, channelName, isLive, channelLogo, streamUrl);
                    Log.d("UrlTest",""+streamUrl);
                    favDataArray.add(channelData);
                } while (c.moveToNext());
            }
            c.close();
        }
        return favDataArray;
    }

    public void deleteFavItem(String channelId) {
        database.delete(DbConstants.FAVOURITE_TABLE_NAME, DbConstants.CHANNEL_ID + "=" + channelId, null);
    }

    public boolean isAlreadyFavourite(String channelId) {
        Cursor cursor = database.rawQuery("select "+ DbConstants.CHANNEL_ID +" from " + DbConstants.FAVOURITE_TABLE_NAME + " where " + DbConstants.CHANNEL_ID + "=" + channelId + "", null);
        if(cursor!=null && cursor.getCount()>0){
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }
}
