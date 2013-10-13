package com.asdamp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.asdamp.exception.DateNotFoundException;
import com.asdamp.exception.WidgetConfigurationNotFoundException;
import com.asdamp.x_day.Data;

public class DBAdapter {

	public DBAdapter(Context context1) {
		context = context1;
	}

	private ContentValues createContentValues(Data data, int i) {
		ContentValues contentvalues = new ContentValues();
		contentvalues.put("anno", Integer.valueOf(data.getAnno()));
		contentvalues.put("mese", Integer.valueOf(data.getMese()));
		contentvalues.put("giorno", Integer.valueOf(data.getGiorno()));
		contentvalues.put("minuto", Integer.valueOf(data.getMinuto()));
		contentvalues.put("ora", Integer.valueOf(data.getOra()));
		contentvalues.put("anni", Boolean.valueOf(data.getBoolAnni()));
		contentvalues.put("mesi", Boolean.valueOf(data.getBoolMesi()));
		contentvalues
				.put("settimane", Boolean.valueOf(data.getBoolSettimana()));
		contentvalues.put("giorni", Boolean.valueOf(data.getBoolGiorni()));
		contentvalues.put("ore", Boolean.valueOf(data.getBoolOre()));
		contentvalues.put("minuti", Boolean.valueOf(data.getBoolMinuti()));
		contentvalues.put("millisecondiIniziali",
				Long.valueOf(data.getMillisecondiIniziali()));
		contentvalues.put("descrizione", data.getDescrizione());
		if (i != -1)
			contentvalues.put("posizione", Integer.valueOf(i));
		return contentvalues;
	}

	private ContentValues createContentValues(int year, int month, int day,
			int hour, int minute, boolean years, boolean months, boolean weeks,
			boolean days, boolean hours, boolean minutes, boolean seconds,
			String s, long msI, int color, boolean notifica, int i) {
		ContentValues contentvalues = new ContentValues();
		contentvalues.put("anno", year);
		contentvalues.put("mese", month);
		contentvalues.put("giorno", day);
		contentvalues.put("minuto", minute);
		contentvalues.put("ora", hour);
		contentvalues.put("anni", years);
		contentvalues.put("mesi", months);
		contentvalues.put("settimane", weeks);
		contentvalues.put("giorni", days);
		contentvalues.put("ore", hours);
		contentvalues.put("minuti", minutes);
		contentvalues.put("secondi", seconds);
		contentvalues.put(MILLISECONDI_INIZIALI, msI);
		contentvalues.put("descrizione", s);
		contentvalues.put("colore", color);
		contentvalues.put("notifica", notifica);

		if (i != -1)
			contentvalues.put("posizione", i);
		return contentvalues;
	}

	public Cursor AssociaDataAWidget(int i)
			throws WidgetConfigurationNotFoundException {
		Cursor cursor = database.query(NOME_TAVOLA_WIDGET, null, IDWIDGET+"="
				+ i, null, null, null, null);
		if (cursor.getCount() <= 0)
			throw new WidgetConfigurationNotFoundException(
					"Widget configuration not found in database, please reconfigure the widget");
		cursor.moveToFirst();

		return cursor/*
					 * cercaData(cursor.getLong(cursor.getColumnIndex(DATA_WIDGET
					 * )))
					 */;
	}

	public int cercaColore(long i) throws DateNotFoundException {

		String column[] = new String[1];
		column[0] = "colore";
		Cursor cursor = database.query(NOME_TAVOLA, column,
				MILLISECONDI_INIZIALI + "=" + i, null, null, null, null);
		if (cursor.getCount() <= 0)
			throw new DateNotFoundException("");
		cursor.moveToFirst();

		return cursor.getInt(cursor.getColumnIndexOrThrow("colore"));
	}

	public boolean eliminaDataWidget(int id) {
		return database.delete(NOME_TAVOLA_WIDGET, IDWIDGET + "=" + id, null) > 0;
	}

	public void SalvaDataWidget(int id, long msData, int color) {
		ContentValues contentvalues;
		contentvalues = new ContentValues();
		contentvalues.put(IDWIDGET, id);
		contentvalues.put(DATA_WIDGET, msData);
		contentvalues.put(COLORE_WIDGET, color);

		try {
			database.insertOrThrow(NOME_TAVOLA_WIDGET, null, contentvalues);
		} catch (SQLiteConstraintException sqliteconstraintexception) {
			database.replaceOrThrow(NOME_TAVOLA_WIDGET, null, contentvalues);
		}
	}

	public DBAdapter apri() throws SQLException {
		try {
			dbHelper = new DBHelper(context);
			database = dbHelper.getWritableDatabase();
		} catch (Exception exception) {
			Log.e("DB", "il database era già aperto");
		}
		return this;
	}

	public Data cercaData(long l) throws WidgetConfigurationNotFoundException {
		Cursor cursor = database.query(NOME_TAVOLA, null, (new StringBuilder(
				"millisecondiIniziali=")).append(l).toString(), null, null,
				null, null);
		cursor.moveToFirst();
		if (cursor.getCount() <= 0)
			throw new WidgetConfigurationNotFoundException(
					"Widget configuration not found in database, please reconfigure the widget");
		return Data.leggi(cursor, context);
	}

