package com.san.timesense.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.san.timesense.R;

public class DialFragment  extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, 
							 ViewGroup container,
							 Bundle savedInstanceState) {
		
		if (container != null)
			container.removeAllViews();
		
		View view = inflater.inflate(R.layout.layout_dial_pad, null);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT, 
							LinearLayout.LayoutParams.MATCH_PARENT);
		view.setLayoutParams(layoutParams);
		
		getActivity().getActionBar().setTitle("DIAL");
		
		Button one = (Button) view.findViewById(R.id.one);
		
		return view;
	}
}
