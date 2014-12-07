package com.san.timesense.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.san.timesense.R;
import com.san.timesense.dto.TimeCode;
import com.san.timesense.service.TimeService;

public class TimeZoneListViewAdapter extends ArrayAdapter<TimeCode> {

	private List<TimeCode> timeCodes = null;

	private Context context = null;
	
	public TimeZoneListViewAdapter(Context context, List<TimeCode> timeCodes) {
		super(context, R.layout.listview_timezone, timeCodes);
		this.context = context;
		this.timeCodes = timeCodes;
	}
	
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		if (view == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.listview_timezone, null);
		}
		
		if (timeCodes == null)
			return null;
		
		final TimeCode timeCode = timeCodes.get(position);
		
		final LinearLayout layout = (LinearLayout) view;
		layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((CheckBox)layout.findViewById(R.id.chkBoxSelect)).performClick();
			}
		});
		
		TextView txtVwCountry = (TextView) view.findViewById(R.id.txtViewCountry);
		TextView txtVwTz      = (TextView) view.findViewById(R.id.txtViewTimeZone);
		TextView txtVwDCode   = (TextView) view.findViewById(R.id.txtViewDialCode);
		
		ImageView imgVwFlag   = (ImageView) view.findViewById(R.id.imgVwFlag);
		
		CheckBox box = (CheckBox) view.findViewById(R.id.chkBoxSelect);
		box.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (((CheckBox)v).isChecked()) {
					timeCode.setSelect(true);
				} else {
					timeCode.setSelect(false);
				}
			}
		});
		
		txtVwCountry.setText(timeCode.getCountry());
		txtVwTz.setText(timeCode.getTimeZone());
		txtVwDCode.setText(String.format(" (+%s) ", timeCode.getDialCode()));
		
		imgVwFlag.setImageResource(R.drawable.no_flag);
		
		if (timeCode.isSelect()) {
			box.setChecked(true);
		} else {
			box.setChecked(false);
		}
		
		TimeService.getInstance().setCountryFlag(imgVwFlag,timeCode.getCountry());
		
		return view;
	}
}