package com.example.moneyapps.data;

import android.database.Cursor;
import com.example.moneyapps.DataBaseAdapter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        Expense d = new Expense("4","Mobilis", "02/10/16", "Geneve", "Transport", "Echange", 1000);

        final List<Expense> Expenses = new ArrayList<>();
        Expenses.add(a);
        Expenses.add(b);
        Expenses.add(c);
        Expenses.add(d);

        return Expenses;
    }

    public static List<Expense> UpdateDataBaseExpenseList(DataBaseAdapter mDbHelper) {
        final List<Expense> Expenses = new ArrayList<>();
        mDbHelper.open();
        Cursor expenseCursor = mDbHelper.fetchAllExpense();

        NumberFormat nf = NumberFormat.getInstance(Locale.FRANCE);

        if (expenseCursor != null) {
            expenseCursor.moveToFirst();
            while(!expenseCursor.isAfterLast()) {
                Number amount = null;
                try {
                    amount = nf.parse(expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(mDbHelper.KEY_AMOUNT)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Expenses.add(new Expense(expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(mDbHelper.KEY_ROWID)),
                            expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(mDbHelper.KEY_RETAIL)),
                            expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(mDbHelper.KEY_DATE)),
                            expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(mDbHelper.KEY_PLACE)),
                            expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(mDbHelper.KEY_CATOGORY)),
                            expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(mDbHelper.KEY_TAG)),
                            amount.doubleValue()));

                expenseCursor.moveToNext();
            }
        }
        mDbHelper.close();
        return Expenses;
    }

}
