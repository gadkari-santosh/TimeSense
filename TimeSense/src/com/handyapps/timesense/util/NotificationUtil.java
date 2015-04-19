package com.handyapps.timesense.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.handyapps.timesense.R;
import com.handyapps.timesense.activity.TimeSenseActivity;

public class NotificationUtil {

	public static void showNotification(Context context, String message, String title) {
		
		  Intent intent2 = new Intent(context, TimeSenseActivity.class);
          PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent2, 0);
          
		   NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
											           .setSmallIcon(R.drawable.ic_launcher) // notification icon
											           .setContentTitle(title) // title for notification
											           .setContentText(message) // message for notification
											           .setAutoCancel(false)
											           .setDefaults(Notification.DEFAULT_SOUND)
											           .setContentIntent(pIntent)
												       .addAction(R.drawable.ic_launcher, "Open", pIntent);

		//PendingIntent pi = PendingIntent.getActivity(context,0,intent,Intent.FLAG_ACTIVITY_NEW_TASK);
		//mBuilder.setContentIntent(pi);
		NotificationManager mNotificationManager =
		(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		Notification notification = mBuilder.build();
		
		// hide the notification after its selected
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		mNotificationManager.notify(0, notification);
	}
}
