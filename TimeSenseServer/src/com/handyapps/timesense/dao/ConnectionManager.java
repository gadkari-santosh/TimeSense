package com.handyapps.timesense.dao;

import static com.handyapps.timesense.constant.AppConstants.PROP_DATA_SOURCE;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.Logger;

import com.handyapps.timesense.util.PropUtil;

public class ConnectionManager {

	private static final ConnectionManager INSTANCE = new ConnectionManager();
	
	private static final Logger LOG = Logger.getLogger(ConnectionManager.class);
	
	private static final String DB_URL 		= ".db.url";
	private static final String DB_USERNAME = ".db.user";
	private static final String DB_PASSWORD = ".db.password";
	private static final String DB_DRIVER 	= ".db.driver";
	private static final String DB_MAX_CON 	= ".db.max.open";

	private BasicDataSource connectionPool = null; 
			
	private ConnectionManager() {}
	
	public static ConnectionManager  getInstance() {
		return INSTANCE;
	}

	public void init() {
		
		String dataSource = PropUtil.getProperty(PROP_DATA_SOURCE);
		LOG.info("Loading Data Source : " + dataSource);
		
		String url      = PropUtil.getProperty(dataSource+DB_URL);
		String username = PropUtil.getProperty(dataSource+DB_USERNAME);
		String password = PropUtil.getProperty(dataSource+DB_PASSWORD);
		String driver   = PropUtil.getProperty(dataSource+DB_DRIVER);
		
		int maxOpenCon  = PropUtil.getIntProperty(dataSource+DB_MAX_CON);
		
		connectionPool = new BasicDataSource();
		connectionPool.setUrl(url);
		connectionPool.setUsername(username);
		connectionPool.setPassword(password);
		connectionPool.setMaxIdle(2);
		connectionPool.setMaxTotal(maxOpenCon);
		connectionPool.setDriverClassName(driver);
		
		LOG.info("Connection pool initialized successfully.");
	}
	
	public boolean checkStatus() {
		try {
			Connection connection = connectionPool.getConnection();
			connection.close();
			
			return true;
		} catch (Throwable th) {
			LOG.error("Exception: ", th);
			return false;
		}
	}
	
	public Connection getConnection() throws SQLException {
		return connectionPool.getConnection();
	}
	
	public void close(Connection connection) {
		
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException exp) {
				LOG.error("Unable to close connection", exp);
			}
		}
	}
}
