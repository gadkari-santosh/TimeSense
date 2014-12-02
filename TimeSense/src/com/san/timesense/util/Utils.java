package com.san.timesense.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;

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
}