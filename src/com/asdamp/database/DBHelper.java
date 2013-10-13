package com.asdamp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void onCreate(SQLiteDatabase sqlitedatabase) {
		sqlitedatabase.execSQL(DATABASE_CREATE);
		sqlitedatabase.execSQL(DATABASE_WIDGET);
		sqlitedatabase.execSQL(TRIGGER_POSIZIONE_NULL);
	}

	public void onUpgrade(SQLiteDatabase sqlitedatabase, int i, int j) {
		sqlitedatabase.execSQL(DATABASE_TEMP);
		sqlitedatabase.execSQL(TRIGGER_POSIZIONE_NULL_temp);
		sqlitedatabase.execSQL(DATABASE_WIDGET_TEMP);
		switch (i) {
		case 1:
			sqlitedatabase
					.execSQL("INSERT INTO temp (anno, mese,giorno,ora,minuto,millisecondiIniziali, anni,mesi,giorni,ore,minuti,descrizione)SELECT anno, mese,giorno,ora,minuto,millisecondiIniziali,anni,mesi,giorni,ore,minuti,descrizione from date;");
			break;
		case 3:
			sqlitedatabase
					.execSQL("INSERT INTO temp (posizione, anno, mese,giorno,ora,minuto,millisecondiIniziali, anni,mesi,settimane,giorni,ore,minuti,descrizione)SELECT posizione, anno, mese,giorno,ora,minuto,millisecondiIniziali,anni,mesi,settimane,giorni,ore,minuti,descrizione from date;");
			sqlitedatabase.execSQL("INSERT INTO temp2(idWidget, data) SELECT idWidget, data from widget");
			break;
		case 4: sqlitedatabase
		.execSQL("INSERT INTO temp (posizione, anno, mese,giorno,ora,minuto,millisecondiIniziali, anni,mesi,settimane,giorni,ore,minuti,descrizione)SELECT posizione, anno, mese,giorno,ora,minuto,millisecondiIniziali,anni,mesi,settimane,giorni,ore,minuti,descrizione from date;");
		sqlitedatabase.execSQL("INSERT INTO temp2(idWidget, data) SELECT idWidget, data from widget");
		break;
		case 5: sqlitedatabase
		.execSQL("INSERT INTO temp (posizione, anno, mese,giorno,ora,minuto,millisecondiIniziali, anni,mesi,settimane,giorni,ore,minuti,secondi,descrizione)SELECT posizione, anno, mese,giorno,ora,minuto,millisecondiIniziali,anni,mesi,settimane,giorni,ore,minuti,secondi,descrizione from date;");
		sqlitedatabase.execSQL("INSERT INTO temp2(idWidget, data) SELECT idWidget, data from widget");
		break;
		case 6:
			 sqlitedatabase
				.execSQL("INSERT INTO temp (posizione, anno, mese,giorno,ora,minuto,millisecondiIniziali, anni,mesi,settimane,giorni,ore,minuti,secondi,descrizione,colore)SELECT posizione, anno, mese,giorno,ora,minuto,millisecondiIniziali,anni,mesi,settimane,giorni,ore,minuti,secondi,descrizione,colore from date;");
				sqlitedatabase.execSQL("INSERT INTO temp2(idWidget, data) SELECT idWidget, data from widget");
				break;
		}
		sqlitedatabase.execSQL("drop table if exists date;");
		sqlitedatabase.execSQL("drop table if exists widget;");

		sqlitedatabase.execSQL("drop trigger TRIGGER_POSIZIONE_NULL_temp;");
		sqlitedatabase.execSQL("alter table temp rename to date;");
		sqlitedatabase.execSQL("alter table temp2 rename to widget;");

		sqlitedatabase.execSQL(TRIGGER_POSIZIONE_NULL);
	}

	private static final String DATABASE_CREATE = "create table if not exists date (posizione integer default 0,anno integer not null, mese integer not null, giorno integer not null, ora integer not null, minuto integer not null, millisecondiIniziali integer not null primary key, anni integer not null, mesi integer not null, settimane integer not null default 0, giorni integer not null, ore integer not null, minuti integer not null, secondi integer not null default 0, descrizione text, colore integer default -16746590, notifica integer not null default 1);";
	public static final String DATABASE_NAME = "date.db";
	private static final String DATABASE_TEMP = "create table if not exists temp (posizione integer default 0,anno integer not null, mese integer not null, giorno integer not null, ora integer not null, minuto integer not null, millisecondiIniziali integer not null primary key, anni integer not null, mesi integer not null,settimane integer not null default 0, giorni integer not null, ore integer not null, minuti integer not null,secondi integer not null default 0, descrizione text, colore integer default -16746590, notifica integer not null default 0);";
	public static final int DATABASE_VERSION = 7;
	private static final String DATABASE_WIDGET = "create table if not exists widget (idWidget integer not null primary key, data integer,colore integer default -16746590);";
	private static final String DATABASE_WIDGET_TEMP = "create table if not exists temp2 (idWidget integer not null primary key,data integer, colore integer default -16746590);";
	private static final String TRIGGER_POSIZIONE_NULL = "CREATE TRIGGER if not exists `TRIGGER_POSIZIONE_NULL` after insert ON `date` FOR EACH ROW BEGIN update date set posizione=(select max(posizione) from date)+1 where posizione=0;END;";
	private static final String TRIGGER_POSIZIONE_NULL_temp = "CREATE TRIGGER if not exists `TRIGGER_POSIZIONE_NULL_temp` after insert ON `temp` FOR EACH ROW BEGIN update temp set posizione=(select max(posizione) from temp)+1 where posizione=0;END;";
/**
 * DATABASE changelog
 * version 6: modified color default(date), added widget color(widget)
 * version 7: added notification boolean(date)*/
}
