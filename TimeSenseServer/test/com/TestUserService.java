package com;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.google.gson.Gson;
import com.handyapps.timesense.dataobjects.User;
import com.handyapps.timesense.service.UserService;

public class TestUserService {

	@Test
	public void testFindTimeSenseUsers() {
		
		UserService service = UserService.getInstance();
		
		List<String> numbers = new ArrayList<>();
		numbers.add("+447404015266");
		numbers.add("+65898293892");
		
		List<User> timeSense = service.findTimeSenseUsers("", numbers);
		
		Assert.assertEquals("+447404015266", timeSense.get(0));
		
		Gson gson = new Gson();
	}
}
