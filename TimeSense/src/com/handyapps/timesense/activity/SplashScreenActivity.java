package com.handyapps.timesense.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.handyapps.timesense.R;
import com.handyapps.timesense.dataobjects.Settings;
import com.handyapps.timesense.service.ContactService;
import com.handyapps.timesense.service.SettingsService;
import com.handyapps.timesense.service.TimeService;
import com.handyapps.timesense.task.AsyncContact;

public class SplashScreenActivity extends Activity {
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		setContentView(R.layout.layout_splash_screen);
		
	    if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
		
		// Lets initialize while user enjoy slash screen :)
		init();
		
		Button signon = (Button) findViewById(R.id.buttonSignOn);
		Button skip = (Button) findViewById(R.id.buttonSkip);
		
		SettingsService service = SettingsService.getInstance();
		Settings settings = service.getSettings();
		
		if (settings.isSignOnSuccess()) {
			signon.setVisibility(Button.INVISIBLE);
			skip.setVisibility(Button.INVISIBLE);
			
			new Handler().postDelayed(new Runnable() {
	           public void run() {
	                Intent intent = new Intent(SplashScreenActivity.this, TimeSenseActivity.class);
	                SplashScreenActivity.this.startActivity(intent);
	                finish();
	           }
	       }, 2000);
		}
		
		skip.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SplashScreenActivity.this, NoSignInActivity.class);
                SplashScreenActivity.this.startActivity(intent);
                finish();
			}
		});
		
		signon.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SplashScreenActivity.this, RegisterActivity.class);
                SplashScreenActivity.this.startActivity(intent);
                finish();
			}
		});
		
		new AsyncContact(this).execute();
	}
	
	// Santosh, it may happen that the init may take little longer than splash screen.
	// handle this scenario.
	private void init() {
		
		SettingsService.getInstance().init(this);
		
		TimeService.getInstance().init(this);
		
		ContactService.getInstance().init(this);
	}
}


