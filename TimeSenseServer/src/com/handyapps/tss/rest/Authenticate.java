package com.handyapps.tss.rest;


import javax.ws.rs.POST;
import javax.ws.rs.Path;

import com.authenticate.IAuthenticator;
import com.authenticate.NexmoAuthenticator;
import com.dataobjects.Status;
import com.dataobjects.StatusCode;
import com.dataobjects.User;
import com.google.gson.Gson;

@Path("/auth")
public class Authenticate {

	@POST
	public String authenticate(String authRequest) {
		
		Gson gson = new Gson();
		
		Status status = null;
		
		User user = gson.fromJson(authRequest.toString(), User.class);
		
		IAuthenticator authenticator = new NexmoAuthenticator();
		status = authenticator.authenticate(user);
		
		if (status != null) {
			return gson.toJson(status);
		}
		
		return gson.toJson( new Status(StatusCode.FATAL, "Fatal Error"));
	}
}
