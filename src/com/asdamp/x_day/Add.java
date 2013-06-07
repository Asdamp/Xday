package com.asdamp.x_day;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.joda.time.DurationFieldType;
import org.joda.time.PeriodType;
import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.asdamp.exception.WidgetConfigurationNotFoundException;
import com.asdamp.utility.DatePickerFragment;
import com.asdamp.utility.MultipleChoiceDialog;
import com.asdamp.utility.TextEditDialog;
import com.asdamp.utility.TimePickerFragment;
import com.asdamp.utility.UtilityDate;
import com.asdamp.widget.XdayWidgetProvider;
import com.asdamp.x_day.R;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.ads.AdRequest;
import com.google.ads.AdView;


public class Add extends SherlockFragmentActivity implements
		TextEditDialog.TextEditDialogInterface,
		MultipleChoiceDialog.MultipleChoiceDialogListener,
		DatePickerFragment.DatePickerListener,
		TimePickerFragment.TimePickerListener {

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
		AdView mAdView = (AdView) this.findViewById(R.id.adView);
		/*ad request*/
		SharedPreferences shprs = getSharedPreferences(
				"PrivateOption", 0);
		final android.content.SharedPreferences.Editor spe = shprs
				.edit();
		int i = shprs.getInt("Utilizzi", 0);
		boolean ad;
		if (i == 0) {
			Resources r = this.getResources();
			ad = r.getBoolean(R.bool.ad);
			spe.putBoolean("Premium", ad).commit();
		} else
			ad = shprs.getBoolean("Premium", true);
		if (ad) 
			mAdView.loadAd(new AdRequest().addTestDevice("A2642CE92F5DAD2149B05FE4B1F32EA5").addTestDevice("3A4195F433B132420871F4202A7789C3"));
		AddArrayAdapter a = new AddArrayAdapter(Add.this);
		ListView lista = (ListView) this.findViewById(R.id.listaAdd);
		lista.setAdapter(a);
		Bundle b=this.getIntent().getExtras();
		// se è stato chiamato da main activity allora setta dei valori di
		// default
		if (b.getInt("requestCode") == Costanti.CREA_DATA) {
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
		}
		// altrimenti prende i valori proprio dalla data
		else {
				msIni=b.getLong("MsIniziali");
				Log.d("millisecondi", msIni+"");
			 	Cursor c=Costanti.getDB().fetchOneDate(msIni);
			 	c.moveToFirst();
	            estraiParametri(c);
	            
		}

		bundle = new Bundle();

		creaBundle();

		aggiornaActionBar();
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
	}

	@Override
	protected void onResume() {

		super.onResume();
		creaBundle();
		// non serve se non ho dimenticato nulla
		/* aggiornaActionBar(); */
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
			// aggiungo un bundle al putextra.altrimenti non va.ma Ã©
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
		//Costanti.getDB().apri();
		if (requestCode == Costanti.MODIFICA_DATA) {
			if (resultCode == Costanti.CANCELLA_DATA) {
				Costanti.getDB().deleteData(msIni);
			}
			if (resultCode == Costanti.TUTTO_BENE) {
                Costanti.getDB().updateData(anno, mese, giorno, ora, minuto, anni, mesi, settimane, giorni, ore, minuti,secondi, text, msIni);
			}

		}
		if (requestCode == Costanti.CREA_DATA) {
			if (resultCode == Costanti.TUTTO_BENE) {
				Log.d("millisecondi inseriti", ""+msIni);
	            Costanti.getDB().createData(anno, mese, giorno, ora, minuto, anni, mesi, settimane, giorni, ore, minuti,secondi, text,msIni);
			}

		}
	
		//update list-widget
		if(Costanti.getOsVersion()>=Build.VERSION_CODES.HONEYCOMB){
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
			final ComponentName cn = new ComponentName(this, XdayWidgetProvider.class);
			appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(cn), R.id.list_view_widget);
	
		}
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getSupportMenuInflater().inflate(R.menu.activity_add, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		operazioniFinali(Costanti.ANNULLA);
	}

	/*
	 * questo metodo viene chiamato nel caso in cui si vuole modificare una data
	 * esistente. la data ha un periodtype come parametro di stato tuttavia
	 * l'add tratta più agevolmente i booleani questo metodo semplicemente
	 * converte il periodtype in boolean non restituisce nulla perchï¿½ setta
	 * direttamente i booleani nel suo stato.
	 */
	private void estraiParametri(Cursor cursor) {

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

	private void aggiornaActionBar() {

		Button b = (Button) findViewById(R.id.buttoneriassuntivo);
		if (text.equalsIgnoreCase(""))
			b.setText(Costanti.DescrizioneDefault);
		else
			b.setText(text);
		TextView tv = (TextView) findViewById(R.id.sottotilobottone);
		// converta le informazioni della data in data, poi in stringa
		// in base alla configurazione del sistema
		
		Date d = UtilityDate.creaData(anno, mese, giorno, minuto, ora);
		String dataStr = UtilityDate.convertiDataInStringaBasandosiSuConfigurazione(d,
				Costanti.dt);
		tv.setText(dataStr);

	}

	protected void onListItemClick(View v, int position) {
		switch(position){
		case 0:
			this.showDatePickerDialog(v);
			aggiornaActionBar();
			break;
		case 1:
			this.showTimePickerDialog(v);
			aggiornaActionBar();
			break;
		case 2:
			this.showMultipleChoiceDialog(v);
			break;
		case 3:
			this.showTextEditDialog(v);
		}
	}

	@SuppressLint("NewApi")
	public void showTimePickerDialog(View v) {
		DialogFragment timeDialog = new TimePickerFragment();
		Bundle b = new Bundle();
		b.putInt(TimePickerFragment.HOUR, ora);
		b.putInt(TimePickerFragment.MINUTE, minuto);
		timeDialog.setArguments(b);
		timeDialog.show(getSupportFragmentManager(), "Seleziona ora");
	}

	public void showDatePickerDialog(View v) {
		DialogFragment dateDialog = new DatePickerFragment();
		Bundle b = new Bundle();
		b.putInt(DatePickerFragment.YEAR, anno);
		b.putInt(DatePickerFragment.MONTH, mese);
		b.putInt(DatePickerFragment.DAY, giorno);
		dateDialog.setArguments(b);
		dateDialog.show(getSupportFragmentManager(), "Seleziona data");
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

	public void OnTextEditDialogPositiveClick(String t) {
		text = t;
		aggiornaActionBar();
	}

	public int getOra() {
		return ora;
	}

	public int getMinuto() {
		return minuto;
	}

	public void setHour(int ora) {
		this.ora = ora;
		aggiornaActionBar();
	}

	public void setMinute(int minuto) {
		this.minuto = minuto;
		aggiornaActionBar();
	}

	public int getAnno() {
		return anno;
	}

	public int getMese() {
		return mese;
	}

	public int getGiorno() {
		return giorno;
	}

	public String getText() {
		return text;
	}

	public void setYear(int anno) {
		this.anno = anno;
		aggiornaActionBar();
	}

	public void setMonth(int mese) {
		this.mese = mese;
		aggiornaActionBar();
	}

	public void setDay(int giorno) {
		this.giorno = giorno;
		aggiornaActionBar();
	}

	public boolean isAnni() {
		return anni;
	}

	public boolean isMesi() {
		return mesi;
	}

	public boolean isGiorni() {
		return giorni;
	}

	public boolean isOre() {
		return ore;
	}

	public boolean isMinuti() {
		return minuti;
	}

	public void setAnni(boolean anni) {
		this.anni = anni;

	}

	public void setMesi(boolean mesi) {
		this.mesi = mesi;

	}

	public void setGiorni(boolean giorni) {
		this.giorni = giorni;

	}

	public void setOre(boolean ore) {
		this.ore = ore;

	}

	public void setMinuti(boolean minuti) {
		this.minuti = minuti;

	}

	private int ora;
	private int minuto;
	private int anno;
	private int mese;
	private int giorno;
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



	public void onMultipleDialogNegativeClick(
			android.support.v4.app.DialogFragment dialog) {

	}

}
