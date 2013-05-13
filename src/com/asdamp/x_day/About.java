package com.asdamp.x_day;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import android.net.Uri;

import android.os.Bundle;

import android.content.Intent;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.text.method.LinkMovementMethod;

public class About extends SherlockActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		//
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		TextView t2 = (TextView) findViewById(R.id.textView5);
		t2.setMovementMethod(LinkMovementMethod.getInstance());
		Button gPlay = (Button) this.findViewById(R.id.gPlayButton);
		Button amazon = (Button) this.findViewById(R.id.amazonAppShopButton);
		MainApplication app=(MainApplication) this.getApplication();
		
		View t4=this.findViewById(R.id.textView4);
		if(!this.getResources().getBoolean(R.bool.ad)){
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
									+ "com.asdamp.x_day")));
				} catch (android.content.ActivityNotFoundException anfe) {
					startActivity(new Intent(
							Intent.ACTION_VIEW,
							Uri.parse("http://play.google.com/store/apps/details?id="
									+ "com.asdamp.x_day")));
				}

			}
		});
		amazon.setOnClickListener(new OnClickListener() 
		{

			public void onClick(View v) {
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse("amzn://apps/android?p=com.asdamp.x_day")));
				} catch (android.content.ActivityNotFoundException anfe) {
					startActivity(new Intent(
							Intent.ACTION_VIEW,
							Uri.parse("http://www.amazon.com/gp/mas/dl/android?p=com.asdamp.x_day")));
				}

			}
		});

		}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home: {
			finish();
			break;
		}
		}
		return true;
	}

}
