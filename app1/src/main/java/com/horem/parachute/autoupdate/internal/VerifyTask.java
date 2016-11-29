/*
package com.horem.parachute.autoupdate.internal;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.horem.parachute.autoupdate.ResponseParserInterface;
import com.horem.parachute.common.CustomApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


// 版本比对
public class VerifyTask extends AsyncTask<String, Integer, Version> {

	private ResponseParserInterface parser;
	private ResponseCallbackInterface callback;
	private Context context;

	public VerifyTask(Context context, ResponseParserInterface parser, ResponseCallbackInterface callback) {
		this.parser = parser;
		this.context = context;
		this.callback = callback;
	}

	@Override
	protected Version doInBackground(String... args) {
		String url = args[0];
		Version latestVersion = null;
		try {
			URL targetUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
			InputStream is = connection.getInputStream();
			latestVersion = parser.parser(toStringBuffer(is).toString());
			is.close();
			connection.disconnect();
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		Log.d("异步线程获取最新版本：",latestVersion+"");
		return latestVersion;
	}

	@Override
	protected void onPostExecute(Version latestVersion) {
		super.onPostExecute(latestVersion);
		Log.d("异步线程获取最新版本：",latestVersion+"");
		if (comparedWithCurrentPackage(latestVersion)) {
			callback.onFoundLatestVersion(latestVersion);
		} else {
			callback.onCurrentIsLatest();
		}
	}

	boolean comparedWithCurrentPackage(Version version) {
		if (version == null) {
			return false;
		}
		String versionName = version.name;
		String[] arr = versionName.split("\\.");
		String str = "";
		if (arr.length > 0) {
			for (int i = 0; i < arr.length; i++) {
				str = str + arr[i];
			}
		}
		// 代码需打开
		Integer valueOf = Integer.valueOf(str);
		
		if (valueOf > CustomApplication.version) {
			return true;
		}
		return false;
	}

	StringBuffer toStringBuffer(InputStream is) throws IOException {
		if (null == is)
			return null;
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line = null;
		while ((line = in.readLine()) != null) {
			buffer.append(line).append("\n");
		}
		is.close();
		return buffer;
	}

}
*/
