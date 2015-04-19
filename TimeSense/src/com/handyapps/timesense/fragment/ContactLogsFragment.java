package com.handyapps.timesense.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.handyapps.timesense.R;
import com.handyapps.timesense.adapter.CallLogsListViewAdapter;
import com.handyapps.timesense.dataobjects.CallInfo;
import com.handyapps.timesense.service.CallLogService;
import com.handyapps.timesense.util.ResourceUtils;

public class ContactLogsFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		container.removeAllViews();
		
		View view = inflater.inflate(R.layout.layout_logs, null);

		ListView listView = (ListView) view.findViewById(R.id.list_view);
		
		getActivity().getActionBar().setTitle("LOG");

		final List<CallInfo> callInfos = CallLogService.getInstance().getAllCallLogs(getActivity().getApplicationContext());
		
		final CallLogsListViewAdapter callLogViewAdapter = new CallLogsListViewAdapter(
				getActivity().getApplicationContext(), new ArrayList<CallInfo>(callInfos));
		listView.setAdapter(callLogViewAdapter);
		
		final Button butAll = (Button) view.findViewById(R.id.butAll);
		final Button butMissed = (Button) view.findViewById(R.id.butMissed);
		
		butAll.setBackgroundColor(ResourceUtils.getColor(getActivity(), R.color.DeepSkyBlue));
		butAll.setTextColor(ResourceUtils.getColor(getActivity(), R.color.White));
		
		butAll.performClick();
		
		butAll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				butAll.setSelected(true);
				butAll.setBackgroundColor(ResourceUtils.getColor(getActivity(), R.color.DeepSkyBlue));
				butAll.setTextColor(ResourceUtils.getColor(getActivity(), R.color.White));
				
				butMissed.setBackground(getResources().getDrawable(R.xml.rect_corner));
				butMissed.setTextColor(ResourceUtils.getColor(getActivity(), R.color.DeepSkyBlue));
				
				callLogViewAdapter.clear();
				callLogViewAdapter.addAll(new ArrayList<CallInfo>(callInfos));
				callLogViewAdapter.setNotifyOnChange(true);
			}
		});
		
		butMissed.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				butMissed.setBackgroundColor(ResourceUtils.getColor(getActivity(), R.color.DeepSkyBlue));
				butMissed.setTextColor(ResourceUtils.getColor(getActivity(), R.color.White));
				
				butAll.setBackground(getResources().getDrawable(R.xml.rect_corner));
				butAll.setTextColor(ResourceUtils.getColor(getActivity(), R.color.DeepSkyBlue));
				
				List<CallInfo> missed = new ArrayList<CallInfo>();
				for (CallInfo callInfo : callInfos) {
					if (callInfo.getCallType() == CallLog.Calls.MISSED_TYPE) {
						missed.add(callInfo);
					}
				}
				
				callLogViewAdapter.clear();
				callLogViewAdapter.addAll(new ArrayList<CallInfo>(missed));
				callLogViewAdapter.setNotifyOnChange(true);
			}
		});
		
		return view;
	}
}
