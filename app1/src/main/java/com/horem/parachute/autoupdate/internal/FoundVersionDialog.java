package com.horem.parachute.autoupdate.internal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;

import com.horem.parachute.main.AppMainActivity;
import com.horem.parachute.main.GuideActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.Utils;

public class FoundVersionDialog {

	private Activity context;
	private String version;
	private VersionDialogInterface listener;

	
	public FoundVersionDialog(Activity context, String version, VersionDialogInterface listener){
		this.context = context;
		this.version = version;
		this.listener = listener;
	}
	
	public void show(){
		
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage("版本号："+version);

		builder.setTitle("发现新版本");

		builder.setPositiveButton("立即更新", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				listener.doUpdate(false);// true代表boolean laterOnWifiFlag = true
			}
		});

		builder.setNegativeButton("暂不更新", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		if(SharePreferencesUtils.getBoolean(context, SharePrefConstant.isNeedUpdateVersion,false)) {
			SharePreferencesUtils.setBoolean(context,SharePrefConstant.isNeedUpdateVersion,false);
			builder.create().show();
		}
	}
}
