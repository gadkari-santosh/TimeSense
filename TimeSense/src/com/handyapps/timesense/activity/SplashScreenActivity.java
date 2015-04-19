package com.handyapps.timesense.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;

import com.handyapps.timesense.R;
import com.handyapps.timesense.dataobjects.Settings;
import com.handyapps.timesense.service.SettingsService;
import com.handyapps.timesense.task.AsyncInitialLoading;
import com.handyapps.timesense.task.PostCallBack;

public class SplashScreenActivity extends Activity {
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		setContentView(R.layout.layout_splash_screen);
		
	    if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
		
		Button signon = (Button) findViewById(R.id.buttonSignOn);
		Button skip = (Button) findViewById(R.id.buttonSkip);
		final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progBarLoading);
		
		progressBar.setEnabled(false);
		progressBar.setVisibility(ProgressBar.INVISIBLE);
		
		SettingsService service = SettingsService.getInstance();
		service.init(getApplicationContext());
		Settings settings = service.getSettings();
		
		if (settings.isSignOnSuccess()) {
			signon.setVisibility(Button.INVISIBLE);
			skip.setVisibility(Button.INVISIBLE);

			progressBar.setEnabled(true);
			progressBar.setVisibility(ProgressBar.VISIBLE);
			
			new AsyncInitialLoading(SplashScreenActivity.this, progressBar, new PostCallBack() {
				
				@Override
				public void execute(Context context) {
			        Intent intent = new Intent(context, TimeSenseActivity.class);
			        context.startActivity(intent);
			        
			        finish();
				}
			}).execute();
			
		}

		skip.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				progressBar.setEnabled(true);
				progressBar.setVisibility(ProgressBar.VISIBLE);
				new AsyncInitialLoading(SplashScreenActivity.this, progressBar, new PostCallBack() {
					
					@Override
					public void execute(Context context) {
				        Intent intent = new Intent(context, NoSignInActivity.class);
				        context.startActivity(intent);
				        
				        finish();
					}
				}).execute();
			}
		});
		
		signon.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				progressBar.setEnabled(true);
				progressBar.setVisibility(ProgressBar.VISIBLE);
				
				new AsyncInitialLoading(SplashScreenActivity.this, progressBar, new PostCallBack() {
					
					@Override
					public void execute(Context context) {
						Intent intent = new Intent(context, RegisterActivity.class);
				        context.startActivity(intent);
				        
				        finish();
					}
				}).execute();
			}
		});
	}
}