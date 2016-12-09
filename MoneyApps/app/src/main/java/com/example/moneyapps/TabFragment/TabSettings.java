package com.example.moneyapps.TabFragment;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
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

import com.example.moneyapps.DataBaseAdapter;
import com.github.machinarius.preferencefragment.PreferenceFragment;

import com.example.moneyapps.R;

/**
 * Created by mario on 29/11/2016.
 */

@SuppressLint("ValidFragment")
public class TabSettings extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private DataBaseAdapter mDbHelper;
    public TabSettings(DataBaseAdapter mDbHelper) {
        this.mDbHelper = mDbHelper;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.settings_fragment, container, false);
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
        Log.d("tabsettings_preferences", key);
        switch (key) {
            case "home_choice":
                String sentence = new String();
                TextView button_view = (TextView) getActivity().findViewById(R.id.home_text);
                Typeface typeFace= Typeface.createFromAsset(getActivity().getAssets(),"Roboto-Light.ttf");
                button_view.setTypeface(typeFace);
                String home_display = sharedPreferences.getString(key,"null");

                if (home_display.equals("Day")){
                    sentence = "Today, you've spent : ";
                }

                else if (home_display.equals("Month")){
                    sentence = "This month, you've spent : ";
                }

                else if (home_display.equals("Year")) {
                    sentence = "This year, you've spent : ";
                }

                amount = mDbHelper.getAmount(home_display);
                button_view.setText(sentence + amount + " €");
            case "home_pie":
                String home_pie = sharedPreferences.getString(key,"null");
                if (home_pie.equals("Category")){
                //TODO finir l'update auto
                }

                else if (home_pie.equals("Tag")){

                }
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