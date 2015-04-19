package com.handyapps.timesense.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.handyapps.timesense.constant.AppConstants;
import com.handyapps.timesense.util.PropUtil;

@Path("/info/gcm")
public class GoogleMessagingCode {

	@GET
	public String getGcmCode() {
		return PropUtil.getProperty(AppConstants.PROP_GCM_CODE);
	}
}