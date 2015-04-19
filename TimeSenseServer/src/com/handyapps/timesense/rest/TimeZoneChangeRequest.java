package com.handyapps.timesense.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.handyapps.timesense.dao.DAO;
import com.handyapps.timesense.dataobjects.User;
import com.handyapps.timesense.dataobjects.request.TimeZoneUpdateRequest;
import com.handyapps.timesense.dataobjects.response.Status;
import com.handyapps.timesense.dataobjects.response.StatusCode;
import com.handyapps.timesense.dataobjects.response.TimeZoneUpdateResponse;
import com.handyapps.timesense.service.UserService;

@Path("/brodcast/timezonechange")
public class TimeZoneChangeRequest {

	private static final Logger LOG = Logger.getLogger(TimeZoneChangeRequest.class);
	
	@POST
	public String sendTimeZoneChangeMessage(String input) {
		
		String phoneNumber = null;
		Gson gson = new Gson();
		DAO dao = new DAO();
		
		try {
		
			TimeZoneUpdateRequest timeZoneUpdate = gson.fromJson(input, TimeZoneUpdateRequest.class);
			
			UserService service = UserService.getInstance();
			
			List<User> users = service.findTimeSenseUsers(timeZoneUpdate.getOwnerId(), timeZoneUpdate.getContactNumbers());
			
			LOG.info("Timezone update requester - " + timeZoneUpdate.getOwnerId());
			
			List<String> failed = new ArrayList<>();
			List<String> success = new ArrayList<>();
			
			User userUpdate = new User();
			userUpdate.setUserId(timeZoneUpdate.getOwnerId());
			userUpdate.setTimeZone(timeZoneUpdate.getTimeZone());
			
			String gsonTzUpdate = gson.toJson(userUpdate);
			
			dao.updateTimeZone(timeZoneUpdate.getOwnerId(), timeZoneUpdate.getTimeZone());
			
			for (User user : users) {
				
				phoneNumber = user.getUserId();
				
				DefaultHttpClient httpClient = new DefaultHttpClient();
				
				// Create new getRequest with below mentioned URL
		        HttpPost post = new HttpPost("https://android.googleapis.com/gcm/send");
		        post.setHeader("Authorization", "key=AIzaSyCJoBtvp4xa6dPtub576cF9_IYW8Ukm3ME");
		        post.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		        
		        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		
		        LOG.info(String.format("Notification sending : %s", phoneNumber));
		        
		        formparams.add(new BasicNameValuePair("registration_id", user.getGcmHash()));
		        formparams.add(new BasicNameValuePair("data.message", gsonTzUpdate));
		        formparams.add(new BasicNameValuePair("data", gsonTzUpdate));
		        
		        try {
		        	post.setEntity(new UrlEncodedFormEntity(formparams, "utf-8"));
					
					httpClient.execute(post);
					
					LOG.info(String.format("Notification sent : %s", phoneNumber));
					success.add(phoneNumber);
		        } catch (Exception exp) {
		        	LOG.error(String.format("Unable to send notification: %s", phoneNumber), exp);
		        	failed.add(phoneNumber);
		        } 
			}
			
			TimeZoneUpdateResponse result = new TimeZoneUpdateResponse();
			result.setFailedNumbers(failed);
			result.setSuccessNumbers(success);
			
			String jsonBroadCast = gson.toJson(result);
			return gson.toJson( new Status(StatusCode.SUCCESS, jsonBroadCast)); 
		} catch (Throwable throwable) {
			LOG.error("Exception ", throwable);
			
			return gson.toJson( new Status(StatusCode.Error, "Unable to handle update. Please try again later"));
		}
	}
}
