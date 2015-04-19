package com.handyapps.timesense.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.handyapps.timesense.R;
import com.handyapps.timesense.adapter.TimeZoneListViewAdapter;
import com.handyapps.timesense.dataobjects.TimeCode;
import com.handyapps.timesense.service.TimeService;

public class TimeZoneFragment extends Activity {

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle); 
		
//		if (container != null)
//			container.removeAllViews();
		
		setContentView(R.layout.layout_timezone);

		ListView listView = (ListView) findViewById(R.id.listViewTz);
		
		listView.setFastScrollEnabled(true);
        listView.setScrollingCacheEnabled(true);
        
//        getActionBar().setTitle("TimeZone");
        
		Map<String, List<TimeCode>> allTimeZoneInfo = TimeService.getInstance().getAllTimeZoneInfo();
		List<TimeCode> timeCodes = new ArrayList<TimeCode>();
		for (List<TimeCode> individualCodes : allTimeZoneInfo.values()) {
			timeCodes.addAll(individualCodes);
		}
		
		final TimeZoneListViewAdapter timeZoneViewAdapter 
					= new TimeZoneListViewAdapter(this,timeCodes);
		listView.setAdapter(timeZoneViewAdapter);
		
//		return view;
	}

}
