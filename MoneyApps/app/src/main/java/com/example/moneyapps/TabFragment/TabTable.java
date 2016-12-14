package com.example.moneyapps.TabFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.example.moneyapps.DataBaseAdapter;
import com.example.moneyapps.ExpenseEdit;
import com.example.moneyapps.ExpenseTableDataAdapter;
import com.example.moneyapps.R;
import com.example.moneyapps.SortableExpenseTableView;
import com.example.moneyapps.data.DataExpenses;
import com.example.moneyapps.data.Expense;

import java.io.Serializable;

import de.codecrafters.tableview.listeners.SwipeToRefreshListener;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.listeners.TableDataLongClickListener;

import static android.R.attr.id;

/**
 * Created by trinh on 23/11/16.
 */

public class TabTable extends Fragment {

    private static final int ACTIVITY_EDIT = 1;
    private DataBaseAdapter mDbHelper;
    private SortableExpenseTableView expenseTableView;
    private ExpenseTableDataAdapter expenseTableDataAdapter;

    public TabTable() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public TabTable(DataBaseAdapter mDb) {
        this.mDbHelper = mDb;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.table_view, container, false);
        expenseTableView = (SortableExpenseTableView) view.findViewById(R.id.tableView);
        if (expenseTableView != null) {
            expenseTableDataAdapter = new ExpenseTableDataAdapter(getContext(), DataExpenses.UpdateDataBaseExpenseList(mDbHelper), expenseTableView);
            expenseTableView.setDataAdapter(expenseTableDataAdapter);
            expenseTableView.addDataLongClickListener(new ExpenseLongClickListener());
            expenseTableView.setSwipeToRefreshEnabled(true);
            expenseTableView.setSwipeToRefreshListener(new SwipeToRefreshListener() {
                @Override
                public void onRefresh(final RefreshIndicator refreshIndicator) {
                    expenseTableView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // UPDATE
                            expenseTableDataAdapter.getData().clear();
                            expenseTableDataAdapter.getData().addAll(DataExpenses.UpdateDataBaseExpenseList(mDbHelper));
                            expenseTableDataAdapter.notifyDataSetChanged();
                            refreshIndicator.hide();
                            Toast.makeText(getContext(), "Refresh", Toast.LENGTH_SHORT).show();
                        }
                    }, 2000);
                }
            });
        }
        return view;
    }

    public void onResume() {
        super.onResume();
        // UPDATE TABLE
        expenseTableDataAdapter.getData().clear();
        expenseTableDataAdapter.getData().addAll(DataExpenses.UpdateDataBaseExpenseList(mDbHelper));
    }

    public void onPause() {
        super.onPause();
    }

    private class ExpenseLongClickListener implements TableDataLongClickListener<Expense> {

        @Override
        public boolean onDataLongClicked(final int rowIndex, final Expense clickedData) {
            final String expenseString = "Edit expense: " + " " + clickedData.getRetail();
            Toast.makeText(getContext(), expenseString, Toast.LENGTH_SHORT).show();

            //link the form
            Intent i = new Intent(getContext(), ExpenseEdit.class);
            i.putExtra(DataBaseAdapter.KEY_ROWID, clickedData.getId());
            startActivityForResult(i, ACTIVITY_EDIT);
            return true;
        }
    }

}