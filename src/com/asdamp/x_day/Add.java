package com.asdamp.x_day;

import uk.co.chrisjenx.paralloid.Parallaxor;
import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.android.datetimepicker.time.TimePickerDialog.OnTimeSetListener;
import com.asdamp.notification.Notification;
import com.asdamp.utility.*;
import com.asdamp.x_day.R;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.google.ads.AdView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class Add extends SherlockFragmentActivity implements
		TextEditDialog.TextEditDialogInterface,
		MultipleChoiceDialog.MultipleChoiceDialogListener,
		
		ColorPickerDialog.OnColorChangedListener{


	@Override
	protected void onCreate(Bundle s) {
		super.onCreate(s);
		

		ActionBar bar = getSupportActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		// carica il layout con il pulsante a sinistra

		View actionCustomView = getLayoutInflater().inflate(
				R.layout.pulsante_sinistra, null);
		bar.setCustomView(actionCustomView, new ActionBar.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				Gravity.LEFT));

		int change = bar.getDisplayOptions() ^ ActionBar.DISPLAY_SHOW_CUSTOM;
		bar.setDisplayOptions(change, ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_USE_LOGO);
 
		//
		this.setContentView(R.layout.add); 
		AdView ads=(AdView)this.findViewById(R.id.adView);
		StartupUtility.getInstance(this).showAdMobAds(ads);
		Bundle b=this.getIntent().getExtras();
		// se � stato chiamato da main activity allora setta dei valori di
		// default
		if (b.getInt("requestCode") == Costanti.CREA_DATA) {
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
		ada = showLayout();
		ListView lista = (ListView) this.findViewById(R.id.listaAdd);
		lista.setAdapter(ada);
		lista.bringToFront(); //put the list on the parallax view
		aggiornaAdd();
		Button pulsante = (Button) findViewById(R.id.buttoneriassuntivo);
		pulsante.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showTextEditDialog(v);

			}
		});

		lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				onListItemClick(arg1, arg2);

			}

		});
		parallaxViews();
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



	/**
	 * 
	 */
	public void parallaxViews() {/*
		RelativeLayout imageView = (RelativeLayout) findViewById(R.id.add_date_view);
	        ListView scrollView = (ListView) findViewById(R.id.listaAdd);
	        if (scrollView instanceof Parallaxor) {
	            ((Parallaxor) scrollView).parallaxViewBy(imageView,0.5f);
	        }*/
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
		this.getSupportMenuInflater().inflate(R.menu.activity_add, menu);
		MenuItem shareItem=menu.findItem(R.id.share);
		ShareActionProvider sap=(ShareActionProvider) shareItem.getActionProvider();
		sap.setShareHistoryFileName("xday_share_history.xml");
		String text;
		String subject;
		subject=data.getDescrizione();
		text=data.getShareText(this);
		ShareUtility.shareText(sap, text, subject);
		return true;
	}

	@Override
	public void onBackPressed() {
		operazioniFinali(Costanti.ANNULLA);
	}

	/*
	 * questo metodo viene chiamato nel caso in cui si vuole modificare una data
	 * esistente. la data ha un periodtype come parametro di stato tuttavia
	 * l'add tratta pi� agevolmente i booleani questo metodo semplicemente
	 * converte il periodtype in boolean non restituisce nulla perch� setta
	 * direttamente i booleani nel suo stato.
	 */
