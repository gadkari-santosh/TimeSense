package com.handyapps.tss.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.authenticate.IAuthenticator;
import com.authenticate.NexmoAuthenticator;
import com.dataobjects.Status;
import com.dataobjects.StatusCode;
import com.dataobjects.User;
import com.google.gson.Gson;

@Path("/verify/{phone}")
public class AuthenticateInitiate {

	@GET
	public String initiate(@PathParam("phone") String id) {
		Gson gson = new Gson();
		
		IAuthenticator authenticator = new NexmoAuthenticator();
		
		User user = new User();
		user.setUserId(id);
		
		Status initiate = authenticator.initiate(user);
		 
		if (initiate != null) {
			return gson.toJson(initiate);
		}
		
		return gson.toJson( new Status(StatusCode.FATAL, "Fatal Error"));
	}
}