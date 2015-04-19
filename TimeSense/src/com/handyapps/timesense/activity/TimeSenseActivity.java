package com.handyapps.timesense.activity;

import static com.handyapps.timesense.constant.AppContant.*;
import static com.handyapps.timesense.constant.AppContant.INTENT_PROP_TIMECODES;
import static com.handyapps.timesense.constant.AppContant.INTENT_PROP_USER_SELECTED_TIME_CODE;
import static com.handyapps.timesense.constant.AppContant.INTENT_VAL_TIME_SENSE_PLANNER_TAB;
import static com.handyapps.timesense.constant.AppContant.INTENT_VAL_WORLD_CLOCK_TAB;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.handyapps.timesense.R;
import com.handyapps.timesense.adapter.WorldClockListViewAdapter;
import com.handyapps.timesense.dataobjects.Settings;
import com.handyapps.timesense.dataobjects.TimeCode;
import com.handyapps.timesense.fragment.AboutUsFragment;
import com.handyapps.timesense.fragment.ContactFragment;
import com.handyapps.timesense.fragment.ContactLogsFragment;
import com.handyapps.timesense.fragment.DialFragment;
import com.handyapps.timesense.fragment.HelpFragment;
import com.handyapps.timesense.fragment.SettingsFragment;
import com.handyapps.timesense.fragment.TimerPlannerFragment;
import com.handyapps.timesense.service.SettingsService;
import com.handyapps.timesense.task.AsyncContactLoading;
import com.handyapps.timesense.util.ICallback;
import com.handyapps.timesense.util.ResourceUtils;

public class TimeSenseActivity extends Activity {

	private ActionBarDrawerToggle mDrawerToggle;
	final Stack<DialFragment> lastDialFragment = new Stack<DialFragment>();
	
	public void addToDialNumber(View numView) {
		lastDialFragment.peek().addToDialNumber(numView);
	}
	public void delOneDigitOfDialNumber (View numView) {
		lastDialFragment.peek().delOneDigitOfDialNumber(numView);
	}
	
	public void onRestart()
	{
	    super.onRestart();
	    new AsyncContactLoading(this).execute();
	}
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		setContentView(R.layout.layout_time_sense);

		String redirectFragment = null;
		final List<TimeCode> intentTimeCodes = new ArrayList<TimeCode>();
		
		if (getIntent().getExtras() != null) {
			redirectFragment = (String) getIntent().getExtras().get(INTENT_PROP_REQUESTER);
			try {
				List<TimeCode> object = (List<TimeCode>) getIntent().getExtras().get(INTENT_PROP_USER_SELECTED_TIME_CODE);
				if (object != null) 
					intentTimeCodes.addAll(object);
			} catch (Exception exp) {
				; // ignore
			}
		}
		
		float scale = getResources().getDisplayMetrics().density;
		final int padding_5dp = (int) (4 * scale + 0.5f);

		final Button butContacts = (Button) findViewById(R.id.contacts);
		final Button butLogs = (Button) findViewById(R.id.butLogs);
		final Button butTimePlaner = (Button) findViewById(R.id.butPlanner);
		final Button butTimePlanerLeft = (Button) findViewById(R.id.butLeftPlanner);
		
		final Button butSettings = (Button) findViewById(R.id.butSettings);
		final Button buttonContactUs = (Button) findViewById(R.id.butContactUs);
		final Button butDialPad = (Button) findViewById(R.id.butDialPad);
		final Button butAboutUs = (Button) findViewById(R.id.butAboutUs);
//		final Button butHelp = (Button) findViewById(R.id.butHelp);
		
		final Button buttonWorldClock = (Button) findViewById(R.id.butLeftWorldClock);
		final Button butLeftDialPad = (Button) findViewById(R.id.butLeftDialPad);
		final Button butLeftLog = (Button) findViewById(R.id.butLeftlogs);
		final Button butLeftContact = (Button) findViewById(R.id.butLeftcontacts);
		final Button butSignOut = (Button) findViewById(R.id.butSignOut);

		final FragmentManager fm = getFragmentManager();

		final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		
		final SettingsService settingService = SettingsService.getInstance();
		
		final List<TimeCode> timezones = settingService.getSettings().getWorldClockTimeCodes();
		
