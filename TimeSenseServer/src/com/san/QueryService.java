package com.san;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;

@Path("/query/{id}")
public class QueryService {
 
   @GET
   @Produces("text/plain")
   public String getTrade(@PathParam("id") String id) {
	   if ("123".equals(id)) {
		   return "Santosh Gadkari";
	   } else {
		   return "Id not found." + id;
	   }
   }
}