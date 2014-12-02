package com.san.timesense.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.san.timesense.R;
import com.san.timesense.adapter.TimeZoneListViewAdapter;
import com.san.timesense.dto.TimeCode;
import com.san.timesense.service.SettingsService;
import com.san.timesense.service.TimeService;
import com.san.timesense.util.ResourceUtils;

public class TimerPlannerFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, 
							 ViewGroup container,
							 Bundle savedInstanceState) {
		
		final SettingsService settingService = SettingsService.getInstance();
		
		container.removeAllViews();
		
		final View view = inflater.inflate(R.layout.layout_time_planner, null);
		
		getActivity().getActionBar().setTitle("PLANNER");
		
		final FragmentManager fm = getFragmentManager();
		
		Button butEditTimeRange = (Button) view.findViewById(R.id.butEditTimeRange);
		Button butEditTimeZone = (Button) view.findViewById(R.id.butEditTimeZone);
		
		final List<TimeCode> timezones = settingService.getSettings().getTimePlanerTimeCodes();
		
		createTimePlanner(timezones, view);
		
		butEditTimeZone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TimeZoneFragment settingsFragment = new TimeZoneFragment();
				
				final Dialog alertDialog = new Dialog(TimerPlannerFragment.this.getActivity());
				alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				
				LayoutInflater vi = (LayoutInflater) TimerPlannerFragment.this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View newView = vi.inflate(R.layout.layout_timezone, null);
				
				alertDialog.setContentView(newView);
				
				ListView listView = (ListView) newView.findViewById(R.id.listViewTz);
				Button ok = (Button) newView.findViewById(R.id.buttonOk);
				Button cancel = (Button) newView.findViewById(R.id.buttonCancel);
				
				listView.setFastScrollEnabled(true);
		        listView.setScrollingCacheEnabled(true);
		        
				final Map<String, TimeCode> allTimeZoneInfo = TimeService.getInstance().getAllTimeZoneInfo();
				final List<TimeCode> timeCodes = new ArrayList<TimeCode>(allTimeZoneInfo.values());
				
				final TimeZoneListViewAdapter timeZoneViewAdapter 
							= new TimeZoneListViewAdapter(TimerPlannerFragment.this.getActivity(),timeCodes);
				listView.setAdapter(timeZoneViewAdapter);
				
				alertDialog.show();
				
				ok.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						timezones.clear();
						for (TimeCode timeCode : timeCodes) {
							if (timeCode.isSelect()){
								timezones.add(timeCode);
							}
						}
						
						alertDialog.dismiss();
						
						settingService.getSettings().setTimePlanerTimeCodes(timezones);
						settingService.saveSettings();
						
						createTimePlanner(timezones, view);
					}
				});
				
				cancel.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
					
						alertDialog.dismiss();
					}
				});
				
			}
		});

		butEditTimeRange.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final Dialog alertDialog = new Dialog(TimerPlannerFragment.this.getActivity());
				alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				alertDialog.setContentView(R.layout.layout_range_selector);
				alertDialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				alertDialog.show();	
				
//				TimeZoneFragment settingsFragment = new TimeZoneFragment();
//				FragmentTransaction fragmentTransaction = fm.beginTransaction();
//				fragmentTransaction.add(R.id.fragment_place, settingsFragment);
//				fragmentTransaction.commit();
			}
		});
		
		//createTimePlanner(timezones, view);
		
		return view;
	}
	
	
	private void createTimePlanner(List<TimeCode> timezones, View view) {
		
		final TableLayout timePlannerMatrix = (TableLayout) view.findViewById(R.id.tableTimePlanner);

		
		try {
			DisplayMetrics metrics = new DisplayMetrics();
			getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
			float logicalDensity = metrics.density;
			int px = (int) Math.ceil(5 * logicalDensity);
			
			int hour = 0 ;
			int min = 0;
			
			int day = 0;
			
			int totalCount= 0;
			
			String dayCount = "";
			
			Calendar calendar = new GregorianCalendar(java.util.TimeZone.getDefault());
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			long date = calendar.getTime().getTime();
			
			DateTimeZone timeZoneLondon = DateTimeZone.forID( java.util.TimeZone.getDefault().getID() );
			
			for (int i = 0; i <= timePlannerMatrix.getChildCount(); i++) {		
				View childView = timePlannerMatrix.getChildAt(i);
				
				if (childView instanceof TableRow) {
					
					TableRow tableRow = (TableRow) childView;
					
					TextView txtView = (TextView) tableRow.getChildAt(0);
					
					System.out.println(txtView.getText());
					
					totalCount = tableRow.getChildCount() - 1;
					for (int childCount=totalCount; childCount>0; childCount--) {
						tableRow.removeViewAt( childCount );
					}
					System.out.println(tableRow.getChildCount());
					
					for (TimeCode tz : timezones) {
				        day = calendar.get(Calendar.DATE);
				        
				        DateTime timeZone = new DateTime(date + (i-1)*3600000 + timeZoneLondon.getOffset(0));
				        DateTime newTime = timeZone.withZone( DateTimeZone.forID(tz.getTimeZone()) );
				        
				        TextView asia = new TextView(getActivity().getApplicationContext());
						asia.setLayoutParams(txtView.getLayoutParams());
						asia.setGravity(txtView.getGravity());
						asia.setPadding(px,px,px,px);
						asia.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
						
						day = (int) (newTime.getMillis() - calendar.getTime().getTime()) / 86400000;
						
				        if (i == 0) {
				        	asia.setText(tz.getCountry());
							asia.setTextColor(Color.WHITE);
							
							asia.setBackgroundColor(ResourceUtils.getColor(getActivity(), R.color.DeepSkyBlue));
							
				        } else {
				        	hour = newTime.getHourOfDay();
							min = newTime.getMinuteOfHour();
							
							if (min == 0)
								asia.setText(String.format("%s:00%s", hour, getDayCount (day) ));
							else
								asia.setText(String.format("%s:%s%s", hour, min, getDayCount (day)));
							
							asia.setTextColor(Color.WHITE);
							
							if (hour >= 8 && hour <= 20 && day == 0)
								asia.setBackgroundColor(ResourceUtils.getColor(getActivity(), R.color.Green));
							else 
								asia.setBackgroundColor(ResourceUtils.getColor(getActivity(), R.color.Gray));
				        }
						
						tableRow.addView(asia);
					}
				}
			}
			
			timePlannerMatrix.refreshDrawableState();
		} catch (Exception exp) {
			Toast.makeText(getActivity(), "Error#"+exp, 100).show();
			exp.printStackTrace();
		}
	}

	private String getDayCount(int day) {
		
		String format = "(%s)";
		
		if (day == 0) {
			return "";
		} else if (day >= 0) {
			return String.format(format,"+"+day);
		} else {
			return String.format(format, String.valueOf(day));
		}
	}
}
