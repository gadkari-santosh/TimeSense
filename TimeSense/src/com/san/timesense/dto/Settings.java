package com.san.timesense.dto;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.san.timesense.R;
import com.san.timesense.util.ResourceUtils;

public class Settings {

	private String version;
	private String status;
	private String fromTime;
	private String toTime;
	
	private List<CallPrefix> callPrefixs = new ArrayList<CallPrefix>();
	private boolean interceptCall;
	
	
	private List<TimeCode> timePlanerTimeCodes = new ArrayList<TimeCode>();
	private List<TimeCode> worldClockTimeCodes = new ArrayList<TimeCode>();
	
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
}