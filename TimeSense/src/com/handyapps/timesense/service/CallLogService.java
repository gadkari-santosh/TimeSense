package com.handyapps.timesense.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;

import com.handyapps.timesense.dataobjects.CallInfo;
import com.handyapps.timesense.dataobjects.Settings;
import com.handyapps.timesense.dataobjects.TimeCode;
import com.handyapps.timesense.db.CallLogDAO;

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
		 
		 Settings settings = SettingsService.getInstance().getSettings();
		 
		 CallLogDAO dao = new CallLogDAO(context);
		
		 try {
			callInfos = dao.getAllCallInfos();
		 } catch (Exception e) {
			e.printStackTrace();
		 } 
	
//		 if (!settings.isCallLogLoaded()) {
//		 
//			 while (c.moveToNext()) {
//		       	 try {
//		       		 
//		       		name = c.getString(c.getColumnIndex(CallLog.Calls.CACHED_NAME));
//		       		number = c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));
//		       		callType = c.getInt(c.getColumnIndex(CallLog.Calls.TYPE));
//		       		date = c.getLong(c.getColumnIndex(CallLog.Calls.DATE));
//		       		duration = c.getLong(c.getColumnIndex(CallLog.Calls.DURATION));
//		       		
//		       		if (number != null) {
//		       			callInfo = new CallInfo();
//			       		callInfo.setName(name);
//			       		callInfo.setPhoneNumber(number);
//			       		callInfo.setCallType(callType);
//			       		callInfo.setLocalDateTime(new Date(date));
//			       		callInfo.setDuration(duration);
//			       		callInfo.setId(date);
//			       		
//			       		populateTime(callInfo);
//			       		
//			       		callInfos.add(callInfo);
//		       		}
//		       		
//		       	 } catch (Throwable exp) {
//			       	Toast.makeText(context, exp.toString(), 100).show();
//		       		exp.printStackTrace();
//		       	 }
//			 }
//			 
//			 try {
//				 dao.addCallLogs(callInfos);
//				 
//				 settings.setCallLogLoaded(true);
//				 SettingsService.getInstance().saveSettings();
//				 
//			 } catch (Exception exp) {
//				 Toast.makeText(context, "Unable to load call logs", 100).show();
//			 }
//		 }
		 
		 Collections.sort(callInfos);
		 
		 return callInfos;
	}
	
	
	private void populateTime(CallInfo callInfo) {
		TimeService tService = TimeService.getInstance();
		
		TimeCode timeCode = tService.getTimeCodeByPhoneNumber(callInfo.getPhoneNumber());
		
		if (timeCode != null) {
			String country = timeCode.getCountry();
			callInfo.setRecipientCountry(country);
			callInfo.setRecipientTimeZone(timeCode.getTimeZone());
			
//			if (country != null && !"".equals(country.trim())) {
//				phNumber.setText(callInfo.getPhoneNumber()+","+country);
//			}
//			
//			globImage.setVisibility(ImageView.VISIBLE);
//			
//			remoteDate.setText(tService.getDate(timeCode, callInfo.getLocalTime()));
//			remoteTime.setText(tService.getTime(timeCode, callInfo.getLocalTime()));
			
			callInfo.setRemoteTime(tService.getTime(timeCode));
			callInfo.setRemoteDate(tService.getDate(timeCode));
		} 
//			else {
//			remoteDate.setText("");
//			remoteTime.setText("");
//			globImage.setVisibility(ImageView.INVISIBLE);
//		}
	}
	
	private String getDuration(long duration) {
		long min = duration/60;
		long hour = min/60;
		
		long total = hour*60 + min*60;
		long sec = duration - total;
		
		return String.format("%d:%d:%d", hour,min,sec);
	}
}