	public void chiudi() {
		dbHelper.close();
		database.close();
	}

	public long createData(Data data) {
		ContentValues contentvalues = createContentValues(data, 0);
		return database.insertOrThrow(NOME_TAVOLA, null, contentvalues);
	}

	public long createData(int year, int month, int day, int hour, int minute,
			boolean years, boolean months, boolean weeks, boolean days,
			boolean hours, boolean minutes, boolean seconds, String s,
			long msI, int color,boolean notifica) {
		ContentValues contentvalues = createContentValues(year, month, day,
				hour, minute, years, months, weeks, days, hours, minutes,
				seconds, s, msI, color,notifica, 0);

		return database.insertOrThrow(NOME_TAVOLA, null, contentvalues);
	}

	public boolean deleteData(long l) {
		return database.delete(NOME_TAVOLA, MILLISECONDI_INIZIALI + " = " + l,
				null) > 0;

	}

	public boolean deleteData(Data data) {
		return deleteData(data.getMillisecondiIniziali());
	}

	public Cursor fetchAllData() {
		return database.query(NOME_TAVOLA, null, null, null, null, null,
				"posizione");
	}

	public Cursor fetchOneDate(long l) {
		Log.d("millisecondiDaquery", l + "=millisecondiIniziali");
		Cursor c = database.query(NOME_TAVOLA, null, l
				+ "=millisecondiIniziali", null, null, null, null);
		Log.d("cursor lenght", c.getCount() + "");
		return c;
	}

	public Data idInPosizione(int i) {
		return Data.leggi(database.query(NOME_TAVOLA, null, (new StringBuilder(
				String.valueOf(i))).append("=posizione").toString(), null,
				null, null, null), context);
	}

	public void Upgrade(int old) {
		dbHelper.onUpgrade(database, old, DBHelper.DATABASE_VERSION);
	}

	public void spostamento(int i, int j, long l) {
		if (i > j)
			database.execSQL((new StringBuilder(
					"update date set posizione = posizione+1 where posizione>="))
					.append(j).append(" and posizione<").append(i).toString());
		else
			database.execSQL((new StringBuilder(
					"update date set posizione = posizione-1 where posizione<="))
					.append(j).append(" and posizione>").append(i).toString());
		database.execSQL((new StringBuilder("update date set posizione="))
				.append(j).append(" where ").append("millisecondiIniziali")
				.append("=").append(l).toString());
	}

	public boolean updateData(Data data) {
		ContentValues contentvalues = createContentValues(data, -1);
		boolean flag;
		if (database.update(NOME_TAVOLA, contentvalues, (new StringBuilder(
				"millisecondiIniziali="))
				.append(data.getMillisecondiIniziali()).toString(), null) > 0)
			flag = true;
		else
			flag = false;
		return flag;
	}

	public boolean updateData(int year, int month, int day, int hour,
			int minute, boolean years, boolean months, boolean weeks,
			boolean days, boolean hours, boolean minutes, boolean seconds,
			String s, long msI, int color, boolean notifica) {
		ContentValues contentvalues = createContentValues(year, month, day,
				hour, minute, years, months, weeks, days, hours, minutes,
				seconds, s, msI, color,notifica, -1);
		boolean flag;
		if (database.update(NOME_TAVOLA, contentvalues, MILLISECONDI_INIZIALI
				+ "=" + msI, null) > 0)
			flag = true;
		else
			flag = false;
		return flag;
	}
	public void cambiaPosizione(long msI, int i) {
		ContentValues cv=new ContentValues();
		cv.put(POSIZIONE, i);
		database.update(NOME_TAVOLA, cv, MILLISECONDI_INIZIALI+ "=" + msI, null);
		
		
	}
	public static final String BOOL_ANNO = "anni";
	public static final String BOOL_GIORNO = "giorni";
	public static final String BOOL_MESE = "mesi";
	public static final String BOOL_MINUTO = "minuti";
	public static final String BOOL_ORA = "ore";
	public static final String BOOL_SETTIMANE = "settimane";
	public static final String DESCRIZIONE = "descrizione";
	public static final String MILLISECONDI_INIZIALI = "millisecondiIniziali";
	private static final String NOME_TAVOLA = "date";
	private static final String NOME_TAVOLA_WIDGET = "widget";
	public static final String POSIZIONE = "posizione";
	public static final String VALORE_ANNO = "anno";
	public static final String VALORE_GIORNO = "giorno";
	public static final String VALORE_MESE = "mese";
	public static final String VALORE_MINUTO = "minuto";
	public static final String VALORE_ORA = "ora";
	public static final String DATA_WIDGET = "data";
	public static final String IDWIDGET = "idWidget";
	public static final String COLORE_WIDGET = "colore";

	private Context context;
	public SQLiteDatabase database;
	private DBHelper dbHelper;
	
}
