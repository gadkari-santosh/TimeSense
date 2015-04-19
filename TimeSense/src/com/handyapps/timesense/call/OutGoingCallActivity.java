package com.handyapps.timesense.call;

import static com.handyapps.timesense.constant.AppContant.*;

import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.handyapps.timesense.R;
import com.handyapps.timesense.dataobjects.CallInfo;
import com.handyapps.timesense.dataobjects.TimeCode;
import com.handyapps.timesense.service.SettingsService;
import com.handyapps.timesense.service.TimeService;

public class OutGoingCallActivity extends Activity {

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		Gson gson = new Gson();
		
		final String phoneNumber = (String) this.getIntent().getStringExtra("Phone");
		final String contactName = (String) this.getIntent().getStringExtra("Name");
		
		final String callInfoJson = (String) this.getIntent().getStringExtra(SHARED_CALL_INFO);
		
		CallInfo callInfo = gson.fromJson(callInfoJson, CallInfo.class);
		
		TimeCode timeCode = (TimeCode) this.getIntent().getExtras().getSerializable("TimeCode");
		
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.layout_toast_outgoing_call);
		dialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		
		dialog.setTitle("");

		TextView tz = (TextView) dialog.findViewById(R.id.CountryAndTz);
		TextView country = (TextView) dialog.findViewById(R.id.country);
		TextView time = (TextView) dialog.findViewById(R.id.time);
		TextView date = (TextView) dialog.findViewById(R.id.date);
		
		TextView name = (TextView) dialog.findViewById(R.id.name);
		TextView phone = (TextView) dialog.findViewById(R.id.phoneNumber);
		
		final ImageView kaal = (ImageView) dialog.findViewById(R.id.kaal);
		
		if (timeCode == null)
			timeCode = TimeService.getInstance().getTimeCodeByPhoneNumber(phoneNumber);
		
		phone.setText(phoneNumber);
		name.setText(contactName);
		
		callInfo.setName(contactName);
		
		if (timeCode != null) {
		
			time.setText( TimeService.getInstance().getTime(timeCode) );
			date.setText( TimeService.getInstance().getDate(timeCode) );
			
			tz.setText(timeCode.getTimeZone());
			country.setText(timeCode.getCountry());
			
			TimeService.getInstance().setKaalPic(kaal, TimeService.getInstance().getKaal(timeCode));
			
			callInfo.setRecipientCountry(timeCode.getCountry());
			callInfo.setRemoteTime( TimeService.getInstance().getTime(timeCode) );
			callInfo.setRemoteDate( TimeService.getInstance().getDate(timeCode) );
			callInfo.setRecipientTimeZone( timeCode.getTimeZone() );
			callInfo.setPhoneNumber(phoneNumber);
			
			callInfo.setHomeTimeZone( TimeZone.getDefault().getDisplayName());
			callInfo.setHomeCountry( SettingsService.getInstance().getSettings().getHomeCountry() ) ;

			Date today = new Date();
			
			callInfo.setLocalTime( TimeService.getInstance().getTime(today)  );
			callInfo.setLocalDate( TimeService.getInstance().getDate(today)  );
			
			SharedPreferences sp=this.getSharedPreferences(SHARED_PREF_NAME, 1);
			sp.edit().putString(SHARED_CALL_INFO, gson.toJson(callInfo)).commit();
		}
		
		ImageButton dialogButton = (ImageButton) dialog.findViewById(R.id.cancel);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				
				finish();
			}
		});
		
		ImageButton okButton = (ImageButton) dialog.findViewById(R.id.ok);
		// if button is clicked, close the custom dialog
		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences settings = OutGoingCallActivity.this.getSharedPreferences(SHARED_PREF_NAME, 1);
				Editor edit = settings.edit();
				edit.putBoolean("CustomCall:"+phoneNumber, true);
				edit.commit();
				
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" + phoneNumber));
				OutGoingCallActivity.this.startActivity(intent);
				
				finish();
			}
		});

		dialog.show();
	}
}
