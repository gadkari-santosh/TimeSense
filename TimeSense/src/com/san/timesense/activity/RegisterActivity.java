package com.san.timesense.activity;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class RegisterActivity extends Activity {

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		  if (android.os.Build.VERSION.SDK_INT > 9) {
	            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	            StrictMode.setThreadPolicy(policy);
	            System.out.println("*** My thread is now configured to allow connection");
	        }
		
		new Task(this).execute();
	}
}

class Task extends AsyncTask<Void, String, String> {

	private Context ctx;
	
	Task(Context ctx) {
		this.ctx = ctx;
	}
	
	@Override
	protected String doInBackground(Void... params) {
		
		GoogleCloudMessaging cloud = GoogleCloudMessaging.getInstance(ctx);
		try {
			String register = cloud.register("969393679073");
			System.out.println("Id : " + register);
			
			// create default HTTP Client
            DefaultHttpClient httpClient = new DefaultHttpClient();
 
            // Create new getRequest with below mentioned URL
            HttpPost getRequest = new HttpPost("http://192.168.1.6:7001/RwebService/register");
            getRequest.setEntity(new StringEntity(register));
 
            // Execute your request and catch response
            HttpResponse response = httpClient.execute(getRequest);
			
			System.out.println("******************* Id : " + register);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
