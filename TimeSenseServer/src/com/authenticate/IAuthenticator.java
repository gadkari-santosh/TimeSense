package com.authenticate;

import com.dataobjects.Status;
import com.dataobjects.User;

public interface IAuthenticator {

	Status initiate(User user);
	
	Status authenticate(User user);
	
	String postAuthenticate(User user);
}
