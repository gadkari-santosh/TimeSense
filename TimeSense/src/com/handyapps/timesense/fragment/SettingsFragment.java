package com.handyapps.timesense.fragment;

import java.util.List;
import java.util.TimeZone;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.handyapps.timesense.R;
import com.handyapps.timesense.activity.TimeZoneUpdateActivity;
import com.handyapps.timesense.adapter.CallPrefixListViewAdapter;
import com.handyapps.timesense.dataobjects.CallPrefix;
import com.handyapps.timesense.dataobjects.Settings;
import com.handyapps.timesense.service.SettingsService;

public class SettingsFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
							 ViewGroup container,
							 Bundle savedInstanceState) {
		
		if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
	    }
		 
		if (container != null)
			container.removeAllViews();
		
		final Settings settings = SettingsService.getInstance().getSettings();
		
		final SettingsService settingService = SettingsService.getInstance();
	
		View view = inflater.inflate(R.layout.layout_settings, null);
		
		boolean isWifi = false;
		boolean isMobNetwork = false;
		
		String networkType = null;
		
		getActivity().getActionBar().setTitle("SETTINGS");
		
		ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		
		TelephonyManager manager = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
		String carrierName = manager.getNetworkOperatorName();
		
		NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		isWifi = wifiNetworkInfo.isConnected();
		
		NetworkInfo mobNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		isMobNetwork = mobNetworkInfo.isConnected();
		
		TextView txtViewConnection = (TextView) view.findViewById(R.id.txtViewConnection);
		TextView txtViewMobileCarrier = (TextView) view.findViewById(R.id.txtViewMobileCarrier);
		TextView txtViewTimeZone = (TextView) view.findViewById(R.id.txtViewTimeZone);
		
		Switch switchCallSense = (Switch) view.findViewById(R.id.toggleInterruptCall);
		
		if (settingService.getSettings().isEnableCallSense()) {
			switchCallSense.setChecked(true);
		} else {
			switchCallSense.setChecked(false);
		}
		
		NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();
		 
		if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
			txtViewConnection.setText(activeNetworkInfo.getTypeName());
			
			if (activeNetworkInfo.isRoaming()) {
				txtViewConnection.setText(activeNetworkInfo.getTypeName()+", Roaming");
			}
		} else {
			txtViewConnection.setText("Not Connected");
		}
		
		if (carrierName != null && !"".equals(carrierName)) {
			txtViewMobileCarrier.setText(carrierName);
		} else {
			txtViewMobileCarrier.setText("No Network");
		}
		
		
		txtViewTimeZone.setText( TimeZone.getDefault().getDisplayName() );
		
		LinearLayout buttonCallPrefix = (LinearLayout) view.findViewById(R.id.buttonCallPrefix);
		LinearLayout buttonCallSense = (LinearLayout) view.findViewById(R.id.buttonCallSense);
		LinearLayout butTimeZoneUpdate = (LinearLayout) view.findViewById(R.id.butTimeZoneUpdate);
		
		switchCallSense.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			 
			   @Override
			   public void onCheckedChanged(CompoundButton buttonView,
			     boolean isChecked) {
			 
				    if(isChecked){
				    	settingService.getSettings().setEnableCallSense(true);
				    	settingService.saveSettings();
				    }else{
				    	settingService.getSettings().setEnableCallSense(false);
				    	settingService.saveSettings();
				    }
			   }
		});
		
		butTimeZoneUpdate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingsFragment.this.getActivity(), TimeZoneUpdateActivity.class);
				intent.putExtra("time_zone", TimeZone.getDefault().getDisplayName());
				
				SettingsFragment.this.getActivity().startActivity(intent);
			}
		});
		
		buttonCallSense.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final Dialog alertDialog = new Dialog(SettingsFragment.this.getActivity());
				alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				alertDialog.setContentView(R.layout.layout_range_selector);
				alertDialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				alertDialog.show();	
				

				final TextView txtViewFrom = (TextView) alertDialog.findViewById(R.id.txtViewRangeFrom);
				final TextView txtViewRangeTo = (TextView) alertDialog.findViewById(R.id.txtViewRangeTo);
				
				final SeekBar seekFrom = (SeekBar) alertDialog.findViewById(R.id.seekFrom);
				final SeekBar seekTo = (SeekBar) alertDialog.findViewById(R.id.seekTo);
				
				seekFrom.setProgress(settingService.getSettings().getCallSenseFrom());
				seekTo.setProgress(settingService.getSettings().getCallSenseTo());
				
				txtViewRangeTo.setText(String.format("Time(24HR) To %s:00",seekTo.getProgress()));
				txtViewFrom.setText(String.format("Time(24HR) From %s:00",seekFrom.getProgress()));
				
				Button ok = (Button) alertDialog.findViewById(R.id.buttonOk);
				Button cancel = (Button) alertDialog.findViewById(R.id.buttonCancel);
				
				seekFrom.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						txtViewFrom.setText(String.format("Time(24HR) From %s:00",seekBar.getProgress()));
						settings.setCallSenseFrom(seekBar.getProgress());
						settingService.saveSettings();
					}
					
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// NOP
					}
					
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress,
							boolean fromUser) {
						txtViewFrom.setText(String.format("Time(24HR) From %s:00",seekBar.getProgress()));
					}
				});
				
				seekTo.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						txtViewRangeTo.setText(String.format("Time(24HR) To %s:00",seekBar.getProgress()));
						settingService.getSettings().setCallSenseTo(seekBar.getProgress());
						settingService.saveSettings();
					}
					
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// NOP
					}
					
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress,
							boolean fromUser) {
						txtViewRangeTo.setText(String.format("Time(24HR) To %s:00",seekBar.getProgress()));
					}
				});
				
				ok.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						settingService.getSettings().setCallSenseFrom(seekFrom.getProgress());
						settingService.getSettings().setCallSenseTo(seekTo.getProgress());
						settingService.saveSettings();
							
						alertDialog.dismiss();
					}
				});
				
				cancel.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						alertDialog.dismiss();
					}
				});
			}
		});

		
		buttonCallPrefix.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final Dialog alertDialog = new Dialog(SettingsFragment.this.getActivity());
				alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				
				LayoutInflater vi = (LayoutInflater) SettingsFragment.this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View view = vi.inflate(R.layout.layout_callprefix, null);
				alertDialog.setContentView(view);
				
				final List<CallPrefix> callPrefixs = settings.getCallPrefixs();
				
				final CallPrefixListViewAdapter callPrefixListViewAdapter = new CallPrefixListViewAdapter(SettingsFragment.this.getActivity(), callPrefixs);
				
				final ListView listVwCallPrefix = (ListView) view.findViewById(R.id.listVwPrefix);
				listVwCallPrefix.setAdapter(callPrefixListViewAdapter);
				alertDialog.show();
				
				Button buttonclose = (Button) view.findViewById(R.id.buttonclose);
				buttonclose.setOnClickListener( new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						alertDialog.dismiss();
					}
				});
				
				Button buttonAddPrefix = (Button) view.findViewById(R.id.buttonAddPrefix);
				buttonAddPrefix.setOnClickListener( new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						final Dialog alertDialog = new Dialog(SettingsFragment.this.getActivity());
						alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
						alertDialog.setContentView(R.layout.layout_callprefix_dialog);
						alertDialog.show();
						
						final Button butSumbit = (Button) alertDialog.findViewById(R.id.butSubmit);
						final Button butCancel = (Button) alertDialog.findViewById(R.id.butCancel);
						
						EditText edTxtPrefix = (EditText) alertDialog.findViewById(R.id.edTxtPrefix);
						edTxtPrefix.requestFocus();
						alertDialog.getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
						
						
						butCancel.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								alertDialog.dismiss();
							}
						});
						
						butSumbit.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								EditText edTxtPrefix = (EditText) alertDialog.findViewById(R.id.edTxtPrefix);
								EditText edTxtComments = (EditText) alertDialog.findViewById(R.id.edTxtComments);
								
								String prefix = edTxtPrefix.getText().toString();
								String comments = edTxtComments.getText().toString();
								
								if ("".equalsIgnoreCase(prefix.trim()) || "".equalsIgnoreCase(comments.trim())) {
									Toast.makeText(getActivity(), "Can not add empty prefix or comment", Toast.LENGTH_LONG).show();
									return;
								}
								
								callPrefixs.add(new CallPrefix(comments,prefix));
								callPrefixListViewAdapter.notifyDataSetChanged();
								
								settings.setCallPrefixs(callPrefixs);
								SettingsService.getInstance().saveSettings(settings);
								
								alertDialog.dismiss();
							}
						});
					}
				});
						
			}
		});
		
		return view;
	}
}