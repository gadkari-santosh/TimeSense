package src.com.android.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.san.timesense.dto.TimeCode;
import com.san.timesense.service.TimeService;
import com.san.timesense.util.FileUtil;


public class TestTimeService {

	@Test
	public void testTimeCode() throws Exception {
		TimeService service = TimeService.getInstance();
		
		String json = getFile(System.getProperty("user.dir")+"/assets/timezone.json");
		
		Type listOfObjects = new TypeToken<List<TimeCode>>(){}.getType();
		
		Gson gson = new Gson();
        List<TimeCode> timeCodes = gson.fromJson(json, listOfObjects);
        
        Map<String, List<TimeCode>> TIME_CACHE = new LinkedHashMap<String, List<TimeCode>>();
        Collections.sort(timeCodes);
        
        List<TimeCode> perDialCode = null;
        
        String dialCode = null;
        for (TimeCode timeCode : timeCodes) {
        	
        	dialCode = timeCode.getDialCode();
        	if (TIME_CACHE.containsKey(dialCode)) {
        		perDialCode = TIME_CACHE.get(dialCode);
        	} else {
        		perDialCode = new ArrayList<TimeCode>();
        	}
        	
        	perDialCode.add(timeCode);
        	
        	TIME_CACHE.put(timeCode.getDialCode(), perDialCode);
        }
        
        service.getAllTimeZoneInfo().putAll(TIME_CACHE);
        
        TimeCode timeCode = service.getTimeCode("+61 89162");
        Assert.assertEquals("Cocos", timeCode.getCountry());
        
        TimeCode timeCode1 = service.getTimeCode("+61 89164");
        Assert.assertEquals("Christmas Island", timeCode1.getCountry());
        
        TimeCode timeCode2 = service.getTimeCode("+61 35464");
        Assert.assertEquals("Australia", timeCode2.getCountry());
	}
	
	private String getFile(String name) throws Exception {
		
		StringBuffer buffer = new StringBuffer();
		
		BufferedReader reader = new BufferedReader( new FileReader( new File(name)));
		
		String line = null;
		while ( (line=reader.readLine()) != null) {
			buffer.append(line).append("\n");
		}
		
		reader.close();
		
		return buffer.toString();
	}
}
