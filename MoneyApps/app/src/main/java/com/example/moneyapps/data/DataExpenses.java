package com.example.moneyapps.data;

import android.database.Cursor;
import android.util.Log;

import com.example.moneyapps.DataBaseAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Demonstration of several expenses.
 */
public final class DataExpenses {

    /**
     * Creates a list of expense.
     *
     * @return The created list of expenses.
     */
    public static List<Expense> createExpenseList() {
        // 1, Leclerc , 12/10/16, Grenoble, Alimentation, Etude, 100
        Expense a = new Expense("1","Auchan", "12/10/16", "Grenoble", "Alimentation", "Etude", 10);
        Expense b = new Expense("2","Lidl", "07/10/16", "Lausanne", "Alimentation", "Etude", 500);
        Expense c = new Expense("3","Mobilis", "02/10/16", "Geneve", "Transport", "Echange", 1000);

        final List<Expense> Expenses = new ArrayList<>();
        Expenses.add(a);
        Expenses.add(b);
        Expenses.add(c);

        return Expenses;
    }

    public static List<Expense> UpdateDataBaseExpenseList(DataBaseAdapter mDbHelper) {
        final List<Expense> Expenses = new ArrayList<>();
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
                        Double.parseDouble(expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(mDbHelper.KEY_AMOUNT)))));

                expenseCursor.moveToNext();
            }
        }
        return Expenses;
    }

}
