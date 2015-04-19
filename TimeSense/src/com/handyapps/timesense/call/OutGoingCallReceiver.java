package com.handyapps.timesense.call;

import static com.handyapps.timesense.constant.AppContant.*;

import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;

import com.google.gson.Gson;
import com.handyapps.timesense.dataobjects.Settings;
import com.handyapps.timesense.dataobjects.TimeCode;
import com.handyapps.timesense.dataobjects.User;
import com.handyapps.timesense.service.SettingsService;
import com.handyapps.timesense.service.TimeSenseUsersService;
import com.handyapps.timesense.service.TimeService;

public class OutGoingCallReceiver extends CallReceiver {

	public void processCall(Context context, Intent intent) {
		
		Gson gson = new Gson();
		
		final String phoneNumber = callInfo.getPhoneNumber();

		boolean isCustomCall = false;
		
		SharedPreferences settings = context.getSharedPreferences(SHARED_PREF_NAME, 1);
		
		SettingsService.getInstance().init(context);
		TimeService.getInstance().init(context);
		
		Settings timeSenseSettings = SettingsService.getInstance().getSettings();
		TimeSenseUsersService service = TimeSenseUsersService.getInstance();
		service.init(context);
		
		User user = service.getTimeSenseUser(phoneNumber);
		String timeZone = null;
		if (user != null)
			timeZone = user.getTimeZone();
		
		if (timeSenseSettings != null && !timeSenseSettings.isEnableCallSense()) {
			call(phoneNumber);
			return;
		}
		
		if (settings != null) {
			isCustomCall = settings.getBoolean("CustomCall:"+phoneNumber, false);
		}
		
		if (isCustomCall) {
			OutGoingCallReceiver.this.setResultData(phoneNumber);
			Editor edit = settings.edit();
			edit.remove("CustomCall:"+phoneNumber);
			edit.commit();
			
		} else {
			
			TimeCode timeCode = null;
			
			if (timeZone != null && !"".equalsIgnoreCase(timeZone)) {
				timeCode = TimeService.getInstance().getTimeCodeByTimeZone(timeZone);
			} else {
				timeCode = TimeService.getInstance().getTimeCodeByPhoneNumber(phoneNumber);
			}
			
			// We are not interested if we are not able to find number's country code.
			if (timeCode == null) {
				call(phoneNumber);
				return;
			}
			
			int hour = TimeService.getInstance().getHour(timeCode);
			if ( timeSenseSettings.isCallAllowed(hour) ) {
				call(phoneNumber);
				return;
			}
			
			OutGoingCallReceiver.this.setResultData(null);
			Intent intent2 = new Intent(context, OutGoingCallActivity.class);
			intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent2.putExtra("Phone", phoneNumber);
			
			if (timeCode != null)
				intent2.putExtra("TimeCode", timeCode);
			
			Uri lookupUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
			
			try {
				Cursor c = context.getContentResolver().query(
						lookupUri, 
						new String[]{Data.DISPLAY_NAME,Data.PHOTO_THUMBNAIL_URI},
						Data.CONTACT_ID + " LIKE '%?' " , new String[]{phoneNumber}, null);
				
				if (c.moveToNext()) {
					String name = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
					intent2.putExtra("Name", name);
				}
			} catch (Throwable th) {}
			
			intent2.putExtra(SHARED_CALL_INFO, gson.toJson(callInfo));
			
			context.startActivity(intent2);
		}
	}
	
	private void call(String phoneNumber) {
		OutGoingCallReceiver.this.setResultData(phoneNumber);
	}
}