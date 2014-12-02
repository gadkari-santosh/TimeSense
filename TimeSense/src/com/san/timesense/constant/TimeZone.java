package com.san.timesense.constant;

import java.util.Date;

public class TimeZone {

	private String timeZone = null;
	private Date time = null;

	public TimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
}
