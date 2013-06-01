package com.generalprocessingunit.dodecathedral.core;


//consider: http://developer.android.com/reference/android/os/AsyncTask.html
//http://stackoverflow.com/questions/8693778/taking-heavy-computation-off-the-android-ui-thread

public class UserData {	
	private IDodecathedral _parent;
	private static final String _userData = "UserData";
	
	public static Data data;
	
	public UserData(){
		load();
	}
	
	public static void save(){
		/*SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(_parent.getApplicationContext());
		Editor prefsEditor = appSharedPrefs.edit();		
		
		Gson gson = new Gson();		
		String json = gson.toJson(data);
		
		prefsEditor.putString(_userData, json);
		prefsEditor.commit();*/
	}
	
	void load(){
		/*SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(_parent.getApplicationContext());
		Gson gson = new Gson();		
		String json = appSharedPrefs.getString(_userData, "");
		if(!json.equals("")){
			data = gson.fromJson(json, Data.class);
		}else{
			data = new Data();
		}*/
		data=new Data();
	}
	
	public class Data{
		public int longestRandomSequencePlayed = 0;
		//Map<DeltaSequence, Map<String,?>> deltaSequencesCompleted;
		
		
	}
}
