package com.handyapps.timesense.fragment;

import static com.handyapps.timesense.constant.AppContant.SHARED_CALL_INFO;
import static com.handyapps.timesense.constant.AppContant.*;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.handyapps.timesense.R;
import com.handyapps.timesense.activity.TimeSenseActivity;
import com.handyapps.timesense.dataobjects.CallInfo;
import com.handyapps.timesense.db.CallLogDAO;

public class CallDetailsFragment extends Activity {
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		final CallLogDAO dao = new CallLogDAO(this);
		
		Gson gson = new Gson();
		
		setContentView(R.layout.layout_call_log_detail);
		
		Bundle extras = getIntent().getExtras();
		
		String jsonCallInfo = extras.getString(SHARED_CALL_INFO);
		
		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
		SimpleDateFormat dayFormat = new SimpleDateFormat("EEE, d MMM yyyy");
		
		Button back = (Button) findViewById(R.id.butBack);
		
		back.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CallDetailsFragment.this.onBackPressed();
				CallDetailsFragment.this.finish();
			}
		});
		
		if (jsonCallInfo != null) {
			
			final CallInfo callInfo = gson.fromJson(jsonCallInfo, CallInfo.class); 
			
			TextView txtViewNumber = (TextView) findViewById(R.id.txtViewPhoneNumber);
			TextView txtViewName = (TextView) findViewById(R.id.txtViewName);
			TextView txtViewCallType = (TextView) findViewById(R.id.txtViewCallType);
			
			TextView txtViewDuration = (TextView) findViewById(R.id.txtViewDuration);
			
			txtViewNumber.setText( callInfo.getPhoneNumber() );
			if ( "".equals(callInfo.getName()) ){
				txtViewName.setText( "Unknown" );
			} else {
				txtViewName.setText( callInfo.getName() );
			}
			
			txtViewDuration.setText( getDuration(callInfo.getDuration()) );
			
			switch ( callInfo.getCallType() ) {
			
				case CallLog.Calls.INCOMING_TYPE:
					txtViewCallType.setText("Incoming Call");
					break;
					
				case CallLog.Calls.OUTGOING_TYPE:
					txtViewCallType.setText("Outgoing Call");
					break;
					
				case CallLog.Calls.MISSED_TYPE:
					txtViewCallType.setText("Missed Call");
					break;
			}
			
			// fill local call details.
//			
			TextView txtViewHomeTime = (TextView) findViewById(R.id.txtViewHomeTime);
			TextView txtViewHomeDate = (TextView) findViewById(R.id.txtViewHomeDate);
			TextView txtViewTimeZone = (TextView) findViewById(R.id.txtViewHomeTimeZone);
			TextView txtViewCountry  = (TextView) findViewById(R.id.txtViewHomeCountry);
			
			Button butDelete = (Button) findViewById(R.id.butDelete);
			
			txtViewHomeTime.setText(callInfo.getLocalTime());
			txtViewHomeDate.setText(callInfo.getLocalDate());
//			
			txtViewTimeZone.setText( callInfo.getHomeTimeZone() );
			txtViewCountry.setText( callInfo.getHomeCountry() );
			
			// Fill remote call details
			TextView txtViewRecipientTime = (TextView) findViewById(R.id.txtViewRecipientTime);
			TextView txtViewRecipientDate = (TextView) findViewById(R.id.txtViewRecipientDate);
			TextView txtViewRecipientTimeZone = (TextView) findViewById(R.id.txtViewRecipientTimeZone);
			TextView txtViewRecipientCountry  = (TextView) findViewById(R.id.txtViewRecipientCountry);
			
			txtViewRecipientTime.setText( callInfo.getRemoteTime() );
			txtViewRecipientDate.setText( callInfo.getRemoteDate() );
			
			txtViewRecipientTimeZone.setText( callInfo.getRecipientTimeZone() );
			txtViewRecipientCountry.setText( callInfo.getRecipientCountry() );
			
			
			butDelete.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					try {
						dao.deleteCallInfo(callInfo.getId());
						
						Intent intent = new Intent(CallDetailsFragment.this, TimeSenseActivity.class);
						intent.putExtra(INTENT_PROP_REQUESTER, INTENT_VAL_FRAGMENT_CALL_LOG);
						CallDetailsFragment.this.startActivity(intent);
						
						finish();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	private String getDuration(long duration) {
		long min = duration/60;
		long hour = min/60;
		
		long total = hour*60 + min*60;
		long sec = duration - total;
		
		StringBuilder msg = new StringBuilder();
		if (hour != 0)
			msg.append(hour).append(" hours ");
		
		if (min != 0)
			msg.append(min).append(" mins ");
		
		if (sec != 0)
			msg.append(sec).append(" seconds ");
		
		return msg.toString();
	}
}
