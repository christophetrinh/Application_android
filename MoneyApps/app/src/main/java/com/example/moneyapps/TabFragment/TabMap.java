package com.example.moneyapps.TabFragment;


import android.annotation.SuppressLint;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moneyapps.DataBaseAdapter;
import com.example.moneyapps.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressLint("ValidFragment")
public class TabMap extends Fragment implements OnMapReadyCallback {

    private DataBaseAdapter mDbHelper;
    MapView mapView;
    GoogleMap mMap;

    public TabMap(DataBaseAdapter mDb) {
        this.mDbHelper = mDb;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_fragment, container, false);

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) v.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                setUpMap(googleMap);
            }
        });

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        setUpMap(googleMap);
    }

    public static List<ExpenseMap> ExtractFromDataBase(DataBaseAdapter mDb) {
        // Groupby place and sum amount
        // Return array list of class ExpenseMap
        mDb.open();
        final List<ExpenseMap> PlaceAmount = new ArrayList<>();
        Cursor dataCursor = mDb.groupbyPlace();
        debugDatabase(dataCursor); // Print result
        if (dataCursor != null) {
            dataCursor.moveToFirst();
            while (!dataCursor.isAfterLast()) {

                PlaceAmount.add(new ExpenseMap(dataCursor.getString(dataCursor.getColumnIndexOrThrow(dataCursor.getColumnName(0))),
                        dataCursor.getDouble(dataCursor.getColumnIndexOrThrow(dataCursor.getColumnName(1)))));

                dataCursor.moveToNext();
            }
        }
        mDb.close();
        return PlaceAmount;
    }

    public static class ExpenseMap {

        private String place;
        private double amount;

        public ExpenseMap(String place, double amount) {
            this.place = place;
            this.amount = amount;
        }

        public String toString() {
            return "Place : " + place + ", Amount : " + amount + "\n";
        }

        public String getPlace() {
            return place;
        }

        public double getAmount() {
            return amount;
        }
    }

    private static void debugDatabase(Cursor dataCursor) {
        if (dataCursor != null) {
            // Print
            // TODO remove ?
            Log.v("DataBase Columns: ", Arrays.toString(dataCursor.getColumnNames()));
            Log.v("DataBase rows number:", String.valueOf(dataCursor.getCount()));

            dataCursor.moveToFirst();
            while (!dataCursor.isAfterLast()) {
                // Print all
                StringBuilder row = new StringBuilder();
                for (int i = 0; i < dataCursor.getColumnNames().length; i++) {
                    row.append(dataCursor.getString(i) + " ");
                }
                Log.v("DataBase row:", row.toString());
                dataCursor.moveToNext();
            }
        }
    }

    private void setUpMap(GoogleMap map) {
        mMap = map;
        mDbHelper.open();
        // Extract data from database
        List<ExpenseMap> PlaceAmount = ExtractFromDataBase(this.mDbHelper);
        // Search from a city name
        Geocoder geocode = new Geocoder(getContext());
        for (int i = 0; i < PlaceAmount.size(); i++) {
            if (!PlaceAmount.get(i).getPlace().equals("Empty") & !PlaceAmount.get(i).getPlace().equals("empty")) {
                geocodeAddress(PlaceAmount.get(i).getPlace(), PlaceAmount.get(i).getAmount(), geocode, mMap);
            }
        }
        mDbHelper.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMap(mMap);
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMap.clear();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * Method to get latitude,longitude from address.
     *
     * @param addressStr-Address or zip code of city.
     * @param gc-                Geocoder instance.
     */
    public static void geocodeAddress(String addressStr, double amount, Geocoder gc, GoogleMap mMap) {
        Address address = null;
        List<Address> addressList = null;
        double latitude = 0;
        double longitude = 0;

        try {
            if (!TextUtils.isEmpty(addressStr)) {
                addressList = gc.getFromLocationName(addressStr, 5);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null != addressList && addressList.size() > 0) {
            address = addressList.get(0);
        }
        if (null != address && address.hasLatitude()
                && address.hasLongitude()) {
            latitude = address.getLatitude();
            longitude = address.getLongitude();
        }
        if (latitude != 0 && longitude != 0) {
            try {
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title( addressStr + " : " + String.format( "%.2f", amount) + " €"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(latitude, longitude), 10));
            } catch (Exception e) {

            }
        }
    }

}
