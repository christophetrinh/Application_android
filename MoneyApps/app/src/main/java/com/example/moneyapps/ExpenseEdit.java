package com.example.moneyapps;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.app.DialogFragment;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import java.text.ParseException;
import java.util.Date;

import static android.R.attr.defaultValue;
import static android.app.Activity.RESULT_OK;

/**
 * Created by mario on 15/11/2016.
 */

public class ExpenseEdit extends Activity {

    private Button mDateButton;
    public static Calendar mCalendar;
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    DatePickerFragment dateFragment;
    private DataBaseAdapter mDbHelper;

    private EditText mRetailText;
    private EditText mPlaceText;
    private EditText mAmountText;
    private EditText mCategoryText;
    private EditText mTagText;

    public static final String DATE_FINAL_FORMAT = "dd/MM/yyyy";
    public Long mRowId;

    private Button mConfirmButton;

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_form_activity);
        mDbHelper = new DataBaseAdapter(this);

        mRetailText = (EditText) findViewById(R.id.retail);
        mPlaceText = (EditText) findViewById(R.id.place);
        mAmountText = (EditText) findViewById(R.id.amount);
        mCategoryText = (EditText) findViewById(R.id.category);
        mTagText = (EditText) findViewById(R.id.tag);
        mDateButton = (Button) findViewById(R.id.expense_date);

        mConfirmButton = (Button) findViewById(R.id.confirm);
        mCalendar = Calendar.getInstance();
        registerButtonListenersAndSetDefaultText();


        String amount = getIntent().getStringExtra("amount");
        String full_date = getIntent().getStringExtra("date");
        populateFieldsFromCamera(amount,full_date);
    }




    @RequiresApi(api = Build.VERSION_CODES.N)
    private void registerButtonListenersAndSetDefaultText() {
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveState();
                setResult(RESULT_OK);
                Toast.makeText(ExpenseEdit.this, getString(R.string.expense_saved_message), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });
        updateDateButtonText();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void saveState() {
        String retail = mRetailText.getText().toString();
        String place = mPlaceText.getText().toString();
        String amount = mAmountText.getText().toString();
        String category = mCategoryText.getText().toString();
        String tag = mTagText.getText().toString();

        Log.v("VALUE",amount);

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FINAL_FORMAT);
        String expenseDate = dateFormat.format(mCalendar.getTime());
        if (mRowId == null) {
            mDbHelper.open();
            long id = mDbHelper.createExpense( retail, expenseDate,  place,  amount,  category,  tag);
            mDbHelper.close();
            if (id > 0) {
                mRowId = id;
                Log.v("DataBase ID:", String.valueOf(id));

            }
        } else {
            mDbHelper.open();
            mDbHelper.updateExpense( mRowId,  retail, expenseDate,  place,  amount,  category,  tag);
            mDbHelper.close();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateDateButtonText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String dateForButton = dateFormat.format(mCalendar.getTime());
        mDateButton.setText(dateForButton);
    }


    public void showDatePickerDialog(View v) {
        dateFragment = new DatePickerFragment();
        dateFragment.show(getFragmentManager(), "datePicker");
    }


    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            int year = mCalendar.get(Calendar.YEAR);
            int month = mCalendar.get(Calendar.MONTH);
            int day = mCalendar.get(Calendar.DAY_OF_MONTH);
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void onDateSet(DatePicker view, int year, int month, int day) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, month);
            mCalendar.set(Calendar.DAY_OF_MONTH, day);
            ((ExpenseEdit) getActivity()).updateDateButtonText();
        }
    }

    private void setRowIdFromIntent() {
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(DataBaseAdapter.KEY_ROWID) : null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();
        setRowIdFromIntent();
        //populateFields();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void populateFields() {
        if (mRowId != null) {
            mDbHelper.open();
            Cursor expense = mDbHelper.fetchExpense(mRowId);
            if (expense != null) {
                expense.moveToFirst();

                mRetailText.setText(expense.getString(expense.getColumnIndexOrThrow(DataBaseAdapter.KEY_RETAIL)));
                mPlaceText.setText(expense.getString(expense.getColumnIndexOrThrow(DataBaseAdapter.KEY_PLACE)));
                mAmountText.setText(expense.getString(expense.getColumnIndexOrThrow(DataBaseAdapter.KEY_AMOUNT)));
                mCategoryText.setText(expense.getString(expense.getColumnIndexOrThrow(DataBaseAdapter.KEY_CATOGORY)));
                mTagText.setText(expense.getString(expense.getColumnIndexOrThrow(DataBaseAdapter.KEY_TAG)));

                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FINAL_FORMAT);
                Date date;
                try {
                    String dateString = expense.getString(expense.getColumnIndexOrThrow(DataBaseAdapter.KEY_DATE));
                    date = dateFormat.parse(dateString);
                    mCalendar.setTime(date);
                } catch (ParseException e) {
                    Log.e("ExpenseEdit", e.getMessage(), e);
                }
                expense.close();
            }
            mDbHelper.close();
        }
        updateDateButtonText();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void populateFieldsFromCamera(String value, String date) { //TODO ajouter la location dedans
        if (value!=null && !value.isEmpty()) mAmountText.setText(String.valueOf(value));
        if (date!=null && !date.isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FINAL_FORMAT);
            Date date_format;
            try {
                date_format = dateFormat.parse(date);
                Log.e("VALUE","LA VALEUR DANS FONCTION POP FIELDS EST"+date_format);
                mCalendar.setTime(date_format);
            } catch (ParseException e) {
                Log.e("ExpenseEdit", e.getMessage(), e);
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRowId != null) {
            outState.putLong(DataBaseAdapter.KEY_ROWID, mRowId);
        }
    }
}
