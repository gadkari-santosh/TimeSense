package com.handyapps.timesense.service;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import android.content.Context;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handyapps.timesense.R;
import com.handyapps.timesense.constant.Contact;
import com.handyapps.timesense.constant.Country;
import com.handyapps.timesense.constant.Kaal;
import com.handyapps.timesense.dataobjects.CallPrefix;
import com.handyapps.timesense.dataobjects.Settings;
import com.handyapps.timesense.dataobjects.TimeCode;
import com.handyapps.timesense.util.FileUtil;

public class TimeService {

	private static final TimeService INSTANCE = new TimeService();
	
	private SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
	private SimpleDateFormat dayFormat = new SimpleDateFormat("MMM dd''yy");
	
	private static Map<String, List<TimeCode>> TIME_CACHE = new LinkedHashMap<String, List<TimeCode>>();
	
	Context context = null;
	
	SettingsService service = SettingsService.getInstance();
	Settings settings = service.getSettings();
	
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
		String timeZone = null;
		
		String phoneNumber = removeSpaces (contact.getPhoneNumber());
		
		contact.setKaal(Kaal.NA);
		
		Map<String, String> timeZoneUpdates = settings.getTimeZoneUpdates();
		
//		Map<String, String> timeZoneUpdates = settings.getTimeZoneUpdates();
//		
//		if (timeZoneUpdates != null && timeCode != null) {
//			if (timeZoneUpdates.containsKey( phoneNumber ) ) {
//				timeCode.setParkTimeZone( timeCode.getTimeZone() );
//				
//				timeCode.setTimeZone( timeZoneUpdates.get( phoneNumber) );
//				
//				if (! timeCode.isAwayFromHome()) {
//					//timeZoneUpdates.remove( phoneNumber );
//					
//					service.saveSettings();
//				}
//			}
//		}
		
		
		if (phoneNumber.trim().startsWith("+")) {
			
			phoneNumber = phoneNumber.replace("+", "");
			
			TimeCode timeCode = getTimeCode(phoneNumber);

			if (timeCode != null) {
				
				contact.setCountry(timeCode.getCountry());
				
				if (timeZoneUpdates != null && timeCode != null) {
					if (timeZoneUpdates.containsKey( phoneNumber ) ) {
						contact.setParkTimeZone( timeCode.getTimeZone() );
						
						contact.setTimeZone( timeZoneUpdates.get( phoneNumber) );
						
//						if (! contact.isAwayFromHome()) {
//							timeZoneUpdates.remove( phoneNumber );
//							
//							service.saveSettings();
//						}
					} else {
						contact.setTimeZone(timeCode.getTimeZone());
					}
				} else {
					contact.setTimeZone(timeCode.getTimeZone());
				}
					
				
//				if (timeZoneUpdates.containsKey(phoneNumber)) {
//					timeZone = timeZoneUpdates.get( phoneNumber );
//					
//					contact.setTimeZone(timeZone);
//					contact.setParkTimeZone( timeCode.getTimeZone() );
//				} else {
//					contact.setTimeZone(timeCode.getTimeZone());
//				}
				
				Calendar.getInstance().clear();
				timeZone = contact.getTimeZone();
				
				Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
				calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
				format.setTimeZone(TimeZone.getTimeZone(timeZone));
				
				contact.setTime(format.format(calendar.getTime()));
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
	
	private TimeCode getTimeCode(String phoneNumber) {
		
		TimeCode timeCode = null;
		
		if (TIME_CACHE.size() == 0) {
			init(context);
		}
		
		if (phoneNumber.startsWith("+")) {
			phoneNumber = phoneNumber.replace("+", "");
			phoneNumber = phoneNumber.replace(" ", "");
		} 
		
		List<TimeCode> timeCodes = null;
		
		int maxCount = phoneNumber.length() > 4 ? 4 : phoneNumber.length();
		
		for (int i=maxCount; i>=1; i--) {
			
			String code = phoneNumber.substring(0,i);
			
			timeCodes = TIME_CACHE.get(code);
			
			if (timeCodes != null)
				break;
		}
		
		if (timeCodes != null && timeCodes.size() > 0) {
			for (TimeCode code : timeCodes) {
				List<String> areaCodes = code.getAreaCodes();
				
				if (areaCodes != null) {
					for (String areaCode : areaCodes) {
						
						if (phoneNumber.startsWith(code.getDialCode()+areaCode)) {
							timeCode = code;
							break;
						}
					}
				} else {
					timeCode = code;
				}
			}
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
	
	public Kaal getKaal(TimeCode timeCode) {
		
		if (timeCode != null) {
			Calendar.getInstance().clear();
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeCode.getTimeZone()));
			calendar.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
			
			return getKaal(calendar.get(Calendar.HOUR_OF_DAY));
		}
		
		return null;
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
	
	public Map<String, List<TimeCode>> getAllTimeZoneInfo() {
		return TIME_CACHE;
	}
}