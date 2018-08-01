package com.asdamp.utility;


import android.content.Context;
import android.content.Intent;
import android.widget.ShareActionProvider;

import com.asdamp.x_day.R;

public class ShareUtility {
	public static void shareText(ShareActionProvider s, String text, String subject) {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
	    shareIntent.setType("text/plain");
	    shareIntent.putExtra(Intent.EXTRA_TEXT, text);
	    shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
	    s.setShareIntent(shareIntent);
	}
	public static void shareText(Context c,String body,String subject) {
		Intent shareIntent = getShareIntent(c, body, subject);
	    c.startActivity(shareIntent);
	}
	/**
	 * @param c
	 * @param body
	 * @param subject
	 * @return
	 */
	public static Intent getShareIntent(Context c, String body, String subject) {
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND); 
	    sharingIntent.setType("text/plain");
	    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
	    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
	    Intent shareIntent=Intent.createChooser(sharingIntent, c.getText(R.string.Share));
		return shareIntent;
	}
	
	public static void shareDefaultIntent(ShareActionProvider s) {
	    Intent intent = new Intent(Intent.ACTION_SEND);
	    intent.setType("text/plain");
	    s.setShareIntent(intent);
	}
}
