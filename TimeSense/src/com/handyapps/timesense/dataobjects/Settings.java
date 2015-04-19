package com.handyapps.timesense.dataobjects;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.handyapps.timesense.R;
import com.handyapps.timesense.util.ResourceUtils;

public class Settings {

	private String version;
	private String status;
	private String fromTime;
	private String toTime;
	
	private List<CallPrefix> callPrefixs = new ArrayList<CallPrefix>();
	private boolean interceptCall;
	
	
	private List<TimeCode> timePlanerTimeCodes = new ArrayList<TimeCode>();
	private List<TimeCode> worldClockTimeCodes = new ArrayList<TimeCode>();
	
	private int timePlannerRangeFrom = 8;
	
	private int timePlannerRangeTo = 20;
	
	private int callSenseFrom = 9;
	
	private int callSenseTo = 20;
	
	private boolean enableCallSense = true;
	
	private String gcm;
	
	private String userId;
	
	private String email;
	
	private boolean signOnSuccess;
	 
	private boolean callLogLoaded = false;
	
	private String homeCountry = "";
	
	private Settings() {}
	
	private static final Settings INSTANCE = new Settings();
	
	public static Settings getInstance() {
		return INSTANCE;
	}
	
	public void init(Context conext) {
		fromTime = ResourceUtils.getString(conext, R.string.default_pref_from_time);
		toTime = ResourceUtils.getString(conext, R.string.default_pref_to_time);
		status = ResourceUtils.getString(conext, R.string.default_pref_status);
		interceptCall = ResourceUtils.getBool(conext, R.string.default_pref_interrupt_call);
	}
	
	public String getHomeCountry() {
		return homeCountry;
	}

	public void setHomeCountry(String homeCountry) {
		this.homeCountry = homeCountry;
	}

	public boolean isCallLogLoaded() {
		return callLogLoaded;
	}

	public void setCallLogLoaded(boolean callLogLoaded) {
		this.callLogLoaded = callLogLoaded;
	}

	public boolean isSignOnSuccess() {
		return signOnSuccess;
	}

	public void setSignOnSuccess(boolean signOnSuccess) {
		this.signOnSuccess = signOnSuccess;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGcm() {
		return gcm;
	}

	public void setGcm(String gcm) {
		this.gcm = gcm;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getCallSenseFrom() {
		return callSenseFrom;
	}

	public void setCallSenseFrom(int callSenseFrom) {
		this.callSenseFrom = callSenseFrom;
	}

	public int getCallSenseTo() {
		return callSenseTo;
	}

	public void setCallSenseTo(int callSenseTo) {
		this.callSenseTo = callSenseTo;
	}

	public boolean isEnableCallSense() {
		return enableCallSense;
	}

	public void setEnableCallSense(boolean enableCallSense) {
		this.enableCallSense = enableCallSense;
	}

	public int getTimePlannerRangeFrom() {
		return timePlannerRangeFrom;
	}

	public void setTimePlannerRangeFrom(int timePlannerRangeFrom) {
		this.timePlannerRangeFrom = timePlannerRangeFrom;
	}

	public int getTimePlannerRangeTo() {
		return timePlannerRangeTo;
	}

	public void setTimePlannerRangeTo(int timePlannerRangeTo) {
		this.timePlannerRangeTo = timePlannerRangeTo;
	}

	public List<TimeCode> getTimePlanerTimeCodes() {
		return timePlanerTimeCodes;
	}

	public void setTimePlanerTimeCodes(List<TimeCode> timePlanerTimeCodes) {
		this.timePlanerTimeCodes = timePlanerTimeCodes;
	}

	public List<TimeCode> getWorldClockTimeCodes() {
		return worldClockTimeCodes;
	}

	public void setWorldClockTimeCodes(List<TimeCode> worldClockTimeCodes) {
		this.worldClockTimeCodes = worldClockTimeCodes;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<CallPrefix> getCallPrefixs() {
		setDefaultCallPrefix();
		
		return callPrefixs;
	}

	public void setCallPrefixs(List<CallPrefix> callPrefixs) {
		this.callPrefixs = callPrefixs;
	}

	public boolean isInterceptCall() {
		return interceptCall;
	}

	public void setInterceptCall(boolean interceptCall) {
		this.interceptCall = interceptCall;
	}

	public String getFromTime() {
		return fromTime;
	}

	public void setFromTime(String fromTime) {
		this.fromTime = fromTime;
	}

	public String getToTime() {
		return toTime;
	}

	public void setToTime(String toTime) {
		this.toTime = toTime;
	}
	
	public boolean validate(StringBuffer errors) {
		
		if (Integer.parseInt(fromTime.replace(":", "")) > Integer.parseInt(toTime.replace(":", ""))) {
			errors.append("From time is greater than to time");
			
			return true;
		}
		
		return true;
	}
	
	private void setDefaultCallPrefix() {
		CallPrefix doubleZeroCallPrefix = new CallPrefix();
		doubleZeroCallPrefix.setComment("Universal Standard");
		doubleZeroCallPrefix.setPrefix("00");
		
		if (this.callPrefixs != null && !this.callPrefixs.contains(doubleZeroCallPrefix)) {
			
			this.callPrefixs.add(doubleZeroCallPrefix);
		}
	}
	
	public boolean isCallAllowed(int hour) {
		boolean ok = false;
		
		if (hour == 0) hour = 24;
		
		if ( hour >= callSenseFrom && hour <= callSenseTo) {
			ok = true;
		}
		
		return ok;
	}
}