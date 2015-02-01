package com.handyapps.timesense.fragment;

import static com.handyapps.timesense.constant.AppContant.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.handyapps.timesense.R;
import com.handyapps.timesense.activity.TimeSenseActivity;
import com.handyapps.timesense.activity.TimeZoneActivity;
import com.handyapps.timesense.dataobjects.Settings;
import com.handyapps.timesense.dataobjects.TimeCode;
import com.handyapps.timesense.service.SettingsService;
import com.handyapps.timesense.util.ResourceUtils;

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
				
				Intent intent = new Intent(TimerPlannerFragment.this.getActivity(), TimeZoneActivity.class);
				intent.putExtra(INTENT_PROP_REQUESTER, INTENT_VAL_TIME_SENSE_PLANNER_TAB);
				intent.putExtra(INTENT_PROP_TIMECODES, new ArrayList(settingService.getSettings().getTimePlanerTimeCodes()));
				TimerPlannerFragment.this.startActivity(intent);
				
//				 List<TimeCode> newTimeZones = settingService.getSettings().getTimePlanerTimeCodes();
//				
//				createTimePlanner(newTimeZones, view);
//				TimeZoneFragment settingsFragment = new TimeZoneFragment();
//				
//				final Dialog alertDialog = new Dialog(TimerPlannerFragment.this.getActivity());
//				alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//				alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//				
//				LayoutInflater vi = (LayoutInflater) TimerPlannerFragment.this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//				View newView = vi.inflate(R.layout.layout_timezone, null);
//
//				alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
//				alertDialog.setContentView(newView);
//				alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//				
//				ListView listView = (ListView) newView.findViewById(R.id.listViewTz);
//				Button ok = (Button) newView.findViewById(R.id.buttonOk);
//				Button cancel = (Button) newView.findViewById(R.id.buttonCancel);
//				Button clear = (Button) newView.findViewById(R.id.buttonClear);
//				
//				listView.setFastScrollEnabled(true);
//		        listView.setScrollingCacheEnabled(true);
//		        
//				final Map<String, List<TimeCode>> allTimeZoneInfo = TimeService.getInstance().getAllTimeZoneInfo();
//				final List<TimeCode> timeCodes = new ArrayList<TimeCode>();
//				for (List<TimeCode> individualCodes : allTimeZoneInfo.values()) {
//					
//					for (TimeCode individualCode : individualCodes) {
//						
//						if (settingService.getSettings().getTimePlanerTimeCodes().contains(individualCode)) {
//							individualCode.setSelect(true);
//						} else {
//							individualCode.setSelect(false);
//						}
//					}
//					timeCodes.addAll(individualCodes);
//				}
//				
//				final TimeZoneListViewAdapter timeZoneViewAdapter 
//							= new TimeZoneListViewAdapter(TimerPlannerFragment.this.getActivity(),timeCodes);
//				listView.setAdapter(timeZoneViewAdapter);
//				
//				alertDialog.show();
//				
//				clear.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						
////						timezones.clear();
//						for (TimeCode timeCode : timeCodes) {
//							timeCode.setSelect(false);
//						}
//						
////						alertDialog.dismiss();
////						settingService.getSettings().setTimePlanerTimeCodes(timezones);
////						settingService.saveSettings();
////						
////						createTimePlanner(timezones, view);
//						
//						timeZoneViewAdapter.setNotifyOnChange(true);
//						
//					}
//				});
//				
//				ok.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						
//						timezones.clear();
//						for (TimeCode timeCode : timeCodes) {
//							if (timeCode.isSelect()){
//								if (!timezones.contains(timeCode))
//									timezones.add(timeCode);
//							}
//						}
//						
//						alertDialog.dismiss();
//						
//						settingService.getSettings().setTimePlanerTimeCodes(timezones);
//						settingService.saveSettings();
//						
//						createTimePlanner(timezones, view);
//					}
//				});
//				
//				cancel.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						alertDialog.dismiss();
//					}
//				});
//				
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
				
				Button ok = (Button) alertDialog.findViewById(R.id.buttonOk);
				Button cancel = (Button) alertDialog.findViewById(R.id.buttonCancel);
				
				final TextView txtViewFrom = (TextView) alertDialog.findViewById(R.id.txtViewRangeFrom);
				final TextView txtViewRangeTo = (TextView) alertDialog.findViewById(R.id.txtViewRangeTo);
				
				final SeekBar seekFrom = (SeekBar) alertDialog.findViewById(R.id.seekFrom);
				final SeekBar seekTo = (SeekBar) alertDialog.findViewById(R.id.seekTo);
				
				seekFrom.setProgress(settingService.getSettings().getTimePlannerRangeFrom());
				seekTo.setProgress(settingService.getSettings().getTimePlannerRangeTo());
				
				seekFrom.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// NOP
					}
					
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// NOP
					}
					
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress,
							boolean fromUser) {
						txtViewFrom.setText(String.format("Time From %s:00",progress));
					}
				});
				
				seekTo.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						if (seekFrom.getProgress() > seekBar.getProgress()) {
							Toast makeText = Toast.makeText(TimerPlannerFragment.this.getActivity(),
									"The value should be greater than lower range.", Toast.LENGTH_SHORT);
							makeText.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
							makeText.show();
						} else {
							txtViewRangeTo.setText(String.format("Time From %s:00",seekBar.getProgress()));
						}
					}
					
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// NOP
					}
					
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress,
							boolean fromUser) {
						
						
					}
				});
				
				ok.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						if (seekFrom.getProgress() > seekTo.getProgress()) {
							Toast makeText = Toast.makeText(TimerPlannerFragment.this.getActivity(),
									"The value should be greater than lower range.", Toast.LENGTH_SHORT);
							makeText.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
							makeText.show();
						} else {
							settingService.getSettings().setTimePlannerRangeFrom(seekFrom.getProgress());
							settingService.getSettings().setTimePlannerRangeTo(seekTo.getProgress());
							settingService.saveSettings();
							
							alertDialog.dismiss();
							
							createTimePlanner(timezones, view);
						}
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
		
		return view;
	}
	
	
	private void createTimePlanner(List<TimeCode> timezones, View view) {
		
		final TableLayout timePlannerMatrix = (TableLayout) view.findViewById(R.id.tableTimePlanner);

		final Settings settings = SettingsService.getInstance().getSettings();
		
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
							
							if (hour >= settings.getTimePlannerRangeFrom() 
									&& hour <= settings.getTimePlannerRangeTo() 
									&& day == 0)
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
