package com.asdamp.widget;


import com.asdamp.database.DBAdapter;
import com.asdamp.exception.DateNotFoundException;
import com.asdamp.utility.UserInfoUtility;
import com.asdamp.x_day.Costanti;
import com.asdamp.x_day.Data;
import com.asdamp.x_day.R;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.asdamp.widget.XdayWidgetProvider.OPEN_APP;
import static com.asdamp.widget.XdayWidgetProvider.REFRESH_WIDGET;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WidgetRemote extends RemoteViewsService {
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent arg0) {
		
		return (new ListViewFactory(this.getApplicationContext(), arg0));
	}
}

class ListViewFactory implements RemoteViewsService.RemoteViewsFactory {

	private Context ctxt;
	private Cursor cursore;
	private DBAdapter database;
	private List<Data> date;
	private int widgetID;
	public ListViewFactory(Context ctxt, Intent intent) {
		/*if(!Costanti.inizializzato()) Costanti.inizializza(ctxt);*/
		database=Costanti.getDB()/*.apri()*/;
    	cursore =database.fetchAllData();
		this.ctxt = ctxt;
		date=new ArrayList<>();
		widgetID=intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
		while(cursore.moveToNext()){
			date.add(Data.leggi(cursore));
		}
		Collections.sort(date, Data::compare);
	/*	if(reverse)
			Collections.reverse(date);*/
	}

	public int getCount() {
		return  date.size();
	}

	public long getItemId(int arg0) {
		return(date.get(0).hashCode());
	}

	public RemoteViews getLoadingView() {
		// TODO Auto-generated method stub
		return null;
	}

	public RemoteViews getViewAt(int position) {
	
		RemoteViews row = new RemoteViews(ctxt.getPackageName(), R.layout.widget_list_layout);
		Data data=date.get(position);

		row.setTextViewText(R.id.data,data.toString());
		try{
			String lefttext=data.aggiorna(ctxt);
			row.setTextViewText(R.id.mancante, UserInfoUtility.makeSpannable(lefttext, "\\d+"));

		}
		catch (ArithmeticException e){
			row.setTextViewText(R.id.mancante,ctxt.getResources().getQuantityString(R.plurals.Secondi, Integer.MAX_VALUE)+"+");
		}
		String s = data.getDescrizioneIfExists();
		if(s.equalsIgnoreCase(""))
		{
			row.setInt(R.id.descrizionePersonale, "setVisibility", View.INVISIBLE);
		} else
		{
			row.setInt(R.id.descrizionePersonale, "setVisibility", View.VISIBLE);
			row.setTextViewText(R.id.descrizionePersonale,s);

		}
		row.setInt(R.id.iv_date_image, "setBackgroundColor", data.getColor());
		if(data.getImage()!=null) {

			Bitmap bitmap = null;
			try {
				bitmap = MediaStore.Images.Media.getBitmap(ctxt.getContentResolver(), data.getImage());
				row.setImageViewBitmap(R.id.iv_date_image, bitmap);

			} catch (IOException ignored) {

			}

		}
		Intent inte2 = new Intent(OPEN_APP);
		inte2.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		inte2.putExtra("action", OPEN_APP);

		inte2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
		PendingIntent pi2 = PendingIntent
				.getBroadcast(ctxt, 5, inte2, PendingIntent.FLAG_UPDATE_CURRENT);
		row.setOnClickPendingIntent(R.id.iv_date_image, pi2);
		return (row);
	}

	public int getViewTypeCount() {
		return 1;
	}

	public boolean hasStableIds() {
		return true;
	}

	public void onCreate() {
		// TODO Auto-generated method stub

	}

	public void onDataSetChanged() {
			//database.apri();
		   if (cursore != null) {
	            cursore.close();
	        }
	        cursore = database.fetchAllData();
	       
	}

	public void onDestroy() {
		//database.close();

	}
}