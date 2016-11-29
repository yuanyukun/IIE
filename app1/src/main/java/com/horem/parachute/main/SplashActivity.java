package com.horem.parachute.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.common.HttpUrlConstant;
import com.horem.parachute.R;
import com.horem.parachute.autoupdate.AppUpdateInterface;
import com.horem.parachute.autoupdate.AppUpdateService;
import com.horem.parachute.autoupdate.internal.ResponseParser;
import com.horem.parachute.common.CustomAlertDialog;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.Utils;

/**
 * Created by user on 2015/9/11.
 */
public class SplashActivity extends Activity {

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                SharePreferencesUtils.setBoolean(SplashActivity.this,SharePrefConstant.isNeedUpdateVersion,true);
                boolean mFirst = Utils.isFirstEnter(SplashActivity.this);
                Intent mIntent = new Intent();
                if(mFirst){
                    CustomApplication.getInstance().setFirstLoad(true);
                    mIntent.setClass(SplashActivity.this, GuideActivity.class);
                } else{
                    mIntent.setClass(SplashActivity.this, AppMainActivity.class);
                }
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
                finish();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        String phoneInfo = android.os.Build.VERSION.RELEASE;
        if (phoneInfo != null && phoneInfo.length() > 0) {
            if (phoneInfo.startsWith("2.") || phoneInfo.startsWith("1.")||phoneInfo.startsWith("3.")) {
                new CustomAlertDialog.Builder(SplashActivity.this)
                        .setTitle("提示")
                        .setContent("伞来了不支持android4.0以下版本")
                        .hideCancelButton()
                        .setOnButtonOnClickListener(new CustomAlertDialog.Builder.OnButtonOnClickListener() {
                            @Override
                            public void OnConfirmed() {
                                SplashActivity.this.finish();
                                System.exit(0);
                            }

                            @Override
                            public void OnCanceled() {
                                SplashActivity.this.finish();
                                System.exit(0);
                            }
                }).show();
            }else
                handler.sendEmptyMessageDelayed(1,1000);
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
