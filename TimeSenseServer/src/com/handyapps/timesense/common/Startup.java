package com.handyapps.timesense.common;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.handyapps.timesense.dao.ConnectionManager;

public class Startup extends HttpServlet {

	private static final long serialVersionUID = 6765235338829048827L;

	public void init() throws ServletException
    {
		ConnectionManager.getInstance().init();
    }
}
