package com.san.timesense.dto;

public class CallPrefix {
	
	private String comment;
	private String prefix;
	
	public CallPrefix() {}
	
	public CallPrefix(String comment, String prefix) {
		this.comment = comment;
		this.prefix = prefix;
	}
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}
