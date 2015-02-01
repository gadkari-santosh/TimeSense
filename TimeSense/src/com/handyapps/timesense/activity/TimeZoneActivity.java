package com.handyapps.timesense.activity;

import static com.handyapps.timesense.constant.AppContant.INTENT_PROP_REQUESTER;
import static com.handyapps.timesense.constant.AppContant.INTENT_PROP_TIMECODES;
import static com.handyapps.timesense.constant.AppContant.INTENT_PROP_USER_SELECTED_TIME_CODE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.handyapps.timesense.R;
import com.handyapps.timesense.adapter.TimeZoneListViewAdapter;
import com.handyapps.timesense.dataobjects.TimeCode;
import com.handyapps.timesense.service.TimeService;

public class TimeZoneActivity extends Activity {
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		setContentView(R.layout.layout_timezone);
		
		final Bundle extras = getIntent().getExtras();
		
		List<TimeCode> requesterTimeCode = new ArrayList<TimeCode>();
		Serializable initTimeCodes = extras.getSerializable(INTENT_PROP_TIMECODES);
		if (initTimeCodes != null) {
			requesterTimeCode.addAll( (ArrayList) initTimeCodes);
		}
		
		Button ok = (Button) findViewById(R.id.buttonOk);
		Button cancel = (Button) findViewById(R.id.buttonCancel);
		Button clear = (Button) findViewById(R.id.buttonClear);
		
		ListView listView = (ListView) findViewById(R.id.listViewTz);
		
		// Can't help myself of getting TimeZone in white and client is after me with sharp knife !!
		getActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Time Zone</font>"));
		
		final Map<String, List<TimeCode>> allTimeZoneInfo = TimeService.getInstance().getAllTimeZoneInfo();
		final List<TimeCode> timeCodes = new ArrayList<TimeCode>();
		
		for (List<TimeCode> individualCodes : allTimeZoneInfo.values()) {
		
			for (TimeCode individualCode : individualCodes) {
				
				if (requesterTimeCode.contains(individualCode)) {
					individualCode.setSelect(true);
				} else {
					individualCode.setSelect(false);
				}
				
				if (!timeCodes.contains(individualCode))
					timeCodes.add(individualCode);
			}
		}
		
		final List<TimeCode> timeCodesBkup = new ArrayList<TimeCode>(timeCodes);
		
		final TimeZoneListViewAdapter timeZoneViewAdapter = new TimeZoneListViewAdapter(this,timeCodes);
		
		listView.setAdapter(timeZoneViewAdapter);
		
		EditText editText = (EditText) findViewById(R.id.searchBox);
        editText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				textChange(s);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				textChange(s);
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				textChange(s);		
			}
			
			private void textChange(Object s) {
				ArrayList<TimeCode> newList = new ArrayList<TimeCode>();
				if ("".equals(s.toString().trim()))
					newList.addAll(timeCodesBkup);
				else {
					for (TimeCode timeCode :  timeCodesBkup) {
						if (timeCode.getCountry().toLowerCase().contains(s.toString().toLowerCase())) {
							newList.add(timeCode);
						}
					}
				}
				
				timeZoneViewAdapter.clear();
				timeZoneViewAdapter.addAll(newList);
				timeZoneViewAdapter.setNotifyOnChange(true);
			}
		});
        
		clear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				for (TimeCode timeCode : timeCodesBkup) {
					timeCode.setSelect(false);
				}
				
				timeZoneViewAdapter.clear();
				timeZoneViewAdapter.addAll(timeCodesBkup);
				
				timeZoneViewAdapter.setNotifyOnChange(true);
			}
		});
		
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ArrayList<TimeCode> newTimeCodes = new ArrayList<TimeCode>();
				
				for (TimeCode timeCode : timeCodesBkup) {
					if (timeCode.isSelect()){
						newTimeCodes.add(timeCode);
					}
				}
				
				Intent intent = new Intent(TimeZoneActivity.this, TimeSenseActivity.class);
				intent.putExtra(INTENT_PROP_USER_SELECTED_TIME_CODE, newTimeCodes);
				intent.putExtra(INTENT_PROP_REQUESTER, extras.getString(INTENT_PROP_REQUESTER));
				
				TimeZoneActivity.this.startActivity(intent);
				
				TimeZoneActivity.this.finish();
			}
		});
		
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TimeZoneActivity.this.finish();
			}
		});
	}
}
