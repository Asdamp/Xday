package com.asdamp.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.asdamp.database.DBAdapter;
import com.asdamp.utility.SingleChoiceDialog;
import com.asdamp.utility.UtilityDate;
import com.asdamp.x_day.Costanti;
import com.asdamp.x_day.Data;
import com.asdamp.x_day.R;

import java.util.ArrayList;

public class XdayWidgetSingleDateConfigure extends FragmentActivity implements
		com.asdamp.utility.SingleChoiceDialog.SingleChoiceDialogListener {

	public XdayWidgetSingleDateConfigure() {
	}

	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		Bundle bundle1 = getIntent().getExtras();
		if (bundle1 != null)
			mAppWidgetId = bundle1.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		ArrayList<String> date = new ArrayList<String>();

		DBAdapter dbadapter = Costanti.getDB();
		c = dbadapter.fetchAllData();
		c.moveToFirst();
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
			showSingleChoiceDialog(date);
			// dbadapter.close();
		}
	}

	public void onSingleDialogNegativeClick(int i) {
		Intent intent = new Intent();
		intent.putExtra("appWidgetId", mAppWidgetId);
		setResult(Activity.RESULT_CANCELED, intent);
	}

	public void onSingleDialogPositiveClick(int i) {
		Intent intent = new Intent();
		intent.putExtra("appWidgetId", mAppWidgetId);
		if (i >= 0) {
			AppWidgetManager appwidgetmanager = AppWidgetManager
					.getInstance(this);
			c.moveToPosition(i);
			Data data = Data.leggi(c, this);

			setResult(RESULT_OK, intent);
			Costanti.getDB().SalvaDataWidget(mAppWidgetId,
					data.getMillisecondiIniziali());
			XdayWidgetSingleDateProvider.aggiornaWidget(appwidgetmanager, data,
					this, mAppWidgetId);
		} else
			setResult(Activity.RESULT_CANCELED, intent);

		finish();
	}

	public void showSingleChoiceDialog(ArrayList<String> arraylist) {
		Bundle bundle = new Bundle();
		String as[] = new String[arraylist.size()];
		arraylist.toArray(as);
		SingleChoiceDialog singlechoicedialog = new SingleChoiceDialog();
		bundle.putCharSequenceArray("parametriString", as);
		bundle.putString("titolo", getString(R.string.Opzioni));
		singlechoicedialog.setArguments(bundle);
		singlechoicedialog.show(getSupportFragmentManager(), "parametri");
	}

	Cursor c;
	int mAppWidgetId;
}
