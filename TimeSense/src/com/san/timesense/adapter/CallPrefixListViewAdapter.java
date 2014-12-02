package com.san.timesense.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.san.timesense.R;
import com.san.timesense.dto.CallPrefix;

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
		
		CallPrefix callPrefix = callPrefixes.get(position);
		
		TextView txtVwPrefix = (TextView) view.findViewById(R.id.txtVwPrefix);
		TextView txtVwPrefixComment = (TextView) view.findViewById(R.id.txtVwPrefixComment);
		
		txtVwPrefix.setText(callPrefix.getPrefix());
		txtVwPrefixComment.setText(callPrefix.getComment());
		
		return view;
	}
}
