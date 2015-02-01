package com.san;

import java.util.HashMap;
import java.util.Map;

public class Cache {

	private static final Cache INSTANCE = new Cache();
	
	private Map<String, String> map = new HashMap<String, String>();
	
	public static Cache getInstance() {
		return INSTANCE;
	}
	
	public void add(String id, String hash) {
		map.put(id, hash);
	}
	
	public String getFirst() {
		return map.get(0);
	}
	
	public String get(String id) {
		return map.get(id);
	}
}
