package com.example.moneyapps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

/**
 * Created by mario on 15/11/2016.
 */

public class DataBaseAdapter {

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "expense";
    private static final int DATABASE_VERSION = 1;

    public static final String KEY_RETAIL = "Retail";
    public static final String KEY_DATE = "Date";
    public static final String KEY_DATE_MONTH = "Date_month";
    public static final String KEY_DATE_YEAR = "Date_year";
    public static final String KEY_PLACE = "Place";
    public static final String KEY_AMOUNT = "Amount";
    public static final String KEY_CATOGORY = "Category";
    public static final String KEY_TAG = "Tag";
    public static final String KEY_ROWID = "_id";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_CREATE =
            "create table " + DATABASE_TABLE + " ("
                    + KEY_ROWID + " integer primary key autoincrement, "
                    + KEY_RETAIL + " text not null, "
                    + KEY_DATE + " text not null, "
                    + KEY_DATE_MONTH + " integer, "
                    + KEY_DATE_YEAR + " integer, "
                    + KEY_PLACE + " text not null, "
                    + KEY_AMOUNT + " text not null, "
                    + KEY_CATOGORY + " text not null, "
                    + KEY_TAG + " text not null);";

    private final Context mCtx;

    public DataBaseAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public DataBaseAdapter open() throws android.database.SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Not used, but you could upgrade the database with ALTER scripts
        }
    }

    public void close() {
        mDbHelper.close();
    }

    public long createExpense(String retail, String date, String place, String amount, String category, String tag) {
        ContentValues initialValues = new ContentValues();
        if (retail.isEmpty()){
            initialValues.put(KEY_RETAIL, "empty");}
        else
            initialValues.put(KEY_RETAIL, retail);
        if (date.isEmpty()) {
            initialValues.put(KEY_DATE, "empty");
            initialValues.put(KEY_DATE_MONTH, -1);
            initialValues.put(KEY_DATE_YEAR, -1);
        }
        else {
            initialValues.put(KEY_DATE, date);
            initialValues.put(KEY_DATE_MONTH, getMonthDate(date));
            initialValues.put(KEY_DATE_YEAR, getYearDate(date));
        }
        if (place.isEmpty())
            initialValues.put(KEY_PLACE, "empty");
        else
            initialValues.put(KEY_PLACE, place);
        if (amount.isEmpty())
            initialValues.put(KEY_AMOUNT, "0");
        else
            initialValues.put(KEY_AMOUNT, amount);
        if (category.isEmpty())
            initialValues.put(KEY_CATOGORY, "empty");
        else
            initialValues.put(KEY_CATOGORY, category);
        if (tag.isEmpty())
            initialValues.put(KEY_TAG, "empty");
        else
            initialValues.put(KEY_TAG, tag);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    public boolean deleteExpense(long rowId) {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllExpense() {
        //query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy)
        return mDb.query(
                DATABASE_TABLE, // The table to query
                new String[]{ KEY_ROWID, KEY_RETAIL, KEY_DATE, KEY_PLACE, KEY_AMOUNT, KEY_CATOGORY, KEY_TAG},   // The columns to return
                null,   // The columns for the WHERE clause
                null,   // The values for the WHERE clause
                null,   // don't group the rows
                null,   // don't filter by row groups
                null);  // The sort order
    }

    public Cursor groupbyMonth() {
        return mDb.query(
                DATABASE_TABLE, // The table to query
                new String[]{KEY_DATE_YEAR,KEY_DATE_MONTH,"SUM("+KEY_AMOUNT+")"},   // The columns to return
                null,   // The columns for the WHERE clause
                null,   // The values for the WHERE clause
                "CAST("+KEY_DATE_YEAR+" AS VARCHAR(4) )+'.'+CAST("+KEY_DATE_MONTH+" AS VARCHAR(2))",
                null,   // don't filter by row groups
                null);  // The sort order
    }

    public Cursor groupbyYear() {
        return mDb.query(
                DATABASE_TABLE, // The table to query
                new String[]{KEY_DATE_YEAR,"SUM("+KEY_AMOUNT+")"},   // The columns to return
                null,   // The columns for the WHERE clause
                null,   // The values for the WHERE clause
                KEY_DATE_YEAR,
                null,   // don't filter by row groups
                null);  // The sort order
    }

    public Cursor groupbyPlace() {
        /*
        SELECT
                KEY_PLACE,
        SUM(KEY_AMOUNT)
        FROM
                DATABASE_TABLE
        GROUP BY
        KEY_PLACE;
        */
        // Expanded version:
        return mDb.query(
                DATABASE_TABLE, // The table to query
                new String[]{KEY_PLACE,"SUM("+KEY_AMOUNT+")"},   // The columns to return
                null,   // The columns for the WHERE clause
                null,   // The values for the WHERE clause
                KEY_PLACE,
                null,   // don't filter by row groups
                null);  // The sort order
    }

    public Cursor fetchExpense(long rowId) throws SQLException {
        //query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
        Cursor mCursor = mDb.query(
                true,
                DATABASE_TABLE,
                new String[]{KEY_ROWID, KEY_RETAIL, KEY_DATE, KEY_PLACE, KEY_AMOUNT, KEY_CATOGORY, KEY_TAG},
                KEY_ROWID + "=" + rowId,
                null,
                null,
                null,
                null,
                null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public boolean updateExpense(long rowId, String retail, String date, String place, String amount, String category, String tag) {
        ContentValues args = new ContentValues();
        args.put(KEY_RETAIL, retail);
        args.put(KEY_DATE, date);
        args.put(KEY_DATE_MONTH, getMonthDate(date));
        args.put(KEY_DATE_YEAR, getYearDate(date));
        args.put(KEY_PLACE, place);
        args.put(KEY_AMOUNT, amount);
        args.put(KEY_CATOGORY, category);
        args.put(KEY_TAG, tag);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public int getMonthDate(String date){
        String monthDate[] = date.split("/");
        return Integer.parseInt(monthDate[1]);
    }
    public int getYearDate(String date){
        String monthDate[] = date.split("/");
        return Integer.parseInt(monthDate[2]);
    }

}
