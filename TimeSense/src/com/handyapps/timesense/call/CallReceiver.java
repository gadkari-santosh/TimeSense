package com.handyapps.timesense.call;

import static com.handyapps.timesense.constant.AppContant.SHARED_CALL_INFO;
import static com.handyapps.timesense.constant.AppContant.SHARED_PREF_NAME;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;
import com.handyapps.timesense.dataobjects.CallInfo;

public abstract class CallReceiver extends BroadcastReceiver {
	
	protected CallInfo callInfo = null;
	
	@SuppressLint("NewApi")
	@Override
	public void onReceive(Context c, Intent i) {
		
		Gson gson = new Gson();
		
		String number = null;
		
		Bundle bundle=i.getExtras();
		
		if(bundle==null)
			return;
		
		SharedPreferences sp=c.getSharedPreferences(SHARED_PREF_NAME, 1);
		
		String s=bundle.getString(TelephonyManager.EXTRA_STATE);

		if(i.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
			number=i.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
		}
		
		else if(s.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
			number=bundle.getString("incoming_number");
		}
		
		callInfo = new CallInfo();
		callInfo.setPhoneNumber(number);
		
//		sp.edit().putString(SHARED_CALL_INFO, gson.toJson(callInfo)).commit();
		
		History h=new History(new Handler(),c);
		ContentResolver contentResolver = c.getContentResolver();
		contentResolver.registerContentObserver(CallLog.Calls.CONTENT_URI, false, h);
		
		processCall(c, i);
		
	}
	
	public abstract void processCall(Context context, Intent intent); 
	
}