		final ListView worldClockListView = (ListView) findViewById(R.id.listViewWLClock);
		
		final WorldClockListViewAdapter worldClockListViewAdapter = new WorldClockListViewAdapter(TimeSenseActivity.this.getApplicationContext(), timezones);
		
		worldClockListView.setAdapter(worldClockListViewAdapter);
		
		Button edit = (Button) findViewById(R.id.buttonEdit);
		
		if (INTENT_VAL_TIME_SENSE_PLANNER_TAB.equalsIgnoreCase(redirectFragment)) {
			
			if (intentTimeCodes != null) {
				settingService.getSettings().setTimePlanerTimeCodes(intentTimeCodes);
				SettingsService.getInstance().saveSettings();
			}
			
			butTimePlaner.callOnClick();
			butContacts.setPadding(0, 0, 0, padding_5dp);
			butDialPad.setPadding(0, 0, 0, padding_5dp);
			butLogs.setPadding(0, 0, 0, padding_5dp);
			butTimePlaner.setPadding(0, 0, 0, 0);
			
			FragmentTransaction transaction = fm.beginTransaction();
			TimerPlannerFragment timerPlannerFragment = new TimerPlannerFragment();
			transaction.replace(R.id.fragment_place, timerPlannerFragment);
			transaction.commit();
		} else if (INTENT_VAL_FRAGMENT_CALL_LOG.equalsIgnoreCase(redirectFragment)) {
			butContacts.setPadding(0, 0, 0, padding_5dp);
			butDialPad.setPadding(0, 0, 0, padding_5dp);
			butLogs.setPadding(0, 0, 0, 0);
			butTimePlaner.setPadding(0, 0, 0, padding_5dp);

			FragmentTransaction transaction = fm.beginTransaction();
			ContactLogsFragment contactLogsFragment = new ContactLogsFragment();
			transaction.replace(R.id.fragment_place, contactLogsFragment);
			transaction.commit();
		} else if (INTENT_VAL_WORLD_CLOCK_TAB.equalsIgnoreCase(redirectFragment)) {
			
			if (intentTimeCodes != null) {
				settingService.getSettings().setWorldClockTimeCodes(intentTimeCodes);
				SettingsService.getInstance().saveSettings();
				
				worldClockListViewAdapter.clear();
				worldClockListViewAdapter.addAll(intentTimeCodes);
				worldClockListViewAdapter.setNotifyOnChange(true);
			}
			
			drawerLayout.closeDrawer(Gravity.START);
			drawerLayout.openDrawer(Gravity.END);
			
			buttonWorldClock.callOnClick();
		} else {
			butContacts.setPadding(0, 0, 0, 0);
			butDialPad.setPadding(0, 0, 0, padding_5dp);
			butLogs.setPadding(0, 0, 0, padding_5dp);
			butTimePlaner.setPadding(0, 0, 0, padding_5dp);
		}
		
