package com.san.timesense;

import java.util.Iterator;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

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
        	System.out.println(key);
        	System.out.println(" ******** "+ intent.getExtras().getString(key));
        	System.out.println(" ******** "+ intent.getExtras().get(key));
        }
        
        System.out.println(" ******** "+ intent.getStringExtra("score"));
        System.out.println(" ******** "+ intent.getStringExtra("time"));
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
		
	}

}
