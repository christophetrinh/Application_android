package com.example.moneyapps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataBaseAdapter {

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "expense";
    private static final int DATABASE_VERSION = 1;

    public static final String KEY_RETAIL = "Retail";
    public static final String KEY_DATE = "Date";
    public static final String KEY_DATE_DAY = "Date_day";
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
                    + KEY_DATE_DAY + " integer, "
                    + KEY_DATE_MONTH + " integer, "
                    + KEY_DATE_YEAR + " integer, "
                    + KEY_PLACE + " text not null, "
                    + KEY_AMOUNT + " real, "
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

    public long createExpense(String retail, String date, String place, double amount, String category, String tag) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_AMOUNT, amount);
        if (retail.isEmpty()){
            initialValues.put(KEY_RETAIL, "Empty");}
        else
            initialValues.put(KEY_RETAIL, capitalize(retail));
        if (date.isEmpty()) {
            initialValues.put(KEY_DATE, "Empty");
            initialValues.put(KEY_DATE_DAY, -1);
            initialValues.put(KEY_DATE_MONTH, -1);
            initialValues.put(KEY_DATE_YEAR, -1);
        }
        else {
            initialValues.put(KEY_DATE, date);
            initialValues.put(KEY_DATE_DAY, getDayDate(date));
            initialValues.put(KEY_DATE_MONTH, getMonthDate(date));
            initialValues.put(KEY_DATE_YEAR, getYearDate(date));
        }
        if (place.isEmpty())
            initialValues.put(KEY_PLACE, "Empty");
        else
            initialValues.put(KEY_PLACE, capitalize(place));
        if (category.isEmpty())
            initialValues.put(KEY_CATOGORY, "Empty");
        else
            initialValues.put(KEY_CATOGORY, capitalize(category));
        if (tag.isEmpty())
            initialValues.put(KEY_TAG, "Empty");
        else
            initialValues.put(KEY_TAG, capitalize(tag));

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    public static String capitalize(String s) {
        if (s.length() == 0) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public boolean deleteExpense(long rowId) {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean deleteAllExpense() {
        return mDb.delete(DATABASE_TABLE, null, null) > 0;
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

    public Cursor groupbyMonth(int year) {
        return mDb.query(
                DATABASE_TABLE, // The table to query
                new String[]{KEY_DATE_MONTH,"SUM("+KEY_AMOUNT+")"},   // The columns to return
                KEY_DATE_YEAR+"=?",   // The columns for the WHERE clause
                new String[]{String.valueOf(year)},   // The values for the WHERE clause
                KEY_DATE_MONTH,
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

    public Cursor groupbyCategory() {
        return mDb.query(
                DATABASE_TABLE, // The table to query
                new String[]{KEY_CATOGORY,"SUM("+KEY_AMOUNT+")"},   // The columns to return
                null,   // The columns for the WHERE clause
                null,   // The values for the WHERE clause
                KEY_CATOGORY,
                null,   // don't filter by row groups
                null);  // The sort order
    }

    public Cursor groupbyTag() {
        return mDb.query(
                DATABASE_TABLE, // The table to query
                new String[]{KEY_TAG,"SUM("+KEY_AMOUNT+")"},   // The columns to return
                null,   // The columns for the WHERE clause
                null,   // The values for the WHERE clause
                KEY_TAG,
                null,   // don't filter by row groups
                null);  // The sort order
    }

    public boolean updateExpense(long rowId, String retail, String date, String place, double amount, String category, String tag) {
        ContentValues args = new ContentValues();
        args.put(KEY_RETAIL, retail);
        args.put(KEY_DATE, date);
        args.put(KEY_DATE_DAY, getDayDate(date));
        args.put(KEY_DATE_MONTH, getMonthDate(date));
        args.put(KEY_DATE_YEAR, getYearDate(date));
        args.put(KEY_PLACE, place);
        args.put(KEY_AMOUNT, amount);
        args.put(KEY_CATOGORY, category);
        args.put(KEY_TAG, tag);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public int getDayDate(String date){
        String Date[] = date.split("/");
        return Integer.parseInt(Date[0]);
    }

    public int getMonthDate(String date){
        String Date[] = date.split("/");
        return Integer.parseInt(Date[1]);
    }
    public int getYearDate(String date){
        String Date[] = date.split("/");
        return Integer.parseInt(Date[2]);
    }

    public double getAmount(String period) {
        double Amount = 0;
        Cursor mCursor = null;
        String current_date = new String();

        if (period.equals("Day")) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd");
            current_date = sdf.format(new Date());

            mCursor = mDb.query(
                    DATABASE_TABLE, // The table to query
                    new String[]{KEY_DATE_DAY,"SUM("+KEY_AMOUNT+")"},   // The columns to return
                    null,   // The columns for the WHERE clause
                    null,   // The values for the WHERE clause
                    KEY_DATE_DAY,
                    null,   // don't filter by row groups
                    null);  // The sort order
        }

        else if (period.equals("Month")) {

            SimpleDateFormat sdf = new SimpleDateFormat("MM");
            current_date = sdf.format(new Date());

            mCursor = mDb.query(
                    DATABASE_TABLE, // The table to query
                    new String[]{KEY_DATE_MONTH,"SUM("+KEY_AMOUNT+")"},   // The columns to return
                    KEY_DATE_YEAR+"=?",   // The columns for the WHERE clause
                    new String[]{new SimpleDateFormat("yyyy").format(new Date())},   // The values for the WHERE clause
                    KEY_DATE_MONTH,
                    null,   // don't filter by row groups
                    null);  // The sort order
        }

        else if (period.equals("Year")) {



            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            current_date = sdf.format(new Date());

            mCursor = mDb.query(
                    DATABASE_TABLE, // The table to query
                    new String[]{KEY_DATE_YEAR,"SUM("+KEY_AMOUNT+")"},   // The columns to return
                    null,   // The columns for the WHERE clause
                    null,   // The values for the WHERE clause
                    KEY_DATE_YEAR,
                    null,   // don't filter by row groups
                    null);  // The sort order

        }

        else { //defaut case

            SimpleDateFormat sdf = new SimpleDateFormat("MM");
            current_date = sdf.format(new Date());

            mCursor = mDb.query(
                    DATABASE_TABLE, // The table to query
                    new String[]{KEY_DATE_MONTH,"SUM("+KEY_AMOUNT+")"},   // The columns to return
                    null,   // The columns for the WHERE clause
                    null,   // The values for the WHERE clause
                    KEY_DATE_MONTH,
                    null,   // don't filter by row groups
                    null);  // The sort order
        }


        if (mCursor!= null) {
            mCursor.moveToFirst();

            while (!mCursor.isAfterLast()) {

                String date_cursor = String.format("%02d", mCursor.getInt(mCursor.getColumnIndexOrThrow(mCursor.getColumnName(0))));
                if (date_cursor.equals(current_date)){
                    Amount = mCursor.getDouble(mCursor.getColumnIndexOrThrow(mCursor.getColumnName(1)));
                    break;
                }
                mCursor.moveToNext();
            }
        }
        return Amount;
    }

}
