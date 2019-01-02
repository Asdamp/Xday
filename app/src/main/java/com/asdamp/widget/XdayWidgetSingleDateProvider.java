package com.asdamp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import com.asdamp.database.DBAdapter;
import com.asdamp.exception.DateNotFoundException;
import com.asdamp.exception.WidgetConfigurationNotFoundException;
import com.asdamp.utility.UserInfoUtility;
import com.asdamp.x_day.Costanti;
import com.asdamp.x_day.Data;
import com.asdamp.x_day.GlideApp;
import com.asdamp.x_day.R;

import androidx.cardview.widget.CardView;
import androidx.palette.graphics.Palette;

public class XdayWidgetSingleDateProvider extends AppWidgetProvider {


	
	public XdayWidgetSingleDateProvider() {
		sWorkerThread = new HandlerThread("XdayWidgetSingleDateProvider-worker");
		sWorkerThread.start();
		sWorkerQueue = new Handler(sWorkerThread.getLooper());
	}
	public static void aggiornaWidget(AppWidgetManager appwidgetmanager,
			Data data, int color, Context context, int i){
		RemoteViews remoteviews = new RemoteViews(context.getPackageName(),
				R.layout.widget_single_data_layout);
		remoteviews.setInt(R.id.iv_date_image, "setBackgroundColor", data.getColor());

		Palette p = null;
		/*
		holder.mImage.setImageDrawable(null);
		if(data.getImage()!=null)
			GlideApp.with(context).load(data.getImage()).centerCrop().into(holder.mImage);
      /*  else
            holder.mImage.setVisibility(View.INVISIBLE
            );*/
		remoteviews.setTextViewText(R.id.data,data.toString());
		try{
			String lefttext=data.aggiorna(context);
			remoteviews.setTextViewText(R.id.mancante,UserInfoUtility.makeSpannable(lefttext, "\\d+"));

		}
		catch (ArithmeticException e){
			remoteviews.setTextViewText(R.id.mancante,context.getResources().getQuantityString(R.plurals.Secondi, Integer.MAX_VALUE)+"+");
			}
		String s = data.getDescrizioneIfExists();
		if(s.equalsIgnoreCase(""))
		{
			remoteviews.setInt(R.id.descrizionePersonale, "setVisibility", View.INVISIBLE);
			} else
		{
			remoteviews.setInt(R.id.descrizionePersonale, "setVisibility", View.VISIBLE);
			remoteviews.setTextViewText(R.id.descrizionePersonale,s);

		}

			appwidgetmanager.updateAppWidget(i, remoteviews);
		


		
	}
	public static void aggiornaWidget(AppWidgetManager appwidgetmanager,
			Cursor c, Context context, int i) {
		int col;
		Data data;
		try {
			data = dbadapter.cercaData(c.getLong(c.getColumnIndex("data")));
			col= c.getInt(c.getColumnIndex(DBAdapter.COLORE_WIDGET));
			c.close();
			aggiornaWidget(appwidgetmanager, data, col, context, i);
		} catch (WidgetConfigurationNotFoundException e) {
			ConfigurationNotFound(appwidgetmanager, context, i);
		}
		
	}
	public static void ConfigurationNotFound(AppWidgetManager appwidgetmanager, Context context, int irta){
		Log.d("WidgetErrIrta",""+irta);
		RemoteViews remoteviews = new RemoteViews(context.getPackageName(),
				R.layout.empty_view);
		remoteviews.setTextViewText(R.id.empty_view_text, context.getString(R.string.ConfigurationNotFoundWidgetSolo));
		Intent intent1 = new Intent(OPEN_CONFIGURATION);
		intent1.putExtra("appWidgetId", irta);
		remoteviews.setOnClickPendingIntent(R.id.empty_view_text,
				PendingIntent.getBroadcast(context, irta, intent1, 0));
		appwidgetmanager.updateAppWidget(irta, remoteviews);

	}
	public void onReceive(final Context context, final Intent intent) {
		super.onReceive(context, intent);
		Bundle b=intent.getExtras();
		if(b== null) return;
		int idWidget=b.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
		if (intent.getAction().equals(OPEN_APP)) {
			PackageManager pm = context.getPackageManager();
			Intent i = pm.getLaunchIntentForPackage(context.getPackageName());
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			context.startActivity(i);

		} else if (intent.getAction().equals(
				AppWidgetManager.ACTION_APPWIDGET_DELETED)) {
			Log.d("widgetAction", "eliminazione widget avvenuta:");
			DBAdapter db = Costanti.getDB(); 
			boolean a = db.eliminaDataWidget(idWidget);
			Log.d("widgetAction", "" + a);
		}
			else if(intent.getAction().equals(OPEN_CONFIGURATION)){
				Intent add = new Intent("com.asdamp.widget.CONFIGURE_WIDGET_SOLO");
				Log.d("WidgetErr pre open",""+idWidget);
				add.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, idWidget);
				add.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				add.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(add);
			
			}
			else if (intent.getAction().equals(REFRESH_WIDGET)){
				sWorkerQueue.post(new Runnable() {
					public void run() {
						Log.d("widgetAction", "in aggiornamento");
						DBAdapter dbadapter = Costanti.getDB()/*.apri()*/;
						AppWidgetManager appwidgetmanager = AppWidgetManager
								.getInstance(context);
						int a[]=appwidgetmanager.getAppWidgetIds(new ComponentName(context, XdayWidgetSingleDateProvider.class));
						for(int id:a) {
							Log.d("IDwidget",""+id);
		
							try {
								Cursor c=dbadapter.AssociaDataAWidget(id);
								aggiornaWidget(appwidgetmanager, c, context, id);
							} catch (WidgetConfigurationNotFoundException e) {
								ConfigurationNotFound(appwidgetmanager, context, id);
							}
							
							
							
						}
						
					}
				});}
	}

	public void onUpdate(Context context, AppWidgetManager appwidgetmanager,
			int ai[]) {

		for (int i = 0; i < ai.length; i++) {
			 try {
				for (int id : ai) {
				
					try {
						Cursor c=dbadapter.AssociaDataAWidget(id);
						aggiornaWidget(appwidgetmanager, c, context, id);
					} catch (WidgetConfigurationNotFoundException e) {
						Log.d("widgetErr",""+id);
						ConfigurationNotFound(appwidgetmanager, context, id);
					}
				}
			} catch (ArrayIndexOutOfBoundsException e) { 
				Log.d("widgetAction", "nuovo widget");
			}
			 catch (CursorIndexOutOfBoundsException e) { 
				Log.d("widgetAction", "nuovo widget");
			}
		}

	}

	private static final String OPEN_APP = "com.asdamp.widget.OPEN_APP_SOLO";
	private static final String OPEN_CONFIGURATION = "com.asdamp.widget.OPEN_CONFIGURATION_SOLO";

	private static final String REFRESH_WIDGET = "com.asdamp.widget.REFRESH_WIDGET_SOLO";

	private static Handler sWorkerQueue;
	private static HandlerThread sWorkerThread;
	private static DBAdapter dbadapter=Costanti.getDB();
}
