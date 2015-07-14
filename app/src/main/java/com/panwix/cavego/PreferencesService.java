package com.panwix.cavego;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesService {
	private Context context;
	
	public PreferencesService(Context context){
		this.context = context;
	}
	/**
	 * ������߷�
	 * @param score
	 */
	public  void save(Integer score){
		SharedPreferences preferences = context.getSharedPreferences("scores",Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putInt("HighScore", score);
		editor.commit();
	}
	
	public int getPreferences(){
		SharedPreferences Prefereces = context.getSharedPreferences("scores", Context.MODE_PRIVATE);
		return Prefereces.getInt("HighScore", 30);
	}
}
