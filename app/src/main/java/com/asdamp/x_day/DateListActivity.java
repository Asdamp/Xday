package com.asdamp.x_day;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;

import com.asdamp.utility.ImageUtils;
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
import android.widget.Adapter;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.asdamp.adapters.DateListAdapter;
import com.asdamp.database.DBAdapter;
import com.asdamp.database.DBHelper;
import com.shrikanthravi.collapsiblecalendarview.data.Day;
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    public Handler mHandler = new Handler() {

        synchronized public void handleMessage(Message msg) {
            aggiorna();
        }
    };
    private CollapsibleCalendar mCollapsibleCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_list);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, Add.class);
            Date selectedDate = null;
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            try {
                selectedDate=formatter.parse(""+mCollapsibleCalendar.getSelectedDay().getDay()+"-"+(mCollapsibleCalendar.getSelectedDay().getMonth()+1)+"-"+mCollapsibleCalendar.getSelectedDay().getYear());
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
        DateListAdapter ad=new DateListAdapter(dates);
        ad.setOnListItemClickListener(new DateListAdapter.OnListItemClickListener() {
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
                            ad.notifyItemRemoved(i);
                            break;
                        case 1:
                            ImageUtils.shareView(DateListActivity.this,v);
                    }

                });
                powerMenu.showAsAnchorLeftTop(v);
                return true;
            }
        });
        mDateRecyclerView.setAdapter(ad);

        mCollapsibleCalendar = findViewById(R.id.calendarView);
        mCollapsibleCalendar.setCalendarListener(new CollapsibleCalendar.CalendarListener() {
            @Override
            public void onDaySelect() {
                Day day = mCollapsibleCalendar.getSelectedDay();
                //todo selezionare date
                Log.i(getClass().getName(), "Selected Day: "
                        + day.getYear() + "/" + (day.getMonth() + 1) + "/" + day.getDay());
                mCollapsibleCalendar.collapse(500);
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        switch (menuitem.getItemId()) {
            case R.id.Temporale:
                boolean ch = !menuitem.isChecked();
                /*shprs.edit().putBoolean(autoreorder, ch).commit();
                menuitem.setChecked(ch);
                if (ch && !date.isEmpty()) {
                    Collections.sort(date, getDate(0));
                    for (int i = 1; i <= date.size(); i++) {
                        long msTemp = getDate(i - 1).getMillisecondiIniziali();
                        Costanti.getDB().cambiaPosizione(msTemp, i);
                    }
                    ((MainApplication) this.getApplication()).aggiornaWidget();
                }
                mainMenu.findItem(R.id.Manuale).setVisible(!ch);*/
                break;
            case R.id.menu_settings:
                Intent intent = new Intent(this,Add.class);
                intent.putExtra("requestCode", 4);
                this.pauseAutoUpdate();
                startActivityForResult(intent, 4);
                break;
            case R.id.Aggiorna:
                aggiorna();
                break;
            case R.id.aboutMain:
                startActivity(new Intent(this, About.class));
                break;
		/*case R.id.Manuale:
			menuitem.setVisible(false);
			this.pauseAutoUpdate();
			lv.setDragEnabled(true);
			mainMenu.findItem(R.id.Fine_riordinamento).setVisible(true);
			vista.ModRiordina(true);
			vista.notifyDataSetChanged();
			break;
		case R.id.Fine_riordinamento:
			menuitem.setVisible(false);
			lv.setDragEnabled(false);
			mainMenu.findItem(R.id.Manuale).setVisible(true);
			vista.ModRiordina(false);
			vista.notifyDataSetChanged();
			((MainApplication) this.getApplication()).aggiornaWidget();
			this.resumeAutoUpdate();
			break;*/
            case R.id.Esporta:
                File dbf = this.getDatabasePath(DBHelper.DATABASE_NAME);
                File dest = new File(Environment.getExternalStorageDirectory(),
                        "Xdaydb.xdy");

                if (dbf.exists()) {
                    FileChannel src = null;
                    FileChannel dst = null;
                    try {
                        src = new FileInputStream(dbf).getChannel();
                        dst = new FileOutputStream(dest).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                        Snackbar.make(
                                mDateRecyclerView,
                                this.getString(R.string.ExportSucceded,
                                        dest.getAbsolutePath()), Snackbar.LENGTH_SHORT).show();

                    } catch (FileNotFoundException e) {
                        Snackbar.make(
                                mDateRecyclerView, this.getText(R.string.ExportError),
                                Snackbar.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (IOException e) {
                        Snackbar.make(
                                mDateRecyclerView, this.getText(R.string.ExportError),
                                Snackbar.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                }

                break;
            case R.id.Importa: {
                this.showFileChooser();

                break;
            }
            default:
                return false;
        }
        return true;
    }

    protected void onResume() {
        super.onResume();
        this.resumeAutoUpdate();
        leggiDati();
        for(Data date:dates){
            mCollapsibleCalendar.addEventTag(date.getYear(),date.getMonth(),date.getDay(),getResources().getColor(R.color.md_white_1000));
        }
       /* if (lv == null) {
            lv = (ListView) findViewById(R.id.listaMainActivity);

        }*/
        aggiorna();
       /* boolean reorder = shprs.getBoolean(autoreorder, false);
        if (reorder && !date.isEmpty())/*
         * la riga sottostante riordina in
         * ordine temporale.
         */
            /*Collections.sort(date, getDate(0));*/

    }

    private synchronized void pauseAutoUpdate() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

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

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent,
                            this.getString(R.string.importFile)),
                    Costanti.FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }



}
