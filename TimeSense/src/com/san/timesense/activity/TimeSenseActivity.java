package com.san.timesense.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.san.timesense.R;
import com.san.timesense.adapter.TimeZoneListViewAdapter;
import com.san.timesense.adapter.WorldClockListViewAdapter;
import com.san.timesense.dto.TimeCode;
import com.san.timesense.fragment.ContactFragment;
import com.san.timesense.fragment.ContactLogsFragment;
import com.san.timesense.fragment.DialFragment;
import com.san.timesense.fragment.SettingsFragment;
import com.san.timesense.fragment.TimeZoneFragment;
import com.san.timesense.fragment.TimerPlannerFragment;
import com.san.timesense.service.SettingsService;
import com.san.timesense.service.TimeService;

public class TimeSenseActivity extends Activity {

	private ActionBarDrawerToggle mDrawerToggle;
	final Stack<DialFragment> lastDialFragment = new Stack<DialFragment>();
	
	public void addToDialNumber(View numView) {
		lastDialFragment.peek().addToDialNumber(numView);
	}
	public void delOneDigitOfDialNumber (View numView) {
		lastDialFragment.peek().delOneDigitOfDialNumber(numView);
	}
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		setContentView(R.layout.layout_time_sense);

		float scale = getResources().getDisplayMetrics().density;
		final int padding_5dp = (int) (4 * scale + 0.5f);

		final Button butContacts = (Button) findViewById(R.id.contacts);
		final Button logs = (Button) findViewById(R.id.logs);
		final Button timePlaner = (Button) findViewById(R.id.clock);
		final Button butSettings = (Button) findViewById(R.id.butSettings);
		final Button butDialPad = (Button) findViewById(R.id.butDialPad);
		
		final Button buttonWorldClock = (Button) findViewById(R.id.buttonWorldClock);

		final FragmentManager fm = getFragmentManager();

		final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		
		final SettingsService settingService = SettingsService.getInstance();
		
		final List<TimeCode> timezones = settingService.getSettings().getWorldClockTimeCodes();
		
		final ListView worldClockListView = (ListView) findViewById(R.id.listViewWLClock);
		worldClockListView.setAdapter(new WorldClockListViewAdapter(TimeSenseActivity.this.getApplicationContext(), timezones));
		
		Button edit = (Button) findViewById(R.id.buttonEdit);
		
		
		buttonWorldClock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				drawerLayout.closeDrawer(Gravity.START);
				drawerLayout.openDrawer(Gravity.END);
			}
		});
		
		edit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TimeZoneFragment settingsFragment = new TimeZoneFragment();
				
				final Dialog alertDialog = new Dialog(TimeSenseActivity.this);
				alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				
				LayoutInflater vi = (LayoutInflater) TimeSenseActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
							= new TimeZoneListViewAdapter(TimeSenseActivity.this,timeCodes);
				listView.setAdapter(timeZoneViewAdapter);
				
				alertDialog.show();
				
				ok.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						for (TimeCode timeCode : timeCodes) {
							if (timeCode.isSelect()){
								timezones.add(timeCode);
							}
						}
						
						alertDialog.dismiss();
						settingService.getSettings().setWorldClockTimeCodes(timezones);
						settingService.saveSettings();
						
						final ListView worldClockListView = (ListView) findViewById(R.id.listViewWLClock);
						worldClockListView.setAdapter(new WorldClockListViewAdapter(TimeSenseActivity.this.getApplicationContext(), timezones));
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
		
		butSettings.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				butContacts.setPadding(0, 0, 0, padding_5dp);
				butDialPad.setPadding(0, 0, 0, padding_5dp);
				logs.setPadding(0, 0, 0, padding_5dp);
				timePlaner.setPadding(0, 0, 0, padding_5dp);
				
				SettingsFragment settingsFragment = new SettingsFragment();
				FragmentTransaction fragmentTransaction = fm.beginTransaction();
				fragmentTransaction.add(R.id.fragment_place, settingsFragment);
				fragmentTransaction.commit();
				
				drawerLayout.closeDrawer(Gravity.START);
			}
		});

		mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, 
														R.drawable.ic_action_overflow, 
														R.string.abc_action_mode_done, 
														R.string.abc_action_mode_done )
		{

			public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }
 
            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
		};
		
		drawerLayout.setDrawerListener(mDrawerToggle);
		 
		
		 getActionBar().setDisplayHomeAsUpEnabled(true);
	        getActionBar().setHomeButtonEnabled(true);
	        getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
	        
	        int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
	        if (actionBarTitleId > 0) {
	            TextView title = (TextView) findViewById(actionBarTitleId);
	            if (title != null) {
	                title.setTextColor(Color.WHITE);
	            }
	        }
	        
		butContacts.setPadding(0, 0, 0, 0);
		butDialPad.setPadding(0, 0, 0, padding_5dp);
		logs.setPadding(0, 0, 0, padding_5dp);
		timePlaner.setPadding(0, 0, 0, padding_5dp);

		butDialPad.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				butContacts.setPadding(0, 0, 0, padding_5dp);
				butDialPad.setPadding(0, 0, 0, 0);
				logs.setPadding(0, 0, 0, padding_5dp);
				timePlaner.setPadding(0, 0, 0, padding_5dp);
				
				DialFragment dialFragment = new DialFragment();
				lastDialFragment.clear();
				lastDialFragment.add(dialFragment);
				FragmentTransaction fragmentTransaction = fm.beginTransaction();
				fragmentTransaction.add(R.id.fragment_place, dialFragment);
				fragmentTransaction.commit();
			}
		});

		butContacts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				butContacts.setPadding(0, 0, 0, 0);
				butDialPad.setPadding(0, 0, 0, padding_5dp);
				logs.setPadding(0, 0, 0, padding_5dp);
				timePlaner.setPadding(0, 0, 0, padding_5dp);
				
				ContactFragment contactFragment = new ContactFragment();
				FragmentTransaction fragmentTransaction = fm.beginTransaction();
				fragmentTransaction.add(R.id.fragment_place, contactFragment);
				fragmentTransaction.commit();
			}
		});

		logs.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				butContacts.setPadding(0, 0, 0, padding_5dp);
				butDialPad.setPadding(0, 0, 0, padding_5dp);
				logs.setPadding(0, 0, 0, 0);
				timePlaner.setPadding(0, 0, 0, padding_5dp);

				FragmentTransaction transaction = fm.beginTransaction();
				ContactLogsFragment contactLogsFragment = new ContactLogsFragment();
				transaction.replace(R.id.fragment_place, contactLogsFragment);
				transaction.commit();
			}
		});

		timePlaner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				butContacts.setPadding(0, 0, 0, padding_5dp);
				butDialPad.setPadding(0, 0, 0, padding_5dp);
				logs.setPadding(0, 0, 0, padding_5dp);
				timePlaner.setPadding(0, 0, 0, 0);
				
				FragmentTransaction transaction = fm.beginTransaction();
				TimerPlannerFragment timerPlannerFragment = new TimerPlannerFragment();
				transaction.replace(R.id.fragment_place, timerPlannerFragment);
				transaction.commit();
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.time_sense, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
        	case R.id.action_settings:
        		return true;
        	case R.id.action_new:
        		return true;
        	
        default:
            return super.onOptionsItemSelected(item);
        }
    }
 
    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
 
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
 
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}