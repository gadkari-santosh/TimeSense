package com.handyapps.timesense.dao;

import static com.handyapps.timesense.constant.AppConstants.SQL_INSERT_AUTH_LOG;
import static com.handyapps.timesense.constant.AppConstants.SQL_INSERT_USER;
import static com.handyapps.timesense.constant.AppConstants.SQL_UPDATE_TZ;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.handyapps.timesense.common.AUTH_STATUS;
import com.handyapps.timesense.dataobjects.Authentication;
import com.handyapps.timesense.dataobjects.User;
import com.handyapps.timesense.util.PropUtil;
import com.handyapps.timesense.util.StringUtil;

public class DAO {

	private static final Logger LOG = Logger.getLogger(DAO.class);
	
	public void createNewUser(User user) throws SQLException {
		
		Connection connection = null;
		java.sql.CallableStatement statement = null;
		
		try {
			connection = ConnectionManager.getInstance().getConnection();
			
			String sql = PropUtil.getProperty(SQL_INSERT_USER);
			
			statement = connection.prepareCall(sql);
			
			statement.setString(1, user.getUserId());
			statement.setString(2, user.getGcmHash());
			statement.setString(3, user.getSessionId());
			statement.setString(4, user.getTimeZone());
			
			statement.execute();
			
		} catch (Exception exp) {
			LOG.error("Exception ", exp);
			throw new SQLException(exp);
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	
	public void updateTimeZone(String id, String timeZone) throws SQLException {
		Connection connection = null;
		java.sql.PreparedStatement statement = null;
		
		try {
			connection = ConnectionManager.getInstance().getConnection();
			
			String sql = PropUtil.getProperty(SQL_UPDATE_TZ);
			
			statement = connection.prepareStatement(sql);
			
			statement.setString(2, id);
			statement.setString(1, timeZone);
			
			statement.executeUpdate();
			
		} catch (Exception exp) {
			LOG.error("Exception ", exp);
			throw new SQLException(exp);
		} finally {
			if (connection != null)
				connection.close();
			if (statement != null)
				statement.close();
		}
	}
	
	public String createAuthenticationProcess(String phone, String pin) throws SQLException {
		
		Connection connection = null;
		PreparedStatement statement = null;
		
		String uuid = UUID.randomUUID().toString();
		
		try {
			connection = ConnectionManager.getInstance().getConnection();
		
			String sql = PropUtil.getProperty(SQL_INSERT_AUTH_LOG);
			
			statement = connection.prepareStatement(sql);
			
			statement.setString(1, uuid);
			statement.setString(2, phone);
			statement.setString(3, pin);
			statement.setString(4, AUTH_STATUS.INIT.name());
			statement.setTimestamp(5, new Timestamp(new Date().getTime()));

			statement.execute();
			
			return uuid;
			
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new SQLException(exp);
		} finally {
			if (statement != null)
				statement.close();
			
			if (connection != null)
				connection.close();
		}
	}
	
	public Authentication getAuthenticationByPhone(User user) throws SQLException {

		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		
		Authentication authentication = null;
		
		try {
			connection = ConnectionManager.getInstance().getConnection();
		
			statement = connection.createStatement();

			rs = statement.executeQuery(
					String.format("SELECT * FROM auth_log WHERE session_id='%s' and auth_string='%s'", 
							user.getSessionId(), 
							user.getUserId()));
			
			if (rs.next()) {
				authentication = new Authentication();
				
				authentication.setPhone( rs.getString("auth_string") );
				authentication.setPin( rs.getString("pin") );
			}
			
		} catch (Exception exp) {
			LOG.error("Exception", exp);
			throw new SQLException(exp);
		} finally {
			if (connection != null)
				connection.close();
			if (statement != null)
				statement.close();
		}
		
		return authentication;
	}
	
	public List<User> findTimeSenseUserNumbers(String ownerId, List<String> numbers) throws SQLException {
		
		List<String> batch = new ArrayList<>();
		
		List<User> timeSense = new ArrayList<>();
		
		for (int i=0; i <numbers.size(); i++) {
			
			batch.add(numbers.get(i));
			
			if (batch.size() % 18 == 0) {
				timeSense.addAll ( findTimeSenseUserNumbers(batch) );
				
				batch.clear();
			}
		}
		
		if (!batch.isEmpty())
			timeSense.addAll( findTimeSenseUserNumbers(batch) );
		
		return timeSense;
	}
	
	private List<User> findTimeSenseUserNumbers(List<String> numbers) throws SQLException {
		
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		
		List<User> users = new ArrayList<>();
		
		try {
			
			connection = ConnectionManager.getInstance().getConnection();
		
			statement = connection.createStatement();
			
			String strNumbers = StringUtil.getCommaSeperated(numbers);
			
			rs = statement.executeQuery(String.format("select * from user where user_id in (%s)", strNumbers));
			
			User user = null;
			while (rs.next()) {
				user = new User();
				user.setUserId(rs.getString("user_id"));
				user.setTimeZone(rs.getString("time_zone"));
				user.setGcmHash(rs.getString("gcm_code"));
				
				users.add(user);
			}
			
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new SQLException(exp);
		} finally {
			if (connection != null)
				connection.close();
			if (statement != null)
				statement.close();
		}
		
		return users;
	}
}
