package com.san.timesense.activity;

import static com.san.timesense.util.Utils.getInt;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.san.timesense.R;
import com.san.timesense.service.ContactService;
import com.san.timesense.service.SettingsService;
import com.san.timesense.service.TimeService;

public class SplashScreenActivity extends Activity {
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		setContentView(R.layout.layout_splash_screen);
		
		// Lets initialize while user enjoy slash screen :)
		init();
		
		new Handler().postDelayed(new Runnable() {
	           public void run() {
	                Intent intent = new Intent(SplashScreenActivity.this, TimeSenseActivity.class);
	                SplashScreenActivity.this.startActivity(intent);
	                finish();
	           }
	    }, getInt(this, R.string.splash_delay));
	}
	
	// Santosh, it may happen that the init may take little longer than splash screen.
	// handle this scenario.
	private void init() {
		
		SettingsService.getInstance().init(this);
		
		TimeService.getInstance().init(this);
		
		ContactService.getInstance().init(this);
	}
}
