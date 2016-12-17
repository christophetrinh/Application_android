package com.example.moneyapps.TabFragment;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moneyapps.DataBaseAdapter;
import com.example.moneyapps.R;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

@SuppressLint("ValidFragment")
public class TabViz extends Fragment {

    private DataBaseAdapter mDbHelper;
    private List<Integer> years;

    public final static String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
            "Sep", "Oct", "Nov", "Dec",};
    public final static int Max_ChartTop = 100;
    public final static int Max_ChartBottom = 100;
    private LineChartView chartTop;
    private ColumnChartView chartBottom;

    private LineChartData lineData;
    private ColumnChartData columnData;

    public TabViz(DataBaseAdapter mDb) {
        this.mDbHelper = mDb;
        this.years = new ArrayList<Integer>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.viz_fragment, container, false);

        // *** TOP LINE CHART ***
        chartTop = (LineChartView) rootView.findViewById(R.id.chart_top);

        // Generate and set data for line chart
        generateInitialLineData();

        // *** BOTTOM COLUMN CHART ***
        chartBottom = (ColumnChartView) rootView.findViewById(R.id.chart_bottom);
        mDbHelper.open();
        updateColumnData(this.mDbHelper);
        mDbHelper.close();
        return rootView;
    }

    private void updateColumnData(DataBaseAdapter mDb) {

        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;
        years.clear();

        // Retrieve amount by year
        Cursor dataCursor = mDb.groupbyYear();
        if (dataCursor != null) {
            int i = 0;
            dataCursor.moveToFirst();
            while (!dataCursor.isAfterLast()) {
                values = new ArrayList<SubcolumnValue>();
                values.add(new SubcolumnValue(dataCursor.getInt(dataCursor.getColumnIndexOrThrow(dataCursor.getColumnName(1))) , ChartUtils.pickColor()));
                axisValues.add(new AxisValue(i).setLabel(dataCursor.getString(dataCursor.getColumnIndexOrThrow(dataCursor.getColumnName(0)))));

                years.add(dataCursor.getInt(dataCursor.getColumnIndexOrThrow(dataCursor.getColumnName(0))));
                columns.add(new Column(values).setHasLabelsOnlyForSelected(true));

                dataCursor.moveToNext();
                i++;
            }
        }

        columnData = new ColumnChartData(columns);

        columnData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
        columnData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(2));

        chartBottom.setColumnChartData(columnData);

        // Set value touch listener that will trigger changes for chartTop.
        chartBottom.setOnValueTouchListener(new ValueTouchListener());

        // Set selection mode to keep selected month column highlighted.
        chartBottom.setValueSelectionEnabled(true);

        chartBottom.setZoomEnabled(false);

    }

    /**
     * Generates initial data for line chart. At the begining all Y values are equals 0. That will change when user
     * will select value on column chart.
     */
    private void generateInitialLineData() {

        int numValues = months.length;

        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<PointValue> values = new ArrayList<PointValue>();
        for (int i = 0; i < numValues; ++i) {
            values.add(new PointValue(i, 0));
            axisValues.add(new AxisValue(i).setLabel(months[i]));
        }

        Line line = new Line(values);
        line.setColor(ChartUtils.COLOR_GREEN).setCubic(true);

        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        lineData = new LineChartData(lines);
        lineData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
        lineData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(3));

        chartTop.setLineChartData(lineData);

        // For build-up animation you have to disable viewport recalculation.
        chartTop.setViewportCalculationEnabled(false);
        chartTop.setZoomEnabled(false);
        // And set initial max viewport and current viewport- remember to set viewports after data.
        Viewport v = new Viewport(0, Max_ChartTop, 11, 0);
        chartTop.setMaximumViewport(v);
        chartTop.setCurrentViewportWithAnimation(v,300);
    }

    private void updateLineData(int columnIndex, int color, boolean flag) {
        int max = Max_ChartTop;
        mDbHelper.open();
        // Cancel last animation if not finished.
        chartTop.cancelDataAnimation();

        // Modify data targets
        Line line = lineData.getLines().get(0);// For this example there is always only one line.
        line.setColor(color);
        List<PointValue> values = line.getValues();

        // Clear data
        for (PointValue value : line.getValues()) {
            value.setTarget(value.getX(), 0);
        }

        if(flag) {
            // Retrieve amount by month
            Cursor dataCursor = this.mDbHelper.groupbyMonth(this.years.get(columnIndex));
            if (dataCursor != null) {
                dataCursor.moveToFirst();
                while (!dataCursor.isAfterLast()) {
                        //Retrieve month info
                        System.out.println(dataCursor.getInt(dataCursor.getColumnIndexOrThrow(dataCursor.getColumnName(0)))+","
                                +dataCursor.getInt(dataCursor.getColumnIndexOrThrow(dataCursor.getColumnName(1))));

                        PointValue value = values.get(dataCursor.getInt(dataCursor.getColumnIndexOrThrow(dataCursor.getColumnName(0))) - 1);
                        value.setTarget(dataCursor.getInt(dataCursor.getColumnIndexOrThrow(dataCursor.getColumnName(0))) - 1,
                                dataCursor.getInt(dataCursor.getColumnIndexOrThrow(dataCursor.getColumnName(1))));
                        if (dataCursor.getInt(dataCursor.getColumnIndexOrThrow(dataCursor.getColumnName(1)))>max)
                            max = dataCursor.getInt(dataCursor.getColumnIndexOrThrow(dataCursor.getColumnName(1)));

                    dataCursor.moveToNext();
                }
            }
        }

        Viewport v = new Viewport(0, max, 11, 0);
        chartTop.setMaximumViewport(v);
        chartTop.setCurrentViewportWithAnimation(v,300);

        // Start new data animation with 300ms duration;
        chartTop.startDataAnimation(300);
        mDbHelper.close();
    }

    private class ValueTouchListener implements ColumnChartOnValueSelectListener {

        @Override
        public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
            updateLineData(columnIndex, value.getColor(), true);
        }

        @Override
        public void onValueDeselected() {
            updateLineData(0, ChartUtils.COLOR_GREEN, false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        generateInitialLineData();
        mDbHelper.open();
        updateColumnData(this.mDbHelper);
        mDbHelper.close();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}


