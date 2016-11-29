package com.horem.parachute.autoupdate;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.horem.parachute.main.AppMainActivity;
import com.horem.parachute.main.GuideActivity;
import com.horem.parachute.R;
import com.horem.parachute.autoupdate.internal.FoundVersionDialog;
import com.horem.parachute.autoupdate.internal.NetworkUtil;
import com.horem.parachute.autoupdate.internal.VersionDialogInterface;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.horem.parachute.util.Utils;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class AppUpdateService {

	private Activity context;
	private DownloadManager downloader;
	private DownloadReceiver downloaderReceiver;
	private NetworkStateReceiver networkReceiver;

	private boolean updateDirectly = false;
	private long downloadTaskId = -12306;
	private static AutoUpgradeDelegate updateDelegate;

	class AutoUpgradeDelegate implements AppUpdateInterface,
			 VersionDialogInterface {

		private DisplayerInterface customShowingDelegate;
		private String latestVersion;
		private String latestVersionUrl;

		// 检查最新版本，不直接下载
		@Override
		public void checkLatestVersion(String url,
				ResponseParserInterface parser) {
			checkVersion(url, parser, false);
		}

		@Override
		public void checkAndUpdateDirectly(String url,
				ResponseParserInterface parser) {
			checkVersion(url, parser, true);
		}

		void checkVersion(String url, ResponseParserInterface parser,
				boolean isUpdateDirectly) {
			updateDirectly = isUpdateDirectly;// 为false
			//执行异步线程
			if (isNetworkActive()) {
				/*VerifyTask task = new VerifyTask(context, parser, this);
				task.execute(url);*/
				HashMap<String,String> map = new HashMap<>();
				map.put("memberId", SharePreferencesUtils.getLong(context,SharePrefConstant.MEMBER_ID
				,(long)0)+"");
				map.put("lat","");
				map.put("lng","");
				map.put("deviceType","android");
				map.put("clientId",SharePreferencesUtils.getString(context,SharePrefConstant.INSTALL_CODE,""));
//				for (Map.Entry<String,String> entry: map.entrySet()){
//					Log.d(getClass().getName(),entry.getKey()+": "+entry.getValue());
//				}
				OkHttpClientManager.postAsyn(url, new OkHttpClientManager.ResultCallback<String>() {
					@Override
					public void onError(Request request, Exception e) {
//						Log.d(getClass().getName(),e.toString());
						ToastManager.show(context,"噢！网络不给力");
					}

					@Override
					public void onResponse(String response) {
//						Log.d("版本更新借口返回值",response);
							try {
								JSONObject jsonObject = new JSONObject(response);
								if(!jsonObject.isNull("result")){
									if(jsonObject.getInt("statusCode") == 1) {
										latestVersion = jsonObject.getJSONObject("result").getString("version");
										latestVersionUrl = jsonObject.getJSONObject("result").getString("downloadUrl");
										if (comparedWithCurrentPackage(latestVersion)) {
											onFoundLatestVersion(latestVersion);
										} else {
											onCurrentIsLatest();
										}
									}else
										ToastManager.show(context,"噢，网络不给力！");
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
					}
				},map);
			}
		}
		boolean comparedWithCurrentPackage(String versionName) {

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


		@Override
		public void downloadAndInstallCurrent() {
			downloadAndInstall(latestVersion);
		}

		@Override
		public void downloadAndInstall(String latestVersion) {
			if (!isNetworkActive())
				return;
			downloader = (DownloadManager) context
					.getSystemService(Context.DOWNLOAD_SERVICE);
			Query query = new Query();
			query.setFilterById(downloadTaskId);
			Cursor cur = downloader.query(query);
			// 下载任务已经存在的话
			if (cur.moveToNext())
				return;
			DownloadManager.Request task = new DownloadManager.Request(
					Uri.parse(latestVersionUrl));
			String apkName = extractName(latestVersionUrl);
			String title = String.format("%s - v%s", apkName,
					latestVersion);
			task.setTitle(title);
//			task.setDescription(latestVersion.feature);
			task.setVisibleInDownloadsUi(true);
			task.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
					| DownloadManager.Request.NETWORK_WIFI);
			task.setDestinationInExternalPublicDir(
					Environment.DIRECTORY_DOWNLOADS, apkName);
			downloadTaskId = downloader.enqueue(task);
		}

		boolean isNetworkActive() {
			return NetworkUtil.getNetworkType(context) != NetworkUtil.NOCONNECTION;
		}

		String extractName(String path) {
			String tempFileName = "_temp@" + path.hashCode();
			if (path != null) {
				boolean fileNameExist = path.substring(path.length() - 5,
						path.length()).contains(".");
				if (fileNameExist) {
					tempFileName = path.substring(path
							.lastIndexOf(File.separator) + 1);
				}
			}
			return tempFileName;
		}


		public void onFoundLatestVersion(String version) {
			this.latestVersion = version;

			if (updateDirectly) {
				downloadAndInstall(latestVersion);
				String versionTipFormat = context.getResources().getString(
						R.string.update_latest_version_title);
				Toast.makeText(context,
						String.format(versionTipFormat, latestVersion),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (customShowingDelegate != null) {
				customShowingDelegate.showFoundLatestVersion(latestVersion);
			} else {
				FoundVersionDialog dialog = new FoundVersionDialog(context,
						latestVersion, this);
				dialog.show();
			}
		}


		public void onCurrentIsLatest() {
			if (customShowingDelegate != null) {
				customShowingDelegate.showIsLatestVersion();
			} else {
			}
		}

		@Override
		public void callOnResume() {
        if (CustomApplication.isRegistered)
				return;
            CustomApplication.isRegistered = true;
			context.getApplicationContext().registerReceiver(downloaderReceiver, new IntentFilter(
					DownloadManager.ACTION_DOWNLOAD_COMPLETE));
			context.getApplicationContext().registerReceiver(networkReceiver, new IntentFilter(
					ConnectivityManager.CONNECTIVITY_ACTION));


		}

@Override
		public void callOnPause() {
			if (!CustomApplication.isRegistered)
				return;
            CustomApplication.isRegistered = false;
			context.getApplicationContext().unregisterReceiver(downloaderReceiver);
			context.getApplicationContext().unregisterReceiver(networkReceiver);
		}


		@Override
		public void setCustomDisplayer(DisplayerInterface delegate) {
			customShowingDelegate = delegate;
		}

		@Override
		public String getLatestVersion() {
			return latestVersion;
		}

		@Override
		public void doUpdate(boolean laterOnWifi) {
			if (!laterOnWifi) {
				downloadAndInstall(latestVersion);
			} else {
//				SharePreferencesUtils.setString(context,SharePrefConstant.VERSION_NUM,latestVersion);
			}
		}

		@Override
		public void doIgnore() {

		}

	}

	class DownloadReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context c, Intent intent) {
			if (downloader == null)
				return;
			long completeId = intent.getLongExtra(
					DownloadManager.EXTRA_DOWNLOAD_ID, 0);
			if (completeId == downloadTaskId) {
				Query query = new Query();
				query.setFilterById(downloadTaskId);
				Cursor cur = downloader.query(query);
				if (cur.moveToFirst()) {
					int columnIndex = cur
							.getColumnIndex(DownloadManager.COLUMN_STATUS);
					if (DownloadManager.STATUS_SUCCESSFUL == cur
							.getInt(columnIndex)) {
						// 下载任务已经完成，清除
//						new VersionPersistent(context).clear();
						String uriString = cur
								.getString(cur
										.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
						//获取下载文件
						File apkFile = new File(Uri.parse(uriString).getPath());
						//安装文件
						Intent installIntent = new Intent();
						installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						installIntent
								.setAction(android.content.Intent.ACTION_VIEW);
						installIntent.setDataAndType(Uri.fromFile(apkFile),
								"application/vnd.android.package-archive");
						context.startActivity(installIntent);

					} else {
						Toast.makeText(context, R.string.download_failure,
								Toast.LENGTH_SHORT).show();
					}
				}
				cur.close();
			}

		}
	}

	class NetworkStateReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (!intent.getBooleanExtra(
					ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
				if (NetworkUtil.getNetworkType(context) == NetworkUtil.WIFI) {
					String versionTask = SharePreferencesUtils.getString(context,SharePrefConstant.VERSION_NUM,"");
					if (!versionTask.equals("")) {
						Toast.makeText(context, R.string.later_update_tip,
								Toast.LENGTH_SHORT).show();
						updateDelegate.downloadAndInstall(versionTask);
					}
				}
			}
		}
	}

	private AppUpdateInterface getAppUpdate() {
		if (updateDelegate == null) {
			updateDelegate = new AutoUpgradeDelegate();
		}
		return updateDelegate;
	}

	public static AppUpdateService updateServiceInstance = null;

	public static AppUpdateInterface getAppUpdate(Activity context) {
		if (null == updateServiceInstance) {
			updateServiceInstance = new AppUpdateService(context);
		}
		return updateServiceInstance.getAppUpdate();
	}

	private AppUpdateService(Activity context) {
		this.context = context;
		downloaderReceiver = new DownloadReceiver();
		networkReceiver = new NetworkStateReceiver();
	}

}
