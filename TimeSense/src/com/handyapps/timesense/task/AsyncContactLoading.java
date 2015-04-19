package com.handyapps.timesense.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.os.AsyncTask;

import com.handyapps.timesense.R;
import com.handyapps.timesense.constant.Contact;
import com.handyapps.timesense.dataobjects.Settings;
import com.handyapps.timesense.dataobjects.User;
import com.handyapps.timesense.dataobjects.request.FindTimeSenseUserRequest;
import com.handyapps.timesense.service.ContactService;
import com.handyapps.timesense.service.SettingsService;
import com.handyapps.timesense.service.TimeSenseRestClient;
import com.handyapps.timesense.service.TimeSenseUsersService;
import com.handyapps.timesense.util.ResourceUtils;

public class AsyncContactLoading extends AsyncTask<String, String, Void> {

	private Context ctx;
	
	public AsyncContactLoading(Context ctx) {
		this.ctx = ctx;
	}
	
	@Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

	@Override
	protected Void doInBackground(String... params) {
		
		FindTimeSenseUserRequest findTsUsersReq = new FindTimeSenseUserRequest();
	    Set<User> timeSenseNumbers = null;
	    
		try {
			String webservice = ResourceUtils.getString(ctx, R.string.time_sense_rest_ws);
		    final TimeSenseRestClient client = new TimeSenseRestClient(webservice);
		    
		    SettingsService service = SettingsService.getInstance();
			Settings settings = service.getSettings();
			
			if (settings.getUserId() == null || "".equals(settings.getUserId()))
				return null;
			
		    List<Contact> contacts = ContactService.getInstance().getContacts();
		    List<String> numbers = new ArrayList<String>();
		    for (Contact contact : contacts) {
		    	if (contact.getPhoneNumber().contains("+"))
		    		numbers.add( contact.getPhoneNumber() );
		    }
		    
		    if (numbers.size() > 0) {
		    	findTsUsersReq.setOwnerId( settings.getUserId() );
		    	findTsUsersReq.setContactNumbers( numbers );
		    	timeSenseNumbers = client.findTimeSenseUsers(findTsUsersReq);
		    }
		    
		    if (timeSenseNumbers != null) {
		    	TimeSenseUsersService.getInstance().clearAndAddNewUsers(timeSenseNumbers);
		    }
			
			TimeSenseUsersService.getInstance().init(ctx);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
	}
}
