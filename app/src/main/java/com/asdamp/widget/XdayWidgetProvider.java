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
	public static final String OPEN_APP = "com.asdamp.widget.OPEN_APP";
	public static final String REFRESH_WIDGET = "com.asdamp.widget.REFRESH_WIDGET";
	private static final String OPEN_APP_SOLO = "com.asdamp.widget.OPEN_APP_SOLO";

	private static HandlerThread sWorkerThread;
    private static Handler sWorkerQueue;
	private static final String action="action";

	public  XdayWidgetProvider() {
		sWorkerThread = new HandlerThread("XdayWidgetProvider-worker");
        sWorkerThread.start();
        sWorkerQueue = new Handler(sWorkerThread.getLooper());
	}
	@SuppressLint("NewApi")
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context,appWidgetManager,appWidgetIds);
		for (int appWidgetId : appWidgetIds) {
			System.out.println("inizio aggiornamento posizione numero "
					+ appWidgetId);
			Intent intent = new Intent(context, WidgetRemote.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					appWidgetId);
			intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
			// Instantiate the RemoteViews object for the App Widget layout.
			RemoteViews rv = new RemoteViews(context.getPackageName(),
					R.layout.layout_widget);


			Intent inte = new Intent(ACTION_ADD);
			inte.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			inte.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			inte.putExtra(action, ACTION_ADD);
			PendingIntent pi = PendingIntent.getBroadcast(context, 0, inte, PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setOnClickPendingIntent(R.id.pulsante_add_widget, pi);

			Intent inte2 = new Intent(OPEN_APP);
			inte2.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			inte2.putExtra(action, OPEN_APP);

			inte2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			PendingIntent pi2 = PendingIntent
					.getBroadcast(context, 3, inte2, PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setOnClickPendingIntent(R.id.list_widget, pi2);

			Intent inte3 = new Intent(REFRESH_WIDGET);
			inte3.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			inte3.putExtra(action, REFRESH_WIDGET);

			inte3.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			PendingIntent pi3 = PendingIntent.getBroadcast(context, 1, inte3, PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setOnClickPendingIntent(R.id.pulsante_refresh_widget, pi3);
			rv.setRemoteAdapter( R.id.list_view_widget, intent);
			appWidgetManager.updateAppWidget(appWidgetId, rv);
		}

	}

	@Override
	public void onReceive(final Context context, final Intent intent) {
		super.onReceive(context, intent);

		System.out.println("premuto");
		String currAction=null;
		if(intent.getExtras()!=null) {
			currAction = intent.getExtras().getString(action);
		}
		if(currAction!=null)
			if (currAction.equals(ACTION_ADD)) {

				Intent add = new Intent(context, Add.class);
				add.putExtra("requestCode", Costanti.CREA_DATA);
				add.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				add.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(add);
			} else if (currAction.equals(OPEN_APP)|| currAction.equals(OPEN_APP_SOLO)) {
						PackageManager pm = context.getPackageManager();
						Intent i = pm.getLaunchIntentForPackage(context.getPackageName());
						i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						context.startActivity(i);

			}
			sWorkerQueue.post(new Runnable() {
			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			public void run() {

				AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
				final ComponentName cn = new ComponentName(context, XdayWidgetProvider.class);
                    
				appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(cn), R.id.list_view_widget);
			}
			
			});

		}
	}

	
	
