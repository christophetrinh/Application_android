package com.example.moneyapps;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.SortStateViewProviders;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;
import com.example.moneyapps.data.Expense;


/**
 * An extension of the {@link SortableTableView} that handles {@link Expense}s.
 */
public class SortableExpenseTableView extends SortableTableView<Expense> {

    public SortableExpenseTableView(final Context context) {
        this(context, null);
    }

    public SortableExpenseTableView(final Context context, final AttributeSet attributes) {
        this(context, attributes, android.R.attr.listViewStyle);
    }

    public SortableExpenseTableView(final Context context, final AttributeSet attributes, final int styleAttributes) {
        super(context, attributes, styleAttributes);

        final SimpleTableHeaderAdapter simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(context, R.string.retail, R.string.date, R.string.place, R.string.category, R.string.tag, R.string.amount);
        simpleTableHeaderAdapter.setTextColor(ContextCompat.getColor(context, R.color.table_header_text));
        setHeaderAdapter(simpleTableHeaderAdapter);

        final int rowColorEven = ContextCompat.getColor(context, R.color.table_data_row_even);
        final int rowColorOdd = ContextCompat.getColor(context, R.color.table_data_row_odd);
        setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(rowColorEven, rowColorOdd));
        setHeaderSortStateViewProvider(SortStateViewProviders.brightArrows());

        final TableColumnWeightModel tableColumnWeightModel = new TableColumnWeightModel(6);
        tableColumnWeightModel.setColumnWeight(0, 2);
        tableColumnWeightModel.setColumnWeight(1, 2);
        tableColumnWeightModel.setColumnWeight(2, 3);
        tableColumnWeightModel.setColumnWeight(3, 3);
        tableColumnWeightModel.setColumnWeight(4, 2);
        tableColumnWeightModel.setColumnWeight(5, 2);
        setColumnModel(tableColumnWeightModel);

        setColumnComparator(0, ExpenseComparators.getExpenseRetailComparator());
        setColumnComparator(1, ExpenseComparators.getExpenseDateComparator());
        setColumnComparator(2, ExpenseComparators.getExpensePlaceComparator());
        setColumnComparator(3, ExpenseComparators.getExpenseCategoryComparator());
        setColumnComparator(4, ExpenseComparators.getExpenseTagComparator());
        setColumnComparator(5, ExpenseComparators.getExpenseAmountComparator());

    }

}
