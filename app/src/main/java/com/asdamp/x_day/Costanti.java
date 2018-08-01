package com.asdamp.x_day;


import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import com.asdamp.database.DBAdapter;
import com.asdamp.widget.XdayWidgetProvider;
//the class Costanti is supposed to be a singleton. this class is initializated by MainApplication class
public class Costanti {
	public static final int MODIFICA_DATA = 1;
	public static final int CANCELLA_DATA = 2;
	public static final int TUTTO_BENE = 3;
	public static final int CREA_DATA = 4;
	public static final int ANNULLA = 5;
	public static final int FILE_SELECT_CODE = 6;
	public static int DPI = 0;
	private static Costanti costanti = null;
	public static java.text.DateFormat dt;
	public static java.text.DateFormat tf;

	private static DBAdapter database;
	public static String DescrizioneDefault ;
	private static int Os;
	public static SharedPreferences shprs;
	public static android.content.SharedPreferences.Editor spe;
	public static void inizializza(Context c) {
		if(costanti==null) costanti= new Costanti(c);
	}

	private Costanti(Context c) {
		
		dt = DateFormat.getDateFormat(c);
		tf = DateFormat.getTimeFormat(c);
		database = (new DBAdapter(c));
		Os = VERSION.SDK_INT;
		DisplayMetrics dm = c.getResources().getDisplayMetrics();
		DPI = dm.densityDpi;	
		DescrizioneDefault=c.getString(R.string.DescrizioneDefault);
		shprs = c.getSharedPreferences(
				"PrivateOption", 0);
		final android.content.SharedPreferences.Editor spe = shprs
				.edit();
	}

	public static DBAdapter getDB() {
		return database;
	}
	public static void chiudiDB() {
		database.chiudi();
	}

	public static int getOsVersion() {
		return Os; 
	}
	
	public static int getDPI(){
		
		return DPI;
	}

	@SuppressLint("NewApi")
	public static void updateWidget(Context c) {
		if(Costanti.getOsVersion()>=Build.VERSION_CODES.HONEYCOMB){
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(c);
			final ComponentName cn = new ComponentName(c, XdayWidgetProvider.class);
			appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(cn), R.id.list_view_widget);
	
		}
		
	}
	

}
