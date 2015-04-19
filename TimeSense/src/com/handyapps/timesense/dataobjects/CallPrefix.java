package com.handyapps.timesense.dataobjects;

import com.google.android.gms.internal.an;

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
	
	@Override
	public int hashCode() {
		return this.prefix.hashCode();
	}
	
	@Override
	public boolean equals(Object another) {
		if (another instanceof CallPrefix) {
			
			CallPrefix prefix = (CallPrefix) another;
			
			if (this.getPrefix().equalsIgnoreCase(prefix.getPrefix())) {
				return true;
			}
		}
		
		return false;
	}
}
