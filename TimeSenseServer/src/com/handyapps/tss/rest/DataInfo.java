package com.handyapps.tss.rest;

import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import com.dataobjects.RelationRequest;
import com.google.gson.Gson;
import com.service.UserService;

@Path("/find")
public class DataInfo {

	@POST
	public String getTimeSenseInfo(String relationReq) {
		Gson gson = new Gson();
		RelationRequest relationRequest = gson.fromJson(relationReq, RelationRequest.class);
		
		System.out.println("Owner Id :" + relationRequest.getOwnerId());
		System.out.println("Numbers : " + relationRequest.getRelatedNumbers());
		
		UserService service = UserService.getInstance();
		List<String> timeSenseUsers = service.findTimeSenseUsers(relationRequest.getOwnerId(), 
																 relationRequest.getRelatedNumbers());
		
		return gson.toJson(timeSenseUsers);
	}
}
