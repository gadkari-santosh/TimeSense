package com.handyapps.timesense.dataobjects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.handyapps.timesense.service.TimeService;

public class CallInfo implements Comparable<CallInfo> {

	private int id;
	private long duration;
	
	private int callType;
	
	private String phoneNumber;
	private String name;
	
	private String recipientTimeZone="";
	private String recipientCountry ="";
	
	private String homeCountry = "" ;
	private String homeTimeZone = "";
	
	private String localTime;
	private String localDate;
	
	private String remoteTime;
	private String remoteDate;
	
	private Date localDateTime;
	
	
	public Date getLocalDateTime() {
		
		if (localDateTime == null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(TimeService.DATE_TIME_FORMAT);
			
			try {
				localDateTime = dateFormat.parse(localDate+" "+localTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return localDateTime;
	}
	public void setLocalDateTime(Date localDateTime) {
		this.localDateTime = localDateTime;
		
		if (localDateTime != null) {
			localDate = TimeService.getInstance().getDate(localDateTime);
			localTime = TimeService.getInstance().getTime(localDateTime);
		}
	}
	public String getHomeCountry() {
		return homeCountry;
	}
	public void setHomeCountry(String homeCountry) {
		this.homeCountry = homeCountry;
	}
	public String getHomeTimeZone() {
		return homeTimeZone;
	}
	public void setHomeTimeZone(String homeTimeZone) {
		this.homeTimeZone = homeTimeZone;
	}
	public String getRecipientCountry() {
		return recipientCountry;
	}
	public void setRecipientCountry(String country) {
		this.recipientCountry = country;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public int getCallType() {
		return callType;
	}
	public void setCallType(int callType) {
		this.callType = callType;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocalDate() {
		return localDate;
	}
	public void setLocalDate(String localDate) {
		this.localDate = localDate;
	}
	public String getLocalTime() {
		return localTime;
	}
	public void setLocalTime(String localTime) {
		this.localTime = localTime;
	}
	public String getRemoteDate() {
		return remoteDate;
	}
	public void setRemoteDate(String remoteDate) {
		this.remoteDate = remoteDate;
	}
	public String getRemoteTime() {
		return remoteTime;
	}
	public void setRemoteTime(String remoteTime) {
		this.remoteTime = remoteTime;
	}
	public String getRecipientTimeZone() {
		return recipientTimeZone;
	}
	public void setRecipientTimeZone(String timeZone) {
		this.recipientTimeZone = timeZone;
	}
	@Override
	public int compareTo(CallInfo another) {
		return another.getId() - this.getId();
	}
}