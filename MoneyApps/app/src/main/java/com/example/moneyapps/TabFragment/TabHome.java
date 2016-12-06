package com.example.moneyapps.TabFragment;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.moneyapps.R;

/**
 * Created by mario on 29/11/2016.
 */

public class TabHome extends Fragment {
    public static final String HOME_CHOICE = "home_preference";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String sentence = new String();
        View v;
        v= inflater.inflate(R.layout.home_fragment, container, false);
        TextView button_view = (TextView) v.findViewById(R.id.home_text);
        Typeface typeFace= Typeface.createFromAsset(getActivity().getAssets(),"Roboto-Light.ttf");
        button_view.setTypeface(typeFace);

        SharedPreferences settings = getActivity().getSharedPreferences(HOME_CHOICE, 0);

        String home_display = settings.getString("home_preference","nothing");

        Log.e("I RECEIVE", home_display);

        if (home_display.equals("Day")){
            sentence = "Today, you've spent :";
        }

        else if (home_display.equals("Month")){
            sentence = "This month, you've spent :";
        }

        else if (home_display.equals("Year")) {
            sentence = "This year, you've spent :";
        }

        //TODO recuperer l'amount
        button_view.setText(sentence + " 10 €");


        return v;
    }

}