package com.handyapps.timesense.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.handyapps.timesense.R;
import com.handyapps.timesense.constant.Contact;
import com.handyapps.timesense.dataobjects.Settings;
import com.handyapps.timesense.util.Utils;

public class ContactService {

	private static final ContactService INSTANCE = new ContactService();
	
	private List<Contact> contacts = new Vector<Contact>();
	
	private List<String> timeSense = new ArrayList<String>();
	
	private HashMap<String, Integer> alphaIndexer = new HashMap<String, Integer>();
	
	private String[] sections;
	
	public static ContactService getInstance() {
		return INSTANCE;
	}
	
	public synchronized void init (Context context) {
		
		contacts.clear();
		
		Settings settings = SettingsService.getInstance().getSettings();
		timeSense = TimeSenseUsersService.getInstance().getTimeSenseNumbers();
		Map<String, String> timeSenseUserTimeZones = TimeSenseUsersService.getInstance().getTimeSenseUserTimeZones();
		
		Cursor contactCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				 				null, null, null);
		 
		String contactName = null;
		String phNumber    = null;
		String contactType = null;
		
		
		int unknownCount = 1;
        while (contactCursor.moveToNext()) {
	       	try {
		   		 	Contact contact = new Contact();
		   		 
		            contactName = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
		            phNumber = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		            contactType = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
		            
		            if (phNumber == null || "".equals(phNumber))
		             	continue;
		             	
		            if (contactName == null || "".equals(contactName))
		            	contactName="UNKNOWN_"+(unknownCount++);
		            
		            contact.setDisplayName(contactName);
		            contact.setPhoneNumber( Utils.removeSpaces(phNumber) );
		            
		            try {
		            	contact.setContactType(getContactType(Integer.parseInt(contactType)));
		            } catch (Throwable e) {}
		             
		            if (timeSense.contains( contact.getPhoneNumber() )) {
		            	String timeZone = timeSenseUserTimeZones.get(contact.getPhoneNumber());
		            	
		            	if (timeZone != null && !"".equals(timeZone)) {
		            		contact.setParkTimeZone(timeZone);
		            	}
			    		contact.setTimeSense(true);
			    	}
		            
		            TimeService.getInstance().populateTimeInformation(contact);
		             
		            contacts.add(contact);
		     		
	       	 } catch (Throwable t) {
	       		 t.printStackTrace();
	       		Toast.makeText(context, "Error#"+contactName+","+phNumber+"#"+t.toString(), 100).show();
	        }
        }
        
        if (contactCursor != null) {
    		contactCursor.close();
    	}
        
        Collections.sort(contacts);

        int size = contacts.size();

 		for (int x = 0; x < size; x++) {
 			String s = contacts.get(x).getDisplayName();
 			String ch = s.trim().substring(0, 1);
 			ch = ch.toUpperCase();
 			if (!alphaIndexer.containsKey(ch))
 				alphaIndexer.put(ch, x);
 		}

 		Set<String> sectionLetters = alphaIndexer.keySet();
 		
 		ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
 		Collections.sort(sectionList);
 		sections = new String[sectionList.size()];
 		sections = sectionList.toArray(sections);
	}
	
	public Object[] getSections() {
		return sections;
	}
	
	public int getPositionForSection(int section) {
		String pos = sections[section];
		
		if (alphaIndexer.containsKey(pos))
			return alphaIndexer.get(sections[section]);
		else 
			return -1;
	}
	
	public int getFirstPositionOfAlphabet(String letter) {
		if (alphaIndexer.containsKey(letter))
			return alphaIndexer.get(letter);
		else 
			return -1;
	}
	
	public List<Contact> getContacts() {
		return contacts;
	}
	
	public List<Contact> getContacts(Context context) {
		init(context);
		
		return contacts;
	}

	private int getContactType (int type) {
		switch (type) {
		
			case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
				return R.drawable.ic_contact_mobile;
				
			case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
				return R.drawable.ic_contact_home;
				
			case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
				return R.drawable.ic_contact_work;
				
			case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
			case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME:
				return R.drawable.ic_contact_fax;

			default:
				return R.drawable.ic_contact_other;
		}
	}
	
	public void updateTimeZone(String id, String timeZone) {
		for (Contact contact : contacts) {
			if (id.equalsIgnoreCase(contact.getPhoneNumber())) {
				contact.setTimeZone(timeZone);
			}
		}
	}
}
