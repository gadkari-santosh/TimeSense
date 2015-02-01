package com.handyapps.timesense.activity;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.handyapps.timesense.dataobjects.Status;
import com.handyapps.timesense.dataobjects.StatusCode;
import com.handyapps.timesense.dataobjects.User;
import com.handyapps.timesense.service.SettingsService;
import com.handyapps.timesense.service.TimeSenseRestClient;
import com.handyapps.timesense.task.AsyncContact;
import com.handyapps.timesense.util.Dialog;
import com.handyapps.timesense.util.ResourceUtils;

public class RegisterActivity extends Activity {

	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.layout_register);
	    
	    final ProgressDialog progress = new ProgressDialog(this);
	    
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

	    final Button buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
	    
	    final EditText phoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
	    final TextView txtViewEmail = (TextView) findViewById(R.id.txtViewEmail);
	    
	    final EditText code1 = (EditText) findViewById(R.id.code1);
	    final EditText code2 = (EditText) findViewById(R.id.code2);
	    final EditText code3 = (EditText) findViewById(R.id.code3);
	    final EditText code4 = (EditText) findViewById(R.id.code4);
	    final EditText code5 = (EditText) findViewById(R.id.code5);
	    final EditText code6 = (EditText) findViewById(R.id.code6);
	    
	    final EditText[] codes = {code1, code2, code3, code4, code5, code6};
	    
	    for (EditText code : codes) {
			code.setEnabled(false);
			code.setVisibility(EditText.INVISIBLE);
		}
	    
	    String webservice = ResourceUtils.getString(this, R.string.time_sense_rest_ws);
	    final TimeSenseRestClient client = new TimeSenseRestClient(webservice);
	    
	    buttonSignIn.setOnClickListener( new OnClickListener() {
			
//	    	public static final String API_KEY = "5fc27787";
//	        public static final String API_SECRET = "bcd28320";
//
//	        public static final String SMS_FROM = "12345";
//	        public static final String SMS_TEXT = "Your six digit one time pin is %s. Please enter this to proceed with time sense.";
	        
			@Override
			public void onClick(View v) {
				
				if (! ResourceUtils.isNetworkAvailable( RegisterActivity.this )) {
					Dialog.show(RegisterActivity.this, "No network found. Please check your internet connection.", "Error");
					return;
				}
				
				 // Check device for Play Services APK.
			    if (checkPlayServices()) {
			    	
			    	if ("Verify".equalsIgnoreCase(buttonSignIn.getText().toString())) {
			    	      
			    		buttonSignIn.setEnabled(false);
			    		
			    		CallRest rest = new CallRest(RegisterActivity.this, buttonSignIn, phoneNumber,codes);
			    		rest.execute(phoneNumber.getText().toString());
			    		
			    		return;
			    	} 
			    	
			    	if ("Sign In".equalsIgnoreCase(buttonSignIn.getText().toString())) {
			    		
			    		StringBuffer userCode = new StringBuffer();
			    		for (EditText code : codes) {
			    			userCode.append( code.getText().toString() );
			    		}
			    		
			    		User user = new User();
			    		user.setEmail(txtViewEmail.getText().toString());
			    		user.setPin(userCode.toString());
			    		user.setUserId(phoneNumber.getText().toString());
			    		
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

	private String url;
	private ProgressDialog dialog = null;
	private Context ctx;
	Button progress; EditText text;
	EditText[] codes;
	
	CallRest(Context ctx, Button progress, EditText text,EditText[] codes) {
		this.ctx = ctx;
		this.progress = progress;
		this.text = text;
		this.codes = codes;
		
	}
	
	 @Override
	    protected void onPreExecute() {
	      super.onPreExecute();
	      dialog = new ProgressDialog(ctx);
			dialog.setMessage("Working ...");
			dialog.setIndeterminate(false);
			dialog.setCancelable(false);
			dialog.show();
	    }
	
	@Override
	protected com.handyapps.timesense.dataobjects.Status doInBackground(String... params) {
		
		String webservice = ResourceUtils.getString(ctx, R.string.time_sense_rest_ws);
		    final TimeSenseRestClient client = new TimeSenseRestClient(webservice);
		    
		return client.verifyPhoneNumber(params[0]);
	}
	
	@Override
	protected void onPostExecute(com.handyapps.timesense.dataobjects.Status status) {
		
		if (status != null && status.getStatusCode() == StatusCode.SUCCESS) {
			
			Toast.makeText(ctx, status.getStatusDescription(), Toast.LENGTH_LONG).show();
			
			text.setEnabled(false);
			progress.setText("Sign In");
			
			for (EditText code : codes) {
				code.setEnabled(true);
				code.setVisibility(EditText.VISIBLE);
			}
			
//			return;
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
	
	private ProgressDialog dialog = null;
	
	Task(Context ctx, User user, ProgressDialog progress) {
		this.ctx = ctx;
		this.user = user;
		
		dialog = new ProgressDialog(ctx);
		dialog.setMessage("Working ...");
		dialog.setIndeterminate(false);
		dialog.setCancelable(false);
		dialog.show();
	}
	
	@Override
	protected void onPostExecute(String str) {
		
		String webservice = ResourceUtils.getString(ctx, R.string.time_sense_rest_ws);
		final TimeSenseRestClient client = new TimeSenseRestClient(webservice);
		
		com.handyapps.timesense.dataobjects.Status status = client.authenticate(user);
		
		if (status.getStatusCode() == StatusCode.Error) {
			Dialog.show(ctx, status.getStatusDescription(), "Error");
		} else {
			
			SettingsService service = SettingsService.getInstance();
			Settings settings = service.getSettings();
			
			settings.setGcm( user.getGcmHash() );
			settings.setUserId( user.getUserId() );
			settings.setEmail( user.getEmail() );
			settings.setSignOnSuccess(true);
			
			service.saveSettings();
			
			new AsyncContact(ctx).execute();
			
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
			
			System.out.println("Id : " + gcmHash);
			
			// create default HTTP Client
//            DefaultHttpClient httpClient = new DefaultHttpClient();
// 
//            // Create new getRequest with below mentioned URL
//            HttpPost getRequest = new HttpPost("http://192.168.0.2:7001/RwebService/register/123/"+register);
//            getRequest.setEntity(new StringEntity(register));
// 
//            // Execute your request and catch response
//            HttpResponse response = httpClient.execute(getRequest);
			
            
			System.out.println("GCM Hash Id : " + gcmHash);
		} catch (IOException e) {
			Dialog.show(ctx, "Unable to create GCM Id. Err#"+e.toString(), "Error");
		}
		return null;
	}
}
