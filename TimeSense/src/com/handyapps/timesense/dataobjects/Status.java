package com.handyapps.timesense.dataobjects;


public class Status {

	private StatusCode statusCode = null;
	private String description = null;

	public Status() {}
	
	public Status (StatusCode statusCode, String error) {
		this.statusCode = statusCode;
		this.description = error;
	}
	
	public StatusCode getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(StatusCode statusCode) {
		this.statusCode = statusCode;
	}
	public String getStatusDescription() {
		return description;
	}
	public void setStatusDescription(String error) {
		this.description = error;
	}
}