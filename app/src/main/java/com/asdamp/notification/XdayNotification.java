package com.asdamp.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.asdamp.utility.ShareUtility;
import com.asdamp.x_day.Costanti;
import com.asdamp.x_day.Data;
import com.asdamp.x_day.DateListActivity;
import com.asdamp.x_day.R;
import com.google.android.material.internal.DescendantOffsetUtils;

import java.io.IOException;

public class XdayNotification extends BroadcastReceiver {
	private static int idNotification = 0;

	public void sendNotification(Context c, Intent i) {
		long idData = Long.parseLong(i.getDataString());

		sendNotification(c, idData);
		
	}

	public static void sendNotification(Context c, long idData) {
		Uri alarmSound = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Intent openMain = new Intent(c, DateListActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(c, 0, openMain, 0);
		Cursor notificationDataCursor = Costanti.getDB().fetchOneDate(idData);
		if (notificationDataCursor.moveToFirst()) {
			Data d = Data.leggi(notificationDataCursor);


			Intent openAdd = new Intent("com.asdamp.x_day.ADD");
			openAdd.putExtra("requestCode", Costanti.MODIFICA_DATA);
			openAdd.putExtra("MsIniziali", idData);
			openAdd.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			openAdd.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			PendingIntent pendingAdd = PendingIntent.getActivity(c, 0, openAdd,
					0);
			Bitmap bitmap=null;

			try {
				bitmap = BitmapFactory.decodeStream(c.getContentResolver().openInputStream(d.getImage()));
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("EXCEPTION");
			}
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					c,"dates")
					.setSmallIcon(R.drawable.ic_notification)
					.setContentTitle(d.getDescrizione())
					.setAutoCancel(true)
					.setContentIntent(pIntent)
					.setLights(Color.WHITE, 500, 1500)
					.setSound(alarmSound)
					.setContentText(
							c.getText(R.string.Notifica_il_giorno_e_arrivato))

					.addAction(R.drawable.ic_action_configure,
							c.getString(R.string.Modifica), pendingAdd);
			if(bitmap!=null){
					mBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(bitmap));
			}
		//mBuilder.set
			// Creates an explicit intent for an Activity in your app
			Intent resultIntent = new Intent(c, DateListActivity.class);
			// The stack builder object will contain an artificial back stack
			// for the
			// started Activity.
			// This ensures that navigating backward from the Activity leads out
			// of
			// your application to the Home screen.
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(c);
			// Adds the back stack for the Intent (but not the Intent itself)
			stackBuilder.addParentStack(DateListActivity.class);
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
					0, PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager = (NotificationManager) c
					.getSystemService(Context.NOTIFICATION_SERVICE);

			// mId allows you to update the notification later on.
			mNotificationManager.notify(idNotification, mBuilder.build());
			idNotification++;
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		sendNotification(context, intent);

	}
	public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
		int width = image.getWidth();
		int height = image.getHeight();

		float bitmapRatio = (float) width / (float) height;
		if (bitmapRatio > 1) {
			width = maxSize;
			height = (int) (width / bitmapRatio);
		} else {
			height = maxSize;
			width = (int) (height * bitmapRatio);
		}

		return Bitmap.createScaledBitmap(image, width, height, true);
	}
	public static void scheduleNotificationById(Context c, Data d) {

		Intent inte = new Intent(c, XdayNotification.class);
		inte.setData(Uri.parse((d.getMillisecondiIniziali() + "")));
		Log.d("NOTIFICATION","ID date: "+d.getMillisecondiIniziali());
		/*
		 * se la data da notificare non ha un titolo, setta come titolo della
		 * notifica la sua data altrimenti setta come titolo della notifica, il
		 * titolo della data
		 */

		inte.putExtra("titolo", d.getDescrizione());

		PendingIntent pi = PendingIntent.getBroadcast(c, 0, inte,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) c
				.getSystemService(Context.ALARM_SERVICE);
		if (d.isNotificationEnabled() && d.isAfterToday()) {

			am.set(AlarmManager.RTC_WAKEUP, d.getTimeInMillis(), pi);
		} else
			am.cancel(pi);

	}

}
