package com.asdamp.x_day;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.asdamp.adapters.AddArrayAdapter;
import com.asdamp.notification.Notification;
import com.asdamp.utility.MultipleChoiceDialog;
import com.asdamp.utility.TextEditDialog;
import com.github.zagum.switchicon.SwitchIconView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

public class Add extends AppCompatActivity implements
		TextEditDialog.TextEditDialogInterface,
		MultipleChoiceDialog.MultipleChoiceDialogListener {


    private Data data;
    private AddArrayAdapter ada;
    @BindView(R.id.etv_event_name)
    ExtendedEditText mEditTitle;
    @BindView(R.id.btn_date_select)
    Button mBtnSelectDate;
    @BindView(R.id.btn_time_select)
    Button mBtnSelectTime;
    @BindView(R.id.toggle_year)
    View mToggleYear;
    @BindView(R.id.switch_year)
    SwitchIconView mSwitchYear;
    @Override
	protected void onCreate(Bundle s) {
		super.onCreate(s);

        this.setContentView(R.layout.activity_add_date);
        ButterKnife.bind(this);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar bar = getSupportActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
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
			    Crouton.makeText(this, "Unable to retrive information about this date", Style.ALERT).show();
			    setupNewDate();
			}
	           
	            
		}
        mBtnSelectDate.setText(Costanti.dt.format(data.getTime()));
        mBtnSelectTime.setText(Costanti.tf.format(data.getTime()));
        mBtnSelectDate.setOnClickListener(v -> {
            showDatePickerDialog();
        });
        mBtnSelectTime.setOnClickListener(v -> {
            showTimePickerDialog();
        });
        mToggleYear.setOnClickListener(v ->{
            mSwitchYear.switchState();
        });


        //ada = showLayout();
		/*ListView lista = (ListView) this.findViewById(R.id.listaAdd);
		lista.setAdapter(ada);
		lista.bringToFront(); //put the list on the parallax view
		aggiornaAdd();*/

		/*lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				onListItemClick(arg1, arg2);

			}

		});
		parallaxViews();*/
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


	private AddArrayAdapter showLayout(){
		Bundle bf[]= new Bundle[6];
		Bundle b=new Bundle();
		Resources res=this.getResources();
		//first option 
		b.putString(AddArrayAdapter.TITLE, res.getStringArray(R.array.ADDselezionaData)[0]);
		b.putString(AddArrayAdapter.SUBTITLE, res.getStringArray(R.array.ADDselezionaData)[1]);
		b.putInt(AddArrayAdapter.IMAGE, R.drawable.ic_action_calendar);
		bf[0]=(Bundle) b.clone();
		//second
		b.putString(AddArrayAdapter.TITLE, res.getStringArray(R.array.ADDselezionaOra)[0]);
		b.putString(AddArrayAdapter.SUBTITLE, res.getStringArray(R.array.ADDselezionaOra)[1]);
		b.putInt(AddArrayAdapter.IMAGE, R.drawable.ic_action_clock);
		bf[1]=(Bundle) b.clone();
		//third
		b.putString(AddArrayAdapter.TITLE, res.getStringArray(R.array.ADDselezionaParametri)[0]);
		b.putString(AddArrayAdapter.SUBTITLE, res.getStringArray(R.array.ADDselezionaParametri)[1]);
		b.putInt(AddArrayAdapter.IMAGE, R.drawable.ic_action_tick);
		bf[2]=(Bundle) b.clone();
		
		b.putString(AddArrayAdapter.TITLE, res.getStringArray(R.array.ADDselezionaDescrizione)[0]);
		b.putString(AddArrayAdapter.SUBTITLE, res.getStringArray(R.array.ADDselezionaDescrizione)[1]);
		b.putInt(AddArrayAdapter.IMAGE, R.drawable.ic_action_description);
		bf[3]=(Bundle) b.clone();
		
		b.putString(AddArrayAdapter.TITLE, res.getStringArray(R.array.ADDselezionaColore)[0]);
		b.putString(AddArrayAdapter.SUBTITLE, res.getStringArray(R.array.ADDselezionaColore)[1]);
		b.putInt(AddArrayAdapter.IMAGE, R.drawable.ic_action_color);
		bf[4]=(Bundle) b.clone();
		
		b.putString(AddArrayAdapter.TITLE, res.getStringArray(R.array.ADDselezionaPromemoria)[0]);
		b.putString(AddArrayAdapter.SUBTITLE, res.getStringArray(R.array.ADDselezionaPromemoria)[1]);
		b.putInt(AddArrayAdapter.IMAGE, R.drawable.ic_action_alarms);
		b.putBoolean(AddArrayAdapter.CHECK_BOX, true);
		if(!data.isAfterToday())  b.putBoolean(AddArrayAdapter.INACTIVE, true);//se la data � gi� passata, le notifiche vengono disattivate
		boolean notifica= data.isNotifica();
		Log.d("AddCheckedNotification",data.toString()+" : "+notifica);
		b.putBoolean(AddArrayAdapter.CHECKED, notifica);

		bf[5]=(Bundle) b.clone();
		AddArrayAdapter a=new AddArrayAdapter(Add.this, bf);
		return a;
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


	private void aggiornaAdd() {


		// converta le informazioni della data in data, poi in stringa
		// in base alla configurazione del sistema
		

		//aggiornamento Adapter. Imposta l'opzione di notifica attivata o disattivata in base 
		//a se la data � gi� passata oppure no
		aggiornaOpzioniAttive();

	}

	/**
	 * 
	 */
	private void aggiornaOpzioniAttive() {
		//ada.setInactive(5, !data.isAfterToday());
	}
	



    private  int returnDarkerColor(int color){
        float ratio = 1.0f - 0.2f;
        int a = (color >> 24) & 0xFF;
        int r = (int) (((color >> 16) & 0xFF) * ratio);
        int g = (int) (((color >> 8) & 0xFF) * ratio);
        int b = (int) ((color & 0xFF) * ratio);
        return (a << 24) | (r << 16) | (g << 8) | b;
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


	public void showColorPickerDialog(View v) {
		
		/*ColorPickerDialog colorDialog = new ColorPickerDialog(this, data.getColor());
		colorDialog.setOnColorChangedListener(this);
		colorDialog.setAlphaSliderVisible(true);
		colorDialog.show();*/
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

	public void showMultipleChoiceDialog(View v) {
		Bundle bundle1 = new Bundle();
        boolean aflag[] = new boolean[7];
        aflag[0] = data.getBoolAnni();
        aflag[1] = data.getBoolMesi();
        aflag[2] = data.getBoolSettimane();
        aflag[3] = data.getBoolGiorni();
        aflag[4] = data.getBoolOre();
        aflag[5] = data.getBoolMinuti();
        aflag[6] = data.getBoolSecondi();

        Resources r=v.getContext().getResources();
        String as[] = r.getStringArray(R.array.Parametri);
        MultipleChoiceDialog multiplechoicedialog = new MultipleChoiceDialog();
        bundle1.putCharSequenceArray("parametriString", as);
        bundle1.putBooleanArray("parametriBoolean", aflag);
        bundle1.putString("titolo", this.getString(R.string.Parametri));
        bundle1.putInt("obbligatori", 1);
        bundle1.putString("mdate", this.getString(R.string.minimoNdate));

        multiplechoicedialog.setArguments(bundle1);
        multiplechoicedialog.show(getSupportFragmentManager(), "parametri");
	}

	public void showTextEditDialog(View v) {
		Bundle p = new Bundle();
		p.putString(TextEditDialog.TITOLO, getString(R.string.Descrizione));
		p.putString(TextEditDialog.SOTTOTITOLO, getString(R.string.InserisciDescrizione));
		p.putString(TextEditDialog.STRINGA_BASE, data.getDescrizioneIfExists());
		DialogFragment textDialog = new TextEditDialog();
		textDialog.setArguments(p);
		textDialog.show(this.getSupportFragmentManager(), "testo");
	}

	public void onMultipleDialogPositiveClick(boolean[] parametri) {
		data.setPeriodType(parametri[0],parametri[1],parametri[2],parametri[3],parametri[4],parametri[5],parametri[6]);
	}

	public void onMultipleDialogNegativeClick(
			android.support.v4.app.DialogFragment dialog) {

	}

	public void onColorChanged(int color) {
		data.setColor(color);
	}
	public void OnTextEditDialogPositiveClick(String t) {
		data.setDescription(t);
	}




}
