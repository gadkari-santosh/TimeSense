package com.handyapps.timesense.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.handyapps.timesense.R;
import com.handyapps.timesense.dataobjects.CallPrefix;

public class CallPrefixListViewAdapter extends ArrayAdapter<CallPrefix> {
	
	private List<CallPrefix> callPrefixes = null;

	private Context context = null;
	
	public CallPrefixListViewAdapter(Context context, List<CallPrefix> callPrefixes) {
		super(context, R.layout.listview_call_prefix, callPrefixes);
		this.context = context;
		this.callPrefixes = callPrefixes;
	}
	
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		if (view == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.listview_call_prefix, null);
		}
		
		final CallPrefix callPrefix = callPrefixes.get(position);
		
		TextView txtVwPrefix = (TextView) view.findViewById(R.id.txtVwPrefix);
		TextView txtVwPrefixComment = (TextView) view.findViewById(R.id.txtVwPrefixComment);
		
		ImageButton buttonClose = (ImageButton) view.findViewById(R.id.buttonDiscardPrefix);
		
		buttonClose.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				callPrefixes.remove(callPrefix);
				
				CallPrefixListViewAdapter.this.notifyDataSetChanged();
			}
		});
		
		txtVwPrefix.setText(callPrefix.getPrefix());
		txtVwPrefixComment.setText(callPrefix.getComment());
		
		return view;
	}
}
