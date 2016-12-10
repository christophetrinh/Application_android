package com.example.moneyapps.TabFragment;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.icu.text.SymbolTable;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import lecho.lib.hellocharts.model.PieChartData;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

import com.example.moneyapps.DataBaseAdapter;
import com.github.machinarius.preferencefragment.PreferenceFragment;

import com.example.moneyapps.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.moneyapps.TabFragment.TabHome.Totalsum;

/**
 * Created by mario on 29/11/2016.
 */

@SuppressLint("ValidFragment")
public class TabSettings extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private PieChartView chart;
    private PieChartData data;
    private DataBaseAdapter mDbHelper;

    public TabSettings(DataBaseAdapter mDbHelper) {
        this.mDbHelper = mDbHelper;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings_fragment, container, false);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager.getDefaultSharedPreferences(getContext()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String amount;
        switch (key) {
            case "home_choice":
                String sentence = new String();
                TextView button_view = (TextView) getActivity().findViewById(R.id.home_text);
                Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Light.ttf");
                button_view.setTypeface(typeFace);
                String home_display = sharedPreferences.getString(key, "null");

                if (home_display.equals("Day")) {
                    sentence = "Today, you've spent : ";
                } else if (home_display.equals("Month")) {
                    sentence = "This month, you've spent : ";
                } else if (home_display.equals("Year")) {
                    sentence = "This year, you've spent : ";
                }

                amount = mDbHelper.getAmount(home_display);
                button_view.setText(sentence + amount + " €");
            case "home_pie":
                String home_pie = sharedPreferences.getString(key, "null");
                final List<TabHome.ExpenseCategory> CategoryAmount = new ArrayList<>();
                Cursor dataCursor = null;
                if (home_pie.equals("Category")) {
                    dataCursor = mDbHelper.groupbyCategory();
                } else if (home_pie.equals("Tag")) {
                    dataCursor = mDbHelper.groupbyTag();
                }
                if (dataCursor != null) {
                    dataCursor.moveToFirst();
                    while (!dataCursor.isAfterLast()) {
                        CategoryAmount.add(new TabHome.ExpenseCategory(dataCursor.getString(dataCursor.getColumnIndexOrThrow(dataCursor.getColumnName(0))),
                                Double.parseDouble(dataCursor.getString(dataCursor.getColumnIndexOrThrow(dataCursor.getColumnName(1))))));

                        dataCursor.moveToNext();
                    }
                }
                double sum = Totalsum(CategoryAmount);
                List<SliceValue> values = new ArrayList<SliceValue>();
                for (TabHome.ExpenseCategory expense : CategoryAmount) {
                    SliceValue sliceValue = new SliceValue((float) (expense.getAmount() / sum) * 100, ChartUtils.pickColor());
                    sliceValue.setLabel(expense.getCategory());
                    values.add(sliceValue);
                }

                data = new PieChartData(values);
                data.setHasLabels(true);
                data.setHasCenterCircle(true);

                chart = (PieChartView) getActivity().findViewById(R.id.bottom_pie);
                chart.setOnValueTouchListener(new ValueTouchListener());
                chart.setPieChartData(data);

        }
    }

    public class ValueTouchListener implements PieChartOnValueSelectListener {

        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
            //Toast.makeText(getActivity(), "Selected: " + value, Toast.LENGTH_SHORT).show();

            data.setCenterText1(String.valueOf(((int) value.getValue())) + " %");

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

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}