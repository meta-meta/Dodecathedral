package com.generalprocessingunit.dodecathedral;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

//consider: http://developer.android.com/reference/android/os/AsyncTask.html
//http://stackoverflow.com/questions/8693778/taking-heavy-computation-off-the-android-ui-thread

public class UserData {	
	private Dodecathedral _parent;
	private static final String _userData = "UserData";
	
	public Data data;
	
	void save(){
		SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(_parent.getApplicationContext());
		Editor prefsEditor = appSharedPrefs.edit();		
		
		Gson gson = new Gson();		
		String json = gson.toJson(data);
		
		prefsEditor.putString(_userData, json);
		prefsEditor.commit();
	}
	
	void load(){
		SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(_parent.getApplicationContext());
		Gson gson = new Gson();		
		String json = appSharedPrefs.getString(_userData, "");
		data = gson.fromJson(json, Data.class);			 		
	}
	
	public class Data{
		
	}
}
