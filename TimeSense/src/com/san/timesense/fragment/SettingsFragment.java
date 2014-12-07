package com.san.timesense.fragment;

import java.util.List;
import java.util.TimeZone;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.san.timesense.R;
import com.san.timesense.adapter.CallPrefixListViewAdapter;
import com.san.timesense.dto.CallPrefix;
import com.san.timesense.dto.Settings;
import com.san.timesense.service.SettingsService;

public class SettingsFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
							 ViewGroup container,
							 Bundle savedInstanceState) {
		
		if (container != null)
			container.removeAllViews();
		
		final Settings settings = SettingsService.getInstance().getSettings();
	
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
		
		buttonCallSense.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final Dialog alertDialog = new Dialog(SettingsFragment.this.getActivity());
				alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				alertDialog.setContentView(R.layout.layout_range_selector);
				alertDialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				alertDialog.show();	
				
//				TimeZoneFragment settingsFragment = new TimeZoneFragment();
//				FragmentTransaction fragmentTransaction = fm.beginTransaction();
//				fragmentTransaction.add(R.id.fragment_place, settingsFragment);
//				fragmentTransaction.commit();
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
		
		
//		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//				LinearLayout.LayoutParams.FILL_PARENT, 
//				LinearLayout.LayoutParams.FILL_PARENT);
//		view.setLayoutParams(layoutParams);
//		
//		final Button butFromTime = (Button) view.findViewById(R.id.butFrmTime);
//		final Button butToTime = (Button) view.findViewById(R.id.butToTime);
//		final Button butAddPrefix = (Button) view.findViewById(R.id.butAddPrefix);
//		final Button butSetStatus = (Button) view.findViewById(R.id.butSetStatus);
//		
//		final ToggleButton toggleInterruptCall = (ToggleButton) view.findViewById(R.id.toggleInterruptCall);
//		
//		final TextView txtVwStatus = (TextView) view.findViewById(R.id.txtVwStatus);
//		
//		underlineText(butAddPrefix, butAddPrefix.getText().toString());
//		underlineText(butFromTime, settings.getFromTime());
//		underlineText(butToTime, settings.getToTime());
//		underlineText(butSetStatus, butSetStatus.getText().toString());
//		
//		butSetStatus.setText(settings.getStatus());
//		toggleInterruptCall.setChecked(settings.isInterceptCall());
//		
//		final List<CallPrefix> callPrefixs = settings.getCallPrefixs();
//		final CallPrefixListViewAdapter callPrefixListViewAdapter = new CallPrefixListViewAdapter(getActivity().getApplicationContext(), callPrefixs);
//		
//		final ListView listVwCallPrefix = (ListView) view.findViewById(R.id.listVwPrefix);
//		listVwCallPrefix.setAdapter(callPrefixListViewAdapter);
//		
//		butSetStatus.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				final Dialog alertDialog = new Dialog(SettingsFragment.this.getActivity());
//				alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//				alertDialog.setContentView(R.layout.layout_status_dialog);
//				alertDialog.show();
//				
//				final Button butSumbit = (Button) alertDialog.findViewById(R.id.butSubmit);
//				final Button butCancel = (Button) alertDialog.findViewById(R.id.butCancel);
//				
//				butCancel.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						alertDialog.dismiss();
//					}
//				});
//				
//				butSumbit.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						EditText edTxtPrefix = (EditText) alertDialog.findViewById(R.id.edTxtStatus);
//						
//						txtVwStatus.setText(edTxtPrefix.getText().toString());
//						
//						settings.setStatus(edTxtPrefix.getText().toString());
//						SettingsService.getInstance().saveSettings(settings);
//						
//						alertDialog.dismiss();
//					}
//				});
//			}
//		});
//		
//		butFromTime.setOnClickListener(new OnClickListener() {
//					
//				@Override
//				public void onClick(View v) {
//					TimePickerDialog tpd = new TimePickerDialog(SettingsFragment.this.getActivity(),
//					        new TimePickerDialog.OnTimeSetListener() {
//					 
//					            @Override
//					            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//					                StringBuffer errors = new StringBuffer();
//					            	if (settings.validate(errors)) {
//					            		settings.setFromTime(String.format("%s:%s", hourOfDay,minute));
//					            		SettingsService.getInstance().saveSettings(settings);
//							            
//							            butFromTime.setText(Html.fromHtml(String.format("<u>%s</u>", hourOfDay + ":" + minute)));
//					            	} else {
//					            		Toast.makeText(getActivity(), errors, 100).show();
//					            	}
//					            }
//					        }, 8, 10, true);
//					tpd.show();
//				}
//		});
//		
//		butToTime.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				TimePickerDialog tpd = new TimePickerDialog(SettingsFragment.this.getActivity(),
//				        new TimePickerDialog.OnTimeSetListener() {
//				 
//				            @Override
//				            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//				            	StringBuffer errors = new StringBuffer();
//				            	if (settings.validate(errors)) {
//				            		settings.setToTime(String.format("%s:%s", hourOfDay,minute));
//				            		SettingsService.getInstance().saveSettings(settings);
//				            		
//					                butToTime.setText(Html.fromHtml(String.format("<u>%s</u>", hourOfDay + ":" + minute)));
//				            	} else {
//				            		Toast.makeText(getActivity(), errors, 100).show();
//				            	}
//				            }
//				        }, 8, 10, true);
//				tpd.show();
//			}
//		});
//		
//		butAddPrefix.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				
//				final Dialog alertDialog = new Dialog(SettingsFragment.this.getActivity());
//				alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//				alertDialog.setContentView(R.layout.layout_callprefix_dialog);
//				alertDialog.show();
//				
//				
//				final Button butSumbit = (Button) alertDialog.findViewById(R.id.butSubmit);
//				final Button butCancel = (Button) alertDialog.findViewById(R.id.butCancel);
//				
//				getActivity().getActionBar().setTitle("SETTING");
//
//				butCancel.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						alertDialog.dismiss();
//					}
//				});
//				
//				butSumbit.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						EditText edTxtPrefix = (EditText) alertDialog.findViewById(R.id.edTxtPrefix);
//						EditText edTxtComments = (EditText) alertDialog.findViewById(R.id.edTxtComments);
//						
//						String prefix = edTxtPrefix.getText().toString();
//						String comments = edTxtComments.getText().toString();
//						
//						callPrefixs.add(new CallPrefix(comments,prefix));
//						callPrefixListViewAdapter.notifyDataSetChanged();
//						
//						settings.setCallPrefixs(callPrefixs);
//						SettingsService.getInstance().saveSettings(settings);
//						
//						alertDialog.dismiss();
//					}
//				});
//			}
//		});
		
		return view;
	}
	
	private void underlineText(TextView view, String text) {
		view.setText(Html.fromHtml(String.format("<u>%s</u>", text)));
	}
	
	private void setNetworkWifi() {
		
		
	}
}