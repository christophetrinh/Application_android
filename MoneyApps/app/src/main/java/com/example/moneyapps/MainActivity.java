package com.example.moneyapps;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;

import java.util.Arrays;

public class MainActivity extends ActionBarActivity {

    private static final int ACTIVITY_CREATE = 0;
    MaterialViewPager materialViewPager;
    View headerLogo;
    ImageView headerLogoContent;
    private DataBaseAdapter mDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //DATE BASE
        mDbHelper = new DataBaseAdapter(this);
        mDbHelper.open();

        final FloatingActionButton actionA = (FloatingActionButton) findViewById(R.id.action_a);
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //actionA.setTitle("Action A clicked");
                Log.v("Button", "Clik on button A");
                takePicture();
            }
        });

        final FloatingActionButton actionB = (FloatingActionButton) findViewById(R.id.action_b);
        actionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //actionB.setTitle("Action B clicked");
                Log.v("Button", "Clik on button B");
                createForm();
                debugDatabase();
            }
        });

        //4 onglets
        final int tabCount = 5;

        //les vues définies dans @layout/header_logo
        headerLogo = findViewById(R.id.headerLogo);
        headerLogoContent = (ImageView) findViewById(R.id.headerLogoContent);

        //le MaterialViewPager
        this.materialViewPager = (MaterialViewPager) findViewById(R.id.materialViewPager);
        this.materialViewPager.getViewPager().setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                //je créé pour chaque onglet un RecyclerViewFragment
                return RecyclerViewFragment.newInstance();
            }

            @Override
            public int getCount() {
                return tabCount;
            }

            //le titre à afficher pour chaque page
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

                //seulement si la page est différente
                if (oldItemPosition != position) {
                    oldItemPosition = position;

                    //définir la nouvelle couleur et les nouvelles images
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

                    //puis modifier les images/couleurs
                    int fadeDuration = 200;
                    materialViewPager.setColor(color, fadeDuration);
                    materialViewPager.setImageUrl(imageUrl, fadeDuration);
                    toggleLogo(newDrawable, color, fadeDuration);

                }
            }
        });

        //permet au viewPager de garder 4 pages en mémoire (à ne pas utiliser sur plus de 4 pages !)
        this.materialViewPager.getViewPager().setOffscreenPageLimit(tabCount);
        //relie les tabs au viewpager
        this.materialViewPager.getPagerTitleStrip().setViewPager(this.materialViewPager.getViewPager());
    }

    private void debugDatabase() {
        Cursor expenseCursor = mDbHelper.fetchAllExpense();
        if (expenseCursor != null) {
            // Print
            Log.v("DataBase Columns: ", Arrays.toString(expenseCursor.getColumnNames()));
            Log.v("DataBase rows number:", String.valueOf(expenseCursor.getCount()));

            expenseCursor.moveToFirst();
            while(!expenseCursor.isAfterLast()) {
                // Print only retail
                //Log.v("DataBase retail:", String.valueOf(expenseCursor.getString(expenseCursor.getColumnIndex("Retail"))));
                // Print all
                StringBuilder row = new StringBuilder();
                for(int i = 0; i < expenseCursor.getColumnNames().length; i++){
                    row.append(expenseCursor.getString(i)+" ");
                }
                Log.v("DataBase row:", row.toString());
                expenseCursor.moveToNext();
            }
        }
    }

    private void toggleLogo(final Drawable newLogo, final int newColor, int duration) {

        //animation de disparition
        final AnimatorSet animatorSetDisappear = new AnimatorSet();
        animatorSetDisappear.setDuration(duration);
        animatorSetDisappear.playTogether(
                ObjectAnimator.ofFloat(headerLogo, "scaleX", 0),
                ObjectAnimator.ofFloat(headerLogo, "scaleY", 0)
        );

        //animation d'apparition
        final AnimatorSet animatorSetAppear = new AnimatorSet();
        animatorSetAppear.setDuration(duration);
        animatorSetAppear.playTogether(
                ObjectAnimator.ofFloat(headerLogo, "scaleX", 1),
                ObjectAnimator.ofFloat(headerLogo, "scaleY", 1)
        );

        //après la disparition
        animatorSetDisappear.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                //modifie la couleur du cercle
                ((GradientDrawable) headerLogo.getBackground()).setColor(newColor);

                //modifie l'image contenue dans le cercle
                headerLogoContent.setImageDrawable(newLogo);

                //démarre l'animation d'apparition
                animatorSetAppear.start();
            }
        });

        //démarre l'animation de disparition
        animatorSetDisappear.start();
    }

    private void createForm() {
        Intent i = new Intent(this, ExpenseEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }


    private void takePicture() {
        Intent i = new Intent(this, TakePicture.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }
}