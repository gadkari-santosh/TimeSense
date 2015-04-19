package com.handyapps.timesense.activity;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.handyapps.timesense.R;
import com.handyapps.timesense.dataobjects.Settings;
import com.handyapps.timesense.dataobjects.TimeCode;
import com.handyapps.timesense.dataobjects.User;
import com.handyapps.timesense.dataobjects.response.Status;
import com.handyapps.timesense.dataobjects.response.StatusCode;
import com.handyapps.timesense.service.SettingsService;
import com.handyapps.timesense.service.TimeSenseRestClient;
import com.handyapps.timesense.service.TimeService;
import com.handyapps.timesense.util.Dialog;
import com.handyapps.timesense.util.ResourceUtils;

public class RegisterActivity extends Activity {

	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.layout_register);
	    
	    final ProgressDialog progress = new ProgressDialog(this);
	    
	    final TimeService timeService = TimeService.getInstance();
	    
	    final User user = new User();
	    
	    final Button buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
	    
	    final EditText phoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
	    final EditText editTxtCode = (EditText) findViewById(R.id.editTxtCode);
	    
	    final EditText editTxtPin = (EditText) findViewById(R.id.editTxtPin);
//	    final EditText code2 = (EditText) findViewById(R.id.code2);
//	    final EditText code3 = (EditText) findViewById(R.id.code3);
//	    final EditText code4 = (EditText) findViewById(R.id.code4);
//	    final EditText code5 = (EditText) findViewById(R.id.code5);
//	    final EditText code6 = (EditText) findViewById(R.id.code6);
	    
	    final TextView txtViewCountry = (TextView) findViewById(R.id.txtViewCountry);
	    final TextView txtViewTZ = (TextView) findViewById(R.id.txtViewTZ);
	    
//	    final EditText[] codes = {code1, code2, code3, code4, code5, code6};
	    
	    Button skip = (Button) findViewById(R.id.buttonSkip);
		skip.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RegisterActivity.this, NoSignInActivity.class);
				RegisterActivity.this.startActivity(intent);
                finish();
			}
		});
	    
	    if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
	    
	    if (! checkPlayServices()) {
	    	Toast.makeText(RegisterActivity.this, "Cannot proceed. Please login Google play", Toast.LENGTH_LONG).show();
	    	return;
	    }
	    
    	editTxtPin.setEnabled(false);
		editTxtPin.setVisibility(EditText.INVISIBLE);
	    
	    String webservice = ResourceUtils.getString(this, R.string.time_sense_rest_ws);
	    
	    phoneNumber.requestFocus();
	    getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	    
	    final TimeSenseRestClient client = new TimeSenseRestClient(webservice);
	    
	    String localDialCode = timeService.getLocalDialCode();
	    if (!localDialCode.startsWith("+"))
	    	localDialCode = "+" + localDialCode;
	    
	    TimeCode localTimeCode = timeService.getTimeCodeByPhoneNumber(localDialCode);
	    
	    if (localTimeCode != null) {
	    	txtViewCountry.setText(localTimeCode.getCountry());
	    	txtViewTZ.setText(localTimeCode.getTimeZone());
	    }
	    
	    editTxtCode.setText("+"+TimeService.getInstance().getLocalDialCode());
	    
	    editTxtCode.addTextChangedListener( new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//NOP
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				//NOP
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
				txtViewCountry.setText("");
				txtViewTZ.setText("");
				
				if (!s.toString().startsWith("+")) {
					Toast.makeText(getApplicationContext(), "The Code should start with +", Toast.LENGTH_LONG).show();
					return;
				}
				
				TimeCode timeCode = timeService.getTimeCodeByPhoneNumber(s.toString());
				if (timeCode != null) {
					txtViewCountry.setText(timeCode.getCountry());
			    	txtViewTZ.setText(timeCode.getTimeZone());
				}
			}
		});
	    
	    buttonSignIn.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (! ResourceUtils.isNetworkAvailable( RegisterActivity.this )) {
					Dialog.show(RegisterActivity.this, "No network found. Please check your internet connection.", "Error");
					return;
				}
				
				String number = editTxtCode.getText().toString() + phoneNumber.getText().toString();
				
				 // Check device for Play Services APK.
			    if (checkPlayServices()) {
			    	
			    	if (!ResourceUtils.isNetworkAvailable(RegisterActivity.this)) {
			    		Dialog.show(RegisterActivity.this, "No network available", "Error");
			    		return;
			    	}
			    	
			    	if ("Verify".equalsIgnoreCase(buttonSignIn.getText().toString())) {
			    	      
			    		buttonSignIn.setEnabled(true);
			    		buttonSignIn.setVisibility(EditText.VISIBLE);
			    		
			    		CallRest rest = new CallRest(RegisterActivity.this, buttonSignIn, editTxtPin, user);
			    		rest.execute(number);
			    		
			    		return;
			    	} 
			    	
			    	if ("Sign In".equalsIgnoreCase(buttonSignIn.getText().toString())) {
			    		
//			    		user.setEmail(txtViewEmail.getText().toString());
			    		user.setPin(editTxtPin.getText().toString());
			    		user.setUserId(number);
			    		
			    		user.setTimeZone(txtViewTZ.getText().toString());
			    		
			    		 final Task task = new Task(RegisterActivity.this, user, progress);
			    		 task.execute();
			    	}
			    } else {
			    	Dialog.show(RegisterActivity.this, "Unable to register. Please login Google play", "Error");
	    			return;
			    }
			}
		});
	}

	// You need to do the Play Services APK check here too.
	@Override
	protected void onResume() {
	    super.onResume();
	    checkPlayServices();
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            finish();
	        }
	        return false;
	    }
	    return true;
	}
	
	public  void addCode(View view) {
		 InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
         imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	}
}

