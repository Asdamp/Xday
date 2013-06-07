package com.asdamp.x_day;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.asdamp.utility.TextEditDialog;
import android.net.Uri;

import android.os.Bundle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.DialogFragment;
import android.text.method.LinkMovementMethod;

public class About extends SherlockFragmentActivity implements
TextEditDialog.TextEditDialogInterface{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		//
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		TextView t2 = (TextView) findViewById(R.id.textView5);
		TextView sourceCode = (TextView) findViewById(R.id.sourceCode);
		sourceCode.setMovementMethod(LinkMovementMethod.getInstance());

		t2.setMovementMethod(LinkMovementMethod.getInstance());
		Button gPlay = (Button) this.findViewById(R.id.gPlayButton);
		Button amazon = (Button) this.findViewById(R.id.amazonAppShopButton);		
		View t4=this.findViewById(R.id.textView4);
		SharedPreferences shprs = getSharedPreferences(
				"PrivateOption", 0);
		if(!shprs.getBoolean("Premium", true)){
			t4.setVisibility(View.GONE);
			gPlay.setVisibility(View.GONE);
			amazon.setVisibility(View.GONE);
		}
		else{
		gPlay.setOnClickListener(new OnClickListener() 
		{

			public void onClick(View v) {
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse("market://details?id="
									+ "com.asdamp.x_dayAdFree")));
				} catch (android.content.ActivityNotFoundException anfe) {
					startActivity(new Intent(
							Intent.ACTION_VIEW,
							Uri.parse("http://play.google.com/store/apps/details?id="
									+ "com.asdamp.x_dayAdFree")));
				}

			}
		});
		amazon.setOnClickListener(new OnClickListener() 
		{

			public void onClick(View v) {
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse("amzn://apps/android?p=com.asdamp.x_dayAdFree")));
				} catch (android.content.ActivityNotFoundException anfe) {
					startActivity(new Intent(
							Intent.ACTION_VIEW,
							Uri.parse("http://www.amazon.com/gp/mas/dl/android?p=com.asdamp.x_dayAdFree")));
				}

			}
		});

		}
	}
	@Override
	/*public boolean onCreateOptionsMenu(Menu menu) {
		this.getSupportMenuInflater().inflate(R.menu.activity_about, menu);
		return true;
	}*/
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home: {
			finish();
			break;
		}
		case R.id.CodiceAttivazione:{
			this.showTextEditDialog();
		}
		}
		return true;
	}
	public void showTextEditDialog() {
		Bundle p = new Bundle();
		p.putString(TextEditDialog.TITOLO, getString(R.string.CodiceAttivazione));
		p.putString(TextEditDialog.SOTTOTITOLO, getString(R.string.CodiceAttivazioneDescrizione));
		p.putString(TextEditDialog.STRINGA_BASE, "");
		DialogFragment textDialog = new TextEditDialog();
		textDialog.setArguments(p);
		textDialog.show(getSupportFragmentManager(), "testo");
	}
	public void OnTextEditDialogPositiveClick(String t) {
		if(t.equalsIgnoreCase("appoftheday")){
			Toast.makeText(getApplicationContext(), this.getString(R.string.CodiceAccettato), Toast.LENGTH_LONG).show();
			SharedPreferences sharedpreferences = getSharedPreferences("PrivateOption", 0);
			final android.content.SharedPreferences.Editor spe = sharedpreferences.edit();
			spe.putBoolean("Premium", false);
			spe.commit();
			Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage( getBaseContext().getPackageName() );
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
		}
		
	}
}
