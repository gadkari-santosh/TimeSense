package com.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import com.dataobjects.Authentication;
import com.dataobjects.User;
import com.util.StringUtil;

public class DAO {

	public String getConnectionDetails() {

		try {
			Connection connection = ConnectionManager.getInstance().getConnection();
			
			return connection.getMetaData().getURL();
		} catch (Exception exp) {
			exp.printStackTrace();
			return exp.toString();
		}
	}
	public long createNewUser(User user) throws SQLException {
		
		Connection connection = null;
		CallableStatement statement = null;
		
		try {
			connection = ConnectionManager.getInstance().getConnection();
		
			statement = connection.prepareCall("{ Call create_user (?,?,?,?) } ");
			statement.setString(1, user.getUserId());
			statement.setString(2, user.getEmail());
			statement.setString(3, user.getGcmHash());
			statement.registerOutParameter(4, java.sql.Types.INTEGER);
			
			statement.execute();
			
			int id = statement.getInt(4);
			
			return id;
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new SQLException(exp);
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public List<User> getAllRelatedTrades(String id) throws SQLException {
		
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		
		List<User> gcm = new ArrayList<>();
		
		try {
			connection = ConnectionManager.getInstance().getConnection();
		
			statement = connection.createStatement();
			
			Statement readS = connection.createStatement();

			rs = readS.executeQuery(String.format("SELECT * FROM master_user WHERE user_id=%s", id));
			
			if (rs.next()) {
				id = rs.getString("id");
			}
			
			readS.close();
			rs.close();
			
			rs = statement.executeQuery(String
							.format("select u.id,u.gcm_code,u.user_id from master_user u, "
									+" (select * from contact_relation c where "
									 +" owner_id=%s || relation_id=%s) i "
									+" where u.id=i.owner_id "
									 +" || u.id=i.relation_id "
									+"group by u.id,u.gcm_code", id,id));
			
			User user = null;
			while (rs.next()) {
				user = new User();
				
				user.setGcmHash(rs.getString("gcm_code"));
				user.setUserId(rs.getString("user_id"));
				
				gcm.add( user );
			}
			
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new SQLException(exp);
		} finally {
			if (connection != null)
				connection.close();
		}
		
		return gcm;
	}
	
	public void createRelation(User user, List<String> relations) {
		
	}
	
	public void createAuthenticationProcess(String phone, String pin) throws SQLException {
		
		Connection connection = null;
		CallableStatement statement = null;
		
		try {
			connection = ConnectionManager.getInstance().getConnection();
		
			statement = connection.prepareCall("{ Call create_authentication (?,?,?) } ");
			statement.setString(1, phone);
			statement.setString(2, pin);
			statement.setString(3, "SMS Sent");

			statement.execute();
			
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
	
	public Authentication getAuthenticationByPhone(String phone) throws SQLException {

		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		
		Authentication authentication = null;
		
		try {
			connection = ConnectionManager.getInstance().getConnection();
		
			statement = connection.createStatement();

			rs = statement.executeQuery(String.format("SELECT * FROM authentication WHERE auth_string=%s", phone));
			
			if (rs.next()) {
				authentication = new Authentication();
				
				authentication.setPhone( rs.getString("auth_string") );
				authentication.setPin( rs.getString("pin") );
			}
			
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new SQLException(exp);
		} finally {
			if (connection != null)
				connection.close();
		}
		
		return authentication;
	}
	
	public List<String> checkTimeSenseUsers(String ownerId, List<String> numbers) throws SQLException {
		
		List<String> batch = new ArrayList<>();
		
		Map<String,String> timeSense = new HashMap<>();
		
		for (int i=0; i <numbers.size(); i++) {
			
			batch.add(numbers.get(i));
			
			if (i%18 == 0) {
				timeSense.putAll ( checkUserInDatabase(batch) );
				
				batch.clear();
			}
		}
		
		timeSense.putAll( checkUserInDatabase(batch) );
		
		System.out.println("Owner id :" + ownerId);
		if (ownerId != null && !"".equals(ownerId))
			addRelation(ownerId, timeSense);
		
		return new ArrayList<>(timeSense.values());
	}
	
	private void addRelation(String ownerId, Map<String,String> numbers) throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		String ownerKey = null;
		
		try {
			connection = ConnectionManager.getInstance().getConnection();
			
			Statement readS = connection.createStatement();

			rs = readS.executeQuery(String.format("SELECT * FROM master_user WHERE user_id=%s", ownerId));
			
			if (rs.next()) {
				ownerKey = rs.getString("id");
			}
			
			readS.close();
			rs.close();
		
			statement = connection.prepareStatement("INSERT INTO contact_relation (owner_id,relation_id) VALUES (?,?)");
			
			for (String number : numbers.keySet()) {
				statement.setString(1, ownerKey);
				statement.setString(2, number);
				
				statement.addBatch();
			}
			
			statement.executeBatch();
			
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
	
	private Map<String,String> checkUserInDatabase(List<String> numbers) throws SQLException {
		
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		
		Authentication authentication = null;
		
		Map<String,String> timeSense = new HashMap<>();
		
		try {
			connection = ConnectionManager.getInstance().getConnection();
		
			statement = connection.createStatement();
			
			String strNumbers = StringUtil.getCommaSeperated(numbers);
			
			rs = statement.executeQuery(String.format("select * from master_user where user_id in (%s)", strNumbers));
			
			if (rs.next()) {
				timeSense.put( rs.getString("id"), rs.getString("user_id") );
			}
			
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new SQLException(exp);
		} finally {
			if (connection != null)
				connection.close();
		}
		
		return timeSense;
	}

}
