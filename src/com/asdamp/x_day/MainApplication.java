package com.asdamp.x_day;

import org.acra.*;
import org.acra.annotation.*;

import com.asdamp.widget.XdayWidgetProvider;

import android.annotation.TargetApi;
import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

@ReportsCrashes(
		formKey = "",
        formUri = "https://asdamp.cloudant.com/acra-xday/_design/acra-storage/_update/report",
        reportType = org.acra.sender.HttpSender.Type.JSON,
        httpMethod = org.acra.sender.HttpSender.Method.PUT,
        formUriBasicAuthLogin="teredlyedricumusbytowsto",
        formUriBasicAuthPassword="B5MIIGwjJHeNAvBtqqeSqQLt",		
        mode = ReportingInteractionMode.TOAST, 
        forceCloseDialogAfterToast = true,																																										// false
        resToastText = R.string.sending_crash_report_to_developer)
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
}
