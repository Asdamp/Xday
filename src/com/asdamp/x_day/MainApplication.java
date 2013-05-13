package com.asdamp.x_day;

import org.acra.*;
import org.acra.annotation.*;

import android.app.Application;
import android.content.pm.PackageManager.NameNotFoundException;

@ReportsCrashes(formUri = "http://www.bugsense.com/api/acra?api_key=b3d520b8", 
formKey = "", 
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

		/*ACRA.init(this);*/

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
}
