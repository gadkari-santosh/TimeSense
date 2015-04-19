package com.handyapps.timesense.util;

import static com.handyapps.timesense.constant.AppConstants.PROP_FILE_NAME;

import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Resposible for loading and managing application properties.
 *  
 * @author Santosh
 */
public class PropUtil {

	private static final Properties PROPERTIES = new Properties();
	
	private static final Logger LOG = Logger.getLogger(PropUtil.class);
	
	static {
		try {
			PROPERTIES.load( PropUtil.class.getResourceAsStream(PROP_FILE_NAME));
			
			LOG.info("Property file loaded successfully");
		} catch (Exception exp) {
			LOG.error("Unable to load properties file", exp);
			throw new RuntimeException("Unable to load app.properties");
		}
	}
	
	public static String getProperty(String propertyName) {
		return PROPERTIES.getProperty(propertyName);
	}
	
	public static int getIntProperty(String propertyName) {
		return Integer.parseInt(PROPERTIES.getProperty(propertyName));
	}
	
	public static boolean getBooleanProperty(String propertyName) {
		return Boolean.parseBoolean(PROPERTIES.getProperty(propertyName));
	}
}
