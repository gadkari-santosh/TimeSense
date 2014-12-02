package com.san.timesense.service;

import static com.san.timesense.constant.AppContant.PREFERENCES_FILE;
import android.content.Context;
import android.widget.Toast;

import com.san.timesense.dto.Settings;
import com.san.timesense.util.FileUtil;
import com.san.timesense.util.GsonUtil;

public class SettingsService {
	
	private static final SettingsService INSTANCE = new SettingsService();
	
	private Context context = null;
	
	private Settings settings = null;
	
	public static SettingsService getInstance() {
		return INSTANCE;
	}
	
	public void init(Context context) {
		this.context = context;
		
		loadSettings();
	}
	
	private Settings loadSettings() {
		try {
			String settingsJson = FileUtil.getFile(context, PREFERENCES_FILE);
			if (settingsJson == null){
				settings = Settings.getInstance();
				settings.init(context);
			} else {
				settings = GsonUtil.getObject(settingsJson, Settings.class);
			}
		} catch (Exception exp) {
			exp.printStackTrace();
			Toast.makeText(context, "Unable to load settings. "+exp.toString(), 100).show();
		}
		
		return settings;
	}
	
	public void saveSettings(Settings settings) {
		String jSon = GsonUtil.getJSon(settings, Settings.class);
		
		FileUtil.saveFileInPhone(context, PREFERENCES_FILE, jSon);
	}
	
	public void saveSettings() {
		saveSettings(this.settings);
	}
	
	public Settings getSettings() {
		return this.settings;
	}
	
	
}
