package com.asdamp.widget;


import com.asdamp.database.DBAdapter;
import com.asdamp.exception.DateNotFoundException;
import com.asdamp.x_day.Costanti;
import com.asdamp.x_day.Data;
import com.asdamp.x_day.R;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WidgetRemote extends RemoteViewsService {
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent arg0) {
		
		return (new ListViewFactory(this.getApplicationContext(), arg0));
	}
}

class ListViewFactory implements RemoteViewsService.RemoteViewsFactory {

	private Context ctxt;
	private int idData;
	private Cursor cursore;
	private int idMancante;
	private int idDescrizionePersonale;
	private int idProgressi;
	private int maxBarraProgressi;
	private DBAdapter database;
	public ListViewFactory(Context ctxt, Intent intent) {
		/*if(!Costanti.inizializzato()) Costanti.inizializza(ctxt);*/
		database=Costanti.getDB()/*.apri()*/;
    	cursore =database.fetchAllData();
    	idData=R.id.data;
    	idMancante=R.id.mancante;
    	idDescrizionePersonale=R.id.descrizionePersonale;
    	idProgressi=R.id.progressi;
    	maxBarraProgressi=1000;
		this.ctxt = ctxt;
		//database.close();
	}

	public int getCount() {
		if(cursore==null) cursore =Costanti.getDB().fetchAllData();
		 return(cursore.getCount());
	}

	public long getItemId(int arg0) {
		return(arg0);
	}

	public RemoteViews getLoadingView() {
		// TODO Auto-generated method stub
		return null;
	}

	public RemoteViews getViewAt(int position) {
	
		String data;
		RemoteViews row = new RemoteViews(ctxt.getPackageName(), R.layout.widget_list_layout);
		if(!cursore.moveToPosition(position)) return null;
		Data posizione= Data.leggi(cursore); 
		data=posizione.toString();
		row.setTextViewText(idData, data);
		row.setTextViewText(idMancante, posizione.aggiorna(ctxt));		
		String temp=posizione.getDescrizione();		
		if(temp.equalsIgnoreCase("")){
			row.setViewVisibility(idDescrizionePersonale, View.GONE);	
		}
		else{
			try {
				row.setTextColor(idDescrizionePersonale, database.cercaColore(posizione.getMillisecondiIniziali()));
			} catch (DateNotFoundException e) {
				row.setTextColor(idDescrizionePersonale, -16746590);
			}
			row.setViewVisibility(idDescrizionePersonale, View.VISIBLE);
			row.setTextViewText(idDescrizionePersonale, temp);			
		}
		
		if(posizione.getPercentuale()==maxBarraProgressi){
			row.setTextViewText(R.id.mancanoopassato, ctxt.getText(R.string.Passato));
			row.setTextViewText(R.id.alladata, ctxt.getText(R.string.DallaData));
			row.setViewVisibility(idProgressi, View.GONE);
		}
		else{
			row.setProgressBar(idProgressi, maxBarraProgressi, posizione.getPercentuale(), false);
			row.setViewVisibility(idProgressi, View.VISIBLE);
		}
		
		
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