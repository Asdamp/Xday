package com.asdamp.x_day;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.asdamp.notification.Notification;
import com.asdamp.utility.Glide4Engine;
import com.asdamp.utility.UserInfoUtility;
import com.github.zagum.switchicon.SwitchIconView;
import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import butterknife.BindView;
import butterknife.ButterKnife;
import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

public class Add extends AppCompatActivity {


    private static final int REQUEST_IMAGE_CHOOSE = 500;
    @BindView(R.id.etv_event_name)
    ExtendedEditText mEditTitle;
    @BindView(R.id.btn_date_select)
    Button mBtnSelectDate;
    @BindView(R.id.btn_time_select)
    Button mBtnSelectTime;
    @BindView(R.id.toggle_year)
    View mToggleYear;
    @BindView(R.id.toggle_month)
    View mToggleMonth;
    @BindView(R.id.toggle_day)
    View mToggleDay;
    @BindView(R.id.toggle_week)
    View mToggleWeek;
    @BindView(R.id.toggle_hour)
    View mToggleHour;
    @BindView(R.id.toggle_minute)
    View mToggleMinute;
    @BindView(R.id.toggle_second)
    View mToggleSecond;
    @BindView(R.id.switch_year)
    SwitchIconView mSwitchYear;
    @BindView(R.id.switch_month)
    SwitchIconView mSwitchMonth;
    @BindView(R.id.switch_week)
    SwitchIconView mSwitchWeek;
    @BindView(R.id.switch_day)
    SwitchIconView mSwitchDay;
    @BindView(R.id.switch_hour)
    SwitchIconView mSwitchHour;
    @BindView(R.id.switch_minute)
    SwitchIconView mSwitchMinute;
    @BindView(R.id.switch_second)
    SwitchIconView mSwitchSecond;
    @BindView(R.id.fab_add_image)
    FloatingActionButton mFabAddImage;
    @BindView(R.id.fab)
    FloatingActionButton mFabConfirm;
    @BindView(R.id.iv_date_image)
    ImageView mImageView;
    @BindView(R.id.notification_switch)
    JellyToggleButton mNotificationSwitch;
    @BindView(R.id.coordinator)
    CoordinatorLayout mCoordinator;

    private Data data;
  //  private Uri mImageSelected;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);

        this.setContentView(R.layout.activity_add_date);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        Bundle b=this.getIntent().getExtras();

        if (b==null || b.getInt("requestCode") == Costanti.CREA_DATA) {
            setupNewDate();
        }
        // altrimenti prende i valori proprio dalla data
        else {
            try {
                data=b.getParcelable("data");
                Log.d("Modify", data.toString());
            } catch (Exception e) {
                Snackbar.make(mCoordinator, "Unable to retrieve information about this date", Snackbar.LENGTH_SHORT).show();
                setupNewDate();
            }


        }

        configViews();

        //set previous/default date and time
        mBtnSelectDate.setText(Costanti.dt.format(data.getTime()));
        mBtnSelectTime.setText(Costanti.tf.format(data.getTime()));

        //set previous/default date and time
        mSwitchYear.setIconEnabled(data.getBoolAnni());
        mSwitchMonth.setIconEnabled(data.getBoolMesi());
        mSwitchWeek.setIconEnabled(data.getBoolSettimane());
        mSwitchDay.setIconEnabled(data.getBoolGiorni());
        mSwitchHour.setIconEnabled(data.getBoolOre());
        mSwitchMinute.setIconEnabled(data.getBoolMinuti());
        mSwitchSecond.setIconEnabled(data.getBoolSecondi());

        mEditTitle.setText(data.getDescrizioneIfExists());

