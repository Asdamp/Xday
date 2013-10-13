package com.asdamp.x_day;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class Notification extends BroadcastReceiver{
	public void sendNotification(Context c, Intent i){
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(c)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle(i.getStringExtra("titolo"))
		        .setContentText(c.getText(R.string.Notifica_il_giorno_e_arrivato));
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
		mNotificationManager.notify(159, mBuilder.build());
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		sendNotification(context,intent);
		
	}
}
