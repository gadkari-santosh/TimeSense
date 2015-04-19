package com.handyapps.timesense.db;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.database.DatabaseUtils;

public class DAOUtil {

	public static String getDBString(String name) {
		if (name != null) {
			return DatabaseUtils.sqlEscapeString(name);
		}
		
		return "''";
	}
	
	public static String getDateAsString(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		
		if (date != null) {
			String strDate = dateFormat.format(date);
			
			return DatabaseUtils.sqlEscapeString(strDate);
		}
		
		return "''";
	}
}
