package com.handyapps.timesense.service;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handyapps.timesense.R;
import com.handyapps.timesense.dataobjects.User;
import com.handyapps.timesense.dataobjects.request.FindTimeSenseUserRequest;
import com.handyapps.timesense.dataobjects.request.TimeZoneUpdateRequest;
import com.handyapps.timesense.dataobjects.response.Status;
import com.handyapps.timesense.dataobjects.response.StatusCode;
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
		
		String response = null;
		try {
			response = RestUtil.executeGet(codeURL);
			
			return gson.fromJson(response, Status.class);
		} catch (Exception e) {
			e.printStackTrace();
			return new Status (StatusCode.Error, response);
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
	
	public Set<User> findTimeSenseUsers(FindTimeSenseUserRequest findTimeSenseUserRequest) {
		
		String codeURL = String.format("%s/find", url);
		
		Gson gson = new Gson();
		
		try {
			Type typeOfObjectsList = new TypeToken<HashSet<User>>() {}.getType();
			String response = RestUtil.executePost(codeURL, gson.toJson(findTimeSenseUserRequest));
			return gson.fromJson(response, typeOfObjectsList);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Status sendTimeZoneUpdateMessage(String phone, String timeZone) {
		
		SettingsService settingsService = SettingsService.getInstance();
		
		TimeZoneUpdateRequest update = new TimeZoneUpdateRequest();
		update.setOwnerId(settingsService.getSettings().getUserId());
		
		TimeSenseUsersService service = TimeSenseUsersService.getInstance();
		update.setTimeZone(timeZone);
		update.setContactNumbers( service.getTimeSenseNumbers() );
		
		Gson gson = new Gson();
		
		String codeURL = String.format("%s/brodcast/timezonechange", url );
		
		try {
			String response =  RestUtil.executePost(codeURL, gson.toJson(update));
			
			return gson.fromJson(response, Status.class);
		} catch (Exception e) {
			e.printStackTrace();
			return new Status(StatusCode.FATAL, "Unable to notify time zone change. Please check your internet connection");
		}
	}
}