package com.asdamp.x_day;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import uk.co.chrisjenx.paralloid.Parallaxor;
import uk.co.chrisjenx.paralloid.transform.LeftAngleTransformer;
import uk.co.chrisjenx.paralloid.transform.LinearTransformer;
import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.android.datetimepicker.time.TimePickerDialog.OnTimeSetListener;
import com.asdamp.notification.Notification;
import com.asdamp.utility.*;
import com.asdamp.widget.XdayWidgetProvider;
import com.asdamp.x_day.R;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.google.ads.AdView;

import android.graphics.PorterDuff.Mode;

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
				msIni=b.getLong("MsIniziali");
				Log.d("millisecondi", msIni+"");
				try {
					color = Costanti.getDB().cercaColore(msIni);
					estraiParametri(msIni);
				} catch (Exception e) {
					Toast.makeText(this, "Unable to retrive information about this date", Toast.LENGTH_SHORT).show();
					setupNewDate();
				}
	           
	            
		}
		
		
		ada = showLayout();
		ListView lista = (ListView) this.findViewById(R.id.listaAdd);
		lista.setAdapter(ada);
		View vh=new View(this);
		
		lista.bringToFront();
		bundle = new Bundle();

		creaBundle();

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
		ora = 0;
		minuto = 0;
		anni = false;
		mesi = false;
		giorni = true;
		minuti = false;
		settimane = false;
		ore = false;
		secondi=false;
		Calendar calendar = Calendar.getInstance();
		anno = calendar.get(1);
		mese = calendar.get(2);
		giorno = calendar.get(5);
		text = "";
		msIni=(new GregorianCalendar()).getTimeInMillis();
		notifica=false;   
		color=Color.parseColor("#0099cc");
	}

	@Override
	protected void onResume() {

		super.onResume();
		
		creaBundle();
	}



	/**
	 * 
	 */
	public void parallaxViews() {
		RelativeLayout imageView = (RelativeLayout) findViewById(R.id.add_date_view);
	        ListView scrollView = (ListView) findViewById(R.id.listaAdd);
	        if (scrollView instanceof Parallaxor) {
	            ((Parallaxor) scrollView).parallaxViewBy(imageView,0.5f);
	        }
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
		if(!isAfterToday())  b.putBoolean(AddArrayAdapter.INACTIVE, true);//se la data � gi� passata, le notifiche vengono disattivate

		Log.d("AddCheckedNotification",""+notifica);
		b.putBoolean(AddArrayAdapter.CHECKED, this.notifica);

		bf[5]=(Bundle) b.clone();
		AddArrayAdapter a=new AddArrayAdapter(Add.this, bf);
		return a;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home: {
			// aggiungere bundle per non farlo crashare
			operazioniFinali(Costanti.ANNULLA);
			break;
		}

		case R.id.Conferma: {
			setResult(Costanti.TUTTO_BENE,
					this.getIntent().putExtra("data", creaBundle()));
			operazioniFinali(Costanti.TUTTO_BENE);
			break;
		}
		case R.id.Elimina: {
			// aggiungo un bundle al putextra.altrimenti non va.ma é
			// necessario?
			setResult(Costanti.CANCELLA_DATA,
					this.getIntent().putExtra("data", creaBundle()));
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
		setResult(resultCode, this.getIntent().putExtra("data", creaBundle()));
		int requestCode = this.getIntent().getExtras().getInt("requestCode");
		Data d=new Data(anno, mese, giorno, ora, minuto, anni, mesi, settimane, giorni, ore, minuti, secondi, text,msIni,color,notifica, this);
		//Costanti.getDB().apri();
		if (requestCode == Costanti.MODIFICA_DATA) {
			if (resultCode == Costanti.CANCELLA_DATA) {
				Costanti.getDB().deleteData(msIni);
			}
			if (resultCode == Costanti.TUTTO_BENE) {
                Costanti.getDB().updateData(d);
			}

		}
		if (requestCode == Costanti.CREA_DATA) {
			if (resultCode == Costanti.TUTTO_BENE) {
				Log.d("millisecondi inseriti", ""+msIni);
	            Costanti.getDB().createData(d);
			}

		}
	
		//update list-widget
		if(Costanti.getOsVersion()>=Build.VERSION_CODES.HONEYCOMB){
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
			final ComponentName cn = new ComponentName(this, XdayWidgetProvider.class);
			appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(cn), R.id.list_view_widget);
	
		}
		Notification.scheduleNotificationById(this,d);
		
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
		Data dTemp=new Data(anno, mese, giorno, ora, minuto, anni, mesi, settimane, giorni, ore, minuti, secondi, this.text, msIni, color,notifica, this);
		subject=dTemp.getDescrizione();
		text=dTemp.getShareText();
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
	private void estraiParametri(Long msIni) {
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

	private Bundle creaBundle() {
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

	}

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
		ada.setInactive(5, !this.isAfterToday());
	}
	/* TODO
	 * questo metodo è temporaneo. Quando verrà fatto il refactor di tutto il progetto,
	 * potrà essere eliminato, usando dove necessario, data.isAfterToday()*/
	public boolean isAfterToday(){
		Calendar cal=new GregorianCalendar(anno, mese, giorno,ora,minuto);
		Calendar today=new GregorianCalendar();

		long msFin =cal.getTimeInMillis();
		if(msFin>today.getTimeInMillis()) return true;
		return false;
	}
	/**
	 * 
	 */
	private void aggiornaDataActionBar() {
		Date d = UtilityDate.creaData(anno, mese, giorno, minuto, ora);
		String dataStr = UtilityDate.convertiDataInStringaBasandosiSuConfigurazione(d,
				Costanti.dt);
		dataStr=dataStr+" "+UtilityDate.convertiDataInStringaBasandosiSuConfigurazione(d,
				DateFormat.getTimeFormat(this));
		TextView tv = (TextView) findViewById(R.id.sottotilobottone);
		tv.setText(dataStr);
	}

	/**
	 * 
	 */
	@SuppressLint("NewApi")
	private void aggiornaColoreActionBar() {
		Button b = (Button) findViewById(R.id.buttoneriassuntivo);
		aggiornaTestoActionBar();
		//next 3 lines color the action bar. a white 9.png is colored with a colorFilter
		Drawable iv=this.getResources().getDrawable(R.drawable.ab_solid_xday_white);
		iv.setColorFilter(color, PorterDuff.Mode.DARKEN);
		this.getSupportActionBar().setBackgroundDrawable(iv);
		
		//b.setTextColor(color);
		//TextView tv = (TextView) findViewById(R.id.sottotilobottone);
		//tv.setTextColor(color);
		if(Build.VERSION.SDK_INT >= 19/*KITKAT*/)
		try{	
			View v = findViewById(R.id.linear_layout_main);
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
		if (text.equalsIgnoreCase(""))
			b.setText(Costanti.DescrizioneDefault);
		else
			b.setText(text);
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
			this.notifica=cb.isChecked();
			
			break;
			}
	}

	public void showTimePickerDialog(View v) {
		if(MainApplication.isMoreThenICS()){
		OnTimeSetListener onTimeSet=new OnTimeSetListener(){
			public void onTimeSet(RadialPickerLayout view, int hourOfDay,
					int minute) {
				ora=hourOfDay;
				minuto=minute;
				aggiornaDataActionBar();
				aggiornaOpzioniAttive();
				
			}};
		TimePickerDialog dp= TimePickerDialog.newInstance(onTimeSet,ora,minuto,true);
		dp.show(getSupportFragmentManager(), getString(R.string.seleziona_ora));
		}
		else {
			TimePickerFragment.TimePickerListener tps = new TimePickerFragment.TimePickerListener() {

				public void setTime(int o, int mi) {
					ora = o;
					minuto = mi;
					aggiornaDataActionBar();
					aggiornaOpzioniAttive();

				}
			};
			TimePickerFragment timeDialog = TimePickerFragment.newInstance(ora,
					minuto, tps);
			timeDialog.show(getSupportFragmentManager(), getString(R.string.seleziona_ora));
		}
	}
	public void showColorPickerDialog(View v) {
		
		ColorPickerDialog colorDialog = new ColorPickerDialog(this, color);
		colorDialog.setOnColorChangedListener(this);
		colorDialog.setAlphaSliderVisible(true);
		colorDialog.show();
	}
	public void showDatePickerDialog(View v) {
		if(MainApplication.isMoreThenGB()){
			OnDateSetListener onDateSet=new OnDateSetListener(){

				public void onDateSet(DatePickerDialog dialog, int year,
						int monthOfYear, int dayOfMonth) {
					anno=year;
					mese=monthOfYear;
					giorno=dayOfMonth;
					aggiornaDataActionBar();
					aggiornaOpzioniAttive();
				}};
			DatePickerDialog dp= DatePickerDialog.newInstance(onDateSet,anno,mese,giorno);
			dp.show(getSupportFragmentManager(), getString(R.string.seleziona_data));
		}
		else{
			DatePickerFragment.DatePickerListener tps = new DatePickerFragment.DatePickerListener() {

				public void onDateSet(int y, int m, int d) {
					anno=y;
					mese=m;
					giorno=d;
					aggiornaDataActionBar();
					aggiornaOpzioniAttive();
					
				}
			};
			DatePickerFragment dateDialog = DatePickerFragment.newInstance(anno,mese,giorno, tps);
			dateDialog.show(getSupportFragmentManager(), getString(R.string.seleziona_data));
		}
		
		
	}

	public void showMultipleChoiceDialog(View v) {
		Bundle bundle1 = new Bundle();
        boolean aflag[] = new boolean[7];
        aflag[0] = anni;
        aflag[1] = mesi;
        aflag[2] = settimane;
        aflag[3] = giorni;
        aflag[4] = ore;
        aflag[5] = minuti;
        aflag[6] = secondi;

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
		p.putString(TextEditDialog.STRINGA_BASE, text);
		DialogFragment textDialog = new TextEditDialog();
		textDialog.setArguments(p);
		textDialog.show(this.getSupportFragmentManager(), "testo");
	}

	public void onMultipleDialogPositiveClick(boolean[] parametri) {
		anni = parametri[0];
        mesi = parametri[1];
        settimane = parametri[2];
        giorni = parametri[3];
        ore = parametri[4];
        minuti = parametri[5];
        secondi = parametri [6];
	}

	public void onMultipleDialogNegativeClick(
			android.support.v4.app.DialogFragment dialog) {

	}

	public void onColorChanged(int color) {
		this.color=color;
		this.aggiornaColoreActionBar();
	}
	public void OnTextEditDialogPositiveClick(String t) {
		text = t;
		this.aggiornaTestoActionBar();
	}



	private int ora;
	private int minuto;
	private int anno;
	private int mese;
	private int giorno;
	private boolean notifica;
	private String text;
	private boolean anni;
	private boolean mesi;
	private boolean giorni;
	private boolean ore;
	private boolean settimane;
	private boolean secondi;
	private long msIni;
	private boolean minuti;
	private Bundle bundle;
	private int color;
private AddArrayAdapter ada;


}
