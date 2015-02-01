package com.dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionManager {

	private static final ConnectionManager INSTANCE = 
			new ConnectionManager();
	
	public static ConnectionManager getInstance() {
		return INSTANCE;
	}
	
	public Connection getConnection() throws Exception {
		// this will load the MySQL driver, each DB has its own driver
	      Class.forName("com.mysql.jdbc.Driver");
	      // setup the connection with the DB.
	      return DriverManager
	          .getConnection("jdbc:mysql://localhost/timesens_master?"
	              + "user=timesens_admin&password=admin21");

	}
}