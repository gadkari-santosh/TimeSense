package com.handyapps.timesense.widget;

import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.text.format.Time;
import android.util.AttributeSet;
  
/** 
 * DigitalClock 
 * @author  
 * 
 */  
public class DigiClock extends android.widget.DigitalClock {  
  
    Time mCalendar;  
    private final static String m12 = "h:mm aa";//h:mm:ss aa  
    private final static String m24 = "k:mm";//k:mm:ss  
    private FormatChangeObserver mFormatChangeObserver;  
  
    private Runnable mTicker;  
    private Handler mHandler;  
  
    private boolean mTickerStopped = false;  
  
    String mFormat;  
    
    private String timezone;
    
    public DigiClock(Context context, String timezone) {  
        super(context);  
        this.timezone = timezone;
        initClock(context);  
    }  
  
    public DigiClock(Context context, AttributeSet attrs, String timezone) {  
        super(context, attrs);  
        this.timezone = timezone;
        initClock(context);  
    }  
  
    private void initClock(Context context) {  
        Resources r = context.getResources();  
  
        if (mCalendar == null) {  
            mCalendar = new Time(timezone);
        }  
  
        mFormatChangeObserver = new FormatChangeObserver();  
        getContext().getContentResolver().registerContentObserver(  
                Settings.System.CONTENT_URI, true, mFormatChangeObserver);  
  
    }  
  
    @Override  
    protected void onDetachedFromWindow() {  
        super.onDetachedFromWindow();  
        mTickerStopped = true;  
    }  
  
    /** 
     * Pulls 12/24 mode from system settings 
     */  
    private boolean get24HourMode() {  
        return android.text.format.DateFormat.is24HourFormat(getContext());  
    }  
  
    private void setFormat() {  
        if (get24HourMode()) {  
            mFormat = m24;  
        } else {  
            mFormat = m12;  
        }  
    }  
  
    private class FormatChangeObserver extends ContentObserver {  
        public FormatChangeObserver() {  
            super(new Handler());  
        }  
  
        @Override  
        public void onChange(boolean selfChange) {  
            setFormat();  
        }  
    }  
}  
