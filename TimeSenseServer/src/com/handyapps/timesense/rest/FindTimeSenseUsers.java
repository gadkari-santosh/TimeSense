package com.handyapps.timesense.rest;

import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.handyapps.timesense.dataobjects.User;
import com.handyapps.timesense.dataobjects.request.FindTimeSenseUserRequest;
import com.handyapps.timesense.service.UserService;

@Path("/find")
public class FindTimeSenseUsers {

	private static final Logger LOG = Logger.getLogger(FindTimeSenseUsers.class);
	
	@POST
	public String getTimeSenseInfo(String relationReq) {
		Gson gson = new Gson();
		FindTimeSenseUserRequest findTsUsrRequest = gson.fromJson(relationReq, 
																  FindTimeSenseUserRequest.class);
		
		LOG.info("Owner Id :" + findTsUsrRequest.getOwnerId());
		
		UserService service = UserService.getInstance();
		
		List<User> timeSenseUsers = service.findTimeSenseUsers(findTsUsrRequest.getOwnerId(), 
															   findTsUsrRequest.getContactNumbers());
		
		return gson.toJson(timeSenseUsers);
	}
}
