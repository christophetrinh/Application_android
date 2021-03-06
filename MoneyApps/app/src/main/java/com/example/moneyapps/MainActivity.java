package com.example.moneyapps;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.moneyapps.TabFragment.*;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.florent37.materialviewpager.MaterialViewPager;

import java.util.Arrays;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";
    // Permission request codes need to be < 256
    private static final int RC_HANDLE_ACCESS_FINE = 3;

    private static final int ACTIVITY_CREATE = 0;
    MaterialViewPager materialViewPager;
    View headerLogo;
    ImageView headerLogoContent;
    private DataBaseAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DATE BASE
        mDbHelper = new DataBaseAdapter(this);
        mDbHelper.open();

        // BUTTON
        final FloatingActionButton actionA = (FloatingActionButton) findViewById(R.id.action_a);
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("Button", "Clik on button A");
                takePicture();
                // Close Menu
                FloatingActionsMenu Menu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
                Menu.collapse();
            }
        });

        final FloatingActionButton actionB = (FloatingActionButton) findViewById(R.id.action_b);
        actionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("Button", "Clik on button B");
                createForm();
                debugDatabase();
                // Close Menu
                FloatingActionsMenu Menu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
                Menu.collapse();
            }
        });

        //5 tabs
        final int tabCount = 5;

        headerLogo = findViewById(R.id.headerLogo);
        headerLogoContent = (ImageView) findViewById(R.id.headerLogoContent);

        this.materialViewPager = (MaterialViewPager) findViewById(R.id.materialViewPager);
        this.materialViewPager.getViewPager().setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {

                switch (position) {
                    case 0:
                        TabHome tab1 = new TabHome(mDbHelper);
                        return tab1;
                    case 1:
                        TabTable tab2 = new TabTable(mDbHelper);
                        return tab2;
                    case 2:
                        TabViz tab3 = new TabViz(mDbHelper);
                        return tab3;
                    case 3:
                        TabMap tab4 = new TabMap(mDbHelper);
                        return tab4;
                    case 4:
                        TabSettings tab5 = new TabSettings(mDbHelper);
                        return tab5;
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return tabCount;
            }

            //display the title for each tab
            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return getResources().getString(R.string.home);
                    case 1:
                        return getResources().getString(R.string.table);
                    case 2:
                        return getResources().getString(R.string.viz);
                    case 3:
                        return getResources().getString(R.string.map);
                    case 4:
                        return getResources().getString(R.string.settings);
                    default:
                        return "Page " + position;
                }
            }

            int oldItemPosition = -1;

            @Override
            public void setPrimaryItem(ViewGroup container, int position, Object object) {
                super.setPrimaryItem(container, position, object);

                //only if the page is different
                if (oldItemPosition != position) {
                    oldItemPosition = position;

                    //define new color and new image
                    String imageUrl = null;
                    int color = Color.BLACK;
                    Drawable newDrawable = null;

                    switch (position) {
                        case 0:
                            imageUrl = "http://www.skyscanner.fr/sites/default/files/image_import/fr/micro.jpg";
                            color = getResources().getColor(R.color.purple);
                            newDrawable = getResources().getDrawable(R.drawable.home);
                            break;
                        case 1:
                            imageUrl = "http://infraiq.com/wp-content/uploads/2016/05/6.jpg";
                            color = getResources().getColor(R.color.blue);
                            newDrawable = getResources().getDrawable(R.drawable.table);
                            break;
                        case 2:
                            imageUrl = "http://etnosoft.net/wp-content/uploads/2014/11/consulting2.jpg";
                            color = getResources().getColor(R.color.cyan);
                            newDrawable = getResources().getDrawable(R.drawable.charts);
                            break;
                        case 3:
                            imageUrl = "http://graduate.carleton.ca/wp-content/uploads/prog-banner-masters-international-affairs-juris-doctor.jpg";
                            color = getResources().getColor(R.color.orange);
                            newDrawable = getResources().getDrawable(R.drawable.earth);
                            break;
                        case 4:
                            imageUrl = "http://www.skyscanner.fr/sites/default/files/image_import/fr/micro.jpg";//"http://hdreach.org/images/gearsHiRes.jpg";
                            color = getResources().getColor(R.color.green);
                            newDrawable = getResources().getDrawable(R.drawable.settings);
                            break;
                    }

                    //modify image and color
                    int fadeDuration = 200;
                    materialViewPager.setColor(color, fadeDuration);
                    materialViewPager.setImageUrl(imageUrl, fadeDuration);
                    toggleLogo(newDrawable, color, fadeDuration);
                }
            }
        });


        //keep the 5tabs in memory
        this.materialViewPager.getViewPager().setOffscreenPageLimit(tabCount);
        //link the 5 tabs to the viewpager
        this.materialViewPager.getPagerTitleStrip().setViewPager(this.materialViewPager.getViewPager());


        // Check for the access_fine locaton permission.  If the
        // permission is not granted yet, request permission.
        int raf = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (raf != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG,"Permission not granted");
            requestAccessFindLocationPermission();
        }
    }

    private void debugDatabase() {
        mDbHelper.open();
        Cursor expenseCursor = mDbHelper.fetchAllExpense();
        if (expenseCursor != null) {
            // Print
            Log.v(TAG, "DataBase Columns: " + Arrays.toString(expenseCursor.getColumnNames()));
            Log.v(TAG, "DataBase rows number:" + String.valueOf(expenseCursor.getCount()));

            expenseCursor.moveToFirst();
            while(!expenseCursor.isAfterLast()) {
                StringBuilder row = new StringBuilder();
                for(int i = 0; i < expenseCursor.getColumnNames().length; i++){
                    row.append(expenseCursor.getString(i)+" ");
                }
                Log.v(TAG, "DataBase row:"+row.toString());
                expenseCursor.moveToNext();
            }
        }
        mDbHelper.close();
    }

    private void toggleLogo(final Drawable newLogo, final int newColor, int duration) {

        //disappearing animation
        final AnimatorSet animatorSetDisappear = new AnimatorSet();
        animatorSetDisappear.setDuration(duration);
        animatorSetDisappear.playTogether(
                ObjectAnimator.ofFloat(headerLogo, "scaleX", 0),
                ObjectAnimator.ofFloat(headerLogo, "scaleY", 0)
        );

        //appearing animation
        final AnimatorSet animatorSetAppear = new AnimatorSet();
        animatorSetAppear.setDuration(duration);
        animatorSetAppear.playTogether(
                ObjectAnimator.ofFloat(headerLogo, "scaleX", 1),
                ObjectAnimator.ofFloat(headerLogo, "scaleY", 1)
        );

        //after the disappearing
        animatorSetDisappear.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                //modify the color image
                ((GradientDrawable) headerLogo.getBackground()).setColor(newColor);

                //modify the logo
                headerLogoContent.setImageDrawable(newLogo);

                //start the new annimation
                animatorSetAppear.start();
            }
        });

        //start the disappearing animation
        animatorSetDisappear.start();
    }


    /**
     * create the new form manually
     */
    private void createForm() {
        Intent i = new Intent(this, ExpenseEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }


    /**
     * take a picture
     */
    private void takePicture() {
        Intent i = new Intent(this, TakePicture.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }


    /**
     * Handles the requesting of the access fine permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestAccessFindLocationPermission() {
        Log.w(TAG, "Access find location  permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_ACCESS_FINE);
            return;
        }

        final Activity thisActivity = this;

        ActivityCompat.requestPermissions(thisActivity, permissions,
                RC_HANDLE_ACCESS_FINE);
    }
}