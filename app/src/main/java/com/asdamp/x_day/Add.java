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

import com.asdamp.x_day.databinding.ActivityAddDateBinding;
import com.asdamp.x_day.databinding.AddBinding;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.asdamp.notification.XdayNotification;
import com.asdamp.utility.UserInfoUtility;
import com.asdamp.x_day.databinding.DateParametersSwitchersBinding;
import com.thebluealliance.spectrum.SpectrumDialog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

public class Add extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private static final int REQUEST_IMAGE_CHOOSE = 500;
    private AddBinding innerBinding;
    private ActivityAddDateBinding binding;
    //    @BindView(R.id.text_field_boxes)
//    TextFieldBoxes mTextFildTitleBox;
//    @BindView(R.id.etv_event_name)
//    ExtendedEditText mEditTitle;
//    @BindView(R.id.btn_date_select)
//    Button mBtnSelectDate;
//    @BindView(R.id.btn_time_select)
//    Button mBtnSelectTime;
//    @BindView(R.id.btn_color_select)
//    Button mBtnColorSelect;
//    @BindView(R.id.toggle_year)
//    View mToggleYear;
//    @BindView(R.id.toggle_month)
//    View mToggleMonth;
//    @BindView(R.id.toggle_day)
//    View mToggleDay;
//    @BindView(R.id.toggle_week)
//    View mToggleWeek;
//    @BindView(R.id.toggle_hour)
//    View mToggleHour;
//    @BindView(R.id.toggle_minute)
//    View mToggleMinute;
//    @BindView(R.id.toggle_second)
//    View mToggleSecond;
//    @BindView(R.id.switch_year)
//    SwitchIconView mSwitchYear;
//    @BindView(R.id.switch_month)
//    SwitchIconView mSwitchMonth;
//    @BindView(R.id.switch_week)
//    SwitchIconView mSwitchWeek;
//    @BindView(R.id.switch_day)
//    SwitchIconView mSwitchDay;
//    @BindView(R.id.switch_hour)
//    SwitchIconView mSwitchHour;
//    @BindView(R.id.switch_minute)
//    SwitchIconView mSwitchMinute;
//    @BindView(R.id.switch_second)
//    SwitchIconView mSwitchSecond;
//    @BindView(R.id.fab_add_image)
//    CheckableFloatingActionButton mFabAddImage;
//    @BindView(R.id.fab)
//    FloatingActionButton mFabConfirm;
//    @BindView(R.id.iv_date_image)
//    ImageView mImageView;
//    @BindView(R.id.notification_switch)
//    JellyToggleButton mNotificationSwitch;
//    @BindView(R.id.coordinator)
//    CoordinatorLayout mCoordinator;
//    @BindView(R.id.iv_curr_color)
//    CircleImageView mIvCurrColor;
    private Data data;
    int requestCode;

    //  private Uri mImageSelected;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        binding = ActivityAddDateBinding.inflate(getLayoutInflater());
        innerBinding = binding.appBarLayout.addLayout;
        this.setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        UserInfoUtility.loadAd(findViewById(R.id.adView));
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowTitleEnabled(false);
            bar.setTitle("");
        }
        Bundle b = this.getIntent().getExtras();

        if (b == null) {
            setupNewDate();
        }
        // altrimenti prende i valori proprio dalla data
        else {
            try {
                data = b.getParcelable("data");
            } catch (Exception e) {
                //Snackbar.make(mCoordinator, "Unable to retrieve information about this date", Snackbar.LENGTH_SHORT).show();
                setupNewDate();
            }


        }
        if (data == null) {
            setupNewDate();

        }
        requestCode = this.getIntent().getIntExtra("requestCode", Costanti.CREA_DATA);
        configViews();

        //set previous/default date and time
        innerBinding.btnDateSelect.setText(Costanti.dt.format(data.getTime()));
        innerBinding.btnTimeSelect.setText(Costanti.tf.format(data.getTime()));
        innerBinding.btnColorSelect.setText(getString(R.string.date_color));

        //set previous/default date and time
        innerBinding.include2.switchYear.setIconEnabled(data.getBoolAnni());
        innerBinding.include2.switchMonth.setIconEnabled(data.getBoolMesi());
        innerBinding.include2.switchWeek.setIconEnabled(data.getBoolSettimane());
        innerBinding.include2.switchDay.setIconEnabled(data.getBoolGiorni());
        innerBinding.include2.switchHour.setIconEnabled(data.getBoolOre());
        innerBinding.include2.switchMinute.setIconEnabled(data.getBoolMinuti());
        innerBinding.include2.switchSecond.setIconEnabled(data.getBoolSecondi());

        innerBinding.etvEventName.setText(data.getDescrizioneIfExists());

        innerBinding.notificationSwitch.setCheckedImmediately(data.isNotifica());
        if (data.getImage() != null)
            binding.appBarLayout.fabAddImage.setChecked(true);
    }

    private void configViews() {
        innerBinding.btnDateSelect.setOnClickListener(v -> {
            showDatePickerDialog();
        });
        innerBinding.btnTimeSelect.setOnClickListener(v -> {
            showTimePickerDialog();
        });
        innerBinding.include2.toggleYear.setOnClickListener(v -> {
            innerBinding.include2.switchYear.switchState();
            UserInfoUtility.smallVibration(this);

        });
        innerBinding.include2.toggleMonth.setOnClickListener(v -> {
            innerBinding.include2.switchMonth.switchState();

            UserInfoUtility.smallVibration(this);

        });
        innerBinding.include2.toggleWeek.setOnClickListener(v -> {
            innerBinding.include2.switchWeek.switchState();
            UserInfoUtility.smallVibration(this);

        });
        innerBinding.include2.toggleDay.setOnClickListener(v -> {
            innerBinding.include2.switchDay.switchState();
            UserInfoUtility.smallVibration(this);

        });
        innerBinding.include2.toggleHour.setOnClickListener(v -> {
            innerBinding.include2.switchHour.switchState();
            UserInfoUtility.smallVibration(this);

        });
        innerBinding.include2.toggleMinute.setOnClickListener(v -> {
            innerBinding.include2.switchMinute.switchState();
            UserInfoUtility.smallVibration(this);
        });
        innerBinding.include2.toggleSecond.setOnClickListener(v -> {
            innerBinding.include2.switchSecond.switchState();
            UserInfoUtility.smallVibration(this);

        });
        binding.appBarLayout.fabAddImage.setOnClickListener(v -> {
            if (!binding.appBarLayout.fabAddImage.isChecked()) {
                Intent intent = new Intent();
                intent.setType("image/*");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                } else
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_CHOOSE);
            } else {
                data.setImage(null);
                binding.appBarLayout.fabAddImage.setChecked(false);
                binding.appBarLayout.ivDateImage.setImageDrawable(null);
            }
        });

        binding.appBarLayout.fab.setOnClickListener(v -> {

            operazioniFinali(Costanti.TUTTO_BENE);

        });
        innerBinding.btnColorSelect.setOnClickListener(v -> this.showColorPicker());
        innerBinding.ivCurrColor.setOnClickListener(v -> this.showColorPicker());

        ColorDrawable cd = new ColorDrawable(data.getColor());

        innerBinding.ivCurrColor.setImageDrawable(cd);
        GlideApp.with(this).load(data.getImage()).fitCenter().into(binding.appBarLayout.ivDateImage);

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
            GlideApp.with(this).load(this.data.getImage()).fitCenter().into(binding.appBarLayout.ivDateImage);
            binding.appBarLayout.fabAddImage.setChecked(true);


        }
    }

    private void showColorPicker() {
        new SpectrumDialog.Builder(this)
                .setColors(R.array.colors_selector)
                .setSelectedColor(data.getColor())
                .setDismissOnColorSelected(true)
                .setOutlineWidth(2)
                .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                        if (positiveResult) {
                            data.setColor(color);
                            ColorDrawable cd = new ColorDrawable(data.getColor());

                            innerBinding.ivCurrColor.setImageDrawable(cd);
                        }
                    }
                }).build().show(getSupportFragmentManager(), "dialog_demo_1");
    }

    public void setupNewDate() {
        data = new Data(false, false, false, true, false, false, false);
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
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            operazioniFinali(Costanti.ANNULLA);
        } else if (itemId == R.id.Conferma) {
            operazioniFinali(Costanti.TUTTO_BENE);
        } else if (itemId == R.id.Elimina) {
            operazioniFinali(Costanti.CANCELLA_DATA);
        } else {
            return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NewApi")
    private void operazioniFinali(int resultCode) {
        DateParametersSwitchersBinding dataBinding = innerBinding.include2;
        data.setPeriodType(dataBinding.switchYear.isIconEnabled(), dataBinding.switchMonth.isIconEnabled(), dataBinding.switchWeek.isIconEnabled(), dataBinding.switchDay.isIconEnabled(), dataBinding.switchHour.isIconEnabled(), dataBinding.switchMinute.isIconEnabled(), dataBinding.switchSecond.isIconEnabled());
        data.setNotification(innerBinding.notificationSwitch.isChecked());
        data.setDescription(innerBinding.etvEventName.getText().toString());
        if (resultCode == Costanti.TUTTO_BENE) {
            if (requestCode == Costanti.CREA_DATA) {
                Costanti.getDB().createData(data);

            } else if (requestCode == Costanti.MODIFICA_DATA) {
                Costanti.getDB().updateData(data);

            }
        }

        setResult(resultCode, this.getIntent().putExtra("data", (Parcelable) data));
        Costanti.updateWidget(this);
        XdayNotification.scheduleNotificationById(this, data);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.activity_add, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        // XdayNotification.sendNotification(this,1545927480456L);

        operazioniFinali(Costanti.ANNULLA);
        super.onBackPressed();
    }


    /**
     *
     */
    private void aggiornaOpzioniAttive() {
        // mNotificationSwitch.setEnabled(!data.isAfterToday());
    }


    public void showTimePickerDialog() {
        TimePickerDialog.OnTimeSetListener onTimeSet = (view, hourOfDay, minute, second) -> {
            Log.d("Hour", hourOfDay + "");
            Log.d("Minute", minute + "");

            data.setHour(hourOfDay);
            data.setMinute(minute);
            innerBinding.btnDateSelect.setText(Costanti.tf.format(data.getTime()));

            aggiornaOpzioniAttive();
        };
        TimePickerDialog dp = TimePickerDialog.newInstance(onTimeSet, data.getHour(), data.getMinute(), true);
        dp.setVersion(TimePickerDialog.Version.VERSION_2);
        dp.show(getSupportFragmentManager(), getString(R.string.seleziona_ora));
    }

    public void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener onDateSet = (dialog, year, monthOfYear, dayOfMonth) -> {
            data.setYear(year);
            data.setMonth(monthOfYear);
            data.setDay(dayOfMonth);
            innerBinding.btnTimeSelect.setText(Costanti.dt.format(data.getTime()));
            aggiornaOpzioniAttive();
        };
        DatePickerDialog dp = DatePickerDialog.newInstance(onDateSet, data.getYear(), data.getMonth(), data.getDay());
        dp.setVersion(DatePickerDialog.Version.VERSION_2);
        dp.show(getSupportFragmentManager(), getString(R.string.seleziona_data));


    }


}