class CallRest extends AsyncTask<String, String, Status> {

	private ProgressDialog dialog = null;
	
	private Context ctx;
	
	private User user;
	
	private Button progress; 
	
	private String url;
	
	private EditText codes;
	
	CallRest(Context ctx, Button progress, EditText codes,User user) {
		this.ctx = ctx;
		this.progress = progress;
		this.codes = codes;
		this.user = user;
	}
	
	 @Override
	    protected void onPreExecute() {
	      super.onPreExecute();
	      dialog = new ProgressDialog(ctx);
			dialog.setMessage(Html.fromHtml("<font color='#33B5E5'>Working ...</font>"));
			dialog.setIndeterminate(false);
			dialog.setCancelable(false);
			dialog.show();
	    }
	
	@Override
	protected com.handyapps.timesense.dataobjects.response.Status doInBackground(String... params) {
		
		String webservice = ResourceUtils.getString(ctx, R.string.time_sense_rest_ws);
		    final TimeSenseRestClient client = new TimeSenseRestClient(webservice);
		    
		return client.verifyPhoneNumber(params[0]);
	}
	
	@Override
	protected void onPostExecute(com.handyapps.timesense.dataobjects.response.Status status) {
		
		if (status != null && status.getStatusCode() == StatusCode.SUCCESS) {
			
			Toast.makeText(ctx, status.getStatusDescription(), Toast.LENGTH_LONG).show();
			
			user.setSessionId( status.getSessionId() );
			
			progress.setText("Sign In");
			
			codes.setEnabled(true);
			codes.setVisibility(EditText.VISIBLE);
		} else {
			Dialog.show(ctx, status.getStatusDescription(), "Error");
		}
		
		progress.setEnabled(true);
		dialog.dismiss();
    }
	
}

class Task extends AsyncTask<Void, String, String> {

	private Context ctx;
	private User user;
	private String gcmHash;
	
	private String exception;
	
	private ProgressDialog dialog = null;
	
	Task(Context ctx, User user, ProgressDialog progress) {
		this.ctx = ctx;
		this.user = user;
		
		dialog = new ProgressDialog(ctx);
		dialog.setMessage(Html.fromHtml("<font color='#33B5E5'>Working ...</font>"));
		dialog.setIndeterminate(false);
		dialog.setCancelable(false);
		dialog.show();
	}
	
	@Override
	protected void onPostExecute(String str) {
		
		String webservice = ResourceUtils.getString(ctx, R.string.time_sense_rest_ws);
		final TimeSenseRestClient client = new TimeSenseRestClient(webservice);
		
		if (user == null) {
			Dialog.show(ctx, "Unable to communicate. Please check you internet connection and try again", "Error");
			dialog.dismiss();
			return;
		}
			
		com.handyapps.timesense.dataobjects.response.Status status = client.authenticate(user);
		
		if (status.getStatusCode() == StatusCode.Error) {
			Dialog.show(ctx, status.getStatusDescription(), "Error");
		} else {
			
			SettingsService service = SettingsService.getInstance();
			Settings settings = service.getSettings();
			
			if (user.getGcmHash() == null) {
				Dialog.show(ctx, "Unable to create google messaging id. Ensure you have logged in to google play or please try again." , "Error");
				dialog.dismiss();
				return;
			}
			
			settings.setGcm( user.getGcmHash() );
			settings.setUserId( user.getUserId() );
			settings.setEmail( user.getEmail() );
			settings.setSignOnSuccess(true);
			
			service.saveSettings();
			
			Intent intent = new Intent(ctx, SplashScreenActivity.class);
			ctx.startActivity(intent);	
		}
		
		dialog.dismiss();
	}
	
	@Override
	protected String doInBackground(Void... params) {
		
		GoogleCloudMessaging cloud = GoogleCloudMessaging.getInstance(ctx);
		try {
			String webservice = ResourceUtils.getString(ctx, R.string.time_sense_rest_ws);
			
			TimeSenseRestClient client = new TimeSenseRestClient(webservice);
			
			gcmHash = cloud.register( client.getGcmCode(ctx) );
			
			user.setGcmHash(gcmHash);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
