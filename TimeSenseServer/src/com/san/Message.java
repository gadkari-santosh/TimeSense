package com.san;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

@Path("/message/send/{id}")
public class Message {

	@GET
	public void send(@PathParam("id") String str) {
		
		// create default HTTP Client
        DefaultHttpClient httpClient = new DefaultHttpClient();
        
     // Create new getRequest with below mentioned URL
        HttpPost post = new HttpPost("https://android.googleapis.com/gcm/send");
        post.setHeader("Authorization", "key=AIzaSyCJoBtvp4xa6dPtub576cF9_IYW8Ukm3ME");
        post.setHeader("Content-Type",
                "application/x-www-form-urlencoded;charset=UTF-8");
        
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();

        System.out.println(Cache.getInstance().get(str));
        
        formparams.add(new BasicNameValuePair("registration_id", Cache.getInstance().get(str)));
        formparams.add(new BasicNameValuePair("data.message", "testing"));
        formparams.add(new BasicNameValuePair("data", "just testing"));
        
        String payload = "{ \"data\": { \"score\": \"5x1\",	\"time\": \"15:10\"	},\"registration_ids\": [\"%s\"]}";
        
        try {
			//post.setEntity(new StringEntity(String.format(payload, Cache.getInstance().getFirst())));
        	post.setEntity(new UrlEncodedFormEntity(formparams, "utf-8"));
			
			HttpResponse response = httpClient.execute(post);
			
			BufferedReader rd = new BufferedReader(
	                new InputStreamReader(response.getEntity().getContent()));
	 
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		
		System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
