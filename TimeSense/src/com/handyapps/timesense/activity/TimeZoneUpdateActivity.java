package com.handyapps.timesense.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.handyapps.timesense.R;
import com.handyapps.timesense.dataobjects.BrodcastResult;
import com.handyapps.timesense.dataobjects.Settings;
import com.handyapps.timesense.dataobjects.TimeCode;
import com.handyapps.timesense.dataobjects.response.StatusCode;
import com.handyapps.timesense.service.SettingsService;
import com.handyapps.timesense.service.TimeSenseRestClient;
import com.handyapps.timesense.service.TimeService;
import com.handyapps.timesense.util.Dialog;
import com.handyapps.timesense.util.GsonUtil;
import com.handyapps.timesense.util.NotificationUtil;
import com.handyapps.timesense.util.ResourceUtils;

public class TimeZoneUpdateActivity extends Activity {

	private static final String timeZoneChangeFormat = "<font color='#FF6347'>%s</font>";
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		setContentView(R.layout.layout_timezone_update);
		
		Button notifyButton = (Button) findViewById(R.id.butNotify);
		Button butCancel = (Button) findViewById(R.id.butCancel);
		
		TextView txtVwPrefix = (TextView) findViewById(R.id.txtVwPrefix);
		
		final String timeZone = (String) this.getIntent().getStringExtra("time_zone");
		
		final SettingsService service = SettingsService.getInstance();
		service.init(getApplicationContext());
		final Settings settings = service.getSettings();
		
		if (!TimeService.getInstance().isLoaded())
			TimeService.getInstance().init(this);
		
		TimeCode timeCode = TimeService.getInstance().getTimeCodeByTimeZone(timeZone); 
		
		String message = "<font>" + String.format(txtVwPrefix.getText().toString(), String.format(timeZoneChangeFormat, timeZone)) + "</font>";
		
		if (timeCode == null) {
			message = "<font> Unable to handle " + String.format(timeZoneChangeFormat, timeZone) + " time zone. Sorry for inconviniance caused. </font>";
			notifyButton.setEnabled(false);
			notifyButton.setVisibility(Button.INVISIBLE);
		} else {
			notifyButton.setEnabled(true);
			notifyButton.setVisibility(Button.VISIBLE);
		}
		
		txtVwPrefix.setText(Html.fromHtml(message));
		
		butCancel.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TimeZoneUpdateActivity.this.finish();
			}
		});
		
		notifyButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (!ResourceUtils.isNetworkAvailable(TimeZoneUpdateActivity.this)) {
		    		Dialog.show(TimeZoneUpdateActivity.this, "No network available", "Error");
		    		return;
		    	}
				
			    new AsyncTimeZoneUpdate(TimeZoneUpdateActivity.this).execute(settings.getUserId(), timeZone);
			    TimeZoneUpdateActivity.this.finish();
			}
		});
	}
}

class AsyncTimeZoneUpdate extends AsyncTask<String, String, Void> {

	ProgressDialog dialog;
	
	Context context;
	
	AsyncTimeZoneUpdate(Context context) {
		this.context = context;
	}

	@Override
    protected void onPreExecute() {
      super.onPreExecute();
    }
	
	@Override
	protected Void doInBackground(String... params) {
		String webservice = ResourceUtils.getString(context, R.string.time_sense_rest_ws);
	    TimeSenseRestClient client = new TimeSenseRestClient(webservice);
	    
	    com.handyapps.timesense.dataobjects.response.Status status = client.sendTimeZoneUpdateMessage(params[0], params[1]);
	    
	    if (status.getStatusCode() == StatusCode.SUCCESS) {
	    	BrodcastResult brodcastResult = GsonUtil.getObject(status.getStatusDescription(), BrodcastResult.class);
	    	 
		    NotificationUtil.showNotification(context, "Time Zone Change Notified", "Timezone update - Success");
	    } else {
	    	NotificationUtil.showNotification(context, "Unable to process timezone update. Please try again", "Timezone update - Failed");
	    }
	    
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
	}
	
}