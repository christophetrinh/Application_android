package com.example.moneyapps.data;

import android.database.Cursor;
import com.example.moneyapps.DataBaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstration of several expenses.
 */
public final class DataExpenses {

    public static List<Expense> UpdateDataBaseExpenseList(DataBaseAdapter mDbHelper) {
        final List<Expense> Expenses = new ArrayList<>();
        mDbHelper.open();
        Cursor expenseCursor = mDbHelper.fetchAllExpense();
        if (expenseCursor != null) {
            expenseCursor.moveToFirst();
            while(!expenseCursor.isAfterLast()) {

                Expenses.add(new Expense(expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(mDbHelper.KEY_ROWID)),
                            expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(mDbHelper.KEY_RETAIL)),
                            expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(mDbHelper.KEY_DATE)),
                            expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(mDbHelper.KEY_PLACE)),
                            expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(mDbHelper.KEY_CATOGORY)),
                            expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(mDbHelper.KEY_TAG)),
                            expenseCursor.getDouble(expenseCursor.getColumnIndexOrThrow(mDbHelper.KEY_AMOUNT))));

                expenseCursor.moveToNext();
            }
        }
        mDbHelper.close();
        return Expenses;
    }

}
