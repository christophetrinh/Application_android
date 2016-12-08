package com.example.moneyapps.TabFragment;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.moneyapps.DataBaseAdapter;
import com.example.moneyapps.R;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by mario on 29/11/2016.
 */

@SuppressLint("ValidFragment")
public class TabHome extends Fragment {
    private PieChartView chart;
    private PieChartData data;
    private DataBaseAdapter mDbHelper;

    public TabHome(DataBaseAdapter mDb) {
        this.mDbHelper = mDb;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        PreferenceManager.setDefaultValues(getContext(), R.xml.preferences, false);
        SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sentence;
        String amount;
        View v;
        v= inflater.inflate(R.layout.home_fragment, container, false);

        // Title message
        TextView button_view = (TextView) v.findViewById(R.id.home_text);
        Typeface typeFace= Typeface.createFromAsset(getActivity().getAssets(),"Roboto-Light.ttf");
        button_view.setTypeface(typeFace);
        String home_display = myPref.getString("home_choice","null");

        if (home_display.equals("Day")){
            sentence = "Today, you've spent : ";
        }

        else if (home_display.equals("Month")){
            sentence = "This month, you've spent : ";
        }

        else if (home_display.equals("Year")) {
            sentence = "This year, you've spent : ";
        }
        
        else { //default case
            sentence = "This month, you've spent: ";
        }

        amount = mDbHelper.getAmount(home_display);
        button_view.setText(sentence + amount + " €");

        // Pie chart
        chart = (PieChartView) v.findViewById(R.id.bottom_pie);
        chart.setOnValueTouchListener(new ValueTouchListener());

        updateDataPiechart();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAmount();
        updateDataPiechart();
    }

    private void updateDataPiechart() {

        List<ExpenseCategory> CategoryAmount = ExtractFromDataBase(this.mDbHelper);
        //System.out.println("Cate"+CategoryAmount.size());
        double sum = Totalsum(CategoryAmount);
        List<SliceValue> values = new ArrayList<SliceValue>();
        for (ExpenseCategory expense : CategoryAmount) {
            SliceValue sliceValue = new SliceValue((float) (expense.getAmount()/sum)*100, ChartUtils.pickColor());
            sliceValue.setLabel(expense.getCategory());
            values.add(sliceValue);
        }

        data = new PieChartData(values);
        data.setHasLabels(true);
        data.setHasCenterCircle(true);

        chart.setPieChartData(data);
    }

    public static double Totalsum(List<ExpenseCategory> CategoryAmount) {
        double sum = 0;

        for (ExpenseCategory expense : CategoryAmount) {
            sum = sum + expense.getAmount();
        }

        return sum;
    }

    public static List<ExpenseCategory> ExtractFromDataBase(DataBaseAdapter mDb) {
        // Groupby place and sum amount
        // Return array list of class ExpenseMap
        final List<ExpenseCategory> CategoryAmount = new ArrayList<>();
        Cursor dataCursor = mDb.groupbyCategory();
        if (dataCursor != null) {
            dataCursor.moveToFirst();
            while (!dataCursor.isAfterLast()) {
                CategoryAmount.add(new ExpenseCategory(dataCursor.getString(dataCursor.getColumnIndexOrThrow(dataCursor.getColumnName(0))),
                        Double.parseDouble(dataCursor.getString(dataCursor.getColumnIndexOrThrow(dataCursor.getColumnName(1))))));

                dataCursor.moveToNext();
            }
        }
        return CategoryAmount;
    }

    public static class ExpenseCategory {

        private String category;
        private double amount;

        public ExpenseCategory(String category, double amount) {
            this.category = category;
            this.amount = amount;
        }

        public String toString() {
            return "Category : " + category + ", Amount : " + amount + "\n";
        }

        public String getCategory() {
            return category;
        }

        public double getAmount() {
            return amount;
        }
    }


    private class ValueTouchListener implements PieChartOnValueSelectListener {

        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
            //Toast.makeText(getActivity(), "Selected: " + value, Toast.LENGTH_SHORT).show();

            data.setCenterText1(String.valueOf(((int) value.getValue()))+" %");

            Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Italic.ttf");
            data.setCenterText1Typeface(tf);
            // Get font size from dimens.xml and convert it to sp(library uses sp values).
            data.setCenterText1FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                    (int) getResources().getDimension(R.dimen.pie_chart_text1_size)));

            data.setCenterText2(String.valueOf(value.getLabel()));
            data.setCenterText2Typeface(tf);
            data.setCenterText2FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                    (int) getResources().getDimension(R.dimen.pie_chart_text2_size)));
        }

        @Override
        public void onValueDeselected() {
            // Auto-generated method stub
        }

    }

    private void updateAmount() {
        TextView button_view = (TextView) getActivity().findViewById(R.id.home_text);
        Typeface typeFace= Typeface.createFromAsset(getActivity().getAssets(),"Roboto-Light.ttf");
        button_view.setTypeface(typeFace);
        String sentence;
        String amount;
        SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String home_display = myPref.getString("home_choice","null");

        if (home_display.equals("Day")){
            sentence = "Today, you've spent : ";
        }

        else if (home_display.equals("Month")){
            sentence = "This month, you've spent : ";
        }

        else if (home_display.equals("Year")) {
            sentence = "This year, you've spent : ";
        }

        else { //default case
            sentence = "This month, you've spent: ";
        }

        amount = mDbHelper.getAmount(home_display);
        button_view.setText(sentence + amount + " €");
    }


}