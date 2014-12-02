package com.san.timesense.constant;

public class Contact implements Comparable<Contact>{

	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String displayName;
	private String country;
	private String time;
	private String date;
	private String timeZone;
	private Kaal kaal;
	private String prefix;
	
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