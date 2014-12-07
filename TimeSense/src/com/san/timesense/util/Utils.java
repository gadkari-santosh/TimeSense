package com.san.timesense.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
}