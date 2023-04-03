package com.asdamp.database;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.asdamp.exception.DateNotFoundException;
import com.asdamp.exception.WidgetConfigurationNotFoundException;
import com.asdamp.notification.XdayNotification;
import com.asdamp.utility.IOUtils;
import com.asdamp.x_day.Data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DBAdapter {

	private static final String TAG = "DB";

	public DBAdapter(Context context1) {
		context = context1;
	}

	public void importDB(Uri uri) throws IOException {
		
		Log.d(TAG,
				"File Path: " + uri.toString());
		File dbfi = context.getDatabasePath(DBHelper.DATABASE_NAME);
		InputStream src = null;
		OutputStream dst = null;
		dst = new FileOutputStream(dbfi);
		src = context.getContentResolver().openInputStream(uri);
		IOUtils.copy(src, dst);
		src.close();
		dst.close();
		restart();
	}
	private ContentValues createContentValues(Data data, int i) {
		return this.createContentValues(data.getYear(),
				data.getMonth(), 
				data.getDay(),
				data.getHour(), 
				data.getMinute(),
				data.getBoolAnni(),
				data.getBoolMesi(), 
				data.getBoolSettimane(), 
				data.getBoolGiorni(), 
				data.getBoolOre(),
				data.getBoolMinuti(),
				data.getBoolSecondi(),
				data.getDescrizioneIfExists(),
				data.getMillisecondiIniziali(),
				data.getColor(), 
				data.isNotificationEnabled(), i, data.getImage());
	}

	private ContentValues createContentValues(int year, int month, int day,
			int hour, int minute, boolean years, boolean months, boolean weeks,
			boolean days, boolean hours, boolean minutes, boolean seconds,
			String s, long msI, int color, boolean notifica, int i, Uri image) {
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
		if(image!=null)
			contentvalues.put("immagine", image.toString());
		else
			contentvalues.put("immagine", (String) null);

		if (i != -1)
			contentvalues.put("posizione", i);
		return contentvalues;
	}

	public Cursor AssociaDataAWidget(int i)
			throws WidgetConfigurationNotFoundException {
		Cursor cursor = database.query(NOME_TAVOLA_WIDGET, null, IDWIDGET + "="
				+ i, null, null, null, null);
		if (cursor.getCount() <= 0)
			throw new WidgetConfigurationNotFoundException(
					"Widget configuration not found in database, please reconfigure the widget");
		cursor.moveToFirst();

		return cursor;
	}

	public int cercaColore(long i) throws DateNotFoundException {

		String[] column = new String[1];
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
		return Data.leggi(cursor);
	}

	public void chiudi() {
		dbHelper.close();
		database.close();
	}

	public long createData(Data data) {
		ContentValues contentvalues = createContentValues(data, 0);
		return database.insertOrThrow(NOME_TAVOLA, null, contentvalues);
	}

/*	public long createData(int year, int month, int day, int hour, int minute,
			boolean years, boolean months, boolean weeks, boolean days,
			boolean hours, boolean minutes, boolean seconds, String s,
			long msI, int color, boolean notifica) {
		ContentValues contentvalues = createContentValues(year, month, day,
				hour, minute, years, months, weeks, days, hours, minutes,
				seconds, s, msI, color, notifica, 0);
		return database.insertOrThrow(NOME_TAVOLA, null, contentvalues);
	}*/

	public boolean deleteData(long l) {
		// delete notification if exist
		Intent inte = new Intent(context, XdayNotification.class);
		inte.setData(Uri.parse(l + ""));
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, inte,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pi);
		// delete from table
		return database.delete(NOME_TAVOLA, MILLISECONDI_INIZIALI + " = " + l,
				null) > 0;

	}
	public int numRecordsDate(){
		Cursor mCount= database.rawQuery("select count(*) from "+NOME_TAVOLA, null);
		mCount.moveToFirst();
		int count= mCount.getInt(0);
		mCount.close();
		return count;
	}
	public boolean deleteData(Data data) {
		return deleteData(data.getMillisecondiIniziali());
	}

	public Cursor fetchAllData() {
		return database.query(NOME_TAVOLA, null, null, null, null, null,
				"posizione");
	}
	public Cursor fetchAllData(String selection) {
		return database.query(NOME_TAVOLA, null, selection, null, null, null,
				null);
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
				null, null, null));
	}

	public void Upgrade(int old) {
		if (old != DBHelper.DATABASE_VERSION)
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
        flag = database.update(NOME_TAVOLA, contentvalues, (new StringBuilder(
                "millisecondiIniziali="))
                .append(data.getMillisecondiIniziali()).toString(), null) > 0;
		return flag;
	}

/*	public boolean updateData(int year, int month, int day, int hour,
			int minute, boolean years, boolean months, boolean weeks,
			boolean days, boolean hours, boolean minutes, boolean seconds,
			String s, long msI, int color, boolean notifica) {
		ContentValues contentvalues = createContentValues(year, month, day,
				hour, minute, years, months, weeks, days, hours, minutes,
				seconds, s, msI, color, notifica, -1);
		boolean flag;
		if (database.update(NOME_TAVOLA, contentvalues, MILLISECONDI_INIZIALI
				+ "=" + msI, null) > 0)
			flag = true;
		else
			flag = false;
		return flag;
	}*/

	public void cambiaPosizione(long msI, int i) {
		ContentValues cv = new ContentValues();
		cv.put(POSIZIONE, i);
		database.update(NOME_TAVOLA, cv, MILLISECONDI_INIZIALI + "=" + msI,
				null);

	}

	public void restart() {
		chiudi();
		apri();
		
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

	private final Context context;
	public SQLiteDatabase database;
	private DBHelper dbHelper;

}
