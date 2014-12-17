package com.san.timesense.fragment;

import static com.san.timesense.constant.AppContant.SHARED_PREF_NAME;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.san.timesense.R;
import com.san.timesense.dto.TimeCode;
import com.san.timesense.service.TimeService;

public class DialFragment  extends Fragment {

	View view = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
							 ViewGroup container,
							 Bundle savedInstanceState) {
		
		if (container != null)
			container.removeAllViews();
		
		view = inflater.inflate(R.layout.layout_dial_pad, null);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT, 
							LinearLayout.LayoutParams.MATCH_PARENT);
		view.setLayoutParams(layoutParams);
		
		getActivity().getActionBar().setTitle("DIAL");
		
		final TimeService service = TimeService.getInstance();
		
		final TextView txtViewPhoneNumber = (TextView) view.findViewById(R.id.txtViewPhoneNumber);
		final TextView txtViewTime = (TextView) view.findViewById(R.id.txtViewTime);
		final TextView txtViewTZ = (TextView) view.findViewById(R.id.txtViewTZ);
		
		final ImageView imgViewKaal = (ImageView) view.findViewById(R.id.imgViewKaal);
		imgViewKaal.setVisibility(ImageView.INVISIBLE);
		
		final ImageButton imageButtonCall = (ImageButton) view.findViewById(R.id.imgViewCall);
		final ImageButton buttonSaveContact = (ImageButton) view.findViewById(R.id.buttonSaveContact);
		
		buttonSaveContact.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent addContactIntent = new Intent(Contacts.Intents.Insert.ACTION, Contacts.People.CONTENT_URI);
				addContactIntent.putExtra(Contacts.Intents.Insert.PHONE, txtViewPhoneNumber.getText()); // an example, there is other data available
				startActivity(addContactIntent);
			}
		});
		
		imageButtonCall.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SharedPreferences settings = DialFragment.this.getActivity().getSharedPreferences(SHARED_PREF_NAME, 1);
				Editor edit = settings.edit();
				edit.putBoolean("CustomCall:"+txtViewPhoneNumber.getText(), true);
				edit.commit();
				
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" + txtViewPhoneNumber.getText().toString()));
				DialFragment.this.getActivity().startActivity(intent);
			}
		});
		
		txtViewPhoneNumber.addTextChangedListener( new TextWatcher() {
			
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
				TimeCode timeCode = service.getTimeCodeByPhoneNumber(s.toString());
				if (timeCode != null) {
					txtViewTime.setText( service.getTime(timeCode) );
					txtViewTZ.setText( String.format("%s,%s", timeCode.getCountry(), timeCode.getTimeZone()) );
					service.setKaalPic(imgViewKaal, timeCode);
					imgViewKaal.setVisibility(ImageView.VISIBLE);
				} else {
					txtViewTime.setText("");
					txtViewTZ.setText("");
					imgViewKaal.setVisibility(ImageView.INVISIBLE);
				}
			}
		});
		
		return view;
	}
	
	public void addToDialNumber(View numView) {
		String num = (String) ((View) numView).getTag().toString();
		
		TextView phoneNumber = (TextView) view.findViewById(R.id.txtViewPhoneNumber);
		
		phoneNumber.setText( phoneNumber.getText() + num ) ;
		
	}
	
	public void delOneDigitOfDialNumber (View numView) {
		TextView phoneNumber = (TextView) view.findViewById(R.id.txtViewPhoneNumber);
		
		if (phoneNumber.getText() != null && phoneNumber.getText().length() > 0) {
			phoneNumber.setText( phoneNumber.getText().subSequence(0, phoneNumber.length()-1)  ) ;
		}
	}
}
