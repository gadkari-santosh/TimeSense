package com.handyapps.timesense.task;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.EditText;

import com.handyapps.timesense.R;
import com.handyapps.timesense.constant.Contact;
import com.handyapps.timesense.dataobjects.RelationRequest;
import com.handyapps.timesense.dataobjects.Settings;
import com.handyapps.timesense.service.ContactService;
import com.handyapps.timesense.service.SettingsService;
import com.handyapps.timesense.service.TimeSenseRestClient;
import com.handyapps.timesense.util.ResourceUtils;

public class AsyncContact extends AsyncTask<String, String, Void> {

	private String url;
	private ProgressDialog dialog = null;
	private Context ctx;
	Button progress; EditText text;
	EditText[] codes;
	
	public AsyncContact(Context ctx) {
		this.ctx = ctx;
	}
	
	@Override
    protected void onPreExecute() {
      super.onPreExecute();
//      dialog = new ProgressDialog(ctx);
//		dialog.setMessage("Working ...");
//		dialog.setIndeterminate(false);
//		dialog.setCancelable(false);
//		dialog.show();
    }

	@Override
	protected Void doInBackground(String... params) {
		
		try {
			String webservice = ResourceUtils.getString(ctx, R.string.time_sense_rest_ws);
		    final TimeSenseRestClient client = new TimeSenseRestClient(webservice);
		    
		    SettingsService service = SettingsService.getInstance();
			Settings settings = service.getSettings();
			
		    List<Contact> contacts = ContactService.getInstance().getContacts();
		    List<String> numbers = new ArrayList<String>();
		    for (Contact contact : contacts) {
		    	numbers.add( contact.getPhoneNumber() );
		    }
		    
		    RelationRequest relationRequest = new RelationRequest();
		    relationRequest.setOwnerId( settings.getUserId() );
		    relationRequest.setRelatedNumbers( numbers );
		    
		    List<String> timeSenseNumbers = client.findTimeSenseUsers(relationRequest);
		    
		    ContactService.getInstance().setTimeSenseNumber(timeSenseNumbers);
		    
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}
}
