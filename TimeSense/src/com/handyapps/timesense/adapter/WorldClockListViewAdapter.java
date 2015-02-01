package com.handyapps.timesense.adapter;

import java.util.List;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextClock;
import android.widget.TextView;

import com.handyapps.timesense.R;
import com.handyapps.timesense.dataobjects.TimeCode;

public class WorldClockListViewAdapter extends ArrayAdapter<TimeCode> {

	private Context context = null;
	
	private List<TimeCode> clocks = null;
	
	public WorldClockListViewAdapter(Context context, List<TimeCode> clocks) {
		super(context, R.layout.listview_world_clock, clocks);
		
		this.clocks = clocks;
		this.context = context;
	}

	@Override
	public int getCount() {
		return clocks.size();
	}
	
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		
		if (view == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.listview_world_clock, null);
		}
		
		TimeCode clock = clocks.get(position);
		
		TextView date = (TextView) view.findViewById(R.id.txtViewDate);
		TextView timeZone = (TextView) view.findViewById(R.id.txtViewTimeZone);
		
		TextView country = (TextView) view.findViewById(R.id.txtViewCountry);
		
		com.handyapps.timesense.widget.Clock analogClock = (com.handyapps.timesense.widget.Clock) view.findViewById(R.id.analogTime);
		analogClock.setTimeZone(clock.getTimeZone());
		
		DateTime currentTime = DateTime.now ( DateTimeZone.forTimeZone( TimeZone.getTimeZone(clock.getTimeZone() ) ));
				
		DateTimeFormatter dateFormat = DateTimeFormat.forPattern("EEEE, dd, MMM, yyyy");
		date.setText(currentTime.toString(dateFormat));
		
		TextClock textClock = (TextClock) view.findViewById(R.id.digitalTime);
		textClock.setTimeZone(clock.getTimeZone());
		
		timeZone.setText(clock.getTimeZone());
		country.setText(clock.getCountry());
		
		return view;
	}
}