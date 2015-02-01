package com.service;

import java.sql.SQLException;
import java.util.List;

import com.dao.DAO;
import com.dataobjects.User;

public class UserService {

	private static final UserService INSTANCE = new UserService();
	
	public static UserService getInstance() {
		return INSTANCE;
	}
	
	public long createNewUser(User user) {
		DAO dao = new DAO();
		try {
			return dao.createNewUser(user);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<String> findTimeSenseUsers(String ownerId, List<String> numbers) {
		DAO dao = new DAO();
		try {
			return dao.checkTimeSenseUsers(ownerId, numbers);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<User> findGCMCodes(String id) {
		DAO dao = new DAO();
		try {
			return dao.getAllRelatedTrades(id);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}