package com.handyapps.tss.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.dataobjects.Status;
import com.dataobjects.StatusCode;
import com.dataobjects.TimeZoneUpdate;
import com.dataobjects.User;
import com.google.gson.Gson;
import com.service.UserService;

@Path("/brodcast/timezonechange")
public class BrodcastMessage {

	@POST
	public String sendTimeZoneChangeMessage(String input) {
		
		System.out.println("Received Broadcast : " + input);
		Gson gson = new Gson();
		
		TimeZoneUpdate timeZoneUpdate = gson.fromJson(input, TimeZoneUpdate.class);
		
		UserService service = UserService.getInstance();
		List<User> users = service.findGCMCodes(timeZoneUpdate.getUserId());
		
		String gsonTzUpdate = gson.toJson(timeZoneUpdate);
		
		for (User user : users) {
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
			
			// Create new getRequest with below mentioned URL
	        HttpPost post = new HttpPost("https://android.googleapis.com/gcm/send");
	        post.setHeader("Authorization", "key=AIzaSyCJoBtvp4xa6dPtub576cF9_IYW8Ukm3ME");
	        post.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
	        
	        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
	
	        System.out.println(String.format("%s - %s", user.getGcmHash(), user.getUserId()));
	        
	        formparams.add(new BasicNameValuePair("registration_id", user.getGcmHash()));
	        formparams.add(new BasicNameValuePair("data.message", gsonTzUpdate));
	        formparams.add(new BasicNameValuePair("data", gsonTzUpdate));
	        
	        try {
				//post.setEntity(new StringEntity(String.format(payload, Cache.getInstance().getFirst())));
	        	post.setEntity(new UrlEncodedFormEntity(formparams, "utf-8"));
				
				httpClient.execute(post);
	        } catch (Exception exp) {
	        	exp.printStackTrace();
	        } 
		}
		
		return gson.toJson( new Status(StatusCode.SUCCESS, "")); 
	}
}
