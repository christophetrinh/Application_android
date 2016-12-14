package com.example.moneyapps.TabFragment;

import android.Manifest;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.icu.text.SymbolTable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import lecho.lib.hellocharts.model.PieChartData;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import com.example.moneyapps.DataBaseAdapter;
import com.github.machinarius.preferencefragment.PreferenceFragment;

import com.example.moneyapps.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchClearValuesRequest;
import com.google.api.services.sheets.v4.model.BatchClearValuesResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.example.moneyapps.TabFragment.TabHome.Totalsum;

/**
 * Created by mario on 29/11/2016.
 */

@SuppressLint("ValidFragment")
public class TabSettings extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, EasyPermissions.PermissionCallbacks {

    // Google drive
    GoogleAccountCredential mCredential;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES_SHEETS = {SheetsScopes.SPREADSHEETS};
    private static final String[] SCOPES_DRIVE = { DriveScopes.DRIVE_METADATA_READONLY };
    private static boolean Select_API = false; // False: DRIVE; True: SHEETS

    // Info Spreadsheet
    private static final String spreadsheetId = "128ht2Igh9xGTT1T7Q6D_dSpNsOT2gISE9WVr8Sv8VGw";
    private static final String range = "A1:G";

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
            case "pref_sync":
                boolean sync_value = sharedPreferences.getBoolean(key, false);
                if (sync_value) {
                    // TODO SELECTION AND LINK WITH SPREASHEET ID
                    // DRIVE API to choose the spreadsheet
                    // Initialize credentials and service object.
                    Select_API = false;
                    mCredential = GoogleAccountCredential.usingOAuth2(
                            getContext(), Arrays.asList(SCOPES_DRIVE))
                            .setBackOff(new ExponentialBackOff());
                    getResultsFromApi();

                    // SHEET API to retrieve or update spreadsheet
                    // Initialize credentials and service object.
                    Select_API = true;
                    mCredential = GoogleAccountCredential.usingOAuth2(
                            getContext(), Arrays.asList(SCOPES_SHEETS))
                            .setBackOff(new ExponentialBackOff());
                    getResultsFromApi();
                }
                break;
            case "home_choice":
                List<Fragment> fragList = getFragmentManager().getFragments();
                for(Fragment f: fragList) {
                    if(f.getClass() == TabHome.class) {
                        ((TabHome)f).updateAmount();
                    }
                }
                break;
            case "home_pie":
                fragList = getFragmentManager().getFragments();
                for(Fragment f: fragList) {
                    if(f.getClass() == TabHome.class) {
                        ((TabHome)f).updateDataPiechart();
                    }
                }
                break;
            default :
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        CheckBoxPreference showContact = (CheckBoxPreference)findPreference("pref_sync");
        showContact.setChecked(false);
    }

    public void display_msg(final String text_to_display) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(),text_to_display,Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Google API

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    public void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            display_msg("No network connection available.");
        } else {
            if (Select_API)
                new MakeRequestTaskSheets(mCredential).execute();
            else {
                new MakeRequestTaskDrive(mCredential).execute();
            }
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(getContext(), Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getActivity().getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    display_msg("This app requires Google Play Services. Please install");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     *
     * @param requestCode  The request code passed in
     *                     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     *
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(getContext());
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(getContext());
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                getActivity(),
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the Drive API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTaskDrive extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.drive.Drive mService = null;
        private Exception mLastError = null;

            MakeRequestTaskDrive(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.drive.Drive.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Money App")
                    .build();
        }

        /**
         * Background task to call Drive API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of up to 10 file names and IDs.
         * @return List of Strings describing files, or an empty list if no files
         *         found.
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            // Get a list of up to 10 files.
            List<String> fileInfo = new ArrayList<String>();
            FileList result = mService.files().list()
                    .setPageSize(10)
                    .setFields("nextPageToken, files(id, name)")
                    .execute();
            List<File> files = result.getFiles();
            if (files != null) {
                for (File file : files) {
                    fileInfo.add(String.format("%s (%s)\n",
                            file.getName(), file.getId()));
                }
            }
            return fileInfo;
        }


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(List<String> output) {
            if (output == null || output.size() == 0) {
                display_msg("No results returned.");
            } else {
                //TODO REMOVE ?
                output.add(0, "Data retrieved using the Drive API:");
                System.out.println(TextUtils.join("\n", output));
            }
        }

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            REQUEST_AUTHORIZATION);
                } else {
                    Log.e("DRIVE API", "The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                Log.e("DRIVE API", "Request cancelled.");
            }
        }
    }

    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTaskSheets extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;

        MakeRequestTaskSheets(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Money App")
                    .build();
        }

        /**
         * Background task to call Google Sheets API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                // TODO
                // Save data
                setDataFromApi();
                // Plot data save
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of names and majors of students in a sample spreadsheet:*
         * @return List of names and majors
         * @throws IOException
         */

        private List<String> getDataFromApi() throws IOException {
            List<String> results = new ArrayList<String>();
            // TODO LINK TO THE DATABASE (generate rowId and cut date)
            ValueRange response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values != null) {
                //results.add("_id - Retail - Date - Place - Amount - Category - Place");
                for (List row : values) {
                    results.add(row.get(0) + "\t" + row.get(1)+ "\t" + row.get(2)+"\t" + row.get(3)+
                            "\t" + row.get(4)+ "\t" + row.get(5)+ "\t" + row.get(6));
                }
            }
            return results;
        }

        private boolean setDataFromApi() throws IOException {
            boolean flag = false;
            // Update value :
            try {
                List<List<Object>> arrData = setData();

                ValueRange oRange = new ValueRange();
                oRange.setRange(range).setValues(arrData);

                List<ValueRange> oList = new ArrayList<>();
                oList.add(oRange);

                // TODO Clear all test ?
                //BatchClearValuesRequest oRequest_clear = new BatchClearValuesRequest();
                //oRequest_clear.getRanges(range);
                this.mService.spreadsheets().get(spreadsheetId).clear();
                //BatchClearValuesResponse oResp_clear = this.mService.spreadsheets().values().batchClear(spreadsheetId, oRequest_clear).execute();

                // Update
                BatchUpdateValuesRequest oRequest_update = new BatchUpdateValuesRequest();
                oRequest_update.setValueInputOption("RAW").setData(oList);
                BatchUpdateValuesResponse oResp_update = this.mService.spreadsheets().values().batchUpdate(spreadsheetId, oRequest_update).execute();

                flag = true;
                display_msg("Save to Google sheet");
            } catch (IOException e) {
                display_msg("Sheets failed");
                Log.v("Sheets failed", String.valueOf(e));
            }
            return flag;
        }

        public List<List<Object>> setData ()  {
            // Describe Row
            List<List<Object>> data = new ArrayList<List<Object>>();
            // TODO REMOVE KEY ROW ID
            List<Object> dataTitle = new ArrayList<Object>();
            dataTitle.add(mDbHelper.KEY_ROWID);
            dataTitle.add(mDbHelper.KEY_RETAIL);
            dataTitle.add(mDbHelper.KEY_DATE);
            dataTitle.add(mDbHelper.KEY_PLACE);
            dataTitle.add(mDbHelper.KEY_AMOUNT);
            dataTitle.add(mDbHelper.KEY_CATOGORY);
            dataTitle.add(mDbHelper.KEY_TAG);
            data.add(dataTitle);

            // link the database
            Cursor expenseCursor = mDbHelper.fetchAllExpense();
            if (expenseCursor != null) {
                expenseCursor.moveToFirst();
                while (!expenseCursor.isAfterLast()) {
                    // Describe columns for one row
                    List<Object> dataRow = new ArrayList<Object>();
                    dataRow.add(expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(mDbHelper.KEY_ROWID)));
                    dataRow.add(expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(mDbHelper.KEY_RETAIL)));
                    dataRow.add(expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(mDbHelper.KEY_DATE)));
                    dataRow.add(expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(mDbHelper.KEY_PLACE)));
                    dataRow.add(expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(mDbHelper.KEY_AMOUNT)));
                    dataRow.add(expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(mDbHelper.KEY_CATOGORY)));
                    dataRow.add(expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(mDbHelper.KEY_TAG)));

                    data.add(dataRow);
                    expenseCursor.moveToNext();
                }
            }
            return data;
        }

        @Override
        protected void onPreExecute() {
            display_msg("Calling Google Sheets API ...");
        }


        @Override
        protected void onPostExecute(List<String> output) {
            if (output == null || output.size() == 0) {
                display_msg("No results returned.");
            } else {
                // TODO REMOVE ?
                output.add(0, "Data retrieved using the Google Sheets API:");
                System.out.println(TextUtils.join("\n", output));
            }
        }

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            REQUEST_AUTHORIZATION);
                } else {
                    Log.e("SHEET API", "The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                Log.e("SHEET API", "Request cancelled.");
            }
        }

    }
}