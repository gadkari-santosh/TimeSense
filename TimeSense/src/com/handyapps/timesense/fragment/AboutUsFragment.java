package com.handyapps.timesense.fragment;

import com.handyapps.timesense.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AboutUsFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, 
							 ViewGroup container,
							 Bundle savedInstanceState) {
		
		if (container != null)
			container.removeAllViews();
		
		View view = inflater.inflate(R.layout.layout_about_us, null);
		
		getActivity().getActionBar().setTitle("ABOUT US");
		
		return view;
	}
}
