package com.handyapps.timesense.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;


public class RestUtil {

	public static String executePost(String url, String item) throws Exception {
		// create default HTTP Client
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
        
        // Create new getRequest with below mentioned URL
        HttpPost post = new HttpPost(url);
        post.setEntity(new StringEntity(item));
        
        HttpResponse response = httpClient.execute(post);
        
        return EntityUtils.toString(response.getEntity());
	}
	
	public static String executeGet(String url) throws Exception {
		// create default HTTP Client
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);

        // Create new getRequest with below mentioned URL
        HttpGet get = new HttpGet(url);
      
        // Execute your request and catch response
        try {
        	HttpResponse response = httpClient.execute(get);
        	return EntityUtils.toString(response.getEntity());
        } catch (Throwable th) {
        	th.printStackTrace();
        	return "Unable to connect. Please check your internet connection";
        }
	}
}
