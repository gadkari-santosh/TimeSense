package com.san.timesense.service;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import android.content.Context;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.san.timesense.R;
import com.san.timesense.constant.Contact;
import com.san.timesense.constant.Country;
import com.san.timesense.constant.Kaal;
import com.san.timesense.dto.CallPrefix;
import com.san.timesense.dto.Settings;
import com.san.timesense.dto.TimeCode;
import com.san.timesense.util.FileUtil;

public class TimeService {

	private static final TimeService INSTANCE = new TimeService();
	
	private SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
	private SimpleDateFormat dayFormat = new SimpleDateFormat("MMM dd''yy");
	
	private static Map<String, TimeCode> TIME_CACHE = new LinkedHashMap<String, TimeCode>();
	
	Context context = null;
	
	private TimeService() {}
	
	public static TimeService getInstance() {
		return INSTANCE;
	}
	
	public void init(Context context) {
		this.context = context;
		
		Type listOfObjects = new TypeToken<List<TimeCode>>(){}.getType();
		
		String json = FileUtil.getFileFromAsset(context, "timezone.json");
		
		Gson gson = new Gson();
        List<TimeCode> timeCodes = gson.fromJson(json, listOfObjects);
        
        Collections.sort(timeCodes);
        
        for (TimeCode timeCode : timeCodes) {
        	TIME_CACHE.put(timeCode.getDialCode(), timeCode);
        }
	}
	
	
	private String removeSpaces(String phoneNumber) {

		phoneNumber = phoneNumber.replace(" ", "");
		phoneNumber = phoneNumber.replace("(", "");
		phoneNumber = phoneNumber.replace(")", "");
		
		return phoneNumber;
	}
	
