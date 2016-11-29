/*
package com.horem.parachute.autoupdate.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

// 持久化
public class VersionPersistent {
	
	private SharedPreferences shared;
	
	public VersionPersistent(Context context){
		shared = context.getSharedPreferences("auto_update_version", Context.MODE_PRIVATE);
	}
	
	public void save( version){
		if(version == null) return;
		Editor editor = shared.edit();
		editor.clear();
		editor.putInt("code", version.code);
		editor.putString("feature", version.feature);
		editor.putString("name", version.name);
		editor.putString("url", version.targetUrl);
		editor.commit();
	}
	
	public void clear(){
		Editor editor = shared.edit();
		editor.clear();
		editor.commit();
	}
	
	public Version load(){
		if(shared.contains("code")){
			int code = shared.getInt("code", 0);
			String name = shared.getString("name", null);
			String feature = shared.getString("feature", null);
			String url = shared.getString("url", null);
			if(name == null || url == null) return null;
			return new Version(code, name, feature, url);
		}
		return null;
	}
	
}
*/
