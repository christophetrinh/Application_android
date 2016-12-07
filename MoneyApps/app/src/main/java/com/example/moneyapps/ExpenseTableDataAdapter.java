package com.example.moneyapps;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.LongPressAwareTableDataAdapter;
import com.example.moneyapps.data.Expense;

import java.text.NumberFormat;
import java.util.List;

import static java.lang.String.format;


public class ExpenseTableDataAdapter extends LongPressAwareTableDataAdapter<Expense> {

    private static final int TEXT_SIZE = 14;
    private static final NumberFormat PRICE_FORMATTER = NumberFormat.getNumberInstance();


    public ExpenseTableDataAdapter(final Context context, final List<Expense> data, final TableView<Expense> tableView) {
        super(context, data, tableView);
    }

    @Override
    public View getDefaultCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        final Expense expense = getRowData(rowIndex);
        View renderedView = null;

        switch (columnIndex) {
            case 0:
                renderedView = renderRetail(expense);
                break;
            case 1:
                renderedView = renderDate(expense);
                break;
            case 2:
                renderedView = renderPlace(expense);
                break;
            case 3:
                renderedView = renderCategory(expense);
                break;
            case 4:
                renderedView = renderTag(expense);
                break;
            case 5:
                renderedView = renderAmount(expense);
                break;
        }

        return renderedView;
    }

    @Override
    public View getLongPressCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        final Expense expense = getRowData(rowIndex);
        View renderedView = null;
        Log.v("Table long press", expense.getRetail());
        renderedView = getDefaultCellView(rowIndex, columnIndex, parentView);
        return renderedView;
    }

    private View renderAmount(final Expense expense) {
        final String amountString = PRICE_FORMATTER.format(expense.getAmount()) + " €";

        final TextView textView = new TextView(getContext());
        textView.setText(amountString);
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(TEXT_SIZE);

        if (expense.getAmount() < 100) {
            textView.setTextColor(0xFF2E7D32);
        } else if (expense.getAmount() >= 100 & expense.getAmount() < 1000) {
            textView.setTextColor(0xFFEF6C00);
        } else if (expense.getAmount() >= 1000) {
            textView.setTextColor(0xFFC62828);
        }

        return textView;
    }

    private View renderRetail(final Expense expense) {
        return renderString(expense.getRetail());
    }
    private View renderDate(final Expense expense) {
        return renderString(expense.getDate());
    }
    private View renderPlace(final Expense expense) {
        return renderString(expense.getPlace());
    }
    private View renderCategory(final Expense expense) { return renderString(expense.getCategory()); }
    private View renderTag(final Expense expense) {
        return renderString(expense.getTag());
    }

    private View renderString(final String value) {
        final TextView textView = new TextView(getContext());
        textView.setText(value);
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(TEXT_SIZE);
        return textView;
    }

}
