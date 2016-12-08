package com.example.moneyapps.TabFragment;

import android.content.SharedPreferences;
import android.icu.text.SymbolTable;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.machinarius.preferencefragment.PreferenceFragment;

import com.example.moneyapps.R;

/**
 * Created by mario on 29/11/2016.
 */

public class TabSettings extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public String date_preference_ToLoad;

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
/*
        ListPreference date_preference = (ListPreference) findPreference("date_preference");
        date_preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                date_preference_ToLoad = newValue.toString();
                System.out.println(date_preference_ToLoad);
                return true;
            }
        });*/
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals("date_preference")) {
            //do something
            System.out.println(key);
        }

    }

}