package com.san.timesense.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.san.timesense.R;
import com.san.timesense.dto.TimeCode;

public class ToastService {

	@SuppressLint("NewApi")
	public static void showOutCall(Context context, String phoneNumber) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View layout = inflater.inflate(R.layout.layout_toast_outgoing_call, null);
		
		TextView tz = (TextView) layout.findViewById(R.id.CountryAndTz);
		TextView time = (TextView) layout.findViewById(R.id.time);
		TextView date = (TextView) layout.findViewById(R.id.date);
		
		TextView name = (TextView) layout.findViewById(R.id.name);
		TextView phone = (TextView) layout.findViewById(R.id.phoneNumber);
		
		String code = TimeService.getInstance().getCountryCode(phoneNumber);
		
		phone.setText(phoneNumber);
		
		if (code != null) {
		
			time.setText( TimeService.getInstance().getTime(code) );
			date.setText( TimeService.getInstance().getDate(code) );
			
			TimeService.getInstance().getCountry(code);
		}
		
		
			Toast toast = new Toast(context.getApplicationContext());
			toast.setGravity(Gravity.BOTTOM, 0, 0);
			toast.setDuration(Toast.LENGTH_LONG);
			
			toast.setView(layout);
			toast.show();
	}
	
	@SuppressLint("NewApi")
	public static void showInCall(Context context, String phoneNumber) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View layout = inflater.inflate(R.layout.layout_toast_incoming_call, null);
		
		TextView tz = (TextView) layout.findViewById(R.id.CountryAndTz);
		TextView country = (TextView) layout.findViewById(R.id.Country);
		TextView time = (TextView) layout.findViewById(R.id.time);
		TextView date = (TextView) layout.findViewById(R.id.date);
		
		ImageView kaal =(ImageView) layout.findViewById(R.id.imgViewKaal);
		
		TimeService timeService = TimeService.getInstance();
		String code = timeService.getCountryCode(phoneNumber);
		
		TimeCode timeCode = timeService.getTimeCode(phoneNumber);
		
		if (code != null && !"".equals(code.trim())) {
			String time2 = timeService.getTime(code);
			
			if (time2 != null && !"".equalsIgnoreCase(time2)) {
				time.setText( time2 );
				date.setText( timeService.getDate(code) );
				
				tz.setText(timeCode.getTimeZone());
				country.setText(timeCode.getCountry());
				
				TimeService.getInstance().setKaalPic(kaal, timeService.getKaal(code));
				DisplayMetrics dimension = new DisplayMetrics();
				
				DisplayMetrics metrics = context.getResources().getDisplayMetrics();
				int height = metrics.heightPixels;
				
				for (int i=0; i<5; i++) {
					Toast toast = new Toast(context);
					toast.setDuration(Toast.LENGTH_LONG);
					toast.setGravity(Gravity.FILL_HORIZONTAL, 0, (int) (height-height*0.1));
					
					toast.setView(layout);
					toast.show();
					
				}
			}
		}
	}
}
