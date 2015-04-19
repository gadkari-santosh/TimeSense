package com.handyapps.timesense.constant;

import java.text.SimpleDateFormat;

public interface AppContant {

	String SHARED_PREF_NAME = "SharedPref";
	
	String INTENT_ALBHA = "IntentAlpha";
	
	SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a");
	
	SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd''yy");
	
	String PREFERENCES_FILE = "setting_file";
	
	String TIME_SENSE_CONTACT_FILE = "time_sense_contact_file";
	
	String INTENT_PROP_TIMECODES = "TIME_CODE";
	
	String INTENT_PROP_REQUESTER = "REQUESTER";
	
	String INTENT_PROP_USER_SELECTED_TIME_CODE = "SELECTED_TIMECODE";
	
	String INTENT_VAL_TIME_SENSE_PLANNER_TAB = "PLANNER";
	
	String INTENT_VAL_WORLD_CLOCK_TAB = "WORLD_CLOCK";
	
	String INTENT_VAL_FRAGMENT_CALL_LOG = "CALL_LOG";
	
	String SHARED_CALL_INFO = "CallInfo";
}
