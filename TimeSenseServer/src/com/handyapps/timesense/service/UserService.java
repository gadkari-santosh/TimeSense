package com.handyapps.timesense.service;

import java.sql.SQLException;
import java.util.List;

import com.handyapps.timesense.dao.DAO;
import com.handyapps.timesense.dataobjects.User;

public class UserService {

	private static final UserService INSTANCE = new UserService();
	
	public static UserService getInstance() {
		return INSTANCE;
	}
	
	public void createNewUser(User user) throws SQLException {
		DAO dao = new DAO();
		try {
			dao.createNewUser(user);
		} catch (SQLException e) {
			throw e;
		}
	}
	
	public List<User> findTimeSenseUsers(String ownerId, List<String> numbers) {
		DAO dao = new DAO();
		try {
			return dao.findTimeSenseUserNumbers(ownerId, numbers);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}