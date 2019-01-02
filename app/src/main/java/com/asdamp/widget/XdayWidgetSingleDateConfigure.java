package com.asdamp.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.asdamp.adapters.AddArrayAdapter;
import com.asdamp.adapters.DateListAdapter;
import com.asdamp.database.DBAdapter;
import com.asdamp.exception.WidgetConfigurationNotFoundException;
import com.asdamp.utility.ImageUtils;
import com.asdamp.utility.SingleChoiceDialog;
import com.asdamp.x_day.Costanti;
import com.asdamp.x_day.Data;
import com.asdamp.x_day.DateListActivity;
import com.asdamp.x_day.R;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.ArrayList;

public class XdayWidgetSingleDateConfigure extends AppCompatActivity implements
		com.asdamp.utility.SingleChoiceDialog.SingleChoiceDialogListener{

	private Data data=null;
	private int color;
	public XdayWidgetSingleDateConfigure() {
	}

	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		this.setContentView(R.layout.widget_app_bar_date_list);
		
		Bundle bundle1 = getIntent().getExtras();
		if (bundle1 != null)
			mAppWidgetId = bundle1.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);

		DBAdapter dbadapter = Costanti.getDB();
		Cursor confPre;
		try {
			confPre = dbadapter.AssociaDataAWidget(mAppWidgetId);
			data = dbadapter.cercaData(confPre.getLong(confPre.getColumnIndex("data")));
		} catch (WidgetConfigurationNotFoundException e) {
			data=null;
		}
		
		c = dbadapter.fetchAllData();
		c.moveToFirst();

		RecyclerView mDateRecyclerView=findViewById(R.id.rv_date_list);
		mDateRecyclerView.setHasFixedSize(true);
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);

		mDateRecyclerView.setLayoutManager(layoutManager);
		ArrayList<Data> dates=getAllDate();
		DateListAdapter mListAdapter = new DateListAdapter(dates);
		mListAdapter.setOnListItemClickListener(new DateListAdapter.OnListItemClickListener() {
			@SuppressWarnings("ResultOfMethodCallIgnored")
			@Override
			public void onListItemClick(View v,int i) {
				data=dates.get(i);
				operazioniFinali();
			}

			@Override
			public boolean onListItemLongClick(View v, int i) {
				return false;
			}
		});
		mDateRecyclerView.setAdapter(mListAdapter);
		
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.widget_single_config, menu);
		return true;
	}
	private ArrayList<Data> getAllDate() {
		Cursor cursor = Costanti.getDB().fetchAllData();
		ArrayList<Data> dates=new ArrayList<>();
		while (cursor.moveToNext())
			dates.add(Data.leggi(cursor));
		return dates;
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




	public void onSingleDialogNegativeClick(int i) {
	}
	private void operazioniFinali(){
		Intent intent = new Intent();
		intent.putExtra("appWidgetId", mAppWidgetId);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if(data==null){
		Toast.makeText(this, this.getString(R.string.nessunaDataSelezionata), Toast.LENGTH_LONG).show();
		
		
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
		setResult(RESULT_OK, intent);
		finish();
		}
	}
	public void onSingleDialogPositiveClick(int i) {
		if(i<0) return;
		c.moveToPosition(i);
		data = Data.leggi(c);
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
					s = Data.leggi(c).toString();
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