/*	private void estraiParametri(Long msIni) {
		Cursor cursor=Costanti.getDB().fetchOneDate(msIni);
	 	cursor.moveToFirst();
	 	anno = cursor.getInt(cursor.getColumnIndex("anno"));
        mese = cursor.getInt(cursor.getColumnIndex("mese"));
        giorno = cursor.getInt(cursor.getColumnIndex("giorno"));
        ora = cursor.getInt(cursor.getColumnIndex("ora"));
        minuto = cursor.getInt(cursor.getColumnIndex("minuto"));
        text = cursor.getString(cursor.getColumnIndex("descrizione"));
		if(cursor.getInt(cursor.getColumnIndex("ore")) > 0)
            ore = true;
        else
            ore = false;
		if(cursor.getInt(cursor.getColumnIndex("secondi")) > 0)
            secondi = true;   
        else
            secondi = false;
        if(cursor.getInt(cursor.getColumnIndex("minuti")) > 0)
            minuti = true;
        else
            minuti = false;
        if(cursor.getInt(cursor.getColumnIndex("anni")) > 0)
            anni = true;
        else
            anni = false;
        if(cursor.getInt(cursor.getColumnIndex("mesi")) > 0)
            mesi = true;
        else
            mesi = false;
        if(cursor.getInt(cursor.getColumnIndex("giorni")) > 0)
            giorni = true;
        else
            giorni = false;
        if(cursor.getInt(cursor.getColumnIndex("settimane")) > 0)
            settimane = true;
        else
            settimane = false;
        if(cursor.getInt(cursor.getColumnIndex("notifica")) > 0)
            notifica = true;
        else
            notifica = false;
        Log.d("notifica", ""+notifica);
	}

/*	private Bundle creaBundle() {
		bundle.putInt("anno", anno);
        bundle.putInt("mese", mese);
        bundle.putInt("giorno", giorno);
        bundle.putInt("ora", ora);
        bundle.putInt("minuto", minuto);
        bundle.putString("descrizione", text);
        bundle.putBoolean("anni", anni);
        bundle.putBoolean("mesi", mesi);
        bundle.putBoolean("ore", ore);
        bundle.putBoolean("giorni", giorni);
        bundle.putBoolean("minuti", minuti);
        bundle.putBoolean("secondi", secondi);

        bundle.putBoolean("settimane", settimane);
        return bundle;

	}*/

	private void aggiornaAdd() {
		//aggiornamento ActionBar
		aggiornaTestoActionBar();
		aggiornaColoreActionBar();
		
		// converta le informazioni della data in data, poi in stringa
		// in base alla configurazione del sistema
		
		aggiornaDataActionBar();
		
		//aggiornamento Adapter. Imposta l'opzione di notifica attivata o disattivata in base 
		//a se la data � gi� passata oppure no
		aggiornaOpzioniAttive();

	}

	/**
	 * 
	 */
	private void aggiornaOpzioniAttive() {
		ada.setInactive(5, !data.isAfterToday());
	}
	
	/**
	 * 
	 */
	private void aggiornaDataActionBar() {
		TextView tv = (TextView) findViewById(R.id.sottotilobottone);
		tv.setText(data.toString());
	}

	/**
	 * 
	 */
	@SuppressLint("NewApi")
	private void aggiornaColoreActionBar() {
		aggiornaTestoActionBar();
		int color=data.getColor();
		//next 3 lines color the action bar. a white 9.png is colored with a colorFilter
		Drawable iv=this.getResources().getDrawable(R.drawable.ab_solid_xday_white);
		iv.setColorFilter(color, PorterDuff.Mode.DARKEN);
		this.getSupportActionBar().setBackgroundDrawable(iv);
		
		//b.setTextColor(color);
		//TextView tv = (TextView) findViewById(R.id.sottotilobottone);
		//tv.setTextColor(color);
		if(Build.VERSION.SDK_INT >= 19/*KITKAT*/)
		try{	
			Drawable d=this.getWindow().getDecorView().getBackground();
			LayerDrawable bgDrawable = (LayerDrawable) d;
			Drawable draw=bgDrawable.findDrawableByLayerId(R.id.statusbar_background);
			final ColorDrawable shape = (ColorDrawable)  draw;
			shape.setColor(color);
		}
		catch(Exception e){
			e.printStackTrace(System.err);
		}
	}

	/**
	 * @return
	 */
	private Button aggiornaTestoActionBar() {
		Button b = (Button) findViewById(R.id.buttoneriassuntivo);
		String descrizione=data.getDescrizioneIfExists();
		if (descrizione.equalsIgnoreCase(""))
			b.setText(Costanti.DescrizioneDefault);
		else
			b.setText(descrizione);
		return b;
	}

	protected void onListItemClick(View v, int position) {
		switch(position){
		case 0:
			this.showDatePickerDialog(v);
			break;
		case 1:
			this.showTimePickerDialog(v);
			break;
		case 2:
			this.showMultipleChoiceDialog(v);
			break;
		case 3:
			this.showTextEditDialog(v);
			break;
		case 4:
			this.showColorPickerDialog(v);
			break;
		case 5:			
			CheckBox cb=(CheckBox) v.findViewById(R.id.AddCheckBox);
			cb.toggle();
			data.setNotification(cb.isChecked());
			break;
			}
	}

	public void showTimePickerDialog(View v) {
		if(MainApplication.isMoreThenICS()){
		OnTimeSetListener onTimeSet=new OnTimeSetListener(){
			public void onTimeSet(RadialPickerLayout view, int hourOfDay,
					int minute) {
				Log.d("Hour",hourOfDay+"");
				Log.d("Minute",minute+"");

				data.setHour(hourOfDay);
				data.setMinute(minute);
				aggiornaDataActionBar();
				aggiornaOpzioniAttive();
				
			}};
		TimePickerDialog dp= TimePickerDialog.newInstance(onTimeSet,data.getHour(),data.getMinute(),true);
		dp.show(getSupportFragmentManager(), getString(R.string.seleziona_ora));
		}
		else {
			TimePickerFragment.TimePickerListener tps = new TimePickerFragment.TimePickerListener() {

				public void setTime(int o, int mi) {
					Log.d("Hour",o+"");
					Log.d("Minute",mi+"");
					data.setHour( o);
					data.setMinute( mi);
					aggiornaDataActionBar();
					aggiornaOpzioniAttive();

				}
			};
			TimePickerFragment timeDialog = TimePickerFragment.newInstance(data.getHour(),
					data.getMinute(), tps);
			timeDialog.show(getSupportFragmentManager(), getString(R.string.seleziona_ora));
		}
	}
	public void showColorPickerDialog(View v) {
		
		ColorPickerDialog colorDialog = new ColorPickerDialog(this, data.getColor());
		colorDialog.setOnColorChangedListener(this);
		colorDialog.setAlphaSliderVisible(true);
		colorDialog.show();
	}
	public void showDatePickerDialog(View v) {
		if(MainApplication.isMoreThenGB()){
			OnDateSetListener onDateSet=new OnDateSetListener(){

				public void onDateSet(DatePickerDialog dialog, int year,
						int monthOfYear, int dayOfMonth) {
					data.setYear(year);
					data.setMonth(monthOfYear);
					data.setDay(dayOfMonth);
					aggiornaDataActionBar();
					aggiornaOpzioniAttive();
				}};
			DatePickerDialog dp= DatePickerDialog.newInstance(onDateSet,data.getYear(),data.getMonth(),data.getDay());
			dp.show(getSupportFragmentManager(), getString(R.string.seleziona_data));
		}
		else{
			DatePickerFragment.DatePickerListener tps = new DatePickerFragment.DatePickerListener() {

				public void onDateSet(int year,
						int monthOfYear, int dayOfMonth) {
					data.setYear(year);
					data.setMonth(monthOfYear);
					data.setDay(dayOfMonth);
					aggiornaDataActionBar();
					aggiornaOpzioniAttive();
					
				}
			};
			DatePickerFragment dateDialog = DatePickerFragment.newInstance(data.getYear(),data.getMonth(),data.getDay(), tps);
			dateDialog.show(getSupportFragmentManager(), getString(R.string.seleziona_data));
		}
		
		
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
		this.aggiornaColoreActionBar();
	}
	public void OnTextEditDialogPositiveClick(String t) {
		data.setDescription(t);
		this.aggiornaTestoActionBar();
	}



	private Data data;
	private AddArrayAdapter ada;


}
