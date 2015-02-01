package com.handyapps.tss.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/service/status")
public class Service {

	@GET
	public String getServiceStatus() {
		return "active";
	}
}
