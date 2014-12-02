package com.san.timesense.call;

import static com.san.timesense.constant.AppContant.SHARED_PREF_NAME;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.san.timesense.R;
import com.san.timesense.dto.TimeCode;
import com.san.timesense.service.TimeService;

public class AlertActivity extends Activity {

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		final String phoneNumber = (String) this.getIntent().getStringExtra("Phone");
		final String contactName = (String) this.getIntent().getStringExtra("Name");
		
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.layout_toast_outgoing_call);
		dialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		
		dialog.setTitle("");

		// set the custom dialog components - text, image and button
//		TextView text = (TextView) dialog.findViewById(R.id.);
//		text.setText(phone);
		
		TextView tz = (TextView) dialog.findViewById(R.id.CountryAndTz);
		TextView country = (TextView) dialog.findViewById(R.id.country);
		TextView time = (TextView) dialog.findViewById(R.id.time);
		TextView date = (TextView) dialog.findViewById(R.id.date);
		
		TextView name = (TextView) dialog.findViewById(R.id.name);
		TextView phone = (TextView) dialog.findViewById(R.id.phoneNumber);
		
		final ImageView kaal = (ImageView) dialog.findViewById(R.id.kaal);
		
		TimeCode timeCode = TimeService.getInstance().getTimeCodeByPhoneNumber(phoneNumber);
		
		phone.setText(phoneNumber);
		name.setText(contactName);
		
		if (timeCode != null) {
		
			time.setText( TimeService.getInstance().getTime(timeCode) );
			date.setText( TimeService.getInstance().getDate(timeCode) );
			
			tz.setText(timeCode.getTimeZone());
			country.setText(timeCode.getCountry());
			
			TimeService.getInstance().setKaalPic(kaal, TimeService.getInstance().getKaal(timeCode));
		}
		
		Button dialogButton = (Button) dialog.findViewById(R.id.cancel);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				
				finish();
			}
		});
		
		Button okButton = (Button) dialog.findViewById(R.id.ok);
		// if button is clicked, close the custom dialog
		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences settings = AlertActivity.this.getSharedPreferences(SHARED_PREF_NAME, 1);
				Editor edit = settings.edit();
				edit.putBoolean("CustomCall:"+phoneNumber, true);
				edit.commit();
				
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" + phoneNumber));
				AlertActivity.this.startActivity(intent);
				
				finish();
			}
		});

		dialog.show();

		
		
//		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//				this);
// 
//			// set title
//			alertDialogBuilder.setTitle("Your Title");
// 
//			// set dialog message
//			alertDialogBuilder
//				.setMessage("Click yes to exit!")
//				.setCancelable(false)
//				.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog,int id) {
//						
//						SharedPreferences settings = AlertActivity.this.getSharedPreferences(SHARED_PREF_NAME, 1);
//						Editor edit = settings.edit();
//						edit.putBoolean("CustomCall:"+phone, true);
//						edit.commit();
//						
//						Intent intent = new Intent(Intent.ACTION_CALL);
//						intent.setData(Uri.parse("tel:" + phone));
//						AlertActivity.this.startActivity(intent);
//						
//						finish();
//						
//					}
//				  })
//				.setNegativeButton("No",new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog,int id) {
//						// if this button is clicked, just close
//						// the dialog box and do nothing
//						dialog.cancel();
//						
//						finish();
//					}
//				});
//			
//			alertDialogBuilder.create().show();
	}
}
