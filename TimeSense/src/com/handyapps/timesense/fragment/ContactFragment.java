package com.handyapps.timesense.fragment;

import static com.handyapps.timesense.constant.AppContant.INTENT_ALBHA;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.handyapps.timesense.activity.AlphabetSelectionActivity;
import com.handyapps.timesense.adapter.ContactListViewAdapter;
import com.handyapps.timesense.adapter.SideSelector;
import com.handyapps.timesense.constant.Contact;
import com.handyapps.timesense.service.ContactService;
import com.handyapps.timesense.R;

public class ContactFragment extends Fragment {

	@Override
	public void onSaveInstanceState(Bundle savedState) {
		super.onSaveInstanceState(savedState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
							 ViewGroup container,
							 Bundle savedInstanceState) {
		
		if (container != null)
			container.removeAllViews();
		
		View view = inflater.inflate(R.layout.layout_contacts, null);

		ListView listView = (ListView) view.findViewById(R.id.list_view);
		
		listView.setFastScrollEnabled(true);
        listView.setScrollingCacheEnabled(true);
        
        getActivity().getActionBar().setTitle("CONTACT");
        
		final List<Contact> contacts = ContactService.getInstance().getContacts(getActivity().getApplicationContext());
		final List<Contact> backup = new ArrayList<Contact>(contacts);
		
		final ContactListViewAdapter contactListViewAdapter = new ContactListViewAdapter(getActivity().getApplicationContext(),contacts);
		listView.setAdapter(contactListViewAdapter);
		 
		SideSelector indexBar = (SideSelector) view.findViewById(R.id.side_selector);   
        indexBar.setListView(listView);   
        
        final ImageButton alphaSelect = (ImageButton) view.findViewById(R.id.alphabetSelect);
        alphaSelect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ContactFragment.this.getActivity(), AlphabetSelectionActivity.class);
				ContactFragment.this.getActivity().startActivity(intent);
			}
		});
        
        Intent intent = ContactFragment.this.getActivity().getIntent();
        if (intent != null && intent.getStringExtra(INTENT_ALBHA) != null) {
        	String alpha = intent.getStringExtra(INTENT_ALBHA);
        	int selection = ContactService.getInstance().getFirstPositionOfAlphabet(alpha);
        	
        	if (selection == -1)
        		Toast.makeText(ContactFragment.this.getActivity(), "No contacts found for " + alpha , 100).show();
        	else 
        		listView.setSelection(selection);
        }
        
        EditText editText = (EditText) view.findViewById(R.id.searchBox);
        editText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				textChange(s);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				textChange(s);
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				textChange(s);		
			}
			
			private void textChange(Object s) {
				ArrayList<Contact> newList = new ArrayList<Contact>();
				if ("".equals(s.toString()))
					newList.addAll(backup);
				
				if (newList.size() == 0) {
					for (Contact contact :  backup) {
						if (contact.getDisplayName().toLowerCase().contains(s.toString().toLowerCase())) {
							newList.add(contact);
						}
					}
				}
				contactListViewAdapter.clear();
				contactListViewAdapter.addAll(newList);
				contactListViewAdapter.setNotifyOnChange(true);
			}
		});
        
		return view;
	}
}
