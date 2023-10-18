package com.asdamp.utility;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.view.WindowManager.BadTokenException;

import com.asdamp.x_day.R;


public class StartupUtility {
	private static final String CURR_VERSION = "currVersion";
	private static final String UTILIZZI_PER_REVIEW = "utv";
	private final Context c;
	private static StartupUtility singleton = null;
	public final Editor spe;
	private boolean ad;
	public final SharedPreferences shprs;
	int utilizzi;

	public static StartupUtility getInstance(Context c) {
		if (singleton == null)
			singleton = new StartupUtility(c);
		return singleton;
	}

	private StartupUtility(Context c) {
		this.c = c;
		shprs = c.getSharedPreferences("PrivateOption", 0);
		spe = shprs.edit();
		utilizzi = shprs.getInt("Utilizzi", 0);

		if (utilizzi == 0) {
			Resources r = c.getResources();
			try {
				ad = r.getBoolean(R.bool.ad);
			} catch (Resources.NotFoundException e) {
				ad = true;
			}
			spe.putBoolean("Premium", ad);
		} else
			ad = shprs.getBoolean("Premium", true);
		Log.d("Premium", ""+ad);
		spe.putInt("Utilizzi", utilizzi + 1);
		spe.commit();
	}


	public void setPremium(boolean ads) {
		spe.putBoolean("Premium", ads);
		spe.commit();
		ad=ads;

	}
	public boolean isPremium() {
		return  !ad;

	}

	public void note(String titolo, String descrizione, int iconId) {
		Builder builder = new android.app.AlertDialog.Builder(c);
		builder.setMessage(descrizione).setTitle(titolo).setIcon(iconId);
		builder.create().show();
	}

}
