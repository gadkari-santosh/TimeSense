package com.handyapps.timesense.authenticate;

import static com.handyapps.timesense.constant.AppConstants.NEXMO_API_KEY;
import static com.handyapps.timesense.constant.AppConstants.NEXMO_API_SECRET;
import static com.handyapps.timesense.constant.AppConstants.SMS_FROM;
import static com.handyapps.timesense.constant.AppConstants.SMS_TEXT;
import static com.handyapps.timesense.constant.AppConstants.SMS_USA_FROM;

import java.sql.SQLException;
import java.util.Random;

import org.apache.log4j.Logger;

import com.handyapps.timesense.dao.DAO;
import com.handyapps.timesense.dataobjects.Authentication;
import com.handyapps.timesense.dataobjects.User;
import com.handyapps.timesense.dataobjects.response.Status;
import com.handyapps.timesense.dataobjects.response.StatusCode;
import com.handyapps.timesense.service.UserService;
import com.handyapps.timesense.util.PropUtil;
import com.nexmo.messaging.sdk.NexmoSmsClient;
import com.nexmo.messaging.sdk.SmsSubmissionResult;
import com.nexmo.messaging.sdk.messages.TextMessage;

public class NexmoAuthenticator  implements IAuthenticator {

	private static final Logger LOG = Logger.getLogger(NexmoAuthenticator.class);
	
	public Status initiate(User user) {
		
		NexmoSmsClient client = null;
		
		StringBuffer randomCode = new StringBuffer(); 
		
		SmsSubmissionResult[] results = null;
		
		DAO dao = new DAO();
		
		String sessionId = null;

		String from = SMS_FROM;
		
        try {
            client = new NexmoSmsClient( PropUtil.getProperty(NEXMO_API_KEY), 
            							 PropUtil.getProperty(NEXMO_API_SECRET) );
        } catch (Exception e) {
       	 	return new Status(StatusCode.Error, "Error#101: Unable to send varification code. Please check your mobile network and internet connection");
        }
        
        if (client != null) {
	         for (int i=0; i<6; i++) {
	        	 randomCode.append(new Random().nextInt(9));
	         }
	         
	         if (user.getUserId().startsWith("+1")) {
	        	 from = SMS_USA_FROM;
	         } 
	         
	         // Create a Text SMS Message request object ...
	         TextMessage message = new TextMessage(PropUtil.getProperty(from), 
	        		 			user.getUserId(), 
	        		 			String.format(PropUtil.getProperty(SMS_TEXT), randomCode.toString()));

	         // Use the Nexmo client to submit the Text Message ...
	         try {
	             results = client.submitMessage(message);
	             
	             if (results != null && results.length > 0) {
		             if (results[0].getStatus() == SmsSubmissionResult.STATUS_OK) {
		            	 
	        	 		 sessionId = dao.createAuthenticationProcess(user.getUserId(), randomCode.toString());
	        	 		 LOG.info("User Id :" + user.getUserId() + " , Created session id :" + sessionId);
		            	 
		            	 return new Status(StatusCode.SUCCESS, String.format("SMS Sent to %s", user.getUserId()), sessionId);
		            	 
		             }
		             else if (results[0].getTemporaryError())
		            	 return new Status(StatusCode.Error, "TEMPORARY FAILURE - PLEASE RETRY");
		             else
		            	 return new Status(StatusCode.Error, String.format("%s, You entered %s",results[0].getErrorText(), user.getUserId()) );
	             }
	        	
	         } catch (Exception e) {
	        	LOG.error("Exception ", e);
	            return new Status(StatusCode.Error,"Error#103: Failed to communicate. Please check your network and internet connection");
	         }
        }
        
        return new Status(StatusCode.Error,"Error#102: Unable to send varification code. Please check your mobile network and internet connection");
	}

	public Status authenticate(User user) {
		DAO dao = new DAO();
		
		Authentication authentication = null;
		
		UserService userService = UserService.getInstance();
		
		try {
			authentication = dao.getAuthenticationByPhone(user);
		} catch (Exception exp) {
			exp.printStackTrace();
			
			return new Status(StatusCode.Error, "Error#104: Fatal error on server. Please start Sign on process again.");
		}
		
		if (authentication == null) {
			return new Status(StatusCode.Error, "Error#105: Unable to fetch authentication record. Please start Sign on process again.");
		}
		
		if (user.getPin() != null 
			&& authentication.getPin() != null
			&& authentication.getPin().equals(user.getPin())) {
			
			try {
				userService.createNewUser(user);
			} catch (SQLException e) {
				return new Status(StatusCode.Error, "Error#107: Unable to fetch authentication record. Please start Sign on process again.");
			}
			return new Status(StatusCode.SUCCESS, "Code:"+user.getSessionId()); 
		} else {
			return new Status(StatusCode.Error, String.format("Error#106: Invalid Pin %s", user.getPin())); 
		}
	}
}