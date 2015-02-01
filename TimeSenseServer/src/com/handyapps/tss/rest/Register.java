package com.handyapps.tss.rest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.dataobjects.User;
import com.service.UserService;

@Path("/register/{id}/{hash}")
public class Register {

	@POST
	public long request(@PathParam("id") String id,
						  @PathParam("email") String email,
						  @PathParam("hash") String hash) {
		
		System.out.println("Request: " + id +" , Hash:" + hash + ", Email:" + email);
		
		UserService service = UserService.getInstance();
		
		User user = new User();
		user.setEmail(email);
		user.setUserId(id);
		user.setGcmHash(hash);
		
		long userId = service.createNewUser(user);
		
		System.out.println("Registered. Id" + id +" , Hash:" + hash + " Email:" + email);
		
		return userId;
	}
}