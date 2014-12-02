package com.san.timesense.activity;

import static com.san.timesense.constant.AppContant.*;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.san.timesense.R;
import com.san.timesense.fragment.ContactFragment;
import com.san.timesense.service.ContactService;
import com.san.timesense.util.ResourceUtils;

public class AlphabetSelectionActivity extends Activity implements OnClickListener {
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		setContentView(R.layout.layout_contact_alphabets);
		
		TableLayout tableLayout = (TableLayout) findViewById(R.id.alphaTableLayout);
		
		List<Object> list = Arrays.asList(ContactService.getInstance().getSections());
		
		for (int i=0; i< tableLayout.getChildCount(); i++) {
			View view = tableLayout.getChildAt(i);
			
			if (view instanceof TableRow) {
				for (int j=0; j<((TableRow) view).getChildCount(); j++) {
					Button button = (Button) ((TableRow) view).getChildAt(j);
					
					if (!list.contains(String.valueOf(button.getText()))) {
						button.setEnabled(false);
						button.setBackgroundColor(ResourceUtils.getColor(this, R.color.lightestGray));
					}
				}
			}
			
			System.out.println(view);
		}
	}

	@Override
	public void onClick(View v) {
		Button button = (Button) v;
		
		CharSequence text = button.getText();
		
		button.setBackgroundColor(ResourceUtils.getColor(this, R.color.DeepSkyBlue));
		
		Intent intent = new Intent(this, TimeSenseActivity.class);
		intent.putExtra(INTENT_ALBHA, text);
		this.startActivity(intent);
		
		finish();
	}
}
