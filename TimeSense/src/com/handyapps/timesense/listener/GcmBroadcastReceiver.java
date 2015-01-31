package com.handyapps.timesense.listener;

import java.util.Iterator;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.google.gson.Gson;
import com.handyapps.timesense.R;
import com.handyapps.timesense.activity.TimeSenseActivity;
import com.handyapps.timesense.dataobjects.Settings;
import com.handyapps.timesense.dataobjects.TimeZoneUpdate;
import com.handyapps.timesense.service.SettingsService;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// Explicitly specify that GcmMessageHandler will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
         GcmMessageHandler.class.getName());
        
        System.out.println(intent.getExtras());
        
        Iterator<String> iterator = intent.getExtras().keySet().iterator();
        while (iterator.hasNext()) {
        	String key = iterator.next();
        	System.out.println("Key : " + key);
        	System.out.println("1 ******** "+ intent.getExtras().getString(key));
        	System.out.println("1 ******** "+ intent.getExtras().get(key));
        }
        
        Gson gson = new Gson();
        String data  = intent.getExtras().getString("data");
        
        if (data != null && !"".equals(data)) {
        	TimeZoneUpdate timeZoneUpdate = gson.fromJson(data, TimeZoneUpdate.class);
            
        	String name = null;
        	
        	Uri lookupUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(timeZoneUpdate.getUserId()));
			
			Cursor c = context.getContentResolver().query(
					lookupUri, 
					new String[]{Data.DISPLAY_NAME,Data.PHOTO_THUMBNAIL_URI},
					Data.CONTACT_ID + " LIKE '%?' " , new String[]{timeZoneUpdate.getUserId()}, null);
			
			if (c.moveToNext()) {
				name = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			}
			
			if ( name == null || "".equals(name) ) {
				name = timeZoneUpdate.getUserId();
			}
			
			SettingsService service = SettingsService.getInstance();
            Settings settings = service.getSettings();
            Map<String, String> timeZoneUpdates = settings.getTimeZoneUpdates();
            
            timeZoneUpdates.put( timeZoneUpdate.getUserId().replace("+",""), timeZoneUpdate.getTimeZone() );
            
            service.saveSettings();
            
            Intent intent2 = new Intent(context, TimeSenseActivity.class);
            PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent2, 0);
        	
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
												            .setSmallIcon(R.drawable.ic_app) // notification icon
												            .setContentTitle(String.format("%s has changed Time zone", name)) // title for notification
												            .setContentText(
												            	String.format("%s", 
												            			timeZoneUpdate.getTimeZone())
												            		) // message for notification
												            .setAutoCancel(false)
												            .setContentIntent(pIntent)
													        .addAction(R.drawable.ic_app, "Open", pIntent);
           
//    		PendingIntent pi = PendingIntent.getActivity(context,0,intent,Intent.FLAG_ACTIVITY_NEW_TASK);
//    		mBuilder.setContentIntent(pi);
    		NotificationManager mNotificationManager =
    		                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    		
    		Notification notification = mBuilder.build();
    		
    		// hide the notification after its selected
    		notification.flags |= Notification.FLAG_AUTO_CANCEL;
    		
    		mNotificationManager.notify(0, notification);
    		
            // Start the service, keeping the device awake while it is launching.
//            startWakefulService(context, notificationIntent);
//            setResultCode(Activity.RESULT_OK);
        }
	}
}