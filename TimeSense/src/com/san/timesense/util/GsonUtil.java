package com.san.timesense.util;

import android.content.Context;

import com.google.gson.Gson;

public class GsonUtil {

	public static String getJSon(Object object, Class cls) {
		Gson gson = new Gson();
		return gson.toJson(object, cls);
	}
	
	public static <T> T getObject(String json, Class<T> cls) {
		try {
			Gson gson = new Gson();
			return gson.fromJson(json, cls);
		} catch (Exception exp) {
			return null;
		}
	}
	
	public static <T> T getObject(Context ctx, String filepath, Class<T> cls) {
		try {
			Gson gson = new Gson();
			
			String json = FileUtil.getFile(ctx, filepath);
			
			return gson.fromJson(json, cls);
		} catch (Exception exp) {
			return null;
		}
	}
}
