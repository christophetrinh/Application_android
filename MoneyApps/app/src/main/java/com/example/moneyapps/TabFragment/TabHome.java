package com.example.moneyapps.TabFragment;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
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

/**
 * Created by mario on 29/11/2016.
 */

@SuppressLint("ValidFragment")
public class TabHome extends Fragment {
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
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAmount();
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