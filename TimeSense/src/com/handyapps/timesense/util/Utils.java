package com.handyapps.timesense.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.telephony.TelephonyManager;

public class Utils {

	public static int getInt(Context ctx, int id) {
		return Integer.parseInt( ctx.getResources().getString(id) );
	}
	
	public static String getString(Context ctx, int id) {
		return ctx.getResources().getString(id);
	}
	
	public static Object deepCopy(Object object) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(object);
		oos.flush();
		oos.close();
		bos.close();
		byte[] byteData = bos.toByteArray();
		
		ByteArrayInputStream bais = new ByteArrayInputStream(byteData);
		return (Object) new ObjectInputStream(bais).readObject();
	}
	
	public static String getNetworkClass(Context context) {
	    TelephonyManager mTelephonyManager = (TelephonyManager)
	            context.getSystemService(Context.TELEPHONY_SERVICE);
	    int networkType = mTelephonyManager.getNetworkType();
	    switch (networkType) {
	        case TelephonyManager.NETWORK_TYPE_GPRS:
	        case TelephonyManager.NETWORK_TYPE_EDGE:
	        case TelephonyManager.NETWORK_TYPE_CDMA:
	        case TelephonyManager.NETWORK_TYPE_1xRTT:
	        case TelephonyManager.NETWORK_TYPE_IDEN:
	            return "2G";
	        case TelephonyManager.NETWORK_TYPE_UMTS:
	        case TelephonyManager.NETWORK_TYPE_EVDO_0:
	        case TelephonyManager.NETWORK_TYPE_EVDO_A:
	        case TelephonyManager.NETWORK_TYPE_HSDPA:
	        case TelephonyManager.NETWORK_TYPE_HSUPA:
	        case TelephonyManager.NETWORK_TYPE_HSPA:
	        case TelephonyManager.NETWORK_TYPE_EVDO_B:
	        case TelephonyManager.NETWORK_TYPE_EHRPD:
	        case TelephonyManager.NETWORK_TYPE_HSPAP:
	            return "3G";
	        case TelephonyManager.NETWORK_TYPE_LTE:
	            return "4G";
	        default:
	            return "Unknown";
	    }
	}
	
	public static String removeSpaces(String string) {
		
		if (string == null)
			return "";
		
		String str = string.replaceAll(" ", "");
		str = str.replaceAll("\t", "");
		str = str.replaceAll("\n", "");
		str = str.replaceAll("-", "");
		
		return str.trim();
	}
	
	public static Date removeTime(Date date) {
		
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		
		calendar.setTime( new Date(date.getTime()) );
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		
		return calendar.getTime();
		
	}
	
	public static boolean isToday(Date date) {
	     return removeTime(date).equals( removeTime(new Date()));
	     
	}
	
	public static boolean isYesterday(Date date) {
		Date today = removeTime(new Date());
		
		today = new Date ( today.getTime() - 86400000 );
		
	    return removeTime(date).equals(today);
	}
}