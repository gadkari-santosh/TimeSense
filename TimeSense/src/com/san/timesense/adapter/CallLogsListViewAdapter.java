package com.san.timesense.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.san.timesense.R;
import com.san.timesense.constant.Contact;
import com.san.timesense.dto.CallInfo;
import com.san.timesense.dto.TimeCode;
import com.san.timesense.service.TimeService;

public class CallLogsListViewAdapter extends ArrayAdapter<CallInfo> {
	
	private List<CallInfo> callInfos = null;

	private Context context = null;
	
	private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
	private SimpleDateFormat dayFormat = new SimpleDateFormat("MMM dd''yy");
	
	
	public CallLogsListViewAdapter(Context context, List<CallInfo> callInfos) {
		super(context, R.layout.listview_contact, callInfos);
		this.context = context;
		this.callInfos = callInfos;
	}
	
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		Date remoteDateTime = null;
		
		if (view == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.listview_call_log, null);
		}
		
		try {
			TextView displayName = (TextView) view.findViewById(R.id.contact);
			ImageView globImage = (ImageView) view.findViewById(R.id.globPic);
			TextView phNumber = (TextView) view.findViewById(R.id.number);
			TextView localTime = (TextView) view.findViewById(R.id.localTime);
			TextView localDate = (TextView) view.findViewById(R.id.localDate);
			TextView duration = (TextView) view.findViewById(R.id.duration);
			TextView remoteTime = (TextView) view.findViewById(R.id.remoteTime);
			TextView remoteDate = (TextView) view.findViewById(R.id.remoteDate);
			
			ImageView pic = (ImageView) view.findViewById(R.id.pic);
			
			View findViewById = view.findViewById(R.id.callLogRow);
			findViewById.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_CALL);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					CallInfo callInfo = callInfos.get(position);
					intent.setData(Uri.parse("tel:" + callInfo.getPhoneNumber()));
					context.startActivity(intent);
				}
			});
			
			CallInfo callInfo = callInfos.get(position);
			
			displayName.setText(callInfo.getName());
			phNumber.setText(callInfo.getPhoneNumber());
			
			localTime.setText(timeFormat.format(callInfo.getLocalTime()));
			localDate.setText(dayFormat.format(callInfo.getLocalTime()));
			duration.setText(callInfo.getDuration());
			
			TimeService tService = TimeService.getInstance();
			
			TimeCode timeCode = tService.getTimeCodeByPhoneNumber(callInfo.getPhoneNumber());
			
			String country = timeCode.getCountry();
			
			if (country != null && !"".equals(country.trim())) {
				phNumber.setText(callInfo.getPhoneNumber()+","+country);
			}
			
			if (timeCode != null) {
				globImage.setVisibility(ImageView.VISIBLE);
				
				remoteDate.setText(tService.getDate(timeCode, callInfo.getLocalTime()));
				remoteTime.setText(tService.getTime(timeCode, callInfo.getLocalTime()));
			} else {
				remoteDate.setText("");
				remoteTime.setText("");
				globImage.setVisibility(ImageView.INVISIBLE);
			}
			
			switch ( callInfo.getCallType() ) {
				
				case CallLog.Calls.INCOMING_TYPE:
					pic.setImageResource(R.drawable.in_call);
					break;
					
				case CallLog.Calls.OUTGOING_TYPE:
					pic.setImageResource(R.drawable.out_call);
					break;
					
				case CallLog.Calls.MISSED_TYPE:
					pic.setImageResource(R.drawable.mis_call);
					break;
			}
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			Toast.makeText(getContext(), "Error#"+throwable, 100).show();
		}
		return view;
	}
}
