package com.asdamp.x_day;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.asdamp.utility.UtilityDate;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class Notification extends BroadcastReceiver{
	private static int idNotification=0;
	public void sendNotification(Context c, Intent i){
		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Intent openMain = new Intent(c, MainActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(c, 0, openMain, 0);
		
		Intent openAdd = new Intent("com.asdamp.x_day.ADD");
		openAdd.putExtra("requestCode", Costanti.MODIFICA_DATA);
		openAdd.putExtra("MsIniziali", Long.parseLong(i.getDataString()));
		openAdd.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		openAdd.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingAdd = PendingIntent.getActivity(c, 0, openAdd, 0);

		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(c)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle(i.getStringExtra("titolo"))
		        .setAutoCancel(true)
		        .setContentIntent(pIntent)
		        .setLights(Color.WHITE, 500, 1500)
		        .setSound(alarmSound)
		        .setContentText(c.getText(R.string.Notifica_il_giorno_e_arrivato))
		        .addAction(R.drawable.ic_action_delete, c.getString(R.string.Elimina), pIntent)
        		.addAction(R.drawable.ic_action_configure, c.getString(R.string.Modifica), pendingAdd);

		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(c, MainActivity.class);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(c);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager=(	NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
		
		// mId allows you to update the notification later on.
		mNotificationManager.notify(idNotification, mBuilder.build());
		idNotification++;
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		sendNotification(context,intent);
		
	}
	
}
