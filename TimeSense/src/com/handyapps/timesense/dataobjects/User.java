package com.handyapps.timesense.dataobjects;

public class User {

	private String userId;
	private String email;
	private String gcmHash;
	private String pin;
	
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getGcmHash() {
		return gcmHash;
	}
	public void setGcmHash(String gcmHash) {
		this.gcmHash = gcmHash;
	}
}