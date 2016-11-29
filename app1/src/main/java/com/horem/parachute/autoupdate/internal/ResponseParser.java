package com.horem.parachute.autoupdate.internal;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.horem.parachute.autoupdate.ResponseParserInterface;


public class ResponseParser implements ResponseParserInterface {


	@Override
	public String parser(String response) {
		try {
			JSONTokener jsonParser = new JSONTokener(response);
			JSONObject json = (JSONObject) jsonParser.nextValue();
			// boolean success = json.getBoolean("success");
			String version = null;
			if (1 == json.getInt("statusCode")) {
				// JSONObject dataField = json.getJSONObject("data");
				// int code = dataField.getInt("code");
				int code = 0;
				// String name = dataField.getString("version");
				String name = json.getString("versionNum");
				// String feature = dataField.getString("content");
				String feature = "";// 版本特性
				// 下载apk地址
				String targetUrl = json.getString("downloadUrl");
//				version = new Version(code, name, feature, targetUrl);
			}
			return version;
		} catch (JSONException exp) {
			exp.printStackTrace();
			return null;
		}
	}

}
