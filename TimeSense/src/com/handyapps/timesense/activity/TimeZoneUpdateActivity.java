package com.handyapps.timesense.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.handyapps.timesense.R;
import com.handyapps.timesense.dataobjects.Settings;
import com.handyapps.timesense.dataobjects.Status;
import com.handyapps.timesense.dataobjects.StatusCode;
import com.handyapps.timesense.service.SettingsService;
import com.handyapps.timesense.service.TimeSenseRestClient;
import com.handyapps.timesense.util.Dialog;
import com.handyapps.timesense.util.ResourceUtils;

public class TimeZoneUpdateActivity extends Activity {


	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		setContentView(R.layout.layout_timezone_update);
		
		Button notifyButton = (Button) findViewById(R.id.butNotify);
		Button butCancel = (Button) findViewById(R.id.butCancel);
		
		final String timeZone = (String) this.getIntent().getStringExtra("time_zone");
		
		final SettingsService service = SettingsService.getInstance();
		final Settings settings = service.getSettings();
		
		butCancel.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TimeZoneUpdateActivity.this.finish();
			}
		});
		
		notifyButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String webservice = ResourceUtils.getString(TimeZoneUpdateActivity.this, R.string.time_sense_rest_ws);
			    TimeSenseRestClient client = new TimeSenseRestClient(webservice);
			    
			    Status status = client.sendTimeZoneUpdateMessage(settings.getUserId(), timeZone);
			    
			    if (status != null) {
			    	if (status.getStatusCode() == StatusCode.SUCCESS) {
			    		Dialog.show(TimeZoneUpdateActivity.this, "Time Zone Change Notified", "Success");
			    		TimeZoneUpdateActivity.this.finish();
			    	} else {
			    		Dialog.show(TimeZoneUpdateActivity.this, status.getStatusDescription(), "Error");
			    	}
			    }
			}
		});
	}
}
