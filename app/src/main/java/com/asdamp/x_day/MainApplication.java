package com.asdamp.x_day;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import com.asdamp.widget.XdayWidgetProvider;
import com.google.android.gms.ads.MobileAds;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.pixplicity.easyprefs.library.Prefs;


public class MainApplication extends Application {
	@Override
	public void onTerminate() {
		Costanti.chiudiDB();
		super.onTerminate();
	}

	@Override
	public void onCreate() {
		super.onCreate();

		new Prefs.Builder()
				.setContext(this)
				.setMode(ContextWrapper.MODE_PRIVATE)
				.setPrefsName(getPackageName())
				.setUseDefaultSharedPreference(true)
				.build();		AndroidThreeTen.init(this);
		MobileAds.initialize(this,
				this.getResources().getString(R.string.banner_app_id));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

			NotificationChannel notificationChannel = new NotificationChannel("dates",
				getString(R.string.notification_channel_name_dates), NotificationManager.IMPORTANCE_DEFAULT);
			NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			manager.createNotificationChannel(notificationChannel);
		}
		//ACRA.init(this);
		initSingletons();
	}

	protected void initSingletons() {
		Costanti.inizializza(this);
		Costanti.getDB().apri();
	}
	
	protected final boolean isPackageInstalled(String packageName) {
	    try {
	        getPackageManager().getPackageInfo(packageName, 0);
	    } catch (NameNotFoundException e) {
	        return false;
	    }
	    return true;
	}
	public  static boolean isMoreThenICS(){
		return(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH);
	}
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void aggiornaWidget() {
		if (Costanti.getOsVersion() >= 11) {
			AppWidgetManager appwidgetmanager = AppWidgetManager
					.getInstance(this);
			appwidgetmanager.notifyAppWidgetViewDataChanged(appwidgetmanager
					.getAppWidgetIds(new ComponentName(this,
							XdayWidgetProvider.class)), R.id.list_view_widget);
		}
}

	public static boolean isMoreThenGB() {
		return(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.GINGERBREAD);

	}
}
