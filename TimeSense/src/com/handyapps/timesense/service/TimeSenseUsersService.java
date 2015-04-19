package com.handyapps.timesense.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;

import com.handyapps.timesense.dataobjects.User;
import com.handyapps.timesense.db.UserDAO;

public class TimeSenseUsersService {

	private static final TimeSenseUsersService INSTANCE = new TimeSenseUsersService();
	
	private Context context = null;
	
	private List<User> timeSenseUsers = new ArrayList<User>();
	
	private TimeSenseUsersService() {}
	
	public static TimeSenseUsersService getInstance() {
		return INSTANCE;
	}
	
	public synchronized void init(Context context) {
		this.context = context;
		
		load();
	}
	
	private synchronized void load() {
		try {
			UserDAO dao = new UserDAO(context);
			timeSenseUsers = dao.getAllUsers();
		} catch (Exception exp) {
			exp.printStackTrace();
			timeSenseUsers = new ArrayList<User>();
		}
	}
	
	public void addTimeSenseUser(User user) {
		addTimeSenseUsers(Arrays.asList(user));
	}
	
	public void clearAndAddNewUsers(Set<User> users) {
		timeSenseUsers.clear();
		addTimeSenseUsers(users);
	}
	
	public void addTimeSenseUsers(Collection<User> users) {
		
		if (users == null)
			return;
		
		if (timeSenseUsers == null)
			timeSenseUsers = new ArrayList<User>();
		
		timeSenseUsers.addAll(users);
		
		UserDAO dao = new UserDAO(context);
		dao.addUsers(users);
	}
	
	public Collection<User> getTimeSenseUsers() {
		return timeSenseUsers;
	}
	
	public List<String> getTimeSenseNumbers() {
		List<String> numbers = new ArrayList();
		
		if (timeSenseUsers == null)
			return numbers;
		
		for (User user : timeSenseUsers) {
			numbers.add(user.getUserId().trim());
		}
		
		return numbers;
	}
	
	public Map<String, String> getTimeSenseUserTimeZones() {
		
		Map<String, String> numbers = new HashMap<String,String>();
		
		if (timeSenseUsers == null)
			return numbers;
		
		for (User user : timeSenseUsers) {
			if (user.getTimeZone() != null && user.getUserId() != null)
				numbers.put(user.getUserId().trim(), user.getTimeZone().trim());
		}
		
		return numbers;
	}
	
	public User getTimeSenseUser(String phoneNumber) {
		UserDAO dao = new UserDAO(context);
		return dao.getUser(phoneNumber);
	}
}
