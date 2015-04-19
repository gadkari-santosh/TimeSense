package com.handyapps.timesense.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.handyapps.timesense.dao.ConnectionManager;

@Path("/status")
public class Service {

	@GET
	public String get() {
		if (ConnectionManager.getInstance().checkStatus()) {
			return "OK";
		}
		return "NOT OK. Contact Mr.Santosh";
	}
}