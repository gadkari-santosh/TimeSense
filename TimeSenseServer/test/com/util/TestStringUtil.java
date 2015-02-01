package com.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TestStringUtil {

	@Test
	public void testGetCommaSeperated() {
		
		List<String> numbers = new ArrayList<>();
		numbers.add("+441223142");
		numbers.add("+65898293892");
		
		String str = StringUtil.getCommaSeperated(numbers);
		
		Assert.assertEquals("'+441223142','+65898293892'", str);
		
		numbers.clear();
		numbers.add("+441223142");
		
		str = StringUtil.getCommaSeperated(numbers);
		
		Assert.assertEquals("'+441223142'", str);
	}
}
