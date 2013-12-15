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
import android.widget.Toast;

import com.asdamp.x_day.R;
import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class StartupUtility {
	private static final String CURR_VERSION = "currVersion";
	private static final String UTILIZZI_PER_REVIEW = "utv";
	private Context c;
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
		spe.putInt("Utilizzi", utilizzi + 1);
		spe.commit();
	}

	public void showAdMobAds(AdView ads) {
		if (ad)
			ads.loadAd(new AdRequest().addTestDevice(AdRequest.TEST_EMULATOR)
					.addTestDevice("8D2F8A681D6D472A953FBC3E75CE9276")
					.addTestDevice("A2642CE92F5DAD2149B05FE4B1F32EA5")
					.addTestDevice("3A4195F433B132420871F4202A7789C3")
					.addTestDevice("814F884FB86C9018023155DD51B428D0")
);
	}

	public void changeAd(boolean ads) {
		spe.putBoolean("Premium", ads);
		spe.commit();
	}

	public void toPlayStore(CharSequence titolo, CharSequence descrizione, int iconId,
			CharSequence positive, CharSequence negative, CharSequence neutral, int times,
			final CharSequence packageName) {
		final int nVolte = shprs.getInt(UTILIZZI_PER_REVIEW, times);
		if (utilizzi == nVolte) {
			Builder builder = new android.app.AlertDialog.Builder(c);
			builder.setMessage(descrizione).setTitle(titolo).setIcon(iconId);
			if(positive!=null)
			builder.setPositiveButton(positive,
					new DialogInterface.OnClickListener() {
						/*
						 * this onclick method open the play store for the app
						 * review. if the play store doesn't exist, open the
						 * amazon appshop if neither play store nor appshop are
						 * installed, the method open the app play store web
						 * page
						 */
						public void onClick(DialogInterface dialog, int which) {
							Uri ur;
							try {
								ur = Uri.parse("market://details?id="
										+ packageName);
								c.startActivity(new Intent(
										"android.intent.action.VIEW", ur));
							} catch (ActivityNotFoundException activitynotfoundexception)

							{
								try {
									ur = Uri.parse("amzn://apps/android?p="
											+ packageName);
									c.startActivity(new Intent(
											"android.intent.action.VIEW", ur));
								} catch (ActivityNotFoundException acnf) {
									c.startActivity(new Intent(
											Intent.ACTION_VIEW,
											Uri.parse("http://play.google.com/store/apps/details?id="
													+ packageName)));
								}
							}

						}

					});
			if(negative!=null)
			builder.setNegativeButton(negative,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

						}

					});
			if(neutral!=null)
			builder.setNeutralButton(neutral,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							spe.putInt(UTILIZZI_PER_REVIEW, nVolte + 5);

						}
					});
			builder.create().show();
		}
	}

	public void showChangelogIfVersionChanged(CharSequence cl) {
		PackageInfo pInfo;
		int packVersion;
		try {
			pInfo = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
			packVersion = pInfo.versionCode;
		} catch (NameNotFoundException e) {
			packVersion = 0;
		}

		int currVersion = shprs.getInt(CURR_VERSION, 0);
		int diffVersion = packVersion - currVersion;
		if (diffVersion > 0) {// se la versione corrente è superiore a quella
								// dell'ultimo avvio
			final AlertDialog alert;
			Builder builder = new android.app.AlertDialog.Builder(c);
			builder.setMessage(cl).setTitle(c.getText(R.string.changelog))
					.setIcon(R.drawable.ic_launcher);

			builder.setPositiveButton(c.getText(R.string.Conferma),
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// dismiss

						}
					});
			alert = builder.create();
			alert.show();
			spe.putInt(CURR_VERSION, packVersion); 
			spe.commit();
		}
	}

	public void note(String titolo, String descrizione, int iconId) {
		Builder builder = new android.app.AlertDialog.Builder(c);
		builder.setMessage(descrizione).setTitle(titolo).setIcon(iconId);
		builder.create().show();
	}

	public void carlaVaVia() {
		Toast.makeText(c, "Ciao ciao", Toast.LENGTH_LONG).show();
	}
}
