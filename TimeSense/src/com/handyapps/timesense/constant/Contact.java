package com.handyapps.timesense.constant;

public class Contact implements Comparable<Contact>{

	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String displayName;
	private String country;
	private String time;
	private String date;
	private String timeZone;
	private String prefix;
	private int contactType;
	private String status;
	
	private Kaal kaal;
	
	private boolean timeSense;
	
	private String parkTimeZone;
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isAwayFromHome() {
		if (parkTimeZone != null && !parkTimeZone.equalsIgnoreCase(timeZone)) {
			return true;
		}
		
		if (parkTimeZone != null && parkTimeZone.equalsIgnoreCase(timeZone)) {
			parkTimeZone = null;
		}
		
		return false;
	}
	
	public String getParkTimeZone() {
		return parkTimeZone;
	}

	public void setParkTimeZone(String parkTimeZone) {
		this.parkTimeZone = parkTimeZone;
	}
	
	public boolean isTimeSense() {
		return timeSense;
	}
	public void setTimeSense(boolean timeSense) {
		this.timeSense = timeSense;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	public Kaal getKaal() {
		return kaal;
	}
	public void setKaal(Kaal kaal) {
		this.kaal = kaal;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getCountry() {
		return country == null ? "" : country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getContactType() {
		return contactType;
	}
	public void setContactType(int contactType) {
		this.contactType = contactType;
	}

	@Override
	public int compareTo(Contact another) {
		if (this.displayName != null && another.displayName != null)
			return this.displayName.compareTo(another.displayName);
		else 
			return -1;
	}
	
	@Override
	public boolean equals (Object contact) {
		if (contact instanceof Contact)
			return this.getPhoneNumber().equalsIgnoreCase( ((Contact)contact).getPhoneNumber());
		else
			return false;
	}
}