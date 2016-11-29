package com.horem.parachute.autoupdate;


/**
 * 自定义显示最新版本信息
 * 
 * @author ilovedeals
 * 
 */
public interface DisplayerInterface {
	void showFoundLatestVersion(String version);
	void showIsLatestVersion();
}