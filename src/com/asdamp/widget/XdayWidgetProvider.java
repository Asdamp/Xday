package com.asdamp.widget;

import com.asdamp.x_day.Add;
import com.asdamp.x_day.Costanti;
import com.asdamp.x_day.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.RemoteViews;

public class XdayWidgetProvider extends AppWidgetProvider {
	private static final String ACTION_ADD = "com.asdamp.widget.ADD";
	private static final String OPEN_APP = "com.asdamp.widget.OPEN_APP";
	private static final String REFRESH_WIDGET = "com.asdamp.widget.REFRESH_WIDGET";
	
	private static HandlerThread sWorkerThread;
    private static Handler sWorkerQueue; 
    
	public  XdayWidgetProvider() {
		sWorkerThread = new HandlerThread("XdayWidgetProvider-worker");
        sWorkerThread.start();
        sWorkerQueue = new Handler(sWorkerThread.getLooper());
	}
	@SuppressLint("NewApi")
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		for (int i = 0; i < appWidgetIds.length; i++) {
			System.out.println("inizio aggiornamento posizione numero "
					+ appWidgetIds[i]);
			Intent intent = new Intent(context, WidgetRemote.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					appWidgetIds[i]);
			intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
			// Instantiate the RemoteViews object for the App Widget layout.
			RemoteViews rv = new RemoteViews(context.getPackageName(),
					R.layout.layout_widget);



			Intent inte = new Intent(ACTION_ADD);
			inte.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
			PendingIntent pi = PendingIntent.getBroadcast(context, 0, inte, 0);
			rv.setOnClickPendingIntent(R.id.pulsante_add_widget, pi);

			Intent inte2 = new Intent(OPEN_APP);
			inte2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
			PendingIntent pi2 = PendingIntent
					.getBroadcast(context, 0, inte2, 0);
			rv.setOnClickPendingIntent(R.id.apri_applicazione_widget, pi2);

			Intent inte3 = new Intent(REFRESH_WIDGET);
			inte3.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
			PendingIntent pi3 = PendingIntent.getBroadcast(context, 0, inte3, 0);
			rv.setOnClickPendingIntent(R.id.pulsante_refresh_widget, pi3);
			rv.setRemoteAdapter(appWidgetIds[i], R.id.list_view_widget, intent);			
			appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
		}

	}

	@Override
	public void onReceive(final Context context, final Intent intent) {
		System.out.println("premuto");
		final Context context2 = context;
		super.onReceive(context, intent);
		if (intent.getAction().equals(ACTION_ADD)) {
			Intent add = new Intent(context, Add.class);
			add.putExtra("requestCode", Costanti.CREA_DATA);
			add.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			add.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			context.startActivity(add);
		} else if (intent.getAction().equals(OPEN_APP)) {
					PackageManager pm = context.getPackageManager();
					Intent i = pm.getLaunchIntentForPackage(context.getPackageName());
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					context.startActivity(i);
			
		} 
			sWorkerQueue.post(new Runnable() {
			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			public void run() {
				AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context2);
				final ComponentName cn = new ComponentName(context2, XdayWidgetProvider.class);
                    
				appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(cn), R.id.list_view_widget);
					}
			
			});

		}
	}

	
	
