package com.asdamp.x_day;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.asdamp.views.CheckableFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.asdamp.notification.XdayNotification;
import com.asdamp.utility.UserInfoUtility;
import com.github.zagum.switchicon.SwitchIconView;
import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.thebluealliance.spectrum.SpectrumDialog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import studio.carbonylgroup.textfieldboxes.ExtendedEditText;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

public class Add extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    private static final int REQUEST_IMAGE_CHOOSE = 500;
    @BindView(R.id.text_field_boxes)
    TextFieldBoxes mTextFildTitleBox;
    @BindView(R.id.etv_event_name)
    ExtendedEditText mEditTitle;
    @BindView(R.id.btn_date_select)
    Button mBtnSelectDate;
    @BindView(R.id.btn_time_select)
    Button mBtnSelectTime;
    @BindView(R.id.btn_color_select)
    Button mBtnColorSelect;
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
    CheckableFloatingActionButton mFabAddImage;
    @BindView(R.id.fab)
    FloatingActionButton mFabConfirm;
    @BindView(R.id.iv_date_image)
    ImageView mImageView;
    @BindView(R.id.notification_switch)
    JellyToggleButton mNotificationSwitch;
    @BindView(R.id.coordinator)
    CoordinatorLayout mCoordinator;
    @BindView(R.id.iv_curr_color)
    CircleImageView mIvCurrColor;
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

        if (b==null) {
            setupNewDate();
        }
        // altrimenti prende i valori proprio dalla data
        else {
            try {
                data=b.getParcelable("data");
            } catch (Exception e) {
                //Snackbar.make(mCoordinator, "Unable to retrieve information about this date", Snackbar.LENGTH_SHORT).show();
                setupNewDate();
            }


        }
        if(data==null){
            setupNewDate();

        }
        configViews();

        //set previous/default date and time
        mBtnSelectDate.setText(Costanti.dt.format(data.getTime()));
        mBtnSelectTime.setText(Costanti.tf.format(data.getTime()));
        mBtnColorSelect.setText(getString(R.string.date_color));

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
        if(data.getImage()!=null)
            mFabAddImage.setChecked(true);
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
        mFabAddImage.setOnClickListener(v -> {
             if(!mFabAddImage.isChecked()) {
                 Intent intent = new Intent();
                 intent.setType("image/*");

                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                     intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                 }
                 else
                     intent.setAction(Intent.ACTION_GET_CONTENT);

                 startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_CHOOSE);
             }
            else{
                 data.setImage(null);
                 mFabAddImage.setChecked(false);
                 mImageView.setImageDrawable(null);
             }
        });

        mFabConfirm.setOnClickListener(v -> {

            operazioniFinali(Costanti.TUTTO_BENE);

        });
        mBtnColorSelect.setOnClickListener(v-> this.showColorPicker());
        mIvCurrColor.setOnClickListener(v-> this.showColorPicker());

        ColorDrawable cd = new ColorDrawable(data.getColor());

        mIvCurrColor.setImageDrawable(cd);
        GlideApp.with(this).load(data.getImage()).fitCenter().into(mImageView);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CHOOSE && resultCode == RESULT_OK) {
            //this.data.setImage(Matisse.obtainResult(data).get(0));
            Uri selectedImage = data.getData();
            this.data.setImage(selectedImage);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                this.getContentResolver().takePersistableUriPermission(selectedImage, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            //Log.d("Matisse", "mSelected: " + mImageSelected);
            GlideApp.with(this).load(this.data.getImage()).fitCenter().into(mImageView);
            mFabAddImage.setChecked(true);


        }
    }
    private void showColorPicker() {
        new SpectrumDialog.Builder(this)
                .setColors(R.array.colors_selector)
                .setSelectedColor(data.getColor())
                .setDismissOnColorSelected(true)
                .setOutlineWidth(2)
                .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                    @Override public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                        if (positiveResult) {
                            data.setColor(color);
                            ColorDrawable cd = new ColorDrawable(data.getColor());

                            mIvCurrColor.setImageDrawable(cd);
                        }
                    }
                }).build().show(getSupportFragmentManager(), "dialog_demo_1");
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
        XdayNotification.scheduleNotificationById(this,data);
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
       // XdayNotification.sendNotification(this,1545927480456L);

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
