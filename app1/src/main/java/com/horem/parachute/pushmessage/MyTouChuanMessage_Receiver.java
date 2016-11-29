package com.horem.parachute.pushmessage;

import java.util.List;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.horem.parachute.main.AppMainActivity;
import com.horem.parachute.main.SplashActivity;

public class MyTouChuanMessage_Receiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		boolean isRun = checkAppIsRun(context);
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			String messageId = bundle.getString("id");
			int type = bundle.getInt("type");
			if (!isRun) {// 如果没有运行启动APP
				Intent kintent = new Intent();
				kintent.putExtra("id", messageId);
				kintent.putExtra("type", type);
				kintent.setAction("" + System.currentTimeMillis());
				kintent.setClass(context.getApplicationContext(), SplashActivity.class);
				kintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.getApplicationContext().startActivity(kintent);
			} else {// 如果在运行就只是打开页面
				Intent kintent = null;
				switch (type) {
					case 10:
						kintent = new Intent(context.getApplicationContext(), AppMainActivity.class);
						break;
					case 20:
//                    intent.setClass(context.getApplicationContext(),MainActivity.class);
						break;
					case 30:
						kintent = new Intent(context.getApplicationContext(), AppMainActivity.class);
						break;
					case 40:
						kintent = new Intent(context.getApplicationContext(), AppMainActivity.class);
						break;
					case 50:
						kintent = new Intent(context.getApplicationContext(), AppMainActivity.class);
						break;
				}
				if (kintent != null) {
					kintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(kintent);
				}
			}
		}

	}

	/**
	 * 检查APP是否在运行
	 * 
	 * @param context
	 * @return
	 */
	private boolean checkAppIsRun(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
		for (ActivityManager.RunningTaskInfo info : list) {
			if (info.topActivity.getPackageName().equals("com.horem.parachute")
					&& info.baseActivity.getPackageName().equals(
							"com.horem.parachute")) {
				return true;
			}
		}
		return false;
	}

}