        mNotificationSwitch.setCheckedImmediately(data.isNotifica());
    }

    private void configViews() {
        mBtnSelectDate.setOnClickListener(v -> {
            showDatePickerDialog();
        });
        mBtnSelectTime.setOnClickListener(v -> {
            showTimePickerDialog();
        });
        mToggleYear.setOnClickListener(v ->{
            mSwitchYear.switchState();
            UserInfoUtility.smallVibration(this);

        });
        mToggleMonth.setOnClickListener(v ->{
            mSwitchMonth.switchState();

            UserInfoUtility.smallVibration(this);

        });
        mToggleWeek.setOnClickListener(v ->{
            mSwitchWeek.switchState();
            UserInfoUtility.smallVibration(this);

        });
        mToggleDay.setOnClickListener(v ->{
            mSwitchDay.switchState();
            UserInfoUtility.smallVibration(this);

        });
        mToggleHour.setOnClickListener(v ->{
            mSwitchHour.switchState();
            UserInfoUtility.smallVibration(this);

        });
        mToggleMinute.setOnClickListener(v ->{
            mSwitchMinute.switchState();
            UserInfoUtility.smallVibration(this);
        });
        mToggleSecond.setOnClickListener(v ->{
            mSwitchSecond.switchState();
            UserInfoUtility.smallVibration(this);

        });
        mFabAddImage.setOnClickListener(v -> Matisse.from(Add.this)
                .choose(MimeType.ofAll())
                .maxSelectable(1)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)

                .thumbnailScale(0.85f)
                .imageEngine(new Glide4Engine())
                .forResult(REQUEST_IMAGE_CHOOSE));
        mFabConfirm.setOnClickListener(v -> {
            operazioniFinali(Costanti.TUTTO_BENE);
        });

        GlideApp.with(this).load(data.getImage()).fitCenter().into(mImageView);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CHOOSE && resultCode == RESULT_OK) {
            this.data.setImage(Matisse.obtainResult(data).get(0));
            //Log.d("Matisse", "mSelected: " + mImageSelected);
            GlideApp.with(this).load(this.data.getImage()).fitCenter().into(mImageView);
        }
    }

    public void setupNewDate() {
        data=new Data(false,false,false,true,false,false,false);
        data.set(Data.HOUR_OF_DAY, 0);
        data.set(Data.MINUTE, 0);
        data.set(Data.SECOND, 0);
    }

    @Override
    protected void onResume() {

        super.onResume();

        //creaBundle();
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                operazioniFinali(Costanti.ANNULLA);
                break;
            }

            case R.id.Conferma: {
                operazioniFinali(Costanti.TUTTO_BENE);
                break;
            }
            case R.id.Elimina: {
                operazioniFinali(Costanti.CANCELLA_DATA);
                break;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NewApi")
    private void operazioniFinali(int resultCode) {
        data.setPeriodType(mSwitchYear.isIconEnabled(),mSwitchMonth.isIconEnabled(),mSwitchWeek.isIconEnabled(),mSwitchDay.isIconEnabled(),mSwitchHour.isIconEnabled(),mSwitchMinute.isIconEnabled(),mSwitchSecond.isIconEnabled());
        data.setNotification(mNotificationSwitch.isChecked());
        data.setDescription(mEditTitle.getText().toString());
        setResult(resultCode, this.getIntent().putExtra("data", (Parcelable) data));
        Costanti.updateWidget(this);
        Notification.scheduleNotificationById(this,data);
        finish();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.activity_add, menu);
		/*MenuItem shareItem=menu.findItem(R.id.share);
		ShareActionProvider sap=(ShareActionProvider) shareItem.getActionProvider();
		sap.setShareHistoryFileName("xday_share_history.xml");
		String text;
		String subject;
		subject=data.getDescrizione();
		text=data.getShareText(this);
		ShareUtility.shareText(sap, text, subject);*/
        return true;
    }

    @Override
    public void onBackPressed() {
        operazioniFinali(Costanti.ANNULLA);
    }




    /**
     *
     */
    private void aggiornaOpzioniAttive() {
       // mNotificationSwitch.setEnabled(!data.isAfterToday());
    }







    public void showTimePickerDialog() {
        TimePickerDialog.OnTimeSetListener onTimeSet= (view, hourOfDay, minute, second) -> {
            Log.d("Hour",hourOfDay+"");
            Log.d("Minute",minute+"");

            data.setHour(hourOfDay);
            data.setMinute(minute);
            mBtnSelectTime.setText(Costanti.tf.format(data.getTime()));

            aggiornaOpzioniAttive();
        };
        TimePickerDialog dp= TimePickerDialog.newInstance(onTimeSet,data.getHour(),data.getMinute(),true);
        dp.setVersion(TimePickerDialog.Version.VERSION_2);
        dp.show(getFragmentManager(), getString(R.string.seleziona_ora));
    }

    public void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener onDateSet= (dialog, year, monthOfYear, dayOfMonth) -> {
            data.setYear(year);
            data.setMonth(monthOfYear);
            data.setDay(dayOfMonth);
            mBtnSelectDate.setText(Costanti.dt.format(data.getTime()));
            aggiornaOpzioniAttive();
        };
        DatePickerDialog dp= DatePickerDialog.newInstance(onDateSet,data.getYear(),data.getMonth(),data.getDay());
        dp.setVersion(DatePickerDialog.Version.VERSION_2);
        dp.show(getFragmentManager(), getString(R.string.seleziona_data));



    }










}
