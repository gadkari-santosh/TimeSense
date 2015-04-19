package com.handyapps.timesense.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.handyapps.timesense.R;
import com.handyapps.timesense.util.ResourceUtils;

public class TimeSenseSQLiteHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "timesense.db";
	
	private static final int DATABASE_VERSION = 1;
	
	private Context context = null;
	public TimeSenseSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String create = ResourceUtils.getString(context, R.string.CREATE_CALL_LOG);
		db.execSQL(create);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String drop = ResourceUtils.getString(context, R.string.DROP_CALL_LOG);
	    db.execSQL(drop);
	    
	    onCreate(db);
	}
}
