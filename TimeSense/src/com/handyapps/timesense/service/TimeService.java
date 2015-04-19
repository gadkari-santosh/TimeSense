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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

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
	
	public static final String DATE_TIME_FORMAT = "d MMM yyyy hh:mm a";
	
	private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
	private SimpleDateFormat dayFormat = new SimpleDateFormat("d MMM yyyy");
	
	private static Map<String, List<TimeCode>> TIME_CACHE = new LinkedHashMap<String, List<TimeCode>>();
	
	private Context context = null;
	
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
		
		Map<String, String> timeZoneUpdates = TimeSenseUsersService.getInstance().getTimeSenseUserTimeZones();
		
		if (phoneNumber.trim().startsWith("+")) {
			
			phoneNumber = phoneNumber.replace("+", "");
			
			TimeCode timeCode = getTimeCode(phoneNumber);

			if (timeCode != null) {
				
				contact.setCountry(timeCode.getCountry());
				
				if (timeZoneUpdates != null && timeCode != null) {
					if (timeZoneUpdates.containsKey( phoneNumber ) ) {
						contact.setParkTimeZone( timeCode.getTimeZone() );
						
						contact.setTimeZone( timeZoneUpdates.get( phoneNumber) );
						
					} else {
						contact.setTimeZone(timeCode.getTimeZone());
					}
				} else {
					contact.setTimeZone(timeCode.getTimeZone());
				}
				
				Calendar.getInstance().clear();
				
				if (contact.getParkTimeZone() != null && contact.isAwayFromHome()) {
					timeZone = contact.getParkTimeZone();
				} else {
					timeZone = contact.getTimeZone();
				}
				
				Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
				calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
				timeFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
				
				contact.setTime(timeFormat.format(calendar.getTime()));
				contact.setDate(dayFormat.format(calendar.getTime()));
				contact.setKaal(getKaal(calendar.get(Calendar.HOUR_OF_DAY)));
				
				int hour = calendar.get(Calendar.HOUR_OF_DAY);
				
				if (SettingsService.getInstance().getSettings().isCallAllowed(hour)) {
					contact.setStatus("Available");
				} else {
					contact.setStatus("Avoid Calling.");
				}
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
								timeFormat.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
								
								contact.setCountry(timeCode.getCountry());
								contact.setTime(timeFormat.format(calendar.getTime()));
								contact.setTimeZone(timeCode.getTimeZone());
								contact.setDate(dayFormat.format(calendar.getTime()));
								
								contact.setKaal(getKaal(calendar.get(Calendar.HOUR_OF_DAY)));
								
								int hour = calendar.get(Calendar.HOUR_OF_DAY);
								
								if (SettingsService.getInstance().getSettings().isCallAllowed(hour)) {
									contact.setStatus("Available");
								} else {
									contact.setStatus("Avoid Calling.");
								}
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
	
	public TimeCode getTimeCodeByTimeZone(String timeZone) {
		
		for (List<TimeCode> codes : TIME_CACHE.values()) {
			for (TimeCode code : codes) {
				if (code.getTimeZone().equalsIgnoreCase(timeZone)) {
					return code;
				}
			}
		}
		return null;
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
			timeFormat.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
			
			return timeFormat.format(calendar.getTime());
		}
		
		return null;
	}
	
	public Date getDateByTimeCode(TimeCode timeCode) {
		
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MMM dd yyyy hh:mm a Z");
		
		if (timeCode != null) {
			Calendar.getInstance().clear();
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeCode.getTimeZone()));
			calendar.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
			dateTimeFormat.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
			
			String format2 = dateTimeFormat.format(calendar.getTime());
			
			try {
				DateTime dateTime = new DateTime(calendar.getTime().getTime(), DateTimeZone.forID(timeCode.getTimeZone()));
				return dateTime.toDate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public String getTime(Date date, TimeCode timeCode) {
		
		if (timeCode != null) {
		Calendar.getInstance().clear();
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeCode.getTimeZone()));
		calendar.setTime(date);
		calendar.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
		timeFormat.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
		
		return timeFormat.format(calendar.getTime());
		}
		return null;
	}
	
	public String getDate(Date date, TimeCode timeCode) {
		
		if (timeCode != null) {
		Calendar.getInstance().clear();
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeCode.getTimeZone()));
		calendar.setTime(date);
		calendar.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
		dayFormat.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
		
		return dayFormat.format(calendar.getTime());
		}
		return null;
	}
	
	public int getHour(TimeCode timeCode) {
		
		if (timeCode != null) {
			Calendar.getInstance().clear();
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeCode.getTimeZone()));
			calendar.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
			timeFormat.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
			
			return calendar.get(Calendar.HOUR_OF_DAY);
		}
		
		return 0;
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
	
	public Kaal getKaal(Date date, TimeCode timeCode) {
		
		if (timeCode != null) {
			Calendar.getInstance().clear();
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeCode.getTimeZone()));
			calendar.setTime(date);
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
			timeFormat.setTimeZone(TimeZone.getTimeZone(timeCode.getTimeZone()));
			
			return timeFormat.format(calendar.getTime());
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
	
	public String getDate(Date timeCode) {
		SimpleDateFormat dayFormat = new SimpleDateFormat("d MMM yyyy");
		
		if (timeCode != null) {
			return dayFormat.format(timeCode);
		} else {
			return null;
		}
	}
	
	public String getTime(Date timeCode) {
		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
		
		if (timeCode != null) {
			return timeFormat.format(timeCode);
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
	
	public String getLocalDialCode() {
		String timeZone = TimeZone.getDefault().getID();
		
		for (List<TimeCode> codes : TIME_CACHE.values()) {
			for (TimeCode code : codes) {
				if (timeZone.equalsIgnoreCase(code.getTimeZone())) {
					return code.getDialCode();
				}
			}
		}
 		
		return "00";
	}
	
	public boolean isLoaded() {
		if (TIME_CACHE != null && TIME_CACHE.size() > 50) {
			return true;
		}
		
		return false;
	}
}