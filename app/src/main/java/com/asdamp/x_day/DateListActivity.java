package com.asdamp.x_day;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;

import com.asdamp.utility.ImageUtils;
import com.asdamp.utility.UserInfoUtility;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.asdamp.adapters.DateListAdapter;
import com.asdamp.database.DBAdapter;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.pixplicity.easyprefs.library.Prefs;
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import org.threeten.bp.LocalDate;

import java.io.IOException;
import java.security.KeyPairGenerator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DateListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.rv_date_list)
    RecyclerView mDateRecyclerView;
    private ArrayList<Data> dates;
    private Timer timer;
    DateListAdapter mListAdapter;
    private FirebaseAnalytics mFirebaseAnalytics;
    public Handler mHandler = new Handler() {

        synchronized public void handleMessage(Message msg) {
            aggiorna();
        }
    };
    private CollapsibleCalendar mCollapsibleCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_date_list);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN,null);
        FloatingActionButton fab = findViewById(R.id.fab);


        AdView mAdView = findViewById(R.id.adView);
        UserInfoUtility.loadAd(mAdView);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, Add.class);
            Date selectedDate = null;
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    selectedDate = formatter.parse("" + mCollapsibleCalendar.getSelectedDay().getDayOfMonth() + "-" + (mCollapsibleCalendar.getSelectedDay().getMonthValue()) + "-" + mCollapsibleCalendar.getSelectedDay().getYear());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            if (selectedDate != null) {
                intent.putExtra("data", (Parcelable) new Data(selectedDate.getTime()));
            }
            intent.putExtra("requestCode", Costanti.CREA_DATA);
            startActivityForResult(intent, Costanti.CREA_DATA);
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        dates=new ArrayList<>();
        mDateRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mDateRecyclerView.setLayoutManager(layoutManager);
        mListAdapter = new DateListAdapter(dates);
        mListAdapter.setOnListItemClickListener(new DateListAdapter.OnListItemClickListener() {
            @SuppressWarnings("ResultOfMethodCallIgnored")
            @Override
            public void onListItemClick(View v,int i) {
                DateListActivity.this.onListItemClick(i);
            }

            @Override
            public boolean onListItemLongClick(View v, int i) {

                PowerMenu powerMenu = new PowerMenu.Builder(DateListActivity.this)
                        .addItem(new PowerMenuItem(getString(R.string.delete), false))
                        .addItem(new PowerMenuItem(getString(R.string.share), false))

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
                            Costanti.getDB().deleteData(dates.get(i));
                            dates.remove(i);
                            mListAdapter.notifyItemRemoved(i);
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

        mCollapsibleCalendar = findViewById(R.id.calendarView);
        mCollapsibleCalendar.setCalendarListener(new CollapsibleCalendar.CalendarListener() {
            @Override
            public void onDaySelect() {
                LocalDate day = mCollapsibleCalendar.getSelectedDay();
                //todo selezionare date
                Log.i(getClass().getName(), "Selected Day: "
                        + day.getYear() + "/" + (day.getMonthValue()) + "/" + day.getDayOfMonth());
                mCollapsibleCalendar.collapse(500);
                for (int i = 0; i < dates.size(); i++) {
                    Data date = dates.get(i);
                    if (date.getDay() == day.getDayOfMonth()) {
                        mDateRecyclerView.smoothScrollToPosition(i);
                        break;
                    }
                }
            }

            @Override
            public void onItemClick(View view) {

            }

            @Override
            public void onDataUpdate() {

            }

            @Override
            public void onMonthChange() {

            }

            @Override
            public void onWeekChange(int i) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
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


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
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

        do {
            if (!cursor.moveToNext()) {
                return cursor.getCount();
            }
            try {
                dates.add(Data.leggi(cursor));
            } catch (IllegalArgumentException illegalargumentexception) {
                Log.e("lettura", illegalargumentexception.getMessage(),
                        illegalargumentexception);
            }
        } while (true);
    }

    private void rimuoviData(int i) {
        Costanti.getDB().deleteData((Data) dates.get(i));
        dates.remove(i);
        mDateRecyclerView.getAdapter().notifyItemRemoved(i);
        ((MainApplication) this.getApplication()).aggiornaWidget();
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Data data;
        switch (requestCode) {

            case Costanti.FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    DBAdapter db=Costanti.getDB();
                    Uri uri=intent.getData();
                    try {
                        db.importDB(intent.getData()/* Get the Uri of the selected file*/);
                    } catch (IOException e) {
                        Snackbar.make(mDateRecyclerView,this.getString(R.string.ImportError, uri.toString()), Snackbar.LENGTH_SHORT).show();
                    }
                    if (db.numRecordsDate() > 0)
                        Snackbar.make(mDateRecyclerView,this.getString(R.string.ImportSucceded),Snackbar.LENGTH_SHORT).show();
                    else
                        Snackbar.make(mDateRecyclerView,R.string.inport_error_file_corrupted, Snackbar.LENGTH_SHORT).show();
                    leggiDati();
                    aggiorna();

                }
                break;
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

                        break;
                }

        }

    }



    public boolean onCreateOptionsMenu(Menu menu1) {
        getMenuInflater().inflate(R.menu.activity_main, menu1);
        // activate autoreorder?

        return true;
    }

    protected void onListItemClick(int i) {
            Intent intent = new Intent(this, Add.class);
            intent.putExtra("requestCode", 1);
            intent.putExtra("data", (Parcelable) getDate(i));
            startActivityForResult(intent, 1);

    }

    private synchronized Data getDate(int i) {
        return dates.get(i);
    }

    public boolean onOptionsItemSelected(MenuItem menuitem) {
        return true;
    }

    protected void onResume() {
        super.onResume();
        this.resumeAutoUpdate();
        leggiDati();
        for(Data date:dates){

            mCollapsibleCalendar.addEventTag(LocalDate.of(date.getYear(),date.getMonth()+1,date.getDay()),getResources().getColor(R.color.md_white_1000));
        }
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
