package com.handyapps.timesense.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.handyapps.timesense.authenticate.IAuthenticator;
import com.handyapps.timesense.authenticate.NexmoAuthenticator;
import com.handyapps.timesense.dataobjects.User;
import com.handyapps.timesense.dataobjects.response.Status;
import com.handyapps.timesense.dataobjects.response.StatusCode;


@Path("/verify/{phone}")
public class VerifyPhoneNumber {
	
	private static final Logger LOG = Logger.getLogger(VerifyPhoneNumber.class);
	
	@GET
	public String initiate(@PathParam("phone") String id) {
		Gson gson = new Gson();
		
		LOG.info("Received Phone verify request : " + id);
		
		IAuthenticator authenticator = new NexmoAuthenticator();
		
		User user = new User();
		user.setUserId(id);
		
		Status initiate = authenticator.initiate(user);
		 
		if (initiate != null) {
			
			String json = gson.toJson(initiate);
			
			LOG.info("Output from initiate phone verify " + json);
			
			return json;
		}
		
		return gson.toJson( new Status(StatusCode.FATAL, "Fatal Error"));
	}
}
