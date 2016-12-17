package com.example.moneyapps;

/**
 * Created by mario on 25/11/2016.
 */
import com.example.moneyapps.data.Expense;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class ExpenseComparators {

    private ExpenseComparators() {
        //no instance
    }
    public static Comparator<Expense> getExpenseRetailComparator() {
        return new ExpenseRetailComparator();
    }
    public static Comparator<Expense> getExpenseDateComparator() {
        return new ExpenseDateComparator();
    }
    public static Comparator<Expense> getExpensePlaceComparator() {
        return new ExpensePlaceComparator();
    }
    public static Comparator<Expense> getExpenseCategoryComparator() {
        return new ExpenseCategoryComparator();
    }
    public static Comparator<Expense> getExpenseTagComparator() {
        return new ExpenseTagComparator();
    }
    public static Comparator<Expense> getExpenseAmountComparator() {
        return new ExpenseAmountComparator();
    }

    private static class ExpenseRetailComparator implements Comparator<Expense> {

        @Override
        public int compare(final Expense expense1, final Expense expense2) {
            return expense1.getRetail().compareTo(expense2.getRetail());
        }
    }
    private static class ExpenseDateComparator implements Comparator<Expense> {

        @Override
        public int compare(final Expense expense1, final Expense expense2) {
            Date expense1_date = null;
            Date expense2_date = null;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            try {
                expense1_date = sdf.parse(expense1.getDate());
                expense2_date = sdf.parse(expense2.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return expense1_date.compareTo(expense2_date);
        }
    }
    private static class ExpensePlaceComparator implements Comparator<Expense> {

        @Override
        public int compare(final Expense expense1, final Expense expense2) {
            return expense1.getPlace().compareTo(expense2.getPlace());
        }
    }
    private static class ExpenseCategoryComparator implements Comparator<Expense> {

        @Override
        public int compare(final Expense expense1, final Expense expense2) {
            return expense1.getCategory().compareTo(expense2.getCategory());
        }
    }
    private static class ExpenseTagComparator implements Comparator<Expense> {

        @Override
        public int compare(final Expense expense1, final Expense expense2) {
            return expense1.getTag().compareTo(expense2.getTag());
        }
    }
    private static class ExpenseAmountComparator implements Comparator<Expense> {

        @Override
        public int compare(final Expense expense1, final Expense expense2) {
            if (expense1.getAmount() < expense2.getAmount()) return -1;
            if (expense1.getAmount() > expense2.getAmount()) return 1;
            return 0;
        }
    }
}
