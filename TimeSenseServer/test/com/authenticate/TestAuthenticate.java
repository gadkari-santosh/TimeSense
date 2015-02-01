package com.authenticate;

import java.sql.SQLException;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

import com.dao.DAO;
import com.dataobjects.Authentication;
import com.dataobjects.Status;
import com.dataobjects.StatusCode;
import com.dataobjects.User;

public class TestAuthenticate {

	@Test
	public void testInitiate() {
		
		User user = new User();
		user.setUserId("+447404015266");
		
		NexmoAuthenticator authenticator = new NexmoAuthenticator();
		
		Status status = authenticator.initiate(user);
		
		Assert.assertEquals("status code","Success: SMS Sent to +447404015266", status.getStatusDescription());
	}
	
	@Test
	public void testAuth() throws SQLException {
		
		User user = new User();
		Random random = new Random();
		String phone = String.valueOf(random.nextLong());
		user.setUserId( phone );
		
		NexmoAuthenticator authenticator = new NexmoAuthenticator();
		
		Status status = authenticator.initiate(user);
		
		Assert.assertEquals("status code","Success: SMS Sent to "+ phone, status);
		
		DAO dao = new DAO();
		Authentication authentication = dao.getAuthenticationByPhone(user.getUserId());
		
		user = new User();
		user.setUserId(phone);
		user.setEmail("gadkari.santosh@gmail.com");
		user.setGcmHash("ahdhflsdhfjashlkjfahdjkfhai99990");
		user.setPin(authentication.getPin());
		
		Status statusCode = authenticator.authenticate(user);
		
		Assert.assertEquals(StatusCode.SUCCESS, statusCode.getStatusCode());
		Assert.assertTrue(statusCode.getStatusDescription().contains("Code:"));
	}
	
	@Test
	public void testWrongAuth() throws SQLException {
		
		Random random = new Random();
		String phone = String.valueOf(random.nextLong());
		
		User user = new User();
		user.setUserId(phone);
		
		NexmoAuthenticator authenticator = new NexmoAuthenticator();
		
		Status status = authenticator.initiate(user);
		
		Assert.assertEquals("status code","Success: SMS Sent to "+ phone, status);
		
		DAO dao = new DAO();
		Authentication authentication = dao.getAuthenticationByPhone(user.getUserId());
		
		user = new User();
		user.setUserId(phone);
		user.setEmail("gadkari.santosh@gmail.com");
		user.setGcmHash("ahdhflsdhfjashlkjfahdjkfhai99990");
		user.setPin("ABCD");
		
		Status statusCode = authenticator.authenticate(user);
		
		Assert.assertEquals(StatusCode.Error, statusCode.getStatusCode());
		Assert.assertEquals("Error#106: Invalid Pin ABCD", statusCode.getStatusDescription());
	}
}
