package com.handyapps.timesense.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ResourceUtils {

	public static String getString(Context ctx, int id) {
		return ctx.getResources().getString(id);
	}
	
	public static int getColor(Context ctx, int id) {
		return ctx.getResources().getColor(id);
	}
	
	public static int getInt(Context ctx, int id) {
		return Integer.parseInt( ctx.getResources().getString(id) );
	}
	
	public static boolean getBool(Context ctx, int id) {
		return Boolean.parseBoolean(getString(ctx,id));
	}
	
	public static int getInt(Context ctx, int id, int defaultValue) {
		try {
		return Integer.parseInt( ctx.getResources().getString(id) );
		} catch (Exception exp) {
			return defaultValue;
		}
	}
	
	public static boolean isNetworkAvailable(Context ctx) {
		ConnectivityManager connectivity = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		  if (connectivity != null) 
		  {
			  NetworkInfo[] info = connectivity.getAllNetworkInfo();
			  if (info != null) 
				  for (int i = 0; i < info.length; i++) 
					  if (info[i].getState() == NetworkInfo.State.CONNECTED)
					  {
						  return true;
					  }

		  }
		  return false;

	}
}
