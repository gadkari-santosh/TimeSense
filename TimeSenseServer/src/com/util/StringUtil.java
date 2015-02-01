package com.util;

import java.util.List;

public class StringUtil {

	public static String getCommaSeperated(List<String> listOfString) {
		
		StringBuilder buffer = new StringBuilder();
		
		for (String string : listOfString) {
			buffer.append("'").append(string).append("',");
		}
		
		if (buffer.length() > 0) {
			buffer.deleteCharAt( buffer.length()-1 );
		}
		
		return buffer.toString();
	}
}
