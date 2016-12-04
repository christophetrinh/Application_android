package com.example.moneyapps;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.app.DialogFragment;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static android.R.attr.defaultValue;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private LocationManager locationManager;

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

        // Long Click Table:
        String extra = getIntent().getStringExtra(DataBaseAdapter.KEY_ROWID);
        if (extra != null) {
            Log.v("id press :", extra);
            mRowId = Long.valueOf(extra);
            populateFieldsFromLongPress();
            // TODO ADD REMOVE BUTTON AND LINK WITH DATABASE
            // if rmv : deleteExpense(mRowId)
        }
        // Set text form TakePicture
        Boolean bool_take_picture;
        bool_take_picture = getIntent().getBooleanExtra("take_picture",false);
        if (bool_take_picture) {
            String amount = getIntent().getStringExtra("amount");
            String full_date = getIntent().getStringExtra("date");
            populateFieldsFromCamera(amount,full_date);
        }
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

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FINAL_FORMAT);
        String expenseDate = dateFormat.format(mCalendar.getTime());
        if (mRowId == null) {
            mDbHelper.open();
            long id = mDbHelper.createExpense(retail, expenseDate, place, amount, category, tag);
            mDbHelper.close();
            if (id > 0) {
                mRowId = id;
                Log.v("DataBase ID:", String.valueOf(id));
            }
        } else {
            mDbHelper.open();
            mDbHelper.updateExpense(mRowId, retail, expenseDate, place, amount, category, tag);
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

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
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
            Log.v("Row id press :", String.valueOf(extras.getLong(DataBaseAdapter.KEY_ROWID)));
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
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void populateFieldsFromLongPress() {
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
    private void populateFieldsFromCamera(String value, String date) {
        String complete_address;
        complete_address = getLocationBestProvider();
        mPlaceText.setText(complete_address);

        if (value != null && !value.isEmpty()) mAmountText.setText(String.valueOf(value));
        if (date != null && !date.isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
            Date date_format;
            try {
                date_format = dateFormat.parse(date);
                Log.e("VALUE", "LA VALEUR DANS FONCTION POP FIELDS EST: " + date_format);
                mCalendar.setTime(date_format);
                updateDateButtonText();
            } catch (ParseException e) {
                Log.e("ExpenseEdit", e.getMessage(), e);
            }
        }
    }

    private String getLocation() {
        String address = new String();
        String context = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(context);
        List<String> providers = locationManager.getProviders(true);
        for(String provider: providers) {
            locationManager.requestLocationUpdates(provider, 1000, 0, new
                    LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle
                                extras) {
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                        }
                    });
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                Location location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    address = updateWithNewLocation(location);
                }
            }
        }
        return address;
    }


    private String getLocationBestProvider(){
        String full_address = new String();
        LocationManager locationManager;
        String context = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(context);Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                full_address = updateWithNewLocation(location);
            }
        }
        return full_address;
    }


    private String updateWithNewLocation(Location location) {
        String addressString = "No address found";
        if(location != null){
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            //TODO AJOUTER LA LOCALISATION DANS LA DATASET

            Geocoder gc = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = gc.getFromLocation(latitude, longitude,
                        1);
                StringBuilder sb = new StringBuilder();
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                        sb.append("\n").append(address.getAddressLine(i));
                    }
                    addressString = sb.toString();
                }
            } catch (IOException e){}
        }
        return addressString;
    }



}
