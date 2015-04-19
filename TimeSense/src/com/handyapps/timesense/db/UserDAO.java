package com.handyapps.timesense.db;

import static com.handyapps.timesense.db.DAOUtil.getDBString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.handyapps.timesense.R;
import com.handyapps.timesense.dataobjects.User;
import com.handyapps.timesense.util.ResourceUtils;

public class UserDAO extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "timesense_user.db";

	private static final int DATABASE_VERSION = 2;
	
	private Context context;

	SQLiteDatabase database = null;
	
	public UserDAO(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String create = ResourceUtils.getString(context, R.string.CREATE_USER);
		db.execSQL(create);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String drop = ResourceUtils.getString(context, R.string.DROP_USER);
	    db.execSQL(drop);
	    onCreate(db);
	}
	
	public void addUsers(Collection<User> users)  {
		for (User user : users) {
			addUser(user);
		}
	}
	
	public void addUser(User user) {
		
		try {
			database = this.getWritableDatabase();

			database.beginTransaction();
			
			database.execSQL(String.format("DELETE FROM USER WHERE number='%s'", user.getUserId()));
			
			String query = String.format("INSERT INTO USER values ('%s',%s,%s)", 
										user.getUserId(),
										getDBString(user.getTimeZone()),
										getDBString(new Date().toString()));			
			database.execSQL(query);
			
			database.setTransactionSuccessful();

		} catch (Exception exp) {
			exp.printStackTrace();
		} finally {
			if (database != null) {
				database.endTransaction();
				database.close();
			}
		}
	}
	
	public List<User> getAllUsers() {
		
		List<User> users = new ArrayList<User>();
		
		try {
			database = this.getReadableDatabase();
			
			Cursor cursor = database.query("USER", null, null, null, null, null, null);
			
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				users.add(cursorToUser(cursor));
				cursor.moveToNext();
			}

			cursor.close();
		
		} catch (Exception exp) {
			exp.printStackTrace();
		} finally {
			if (database != null)
				database.close();
		}
		
		return users;
	}
	
	public User getUser(String number) {
		
		User user = null;
		try {
			database = this.getReadableDatabase();
			
			Cursor cursor = database.rawQuery(String.format("select * from user where number=%s", getDBString(number)), null);
			
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				user = cursorToUser(cursor);
				cursor.moveToNext();
			}

			cursor.close();
		
		} catch (Exception exp) {
			exp.printStackTrace();
		} finally {
			if (database != null)
				database.close();
		}
		
		return user;
	}

	
	private User cursorToUser(Cursor cursor) {
		User user = new User();
		
		user.setUserId( cursor.getString(0) );
		user.setTimeZone( cursor.getString(1) );
		
		return user;
	}
}
