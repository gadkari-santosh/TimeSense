package com.handyapps.timesense.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.handyapps.timesense.R;
import com.handyapps.timesense.constant.Contact;
import com.handyapps.timesense.constant.Kaal;
import com.handyapps.timesense.service.ContactService;
import com.handyapps.timesense.service.SettingsService;
import com.handyapps.timesense.service.TimeService;
import com.handyapps.timesense.util.ResourceUtils;

public class ContactListViewAdapter extends ArrayAdapter<Contact> implements SectionIndexer  {
	
	Context context = null;
	List<Contact> contacts = null;
	
	List<String> used = new ArrayList<String>();
	
	public ContactListViewAdapter(Context context, List<Contact> contacts) {
		super(context, R.layout.listview_contact, contacts);
		this.context = context;
		this.contacts = contacts;
	}
	
	@Override
	public int getCount() {
		return contacts.size();
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		TextView displayName = null;
		
		if (view == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.listview_contact, null);
		}

		displayName = (TextView) view.findViewById(R.id.contact);
		
		View findViewById = view.findViewById(R.id.contactRow);
		findViewById.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Contact contact = contacts.get(position);
				intent.setData(Uri.parse("tel:" + contact.getPhoneNumber()));
				context.startActivity(intent);
				
			}
		});
		
		ImageView pic = (ImageView) view.findViewById(R.id.pic);
		
		ImageView imgViewTimeSense = (ImageView) view.findViewById(R.id.imgViewTimeSense);
		ImageView imageViewContactType = (ImageView) view.findViewById(R.id.imgContactType);
		
		TextView justTime = (TextView) view.findViewById(R.id.time);
		TextView number = (TextView) view.findViewById(R.id.number);
		TextView day = (TextView) view.findViewById(R.id.day);
		TextView txtViewStatus = (TextView) view.findViewById(R.id.txtViewStatus);
		
		Contact contact = contacts.get(position);
		if (contact == null)
			return view;
		
		
		if (contact.isAwayFromHome()) {
			txtViewStatus.setTextColor(Color.RED);
			txtViewStatus.setText(String.format("%s Moved to %s", contact.getStatus(), contact.getParkTimeZone()));
		} else {
			txtViewStatus.setTextColor(Color.GRAY);
			txtViewStatus.setText(contact.getStatus());
		}
		
		displayName.setText(contact.getDisplayName());
		number.setText(String.format("%s, %s",contact.getPhoneNumber(),contact.getCountry()));
		justTime.setText(contact.getTime());
		day.setText(contact.getDate());
		
		Kaal kaal = contact.getKaal();
		TimeService.getInstance().setKaalPic(pic, kaal);
		
		if (contact.isTimeSense()) {
			imgViewTimeSense.setImageResource(R.drawable.ic_time_sense_enabled);
		} else {
			imgViewTimeSense.setImageResource(R.drawable.ic_action_time_sense_disabled);
		}
		
		imageViewContactType.setImageResource(contact.getContactType());
		
		return view;
	}
	
	private void setSection(LinearLayout header, String label) {  
		used.add(label);
        TextView text = new TextView(context);   
        text.setTextColor(Color.BLACK);   
        text.setText(label.substring(0, 1).toUpperCase());   
        text.setTextSize(20);   
        text.setPadding(5, 0, 0, 0);   
        text.setGravity(Gravity.CENTER_VERTICAL);   
        header.addView(text);  
        
        View view = new View(context);
        view.setBackgroundColor(ResourceUtils.getColor(context, R.color.DeepSkyBlue));  
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 2));
        header.addView(view);  
    }   

	
	@Override
	public int getPositionForSection(int section) {
		return ContactService.getInstance().getPositionForSection(section);
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

	@Override
	public Object[] getSections() {
		return ContactService.getInstance().getSections();
	}
	
	@Override
	public void addAll(Collection<? extends Contact> collection) {
		Collections.sort( (List<Contact>)collection );
		super.addAll( collection );
	}
	
	public void clear() {
		if (contacts != null)
			contacts.clear();
	}
}