		butSignOut.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				com.handyapps.timesense.util.Dialog.show(TimeSenseActivity.this, "Are you sure to signout? It will delete all your application settings.", "Sign Out", 
						new ICallback() {
							
							@Override
							public void call(Object object) {
								SettingsService service = SettingsService.getInstance();
								Settings settings = service.getSettings();
								
								settings.setSignOnSuccess(false);
								settings.setEmail(null);
								settings.setGcm(null);
								settings.setUserId(null);
								service.saveSettings();
								
								Intent intent = new Intent(TimeSenseActivity.this, SplashScreenActivity.class);
								TimeSenseActivity.this.startActivity(intent);
								
								finish();
								
							}
						}, true);
			}
		});
		
		butTimePlanerLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				drawerLayout.closeDrawer(Gravity.START);
				drawerLayout.closeDrawer(Gravity.END);
				butTimePlaner.callOnClick();				
			}
		});
		
		/*butHelp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				HelpFragment helpFragment = new HelpFragment();
				FragmentTransaction fragmentTransaction = fm.beginTransaction();
				fragmentTransaction.add(R.id.fragment_place, helpFragment);
				fragmentTransaction.commit();
				
				drawerLayout.closeDrawer(Gravity.START);
			}
		});*/
		
		butAboutUs.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AboutUsFragment aboutUsFragment = new AboutUsFragment();
				FragmentTransaction fragmentTransaction = fm.beginTransaction();
				fragmentTransaction.add(R.id.fragment_place, aboutUsFragment);
				fragmentTransaction.commit();
				
				drawerLayout.closeDrawer(Gravity.START);
			}
		});
		
		butLeftDialPad.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				drawerLayout.closeDrawer(Gravity.START);
				drawerLayout.closeDrawer(Gravity.END);
				butDialPad.callOnClick();
			}
		});
		
		butLeftLog.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				drawerLayout.closeDrawer(Gravity.START);
				drawerLayout.closeDrawer(Gravity.END);
				butLogs.callOnClick();
			}
		});
		
		butLeftContact.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				drawerLayout.closeDrawer(Gravity.START);
				drawerLayout.closeDrawer(Gravity.END);
				butContacts.callOnClick();
			}
		});
		
		buttonContactUs.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String email = ResourceUtils.getString(TimeSenseActivity.this, R.string.contact_us_email);
				String subject = ResourceUtils.getString(TimeSenseActivity.this, R.string.contact_us_email_subject);
				
				Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
				emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				emailIntent.setType("vnd.android.cursor.item/email");
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {email});
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
				TimeSenseActivity.this.startActivity(Intent.createChooser(emailIntent, "Send mail using..."));
			}
		});
		
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
				Intent intent = new Intent(TimeSenseActivity.this, TimeZoneActivity.class);
				intent.putExtra(INTENT_PROP_REQUESTER, INTENT_VAL_WORLD_CLOCK_TAB);
				intent.putExtra(INTENT_PROP_TIMECODES, new ArrayList(settingService.getSettings().getWorldClockTimeCodes()));
				TimeSenseActivity.this.startActivity(intent);
			}
		});
		
		butSettings.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				butContacts.setPadding(0, 0, 0, padding_5dp);
				butDialPad.setPadding(0, 0, 0, padding_5dp);
				butLogs.setPadding(0, 0, 0, padding_5dp);
				butTimePlaner.setPadding(0, 0, 0, padding_5dp);
				
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
	        
		butDialPad.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				butContacts.setPadding(0, 0, 0, padding_5dp);
				butDialPad.setPadding(0, 0, 0, 0);
				butLogs.setPadding(0, 0, 0, padding_5dp);
				butTimePlaner.setPadding(0, 0, 0, padding_5dp);
				
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
				butLogs.setPadding(0, 0, 0, padding_5dp);
				butTimePlaner.setPadding(0, 0, 0, padding_5dp);
				
				ContactFragment contactFragment = new ContactFragment();
				FragmentTransaction fragmentTransaction = fm.beginTransaction();
				fragmentTransaction.add(R.id.fragment_place, contactFragment);
				fragmentTransaction.commit();
			}
		});

		butLogs.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				butContacts.setPadding(0, 0, 0, padding_5dp);
				butDialPad.setPadding(0, 0, 0, padding_5dp);
				butLogs.setPadding(0, 0, 0, 0);
				butTimePlaner.setPadding(0, 0, 0, padding_5dp);

				FragmentTransaction transaction = fm.beginTransaction();
				ContactLogsFragment contactLogsFragment = new ContactLogsFragment();
				transaction.replace(R.id.fragment_place, contactLogsFragment);
				transaction.commit();
			}
		});

		butTimePlaner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				butContacts.setPadding(0, 0, 0, padding_5dp);
				butDialPad.setPadding(0, 0, 0, padding_5dp);
				butLogs.setPadding(0, 0, 0, padding_5dp);
				butTimePlaner.setPadding(0, 0, 0, 0);
				
				FragmentTransaction transaction = fm.beginTransaction();
				TimerPlannerFragment timerPlannerFragment = new TimerPlannerFragment();
				transaction.replace(R.id.fragment_place, timerPlannerFragment);
				transaction.commit();
			}
		});
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//	    // Inflate the menu items for use in the action bar
//	    MenuInflater inflater = getMenuInflater();
//	    inflater.inflate(R.menu.time_sense, menu);
//	    return super.onCreateOptionsMenu(menu);
//	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
//        switch (item.getItemId()) {
//        	case R.id.action_settings:
//        		return true;
//        	case R.id.action_new:
//        		return true;
//        	
//        default:
            return super.onOptionsItemSelected(item);
//        }
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