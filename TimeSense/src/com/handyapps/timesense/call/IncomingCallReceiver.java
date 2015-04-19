package com.handyapps.timesense.call;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.handyapps.timesense.service.SettingsService;
import com.handyapps.timesense.service.TimeService;
import com.handyapps.timesense.service.ToastService;

@SuppressLint("NewApi")
public class IncomingCallReceiver extends CallReceiver {

	@Override
	public void processCall(Context context, Intent intent) {

		Bundle bundle = intent.getExtras();

		if (null == bundle)
			return;

		SettingsService.getInstance().init(context);
		TimeService.getInstance().init(context);
		
		Log.i("IncomingCallReceiver", bundle.toString());
		
		String state = bundle.getString(TelephonyManager.EXTRA_STATE);

		Log.i("IncomingCallReceiver", "State: " + state);
		
		if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING))
		{
			String phoneNumber = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
			
			ToastService.isCallEnded.getAndSet(false);
			
			if (phoneNumber != null && phoneNumber.startsWith("+")) {
				ToastService.showInCall(context, phoneNumber);
			}
		} else {
			ToastService.isCallEnded.getAndSet(true);
		}
	}
}

class MyPhoneListener extends PhoneStateListener {
	
	Context context = null;
	MyPhoneListener(Context context) {
		this.context = context;
	}
	
	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:

				if (incomingNumber.startsWith("+")) {
					ToastService.showInCall(context, incomingNumber);
				}
				break;
				
			case TelephonyManager.CALL_STATE_OFFHOOK:
			case TelephonyManager.CALL_STATE_IDLE:
				ToastService.isCallEnded.set(true);
				break;
		}
	}
}
