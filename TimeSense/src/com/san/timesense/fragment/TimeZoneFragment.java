package com.san.timesense.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.san.timesense.R;
import com.san.timesense.adapter.TimeZoneListViewAdapter;
import com.san.timesense.dto.TimeCode;
import com.san.timesense.service.TimeService;

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
        
		Map<String, TimeCode> allTimeZoneInfo = TimeService.getInstance().getAllTimeZoneInfo();
		List<TimeCode> timeCodes = new ArrayList<TimeCode>(allTimeZoneInfo.values());
		
		final TimeZoneListViewAdapter timeZoneViewAdapter 
					= new TimeZoneListViewAdapter(this,timeCodes);
		listView.setAdapter(timeZoneViewAdapter);
		
//		return view;
	}

}
