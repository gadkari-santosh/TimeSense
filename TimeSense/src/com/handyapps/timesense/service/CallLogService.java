package com.handyapps.timesense.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.widget.Toast;

import com.handyapps.timesense.dataobjects.CallInfo;

public class CallLogService {

	private static final CallLogService INSTANCE = new CallLogService();
	
	public static CallLogService getInstance() {
		return INSTANCE;
	}
	
	public List<CallInfo> getAllCallLogs(Context context) {
		Cursor c = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,null,null, null, null);
		
		 String name = null;
		 String number = null;
		 long duration = 0;
		 
		 int callType = 0;
		 long date = 0;
		 
		 CallInfo callInfo = null;
		 
		 List<CallInfo> callInfos = new ArrayList<CallInfo>();
		 
		 while (c.moveToNext()) {
	       	 try {
	       		 
	       		name = c.getString(c.getColumnIndex(CallLog.Calls.CACHED_NAME));
	       		number = c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));
	       		callType = c.getInt(c.getColumnIndex(CallLog.Calls.TYPE));
	       		date = c.getLong(c.getColumnIndex(CallLog.Calls.DATE));
	       		duration = c.getLong(c.getColumnIndex(CallLog.Calls.DURATION));
	       		
	       		if (number != null) {
	       			callInfo = new CallInfo();
		       		callInfo.setName(name);
		       		callInfo.setPhoneNumber(number);
		       		callInfo.setCallType(callType);
		       		callInfo.setLocalTime(new Date(date));
		       		callInfo.setDuration(getDuration(duration));
		       		
		       		callInfos.add(callInfo);
	       		}
	       		
	       	 } catch (Throwable exp) {
		       	Toast.makeText(context, exp.toString(), 100).show();
	       		exp.printStackTrace();
	       	 }
		 }
		 
		 return callInfos;
	}
	
	private String getDuration(long duration) {
		long min = duration/60;
		long hour = min/60;
		
		long total = hour*60 + min*60;
		long sec = duration - total;
		
		return String.format("%d:%d:%d", hour,min,sec);
	}
}
