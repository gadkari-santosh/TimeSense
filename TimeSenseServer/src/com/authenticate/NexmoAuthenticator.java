package com.authenticate;

import static com.common.Constants.NEXMO_API_KEY;
import static com.common.Constants.NEXMO_API_SECRET;
import static com.common.Constants.SMS_FROM;
import static com.common.Constants.SMS_TEXT;

import java.sql.SQLException;
import java.util.Random;

import com.dao.DAO;
import com.dataobjects.Authentication;
import com.dataobjects.Status;
import com.dataobjects.StatusCode;
import com.dataobjects.User;
import com.nexmo.messaging.sdk.NexmoSmsClient;
import com.nexmo.messaging.sdk.SmsSubmissionResult;
import com.nexmo.messaging.sdk.messages.TextMessage;

public class NexmoAuthenticator  implements IAuthenticator {

	public Status initiate(User user) {
		
		NexmoSmsClient client = null;
		
		StringBuffer randomCode = new StringBuffer(); 
		
		SmsSubmissionResult[] results = null;
		
		DAO dao = new DAO();
		
        try {
            client = new NexmoSmsClient(NEXMO_API_KEY, NEXMO_API_SECRET);
        } catch (Exception e) {
       	 	return new Status(StatusCode.Error, "Error#101: Unable to send varification code. Please check your mobile network and internet connection");
        }
        
        if (client != null) {
	         
	         for (int i=0; i<6; i++) {
	        	 randomCode.append(new Random().nextInt(9));
	         }
	         
	         // Create a Text SMS Message request object ...
	         TextMessage message = new TextMessage(SMS_FROM, user.getUserId(), String.format(SMS_TEXT, randomCode.toString()));

	         // Use the Nexmo client to submit the Text Message ...
	         try {
	             results = client.submitMessage(message);
	             
	             dao.createAuthenticationProcess(user.getUserId(), randomCode.toString());
	             
	             return new Status(StatusCode.SUCCESS, String.format("SMS Sent to %s", user.getUserId()));
	        	 
//	             if (results != null && results.length > 0) {
//		             if (results[0].getStatus() == SmsSubmissionResult.STATUS_OK) {
//		            	 
//		            	 dao.createAuthenticationProcess(user.getUserId(), randomCode.toString());
//		            	 
//		            	 return new Status(StatusCode.SUCCESS, String.format("SMS Sent to %s", user.getUserId()));
//		             }
//		             else if (results[0].getTemporaryError())
//		            	 return new Status(StatusCode.Error, "TEMPORARY FAILURE - PLEASE RETRY");
//		             else
//		            	 return new Status(StatusCode.Error, String.format("%s, You entered %s",results[0].getErrorText(), user.getUserId()) );
//	             }
	        	
	         } catch (Exception e) {
	        	 e.printStackTrace();
	             return new Status(StatusCode.Error,"Error#103: Failed to communicate. Please check your network and internet connection");
	         }
        }
        
        return new Status(StatusCode.Error,"Error#102: Unable to send varification code. Please check your mobile network and internet connection");
	}

	public Status authenticate(User user) {
		DAO dao = new DAO();
		
		Authentication authentication = null;
		
		long userId =0;
		try {
			authentication = dao.getAuthenticationByPhone(user.getUserId());
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
				userId = dao.createNewUser(user);
			} catch (SQLException e) {
				return new Status(StatusCode.Error, "Error#107: Unable to fetch authentication record. Please start Sign on process again.");
			}
			return new Status(StatusCode.SUCCESS, "Code:"+userId); 
		} else {
			return new Status(StatusCode.Error, String.format("Error#106: Invalid Pin %s", user.getPin())); 
		}
	}

	public String postAuthenticate(User user) {
		return null;
	}
}
