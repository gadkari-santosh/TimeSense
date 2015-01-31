package com.handyapps.timesense.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.google.gson.Gson;
import com.handyapps.timesense.R;
import com.handyapps.timesense.dataobjects.RelationRequest;
import com.handyapps.timesense.dataobjects.Status;
import com.handyapps.timesense.dataobjects.StatusCode;
import com.handyapps.timesense.dataobjects.TimeZoneUpdate;
import com.handyapps.timesense.dataobjects.User;
import com.handyapps.timesense.util.ResourceUtils;
import com.handyapps.timesense.util.RestUtil;

public class TimeSenseRestClient {
	
	private String url;

	public TimeSenseRestClient (String url) {
		this.url = url;
	}
	
	public Status verifyPhoneNumber(String phone) {
		
		if ("".equals(phone)) {
			return new Status(StatusCode.Error, "Enter valid phone number");
		}
		
		String codeURL = String.format("%s/verify/%s", url,phone);
		
		Gson gson = new Gson();
		
		try {
			String response = RestUtil.executeGet(codeURL);
			
			return gson.fromJson(response, Status.class);
		} catch (Exception e) {
			return new Status (StatusCode.Error, e.toString());
		}

	}
	
	public String getGcmCode(Context context) {
		
		String codeURL = String.format("%s/info/gcm", url);
		
		try {
			return RestUtil.executeGet(codeURL);
		} catch (Exception e) {
			return ResourceUtils.getString(context, R.string.gcm_code);
		}
	}
	
	public Status authenticate(User user) {
		String codeURL = String.format("%s/auth", url);
		
		Gson gson = new Gson();
		
		try {
			String response = RestUtil.executePost(codeURL, gson.toJson(user));
			
			return gson.fromJson(response, Status.class);
		} catch (Exception e) {
			return new Status (StatusCode.Error, e.toString());
		}
	}
	
	public List<String> findTimeSenseUsers(RelationRequest relationRequest) {
		
		String codeURL = String.format("%s/find", url);
		
		Gson gson = new Gson();
		
		try {
			String response = RestUtil.executePost(codeURL, gson.toJson(relationRequest));
			
			return gson.fromJson(response, ArrayList.class);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public Status sendTimeZoneUpdateMessage(String phone, String timeZone) {
		
		TimeZoneUpdate update = new TimeZoneUpdate();
		update.setUserId("+919158663983");
		update.setTimeZone(timeZone);
		
		Gson gson = new Gson();
		
		String codeURL = String.format("%s/brodcast/timezonechange", url );
		
		try {
			String response =  RestUtil.executePost(codeURL, gson.toJson(update));
			
			return gson.fromJson(response, Status.class);
		} catch (Exception e) {
			return new Status (StatusCode.Error, e.toString());
		}
	}
}