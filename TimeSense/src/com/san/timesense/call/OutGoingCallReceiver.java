package com.san.timesense.call;

import static com.san.timesense.constant.AppContant.SHARED_PREF_NAME;

import java.io.InputStream;

import com.san.timesense.dto.TimeCode;
import com.san.timesense.service.TimeService;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;

public class OutGoingCallReceiver extends BroadcastReceiver {

	@SuppressLint("NewApi")
	@Override
	public void onReceive(Context context, Intent intent) {
		
		final String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

		SharedPreferences settings = context.getSharedPreferences(SHARED_PREF_NAME, 1);
		
		boolean isCustomCall = false;
		
		if (settings != null) {
			isCustomCall = settings.getBoolean("CustomCall:"+phoneNumber, false);
		}
		
		if (isCustomCall) {
			OutGoingCallReceiver.this.setResultData(phoneNumber);
			Editor edit = settings.edit();
			edit.remove("CustomCall:"+phoneNumber);
			edit.commit();
			
		} else {
			
			TimeCode timeCodeByPhoneNumber = TimeService.getInstance().getTimeCodeByPhoneNumber(phoneNumber);
			
			// We are not interested if the phone number doesn't start with +.
			if (timeCodeByPhoneNumber == null) {
				OutGoingCallReceiver.this.setResultData(phoneNumber);
				return;
			}
			
			OutGoingCallReceiver.this.setResultData(null);
			Intent intent2 = new Intent(context, AlertActivity.class);
			intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent2.putExtra("Phone", phoneNumber);
			
			Uri lookupUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
			
			Cursor c = context.getContentResolver().query(
					lookupUri, 
					new String[]{Data.DISPLAY_NAME,Data.PHOTO_THUMBNAIL_URI},
					Data.CONTACT_ID + " LIKE '%?' " , new String[]{phoneNumber}, null);
			
			if (c.moveToNext()) {
				String name = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				intent2.putExtra("Name", name);
			}
			context.startActivity(intent2);
			
		}
		
//		OutGoingCallReceiver.this.setResultData(phoneNumber);
		
//		String message = String.format("%s, %s\n%s", callInfo.getCountry(), callInfo.getTimeZone(),callInfo.getTime());
//		
//		Notification n  = new Notification.Builder(context)
//					        .setContentTitle(phoneNumber)
//					        .setContentText(message)
//					        .setSmallIcon(R.drawable.ic_app)
//					        .setAutoCancel(true).build();
//		
//		NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
//		
//		notificationManager.notify(0, n); 
		
		
	}
	
	public static Bitmap loadContactPhoto(ContentResolver cr, long id) {
	    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
	    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
	    if (input == null) {
	         return null;
	     }
	    return BitmapFactory.decodeStream(input);
	}

}
