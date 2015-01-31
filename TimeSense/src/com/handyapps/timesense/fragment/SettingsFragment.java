package com.handyapps.timesense.fragment;

import java.util.List;
import java.util.TimeZone;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
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
            System.out.println("*** My thread is now configured to allow connection");
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
		
		ConnectivityManager connMgr = (ConnectivityManager) 
		        getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		
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
		
//		Button send = (Button) view.findViewById(R.id.buttonSend);
//		send.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				DefaultHttpClient httpClient = new DefaultHttpClient();
//		        
//			     // Create new getRequest with below mentioned URL
//			        HttpPost post = new HttpPost("https://android.googleapis.com/gcm/send");
//			        post.setHeader("Authorization", "key=AIzaSyCJoBtvp4xa6dPtub576cF9_IYW8Ukm3ME");
//			        post.setHeader("Content-Type",
//			                "application/x-www-form-urlencoded;charset=UTF-8");
//			        
//			        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
//
//			        formparams.add(new BasicNameValuePair("registration_id","APA91bG1l2Ers1H8mzJJs5P3t7guK5ZD3cpU1NvOhLj80IS6h0ao1T5DjZ5BILAdZ3nqlrsy98HX8e9IMWshrfkJn-t7wzoaiiIL381cY88CxNNCWCZaCnzqqhp4Z3vo9W0DhS_ir44s4yWGB8ytatNyUNry_RR_iQ"));
//			        formparams.add(new BasicNameValuePair("data.message", "Message Test - Time Zone Updated"));
//			        formparams.add(new BasicNameValuePair("data", "Message Data"));
//			        
//			        String payload = "{ \"data\": { \"score\": \"5x1\",	\"time\": \"15:10\"	},\"registration_ids\": [\"%s\"]}";
//			        
//			        try {
//						//post.setEntity(new StringEntity(String.format(payload, Cache.getInstance().getFirst())));
//			        	post.setEntity(new UrlEncodedFormEntity(formparams, "utf-8"));
//						
//						HttpResponse response = httpClient.execute(post);
//						
//						BufferedReader rd = new BufferedReader(
//				                new InputStreamReader(response.getEntity().getContent()));
//				 
//					StringBuffer result = new StringBuffer();
//					String line = "";
//					while ((line = rd.readLine()) != null) {
//						result.append(line);
//					}
//					
//					System.out.println(result);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//			}
//		});

		
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
				
				seekFrom.setProgress(settingService.getSettings().getTimePlannerRangeFrom());
				seekTo.setProgress(settingService.getSettings().getTimePlannerRangeTo());
				
				Button ok = (Button) alertDialog.findViewById(R.id.buttonOk);
				Button cancel = (Button) alertDialog.findViewById(R.id.buttonCancel);
				
				seekFrom.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// NOP
					}
					
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// NOP
					}
					
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress,
							boolean fromUser) {
						txtViewFrom.setText(String.format("Time From %s:00",progress));
					}
				});
				
				seekTo.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						if (seekFrom.getProgress() > seekBar.getProgress()) {
							Toast makeText = Toast.makeText(SettingsFragment.this.getActivity(),
									"The value should be greater than lower range.", Toast.LENGTH_SHORT);
							makeText.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
							makeText.show();
						} else {
							txtViewRangeTo.setText(String.format("Time From %s:00",seekBar.getProgress()));
						}
					}
					
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// NOP
					}
					
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress,
							boolean fromUser) {
						
						
					}
				});
				
				ok.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						if (seekFrom.getProgress() > seekTo.getProgress()) {
							Toast makeText = Toast.makeText(SettingsFragment.this.getActivity(),
									"The value should be greater than lower range.", Toast.LENGTH_SHORT);
							makeText.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
							makeText.show();
						} else {
							settingService.getSettings().setTimePlannerRangeFrom(seekFrom.getProgress());
							settingService.getSettings().setTimePlannerRangeTo(seekTo.getProgress());
							settingService.saveSettings();
							
							alertDialog.dismiss();
						}
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