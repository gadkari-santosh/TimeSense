package com.handyapps.timesense.call;

import static com.handyapps.timesense.constant.AppContant.*;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.provider.CallLog;

import com.google.gson.Gson;
import com.handyapps.timesense.dataobjects.CallInfo;
import com.handyapps.timesense.db.CallLogDAO;

public class History extends ContentObserver {

	private Context c;
	
	
	public History(Handler handler, Context cc) {
		super(handler);
		c=cc;
	}
	
	@Override
    public boolean deliverSelfNotifications() {
        return false;
    }

	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		
		SharedPreferences sp=c.getSharedPreferences(SHARED_PREF_NAME, 1);
		String callJson = sp.getString(SHARED_CALL_INFO, null);
		
		Gson gson = new Gson();
		
		if(callJson!=null)
		{
			CallInfo callInfo = gson.fromJson(callJson, CallInfo.class);
			getCalldetailsNow(callInfo);
			sp.edit().putString(SHARED_CALL_INFO, null).commit();
		}
	}
	
	private void getCalldetailsNow(CallInfo callInfo) {
		Cursor managedCursor=c.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DATE + " DESC");
		
		int number = managedCursor.getColumnIndex( CallLog.Calls.NUMBER ); 
        int duration1 = managedCursor.getColumnIndex( CallLog.Calls.DURATION);
        int type1=managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        
        int nameIdx = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        
        if( managedCursor.moveToFirst() == true ) {
           String phNumber = managedCursor.getString(number);
           long callDuration = managedCursor.getLong(duration1);
            
           int type = managedCursor.getInt(type1);
           
           String name = managedCursor.getString(nameIdx);
            
           CallLogDAO db=new CallLogDAO(c);
           
           callInfo.setDuration(callDuration);
           callInfo.setCallType(type);
           callInfo.setName(name);
           
           try {
        	   if (phNumber.equalsIgnoreCase(callInfo.getPhoneNumber()))
        		   db.addCallInfo(callInfo);
           } catch (Exception e) {
				e.printStackTrace();
		   }
        }
        
        managedCursor.close();
	}
}