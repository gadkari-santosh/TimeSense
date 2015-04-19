package com.handyapps.timesense.rest;


import javax.ws.rs.POST;
import javax.ws.rs.Path;

import com.google.gson.Gson;
import com.handyapps.timesense.authenticate.IAuthenticator;
import com.handyapps.timesense.authenticate.NexmoAuthenticator;
import com.handyapps.timesense.dataobjects.User;
import com.handyapps.timesense.dataobjects.response.Status;
import com.handyapps.timesense.dataobjects.response.StatusCode;

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
