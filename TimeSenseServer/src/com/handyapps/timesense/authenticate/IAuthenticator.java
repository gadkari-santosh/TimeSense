package com.handyapps.timesense.authenticate;

import com.handyapps.timesense.dataobjects.User;
import com.handyapps.timesense.dataobjects.response.Status;

public interface IAuthenticator {

	Status initiate(User user);
	
	Status authenticate(User user);
}
