package com.asdamp.x_day;

import android.annotation.TargetApi;
import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import com.asdamp.widget.XdayWidgetProvider;


public class MainApplication extends Application {
	@Override
	public void onTerminate() {
		Costanti.chiudiDB();
		super.onTerminate();
	}

	@Override
	public void onCreate() {
		super.onCreate();

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
