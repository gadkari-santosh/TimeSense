package com.san.timesense.call;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.san.timesense.service.ToastService;

@SuppressLint("NewApi")
public class IncomingCallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		Bundle bundle = intent.getExtras();

		if (null == bundle)
			return;

		Log.i("IncomingCallReceiver", bundle.toString());

		String state = bundle.getString(TelephonyManager.EXTRA_STATE);

		Log.i("IncomingCallReceiver", "State: " + state);
		
		if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING))
		{
			String phoneNumber = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

			if (phoneNumber.startsWith("+")) {
				ToastService.showInCall(context, phoneNumber);
			}
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
		}
	}
}
