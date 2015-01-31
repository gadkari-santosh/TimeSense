package com.handyapps.timesense.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.handyapps.timesense.R;

public class NoSignInActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_no_signin_warning);
		
		Button buttonDismiss = (Button) findViewById(R.id.buttonDismiss);
		buttonDismiss.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(NoSignInActivity.this, TimeSenseActivity.class);
				NoSignInActivity.this.startActivity(intent);
                finish();
			}
		});
	}
}
