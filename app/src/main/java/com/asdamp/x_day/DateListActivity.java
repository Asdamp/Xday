package com.asdamp.x_day;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asdamp.adapters.DateListAdapter;
import com.asdamp.database.DBAdapter;
import com.asdamp.utility.ImageUtils;
import com.asdamp.utility.UserInfoUtility;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.pixplicity.easyprefs.library.Prefs;
import com.pixplicity.generate.OnFeedbackListener;
import com.pixplicity.generate.Rate;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DateListActivity extends AppCompatActivity
         {
             Rate rate;
             @BindView(R.id.coordinator)
             CoordinatorLayout coordinatorLayout;
    @BindView(R.id.rv_date_list)
    RecyclerView mDateRecyclerView;
    @BindView(R.id.rv_date_list_empty_view)
    View empty_view;
    private ArrayList<Data> dates;
    private Timer timer;
    DateListAdapter mListAdapter;
    private FirebaseAnalytics mFirebaseAnalytics;
    public Handler mHandler = new Handler() {

        synchronized public void handleMessage(Message msg) {
            aggiorna();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_date_list);

        ButterKnife.bind(this);

        boolean isFirstRun = Prefs.getBoolean("isFirstRun", true);

        if (isFirstRun) {
            //show sign up activity
            startActivity(new Intent(this, IntroActivity.class));

        }
        rateUs();
        rate.count();
        Prefs.putBoolean("isFirstRun", false);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN,null);
        FloatingActionButton fab = findViewById(R.id.fab);


        AdView mAdView = findViewById(R.id.adView);
        UserInfoUtility.loadAd(mAdView);
        fab.setOnClickListener(view -> addNewDate());
        empty_view.setOnClickListener(view -> addNewDate());


        dates=new ArrayList<>();
        mDateRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mDateRecyclerView.setLayoutManager(layoutManager);
        mListAdapter = new DateListAdapter(dates);
        mDateRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0)
                    fab.hide();
                else if (dy < 0)
                    fab.show();
            }
        });
        mListAdapter.setOnListItemClickListener(new DateListAdapter.OnListItemClickListener() {
            @SuppressWarnings("ResultOfMethodCallIgnored")
            @Override
            public void onListItemClick(View v,int i) {
                DateListActivity.this.onListItemClick(v,i);
            }

            @Override
            public boolean onListItemLongClick(View v, int i) {

                PowerMenu powerMenu = new PowerMenu.Builder(DateListActivity.this)
                        .addItem(new PowerMenuItem(getString(R.string.delete), R.drawable.ic_delete_black_24dp))
                        .addItem(new PowerMenuItem(getString(R.string.share), R.drawable.ic_share_black_24dp))

                        .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT) // Animation start point (TOP | LEFT)
                        .setMenuRadius(10f)
                        .setMenuShadow(10f)
                        .setSelectedEffect(true)

                        .setSelectedTextColor(Color.BLUE)
                        .setMenuColor(Color.WHITE)

                        .build();
                powerMenu.setOnMenuItemClickListener((position, item) -> {
                    powerMenu.dismiss();
                    switch (position){
                        case 0:
                           rimuoviData(i);
                            break;
                        case 1:
                            ImageUtils.shareView(DateListActivity.this,v);
                    }

                });
                powerMenu.showAsAnchorLeftTop(v);
                return true;
            }
        });
        mDateRecyclerView.setAdapter(mListAdapter);

    }

             private void rateUs() {

                 rate = new Rate.Builder(this)
                         // Trigger dialog after this many events (optional, defaults to 6)
                         .setTriggerCount(5)
                         // After dismissal, trigger again after this many events (optional, defaults to 30)
                         .setRepeatCount(5)
                         .setMinimumInstallTime((int) TimeUnit.DAYS.toMillis(3))   // Optional, defaults to 7 days
                         .setFeedbackAction(new OnFeedbackListener() {       // Optional
                             @Override
                             public void onFeedbackTapped() {
                                 Intent i = new Intent(Intent.ACTION_SEND);
                                 i.setType("message/rfc822");
                                 i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"altieriantonio.dev@gmail.com"});
                                 i.putExtra(Intent.EXTRA_SUBJECT, "Xday feedback");
                                 try {
                                     startActivity(Intent.createChooser(i, "Send mail..."));
                                 } catch (android.content.ActivityNotFoundException ex) {
                                     Toast.makeText(DateListActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                                 }
                             }

                             @Override
                             public void onRateTapped() {
                                 //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.play_store_link))));
                             }

                             @Override
                             public void onRequestDismissed(boolean dontAskAgain) {
                                   // Prefs.putBoolean("dismissed_forever",dontAskAgain);
                             }
                         })
                         .setSnackBarParent(coordinatorLayout)                            // Optional, shows dialog by default

                         .setLightTheme(true)                                // Default is dark
                         .setSwipeToDismissVisible(true)                    // Add this when using the Snackbar
                         // without a CoordinatorLayout as a parent.
                         .build();
                // rate.showRequest();
             }

             private void addNewDate() {
        Intent intent = new Intent(this, Add.class);
        intent.putExtra("requestCode", Costanti.CREA_DATA);
        startActivityForResult(intent, Costanti.CREA_DATA);
    }

    @Override
    public void onBackPressed() {
      super.onBackPressed();

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.time:
                if(Prefs.getString("sortby","time").equalsIgnoreCase("time")){
                    boolean currReverse=Prefs.getBoolean("reverse",false);
                    Prefs.putBoolean("reverse",!currReverse);

                }
                else {
                    Prefs.putString("sortby", "time");
                    Prefs.putString("sorttype", "asc");

                }
                sortList();


                break;
            case R.id.alphabetical:
                if(Prefs.getString("sortby","time").equalsIgnoreCase("alphabetical")){
                    boolean currReverse=Prefs.getBoolean("reverse",false);
                    Prefs.putBoolean("reverse",!currReverse);

                }
                else {
                    Prefs.putString("sortby", "alphabetical");
                    Prefs.putString("sorttype", "asc");
                }
                sortList();
                break;
            case R.id.color:
                if(Prefs.getString("sortby","time").equalsIgnoreCase("color")){
                    boolean currReverse=Prefs.getBoolean("reverse",false);
                    Prefs.putBoolean("reverse",!currReverse);
                }
                else {
                    Prefs.putString("sortby", "color");
                    Prefs.putString("sorttype", "asc");
                }
                sortList();
                break;
            case R.id.aboutMain:
                startActivity(new Intent(this, About.class));
                break;

            default:
                return false;
        }


        return true;
    }

    private void sortList() {
        if(Prefs.getString("sortby","time").equalsIgnoreCase("time")){
            mListAdapter.sortList(Data::compare,Prefs.getBoolean("reverse",false));
        }
        else if(Prefs.getString("sortby","time").equalsIgnoreCase("alphabetical")){
            mListAdapter.sortList((o1, o2) -> o1.getDescrizione().compareTo(o2.getDescrizione()),Prefs.getBoolean("reverse",false));
        }
        else if(Prefs.getString("sortby","time").equalsIgnoreCase("color")){
            mListAdapter.sortList((o1, o2) -> Integer.compare(o1.getColor(),o2.getColor()),Prefs.getBoolean("reverse",false));
        }
    }

    private synchronized void aggiorna() {
        mDateRecyclerView.getAdapter().notifyDataSetChanged();
    }

    // Get all the date in db
    private int leggiDati() {
        dates.clear();
        Cursor cursor = Costanti.getDB().fetchAllData();

        while (cursor.moveToNext()) {
            try {
                dates.add(Data.leggi(cursor));
            } catch (IllegalArgumentException illegalargumentexception) {
                Log.e("lettura", illegalargumentexception.getMessage(),
                        illegalargumentexception);
            }
        }
        if(dates.isEmpty()){
            mDateRecyclerView.setVisibility(View.GONE);
            empty_view.setVisibility(View.VISIBLE);
        }
        return cursor.getCount();

    }

    private void rimuoviData(int i) {
        Costanti.getDB().deleteData((Data) dates.get(i));
        dates.remove(i);
        mDateRecyclerView.getAdapter().notifyItemRemoved(i);
        ((MainApplication) this.getApplication()).aggiornaWidget();
        if(dates.isEmpty()){
            mDateRecyclerView.setVisibility(View.GONE);
            empty_view.setVisibility(View.VISIBLE);
        }

    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Data data;
        boolean dismissedForever=Prefs.getBoolean("dismissed_forever",false);
    //    rate.test();

        if(!dismissedForever) {
            rate.showRequest();
        }
        switch (requestCode) {


            case Costanti.MODIFICA_DATA:
                data=intent.getParcelableExtra("data");
                int index=dates.indexOf(data);
                switch (resultCode){
                    case Costanti.TUTTO_BENE:
                        Costanti.getDB().updateData(data);
                        dates.remove(index);
                        dates.add(index, data);
                        sortList();

                        break;
                    case Costanti.CANCELLA_DATA:
                        this.rimuoviData(index);break;
                }
                break;
            case Costanti.CREA_DATA:
                data=intent.getParcelableExtra("data");
                switch (resultCode){
                    case Costanti.TUTTO_BENE:
                        Costanti.getDB().createData(data);
                        dates.add(data);
                        sortList();
                        mFirebaseAnalytics.logEvent("NEW_DATE",null);
                        if(!dates.isEmpty()){
                            mDateRecyclerView.setVisibility(View.VISIBLE);
                            empty_view.setVisibility(View.GONE);
                        }
                        break;
                }

        }


    }



    public boolean onCreateOptionsMenu(Menu menu1) {
        getMenuInflater().inflate(R.menu.activity_main, menu1);
        // activate autoreorder?

        return true;
    }

    protected void onListItemClick(View v,int i) {
            Intent intent = new Intent(this, Add.class);
            intent.putExtra("requestCode", 1);
            intent.putExtra("data", (Parcelable) getDate(i));
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this,  v.findViewById(R.id.iv_date_image), "date_image");
            startActivityForResult(intent, 1,options.toBundle());

    }

    private synchronized Data getDate(int i) {
        return dates.get(i);
    }



    protected void onResume() {
        super.onResume();
        this.resumeAutoUpdate();
        leggiDati();

       /* if (lv == null) {
            lv = (ListView) findViewById(R.id.listaMainActivity);

        }*/
       sortList();
        aggiorna();

       /* boolean reorder = shprs.getBoolean(autoreorder, false);
        if (reorder && !date.isEmpty())/*
         * la riga sottostante riordina in
         * ordine temporale.
         */
            /*Collections.sort(date, getDate(0));*/

    }

  /*  private synchronized void pauseAutoUpdate() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }*/

    private void resumeAutoUpdate() {
        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    mHandler.obtainMessage(1).sendToTarget();
                }
            }, 0, 1000);
        }
    }





}
