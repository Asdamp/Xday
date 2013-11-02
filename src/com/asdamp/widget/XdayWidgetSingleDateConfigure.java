package com.asdamp.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.asdamp.database.DBAdapter;
import com.asdamp.exception.WidgetConfigurationNotFoundException;
import com.asdamp.utility.ColorPickerDialog;
import com.asdamp.utility.SingleChoiceDialog;
import com.asdamp.utility.UtilityDate;
import com.asdamp.x_day.AddArrayAdapter;
import com.asdamp.x_day.Costanti;
import com.asdamp.x_day.Data;
import com.asdamp.x_day.R;
import com.google.ads.AdRequest;
import com.google.ads.AdView;

import java.util.ArrayList;

public class XdayWidgetSingleDateConfigure extends SherlockFragmentActivity implements
		com.asdamp.utility.SingleChoiceDialog.SingleChoiceDialogListener,
		ColorPickerDialog.OnColorChangedListener{

	private Data data=null;
	private int color;
	public XdayWidgetSingleDateConfigure() {
	}

	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		this.setContentView(R.layout.add);
		
		Bundle bundle1 = getIntent().getExtras();
		if (bundle1 != null)
			mAppWidgetId = bundle1.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);

		DBAdapter dbadapter = Costanti.getDB();
		Cursor confPre;
		try {
			confPre = dbadapter.AssociaDataAWidget(mAppWidgetId);
			data = dbadapter.cercaData(confPre.getLong(confPre.getColumnIndex("data")));
			color=confPre.getInt(confPre.getColumnIndex("colore"));
		} catch (WidgetConfigurationNotFoundException e) {
			data=null;
			color=Color.argb(161, 81, 81, 81);
		}
		
		c = dbadapter.fetchAllData();
		c.moveToFirst();
		
		//show ad
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
			mAdView.loadAd(new AdRequest().addTestDevice("TEST_EMULATOR").addTestDevice("8D2F8A681D6D472A953FBC3E75CE9276").addTestDevice("A2642CE92F5DAD2149B05FE4B1F32EA5").addTestDevice("3A4195F433B132420871F4202A7789C3"));
		//end of show ad
		AddArrayAdapter a = showLayout();
		ListView lista = (ListView) this.findViewById(R.id.listaAdd);
		lista.setAdapter(a);
		lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				onListItemClick(arg1, arg2);

			}

		});
		
			// dbadapter.close();
		
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getSupportMenuInflater().inflate(R.menu.widget_single_config, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
		case R.id.Conferma:
		
			this.operazioniFinali();
			
		case R.id.Annulla:
			Intent intent = new Intent();
			intent.putExtra("appWidgetId", mAppWidgetId);
			setResult(Activity.RESULT_CANCELED, intent);
			finish();
		}
		
		return super.onOptionsItemSelected(item);
	}

	private AddArrayAdapter showLayout() {
		Bundle bf[]=new Bundle[2];
		Bundle b=new Bundle();
		Resources res=this.getResources();
		b.putString(AddArrayAdapter.TITLE, res.getStringArray(R.array.WidgetSingleSelezionaData)[0]);
		b.putString(AddArrayAdapter.SUBTITLE, res.getStringArray(R.array.WidgetSingleSelezionaData)[1]);
		b.putInt(AddArrayAdapter.IMAGE, R.drawable.ic_action_calendar);
		bf[0]=(Bundle) b.clone();
		b.putString(AddArrayAdapter.TITLE, res.getStringArray(R.array.WidgetSingleSelezionaColore)[0]);
		b.putString(AddArrayAdapter.SUBTITLE, res.getStringArray(R.array.WidgetSingleSelezionaColore)[1]);
		b.putInt(AddArrayAdapter.IMAGE, R.drawable.ic_action_color);
		bf[1]=(Bundle) b.clone();
		AddArrayAdapter a=new AddArrayAdapter(this, bf);
		
		return a;
	}
	protected void onListItemClick(View v, int position) {
		switch(position){
		case 0:
			showSingleChoiceDialog(v);
			break;
		case 1:
			showColorPickerDialog(v);
			break;
		}
	}
	private void showColorPickerDialog(View v) {
		ColorPickerDialog colorDialog = new ColorPickerDialog(this, color);
		colorDialog.setOnColorChangedListener(this);
		colorDialog.setAlphaSliderVisible(true);
		colorDialog.show();		
	}

	public void onSingleDialogNegativeClick(int i) {
	}
	private void operazioniFinali(){
		if(data==null){
		Toast.makeText(this, this.getString(R.string.nessunaDataSelezionata), Toast.LENGTH_LONG).show();
		Intent intent = new Intent();
		intent.putExtra("appWidgetId", mAppWidgetId);
		setResult(RESULT_CANCELED, intent);
		finish();
		}
		else{
		AppWidgetManager appwidgetmanager = AppWidgetManager
				.getInstance(this);
		Costanti.getDB().SalvaDataWidget(mAppWidgetId,
				data.getMillisecondiIniziali(), color);
		XdayWidgetSingleDateProvider.aggiornaWidget(appwidgetmanager, data,color,
				this, mAppWidgetId);
		Intent intent = new Intent();
		intent.putExtra("appWidgetId", mAppWidgetId);
		setResult(RESULT_OK, intent);
		finish();
		}
	}
	public void onSingleDialogPositiveClick(int i) {
		if(i<0) return;
		c.moveToPosition(i);
		data = Data.leggi(c, this);
	}

	private void showSingleChoiceDialog(View v) {
		c.moveToFirst();
		ArrayList<String> date = new ArrayList<String>();
		if (c.getCount() <= 0) {
			Toast.makeText(this, R.string.nessunaData, Toast.LENGTH_LONG)
					.show();
			Intent add = new Intent("com.asdamp.x_day.ADD");
			add.putExtra("requestCode", Costanti.CREA_DATA);
			add.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			add.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			System.out.println("preavvio");
			startActivity(add);
		} else {
			do {
				
				String s = c.getString(c.getColumnIndex("descrizione"));
				if (s.equals(""))
					s = UtilityDate
							.convertiDataInStringaBasandosiSuConfigurazione(
									UtilityDate.creaData(
											c.getInt(c.getColumnIndex("anno")),
											c.getInt(c.getColumnIndex("mese")),
											c.getInt(c.getColumnIndex("giorno")),
											c.getInt(c.getColumnIndex("minuto")),
											c.getInt(c.getColumnIndex("ora"))),
									Costanti.dt);
				date.add(s);
			} while (c.moveToNext());
		Bundle bundle = new Bundle();
		String as[] = new String[date.size()];
		date.toArray(as);
		SingleChoiceDialog singlechoicedialog = new SingleChoiceDialog();
		bundle.putCharSequenceArray("parametriString", as);
		bundle.putString("titolo", getString(R.string.Opzioni));
		singlechoicedialog.setArguments(bundle);
		singlechoicedialog.show(getSupportFragmentManager(), "parametri");
	}
	}
	Cursor c;
	int mAppWidgetId;
	public void onColorChanged(int color) {
		this.color=color;
		
	}
}
