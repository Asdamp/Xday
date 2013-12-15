package com.asdamp.notification;

import com.asdamp.x_day.Costanti;
import com.asdamp.x_day.Data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

public class refreshNotification extends BroadcastReceiver {
	private Context context;
	public refreshNotification() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("notificationDebug","refreshing notification schedule");
		this.context=context;
		refreshAll();

	}

	private void refreshAll() {
		Cursor c=Costanti.getDB().fetchAllData("notifica>0");
		Data data;
		while(c.moveToNext()){
			data=Data.leggi(c, context);
			Notification.scheduleNotificationById(context, data);
		}
	}

}
