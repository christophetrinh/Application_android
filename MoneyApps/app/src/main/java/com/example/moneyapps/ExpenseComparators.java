package com.example.moneyapps;

/**
 * Created by mario on 25/11/2016.
 */
import com.example.moneyapps.data.Expense;
import java.util.Comparator;

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
            return expense1.getDate().compareTo(expense2.getDate());
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
