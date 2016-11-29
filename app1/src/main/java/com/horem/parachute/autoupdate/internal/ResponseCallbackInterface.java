package com.horem.parachute.autoupdate.internal;


public interface ResponseCallbackInterface {
	void onFoundLatestVersion(String version);
	void onCurrentIsLatest();
}
