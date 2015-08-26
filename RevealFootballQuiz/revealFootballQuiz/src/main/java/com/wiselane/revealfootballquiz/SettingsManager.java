package com.wiselane.revealfootballquiz;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SettingsManager {
	
	private static final String PREFS_NAME = "SettingsPrefs";
	private static final String VIBRATE = "vibrate";
	private static final String SOUND = "sound";

	public static void setVibrateSetting(Context context, boolean value){
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putBoolean(VIBRATE, value);
		editor.commit();
	}
	
	public static boolean getVibrateSetting(Context context){
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		boolean vibrate = prefs.getBoolean(VIBRATE, true);
		return vibrate;
	}
	
	public static void setSoundSetting(Context context, boolean value){
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putBoolean(SOUND, value);
		editor.commit();
	}
	
	public static boolean getSoundSetting(Context context){
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		boolean sound = prefs.getBoolean(SOUND, true);
		return sound;
	}
	
}
