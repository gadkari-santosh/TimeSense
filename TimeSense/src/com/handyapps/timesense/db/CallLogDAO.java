package com.handyapps.timesense.db;

import static com.handyapps.timesense.db.DAOUtil.getDBString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.handyapps.timesense.R;
import com.handyapps.timesense.dataobjects.CallInfo;
import com.handyapps.timesense.util.ResourceUtils;

public class CallLogDAO extends SQLiteOpenHelper {

	private SQLiteDatabase database;

	private Context context;
	
	public static final String DATABASE_NAME = "timesense_call_log.db";

	private static final int DATABASE_VERSION = 3;
	
	public CallLogDAO(Context context) {
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
	
	public void addCallLogs (List<CallInfo> callLogs) throws Exception {
		for (CallInfo callInfo : callLogs) {
			addCallInfo(callInfo);
		}
	}
	
	public void clearTable() {
		try {
			database = this.getWritableDatabase();
		
			database.execSQL("DELETE FROM CALL_LOG");
		} catch (Exception exp) {
			exp.printStackTrace();
		} finally {
			database.close();
		}
	}
	
	public void deleteCallInfo(int id) throws Exception {
		try {
			database = this.getWritableDatabase();

			String query = String.format("DELETE FROM CALL_LOG WHERE _id=%d", id);
			
			database.execSQL(query);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw exp;
		} finally {
			database.close();
		}
	}

	public void addCallInfo(CallInfo callInfo) throws Exception {
		try {
			database = this.getWritableDatabase();

			String query = String.format("INSERT INTO CALL_LOG (name,number,home_country,home_timezone,home_date,home_time,recp_country,recp_timezone,recp_date,recp_time,duration,call_type) values (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%d,%d)", 
										getDBString(callInfo.getName()),
										getDBString(callInfo.getPhoneNumber()),
										getDBString(callInfo.getHomeCountry()),
										getDBString(callInfo.getHomeTimeZone()),
										getDBString(callInfo.getLocalDate()),
										getDBString(callInfo.getLocalTime()),
										getDBString(callInfo.getRecipientCountry()),
										getDBString(callInfo.getRecipientTimeZone()),
										getDBString(callInfo.getRemoteDate()),
										getDBString(callInfo.getRemoteTime()),
										callInfo.getDuration(),
										callInfo.getCallType());
			
			database.execSQL(query);

		} catch (Exception exp) {
			exp.printStackTrace();
			throw exp;
		} finally {
			database.close();
		}
	}
	
	public List<CallInfo> getAllCallInfos() throws Exception {

		CallInfo CallInfo = null;

		SQLiteDatabase database = null;
		try {
			database = this.getWritableDatabase();

			List<CallInfo> callInfos = new ArrayList<CallInfo>();

			Cursor cursor = database.query("CALL_LOG", null, null, null, null, null, null);

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				CallInfo = cursorToObject(cursor);
				callInfos.add(CallInfo);
				cursor.moveToNext();
			}
			// make sure to close the cursor
			cursor.close();
			
			if (callInfos.size() > 500) {

				for (int i=callInfos.size()-1; i>500; i--) {
					database.rawQuery(String.format("DELETE FROM CALL_LOG WHERE myid='%s'", callInfos.get(i).getId()), null);
					callInfos.remove(i);
				}
			}

			Collections.sort(callInfos);
			
			return callInfos;
		} catch (Exception exp) {
			throw exp;
		} finally {
			if (database != null)
				database.close();
		}
	}

	private CallInfo cursorToObject(Cursor cursor) {
		CallInfo CallInfo = new CallInfo();

		CallInfo.setId(cursor.getInt(0));
		CallInfo.setName(cursor.getString(1));
		CallInfo.setPhoneNumber(cursor.getString(2));
		
		CallInfo.setHomeCountry(cursor.getString(3));
		CallInfo.setHomeTimeZone(cursor.getString(4));
		
		CallInfo.setLocalDate(cursor.getString(5));
		CallInfo.setLocalTime(cursor.getString(6));
		
		CallInfo.setRecipientCountry(cursor.getString(7));
		CallInfo.setRecipientTimeZone(cursor.getString(8));
		CallInfo.setRemoteDate(cursor.getString(9));
		CallInfo.setRemoteTime(cursor.getString(10));
		
		CallInfo.setDuration(cursor.getInt(11));
		CallInfo.setCallType(cursor.getInt(12));
		
		return CallInfo;
	}
}
