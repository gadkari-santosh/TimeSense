package com.handyapps.timesense.service;

import static com.handyapps.timesense.constant.AppContant.SHARED_PREF_NAME;

import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.handyapps.timesense.R;
import com.handyapps.timesense.dataobjects.CallInfo;
import com.handyapps.timesense.dataobjects.TimeCode;
import com.handyapps.timesense.db.CallLogDAO;

public class ToastService {

	public static AtomicBoolean isCallEnded = new AtomicBoolean(false);
	
	@SuppressLint("NewApi")
	public static void showOutCall(Context context, String phoneNumber) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View layout = inflater.inflate(R.layout.layout_toast_outgoing_call, null);
		
		TextView tz = (TextView) layout.findViewById(R.id.CountryAndTz);
		TextView time = (TextView) layout.findViewById(R.id.time);
		TextView date = (TextView) layout.findViewById(R.id.date);
		
		TextView name = (TextView) layout.findViewById(R.id.name);
		TextView phone = (TextView) layout.findViewById(R.id.phoneNumber);
		
		TimeCode code = TimeService.getInstance().getTimeCodeByPhoneNumber(phoneNumber);
		
		phone.setText(phoneNumber);
		
		if (code != null) {
		
			time.setText( TimeService.getInstance().getTime(code) );
			date.setText( TimeService.getInstance().getDate(code) );
			
		}
		
		Toast toast = new Toast(context.getApplicationContext());
		toast.setGravity(Gravity.BOTTOM, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		
		toast.setView(layout);
		toast.show();
	}
	
	@SuppressLint("NewApi")
	public static void showTimezoneChange(Context context, String phoneNumber) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View layout = inflater.inflate(R.layout.layout_timezone_update, null);
		
		TextView tz = (TextView) layout.findViewById(R.id.CountryAndTz);
		TextView time = (TextView) layout.findViewById(R.id.time);
		TextView date = (TextView) layout.findViewById(R.id.date);
		
		TextView name = (TextView) layout.findViewById(R.id.name);
		TextView phone = (TextView) layout.findViewById(R.id.phoneNumber);
		
		TimeCode code = TimeService.getInstance().getTimeCodeByPhoneNumber(phoneNumber);
		
		phone.setText(phoneNumber);
		
		if (code != null) {
		
			time.setText( TimeService.getInstance().getTime(code) );
			date.setText( TimeService.getInstance().getDate(code) );
			
		}
		
		Toast toast = new Toast(context.getApplicationContext());
		toast.setGravity(Gravity.BOTTOM, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		
		toast.setView(layout);
		toast.show();
	}
	
	@SuppressLint("NewApi")
	public static void showInCall(Context context, String phoneNumber) {
		Gson gson = new Gson();
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View layout = inflater.inflate(R.layout.layout_toast_incoming_call, null);
		
		TextView tz = (TextView) layout.findViewById(R.id.CountryAndTz);
		TextView country = (TextView) layout.findViewById(R.id.Country);
		TextView time = (TextView) layout.findViewById(R.id.time);
		TextView date = (TextView) layout.findViewById(R.id.date);
		
		ImageView kaal =(ImageView) layout.findViewById(R.id.imgViewKaal);
		
		TimeSenseUsersService service = TimeSenseUsersService.getInstance();
		service.init(context);
		
		CallLogDAO dao = new CallLogDAO(context);
		
		Map<String, String> timeSenseUserTimeZones = service.getTimeSenseUserTimeZones();
		
		TimeService timeService = TimeService.getInstance();
		TimeCode timeCode = null;
		
		if (timeSenseUserTimeZones != null && timeSenseUserTimeZones.get(phoneNumber) != null) {
			timeCode = timeService.getTimeCodeByTimeZone(timeSenseUserTimeZones.get(phoneNumber));
		}
		
		if (timeCode == null)
			timeCode = timeService.getTimeCodeByPhoneNumber(phoneNumber);
		
		String code = timeCode.getCountry();
		
		if (code != null && !"".equals(code.trim())) {
			String time2 = timeService.getTime(timeCode);
			
			if (time2 != null && !"".equalsIgnoreCase(time2)) {
				time.setText( time2 );
				date.setText( timeService.getDate(timeCode) );
				
				tz.setText(timeCode.getTimeZone());
				country.setText(timeCode.getCountry());
				
				TimeService.getInstance().setKaalPic(kaal, timeService.getKaal(timeCode));
				DisplayMetrics dimension = new DisplayMetrics();
				
				DisplayMetrics metrics = context.getResources().getDisplayMetrics();
				int height = metrics.heightPixels;
				
				CallInfo callInfo = new CallInfo();
				
				callInfo.setRecipientCountry(timeCode.getCountry());
				callInfo.setRemoteTime( TimeService.getInstance().getTime(timeCode) );
				callInfo.setRemoteDate( TimeService.getInstance().getDate(timeCode) );
				callInfo.setRecipientTimeZone( timeCode.getTimeZone() );
				
				callInfo.setHomeTimeZone( TimeZone.getDefault().getDisplayName() );
				callInfo.setHomeCountry( SettingsService.getInstance().getSettings().getHomeCountry() ) ;
				
				Date today = new Date();
				
				callInfo.setLocalTime( TimeService.getInstance().getTime(today)  );
				callInfo.setLocalDate( TimeService.getInstance().getDate(today)  );
				
				callInfo.setPhoneNumber(phoneNumber);
				
				String json = gson.toJson(callInfo);
				
				SharedPreferences sp=context.getSharedPreferences(SHARED_PREF_NAME, 1);
				Editor edit = sp.edit();
				edit.putString("CallInfo", json);
				edit.commit();

				for (int i=0; i<3; i++) {
					
					if (isCallEnded.get())
						break;
					
					Toast toast = new Toast(context);
					toast.setDuration(Toast.LENGTH_LONG);
					toast.setGravity(Gravity.FILL_HORIZONTAL, 0, (int) (height-height*0.1));
					
					toast.setView(layout);
					toast.show();
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}