package com.asdamp.x_day;


import android.content.Context;
import android.os.Build.VERSION;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;

import com.asdamp.database.DBAdapter;
//the class Costanti is suppesod to be a singleton. this class is initializated by MainApplication class
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
	private static DBAdapter database;
	public static String DescrizioneDefault ;
	private static int Os;

	public static void inizializza(Context c) {
		if(costanti==null) costanti= new Costanti(c);
		
	}

	private Costanti(Context c) {
		
		Costanti.dt = DateFormat.getDateFormat(c);
		database = (new DBAdapter(c));
		Os = VERSION.SDK_INT;
		DisplayMetrics dm = c.getResources().getDisplayMetrics();
		DPI = dm.densityDpi;	
		DescrizioneDefault=c.getString(R.string.DescrizioneDefault);
		
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
	

}
