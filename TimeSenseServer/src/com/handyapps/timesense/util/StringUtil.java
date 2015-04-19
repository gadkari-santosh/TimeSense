package com.handyapps.timesense.util;

import java.util.Arrays;
import java.util.List;

public class StringUtil {
	
	public static String getCommaSeperated(List<String> numbers) {

		if (numbers != null && numbers.size() > 0) {
			StringBuffer buffer = new StringBuffer("'");
	
			for (String number : numbers) {
				buffer.append(number).append("','");
			}
			
			buffer.delete(buffer.length()-2,  buffer.length());
			
			return buffer.toString();
		} else {
			return null;
		}
	}
	
	public static void main(String[] a) {
		System.out.println(getCommaSeperated(Arrays.asList("+44234728347927","+44123456709","+44123456709")));
	}
}
