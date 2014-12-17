package com.san.timesense.dto;

import java.util.List;

public class TimeCode implements Comparable<TimeCode>{
	
	private String dialCode;
	private String timeZone;
	private String country;
	private String shortCountryName;
	private List<String> areaCodes;
	
	private boolean select;
	
	public boolean isSelect() {
		return select;
	}
	public void setSelect(boolean select) {
		this.select = select;
	}
	public String getShortCountryName() {
		return shortCountryName;
	}
	public void setShortCountryName(String shortCountryName) {
		this.shortCountryName = shortCountryName;
	}
	public List<String> getAreaCodes() {
		return areaCodes;
	}
	public void setAreaCodes(List<String> areaCodes) {
		this.areaCodes = areaCodes;
	}
	public String getDialCode() {
		return dialCode;
	}
	public void setDialCode(String dialCode) {
		this.dialCode = dialCode;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TimeCode) {
			
			if (this.getCountry().equalsIgnoreCase( ((TimeCode)obj).getCountry() ) 
				&& this.getDialCode().equalsIgnoreCase( ((TimeCode)obj).getDialCode())
				&& this.getTimeZone().equalsIgnoreCase( ((TimeCode)obj).getTimeZone())
				){
				return true;
			}
		}
			
		return false;
	}
	@Override
	public int compareTo(TimeCode another) {
		return this.getCountry().compareTo(another.getCountry());
	}
}
