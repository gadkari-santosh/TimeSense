package com.handyapps.timesense.adapter;

import static com.handyapps.timesense.constant.AppContant.SHARED_CALL_INFO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.handyapps.timesense.R;
import com.handyapps.timesense.dataobjects.CallInfo;
import com.handyapps.timesense.fragment.CallDetailsFragment;
import com.handyapps.timesense.util.Utils;

public class CallLogsListViewAdapter extends ArrayAdapter<CallInfo> {
	
	private List<CallInfo> callInfos = null;

	private Context context = null;
	
	private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
	private SimpleDateFormat dayFormat = new SimpleDateFormat("d MMM yyyy");
	
	
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
			final LinearLayout layoutCallInfo = (LinearLayout) view.findViewById(R.id.layoutCallInfo);
			ImageButton imgButCallInfo = (ImageButton) view.findViewById(R.id.imgButCallInfo);
			
//			ImageView globImage = (ImageView) view.findViewById(R.id.globPic);
			TextView phNumber = (TextView) view.findViewById(R.id.number);
			TextView txtViewCallTime = (TextView) view.findViewById(R.id.txtViewCallTime);
			
//			TextView localTime = (TextView) view.findViewById(R.id.localTime);
//			TextView localDate = (TextView) view.findViewById(R.id.localDate);
//			TextView duration = (TextView) view.findViewById(R.id.duration);
//			TextView remoteTime = (TextView) view.findViewById(R.id.remoteTime);
//			TextView remoteDate = (TextView) view.findViewById(R.id.remoteDate);
			
			ImageView pic = (ImageView) view.findViewById(R.id.pic);
			
			final CallInfo callInfo = callInfos.get(position);
			
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
			
			imgButCallInfo.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					layoutCallInfo.callOnClick();
				}
			});
			
			layoutCallInfo.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					Gson gson = new Gson ();
					
					Intent intent = new Intent(context, CallDetailsFragment.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra(SHARED_CALL_INFO, gson.toJson(callInfo));
					context.startActivity(intent);
					
					CallLogsListViewAdapter.this.notifyDataSetChanged();
				}
			});
			
			if ("".equalsIgnoreCase(callInfo.getName()))
				displayName.setText("Unknown");
			else 
				displayName.setText(callInfo.getName());
			phNumber.setText(callInfo.getPhoneNumber());
			
			Date localTime = callInfo.getLocalDateTime();
			
			if (Utils.isToday(localTime)) {
				txtViewCallTime.setText("Today");
			} else if (Utils.isYesterday(localTime)) {
				txtViewCallTime.setText("Yesterday");
			} else {
				String callTime = dayFormat.format(localTime);
				txtViewCallTime.setText(callTime);
			}
			
			
//			if (callInfo.getLocalTime() != null) {
//				localTime.setText(timeFormat.format(callInfo.getLocalTime()));
//				localDate.setText(dayFormat.format(callInfo.getLocalTime()));
//			}
//			duration.setText(callInfo.getDuration());
//			
//			if (callInfo.getRemoteTime() != null) {
//				globImage.setVisibility(ImageView.VISIBLE);
//				
//				String time = timeFormat.format(callInfo.getRemoteTime());
//				String date = dayFormat.format(callInfo.getRemoteTime());
//				
//				remoteTime.setText(time);
//				remoteDate.setText(date);
//				
//				phNumber.setText( callInfo.getPhoneNumber() + "," + callInfo.getCountry() );
//				
//			} else {
//				globImage.setVisibility(ImageView.INVISIBLE);
//			}
			
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
