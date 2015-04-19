package com.handyapps.timesense.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.handyapps.timesense.R;
import com.handyapps.timesense.constant.Contact;
import com.handyapps.timesense.dataobjects.Settings;
import com.handyapps.timesense.dataobjects.User;
import com.handyapps.timesense.dataobjects.request.FindTimeSenseUserRequest;
import com.handyapps.timesense.service.ContactService;
import com.handyapps.timesense.service.SettingsService;
import com.handyapps.timesense.service.TimeSenseRestClient;
import com.handyapps.timesense.service.TimeSenseUsersService;
import com.handyapps.timesense.service.TimeService;
import com.handyapps.timesense.util.ResourceUtils;

public class AsyncInitialLoading extends AsyncTask<String, String, Void> {

	private Context ctx;
	private ProgressBar progressBar;
	private PostCallBack callBack;
	
	public AsyncInitialLoading(Context ctx, ProgressBar progressBar, PostCallBack callBack) {
		this.ctx = ctx;
		this.progressBar = progressBar;
		this.callBack = callBack;
	}
	
	public AsyncInitialLoading(Context ctx) {
		this.ctx = ctx;
	}
	
	@Override
    protected void onPreExecute() {
      super.onPreExecute();

      if (progressBar == null) {
    	progressBar = new ProgressBar(ctx);
      }
    }

	@Override
	protected Void doInBackground(String... params) {
		
		FindTimeSenseUserRequest findTsUsersReq = new FindTimeSenseUserRequest();
	    Set<User> timeSenseNumbers = null;
	    
		try {
			SettingsService.getInstance().init(ctx);
			TimeService.getInstance().init(ctx);
			ContactService.getInstance().init(ctx);
			
			TimeSenseUsersService.getInstance().init(ctx);
			
			String webservice = ResourceUtils.getString(ctx, R.string.time_sense_rest_ws);
		    final TimeSenseRestClient client = new TimeSenseRestClient(webservice);
		    
		    SettingsService service = SettingsService.getInstance();
			Settings settings = service.getSettings();
			
		    List<Contact> contacts = ContactService.getInstance().getContacts();
		    List<String> numbers = new ArrayList<String>();
		    for (Contact contact : contacts) {
		    	if (contact.getPhoneNumber().startsWith("+"))
		    		numbers.add( contact.getPhoneNumber() );
		    }
		    
		    if (numbers.size() > 0) {
		    	findTsUsersReq.setOwnerId( settings.getUserId() );
		    	findTsUsersReq.setContactNumbers( numbers );
		    	timeSenseNumbers = client.findTimeSenseUsers(findTsUsersReq);
		    }
		    
		    if (timeSenseNumbers != null && timeSenseNumbers.size() != 0) {
		    	TimeSenseUsersService.getInstance().clearAndAddNewUsers(timeSenseNumbers);
		    }
		} catch (Throwable t) {
			t.printStackTrace();
			progressBar.setProgress(100);
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		if (progressBar != null)
			progressBar.setProgress(100);
		
		this.callBack.execute(ctx);
	}
}
