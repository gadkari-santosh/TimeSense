package com.san;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/query/{id}/{version}")
public class QueryServiceByFilter {

	@GET
	@Produces("text/plain")
	public String getFullName(@PathParam("id") String id,
						      @PathParam("version") String ver) {
		if ("123".equals(id)) {
			return "Santosh Gadkari";
		} else {
			return "Id not found." + id + "." + ver;
		}
	}
}
