package com.handyapps.timesense.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handyapps.timesense.R;

public class HelpFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
							 ViewGroup container,
							 Bundle savedInstanceState) {
		
		if (container != null)
			container.removeAllViews();
		
		View view = inflater.inflate(R.layout.layout_help, null);
		
		getActivity().getActionBar().setTitle("HELP");
		
		return view;
	}
}