	public void populateTimeInformation(Contact contact) {
		
		int prefixLength = 0;
		String prefix = null;
		
		String phoneNumber = removeSpaces (contact.getPhoneNumber());
		
		contact.setKaal(Kaal.NA);
		
		if (phoneNumber.trim().startsWith("+")) {
			
			phoneNumber = phoneNumber.replace("+", "");
			
			TimeCode timeCode = getTimeCode(phoneNumber);

			if (timeCode != null) {
			
				Calendar.getInstance().clear();
				Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeCode.getTimeZone()));
				calendar.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
				format.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
				
				contact.setCountry(timeCode.getCountry());
				contact.setTime(format.format(calendar.getTime()));
				contact.setTimeZone(timeCode.getTimeZone());
				contact.setDate(dayFormat.format(calendar.getTime()));
				
				contact.setKaal(getKaal(calendar.get(Calendar.HOUR_OF_DAY)));
			}
		} else {
		
			List<CallPrefix> callPrefixs = SettingsService.getInstance().getSettings().getCallPrefixs();
			
			if (callPrefixs != null) {
				for (CallPrefix callPrefix : callPrefixs) {
					prefixLength = callPrefix.getPrefix().length();
					
					if (phoneNumber.length() > prefixLength) {
						prefix = phoneNumber.substring(0, prefixLength);
						
						if (callPrefix.getPrefix().equalsIgnoreCase(prefix)) {
							contact.setPrefix(prefix);
							
							phoneNumber = phoneNumber.substring(prefixLength, phoneNumber.length());
							phoneNumber = removeSpaces (phoneNumber);
							TimeCode timeCode = getTimeCode(phoneNumber);
			
							if (timeCode != null) {
							
								Calendar.getInstance().clear();
								Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeCode.getTimeZone()));
								calendar.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
								format.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
								
								contact.setCountry(timeCode.getCountry());
								contact.setTime(format.format(calendar.getTime()));
								contact.setTimeZone(timeCode.getTimeZone());
								contact.setDate(dayFormat.format(calendar.getTime()));
								
								contact.setKaal(getKaal(calendar.get(Calendar.HOUR_OF_DAY)));
							}
						}
					}
				}
			}
		}
	}
	
	public TimeCode getTimeCodeByPhoneNumber(String phoneNumber) {
		getTime(phoneNumber);
		
		int prefixLength = 0;
		String prefix = null;
		
		List<CallPrefix> callPrefixs = SettingsService.getInstance().getSettings().getCallPrefixs();
		
		if (callPrefixs == null) {
			Settings.getInstance().init(context);
			callPrefixs = SettingsService.getInstance().getSettings().getCallPrefixs();
		}
		
		TimeCode code = null;
		if (phoneNumber.startsWith("+")) {
			code = getTimeCode(phoneNumber);
		} else {
		
			if (callPrefixs != null) {
				for (CallPrefix callPrefix : callPrefixs) {
					prefixLength = callPrefix.getPrefix().length();
					
					if (phoneNumber.length() > prefixLength) {
						prefix = phoneNumber.substring(0, prefixLength);
						
						if (callPrefix.getPrefix().equalsIgnoreCase(prefix)) {
							
							phoneNumber = phoneNumber.substring(prefixLength, phoneNumber.length());
							phoneNumber = removeSpaces (phoneNumber);
							code = getTimeCode(phoneNumber);
						}
					}
				}
			}
		}
		
		return code;
	}
	
	public TimeCode getTimeCode(String phoneNumber) {
		
		if (TIME_CACHE.size() == 0) {
			init(context);
		}
		
		if (phoneNumber.startsWith("+")) {
			phoneNumber = phoneNumber.replace("+", "");
		} 
		
		TimeCode timeCode = null;
		
		int maxCount = phoneNumber.length() > 4 ? 4 : phoneNumber.length();
		
		for (int i=maxCount; i>=1; i--) {
			
			String code = phoneNumber.substring(0,i);
			
			timeCode = TIME_CACHE.get(code);
			
			if (timeCode != null)
				break;
		}
		
		return timeCode;
	}
	
	private Kaal getKaal(int hour) {
		
		if (hour >= 4 && hour < 9) {
			return Kaal.EarlyMorning;
		} else if (hour >= 9 && hour < 12) {
			return Kaal.Morning;
		} else if (hour >= 12 && hour < 17) {
			return Kaal.Afternoon;
		} else if (hour >= 17 && hour < 20) {
			return Kaal.Evening;
		} else if (hour >= 20 && hour < 24) {
			return Kaal.Night;
		}  else if (hour >= 0 && hour < 4) {
			return Kaal.MidNight;
		}
		
		return null;
	}
	
	public String getCountryCode(String phoneNumber) {
		String code = "";
		
		try {
			phoneNumber = phoneNumber.trim();
			phoneNumber = phoneNumber.replace(" ", "");
			phoneNumber = phoneNumber.replace("(", "");
			phoneNumber = phoneNumber.replace(")", "");
			
			if (phoneNumber.startsWith("+")) {
				
				phoneNumber = phoneNumber.replace("+", "");
				
				TimeCode timeCode = getTimeCode(phoneNumber);
				code = timeCode == null ? "" : timeCode.getDialCode();
			}
				
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
		
		return code;
	}
	
	public String getCountry(String code) {
		
		TimeCode timeCode = TIME_CACHE.get(code);
		
		if (timeCode != null) {
			
			return timeCode.getCountry();
		} else {
			return null;
		}
	}
	
	public String getTime(String code) {
		TimeCode timeCode = TIME_CACHE.get(code);
		
		if (timeCode != null) {
			Calendar.getInstance().clear();
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeCode.getTimeZone()));
			calendar.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
			format.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
			
			return format.format(calendar.getTime());
		}
		
		return null;
	}
	
	public String getTime(TimeCode timeCode) {
		
		if (timeCode != null) {
			Calendar.getInstance().clear();
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeCode.getTimeZone()));
			calendar.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
			format.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
			
			return format.format(calendar.getTime());
		}
		
		return null;
	}
	
	public Kaal getKaal(String code) {
		TimeCode timeCode = TIME_CACHE.get(code);
		
		if (timeCode != null) {
			Calendar.getInstance().clear();
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeCode.getTimeZone()));
			calendar.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
			
			return getKaal(calendar.get(Calendar.HOUR_OF_DAY));
		}
		
		return null;
	}
	
	public Kaal getKaal(TimeCode timeCode) {
		
		if (timeCode != null) {
			Calendar.getInstance().clear();
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeCode.getTimeZone()));
			calendar.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
			
			return getKaal(calendar.get(Calendar.HOUR_OF_DAY));
		}
		
		return null;
	}
	
	public String getTime(String code, Date date) {
		
		TimeCode timeCode = TIME_CACHE.get(code);
		
		if (timeCode != null) {
			Calendar.getInstance().clear();
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeCode.getTimeZone()));
			calendar.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
			calendar.setTime(date);
			format.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
			
			return format.format(calendar.getTime());
		} else {
			return null;
		}
	}
	
	public String getTime(TimeCode timeCode, Date date) {
		
		if (timeCode != null) {
			Calendar.getInstance().clear();
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeCode.getTimeZone()));
			calendar.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
			calendar.setTime(date);
			format.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
			
			return format.format(calendar.getTime());
		} else {
			return null;
		}
	}
	
	public String getDate(String code, Date date) {
		
		TimeCode timeCode = TIME_CACHE.get(code);
		
		if (timeCode != null) {
			Calendar.getInstance().clear();
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeCode.getTimeZone()));
			calendar.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
			calendar.setTime(date);
			dayFormat.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
			
			return dayFormat.format(calendar.getTime());
		} else {
			return null;
		}
	}
	
	public String getDate(TimeCode timeCode, Date date) {
		
		if (timeCode != null) {
			Calendar.getInstance().clear();
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeCode.getTimeZone()));
			calendar.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
			calendar.setTime(date);
			dayFormat.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
			
			return dayFormat.format(calendar.getTime());
		} else {
			return null;
		}
	}
	
	public String getDate(String code) {
		TimeCode timeCode = TIME_CACHE.get(code);
		
		if (timeCode != null) {
			Calendar.getInstance().clear();
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeCode.getTimeZone()));
			calendar.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
			dayFormat.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
			
			return dayFormat.format(calendar.getTime());
		} else {
			return null;
		}
	}
	
	public String getDate(TimeCode timeCode) {
		
		if (timeCode != null) {
			Calendar.getInstance().clear();
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeCode.getTimeZone()));
			calendar.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
			dayFormat.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
			
			return dayFormat.format(calendar.getTime());
		} else {
			return null;
		}
	}
	
	public void setKaalPic(ImageView imageView, TimeCode code) {
		
		setKaalPic(imageView, this.getKaal(code));
	}
	
	public void setKaalPic(ImageView imageView, Kaal kaal) {
		if (kaal != null)
			switch (kaal) {
				case EarlyMorning:
					imageView.setImageResource(R.drawable.ic_early_morning);
					break;
				case Morning:
					imageView.setImageResource(R.drawable.ic_morning);
					break;
				case Afternoon:
					imageView.setImageResource(R.drawable.ic_afternoon);
					break;
				case Evening:
					imageView.setImageResource(R.drawable.ic_evening);
					break;
				case Night:
					imageView.setImageResource(R.drawable.ic_night);
					break;
				case MidNight:
					imageView.setImageResource(R.drawable.ic_mid_night);
					break;
				default:
				case NA:
					imageView.setImageResource(R.drawable.ic_default_contactpic);
					break;
			}
	}
	
	public void setCountryFlag(ImageView imageView, String countryName) {
		
		try {
			countryName = countryName.replace(" ", "_").toLowerCase();
			Country country = Country.valueOf(countryName);
			
			imageView.setImageResource(country.getDrawable());
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}
	
	public void setKaalPic(ImageView imageView, int hourOftheDay) {
		setKaalPic(imageView, getKaal(hourOftheDay));
	}
	
	public Map<String, TimeCode> getAllTimeZoneInfo() {
		return TIME_CACHE;
	}
}