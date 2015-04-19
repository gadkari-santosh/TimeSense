package com.handyapps.timesense.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.handyapps.timesense.activity.TimeZoneUpdateActivity;
import com.handyapps.timesense.dataobjects.TimeCode;

public class TimeZoneChangeListener extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		 String action=intent.getAction();
		 String timeZone = intent.getStringExtra("time-zone");
		 
		 TimeCode timeCode = new TimeCode();
		 timeCode.setTimeZone(timeZone);
		 
		 if(action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
			Intent intent2 = new Intent(context, TimeZoneUpdateActivity.class);
			intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent2.putExtra("time_zone", timeZone);
			context.startActivity(intent2);
		 }
	}
}
