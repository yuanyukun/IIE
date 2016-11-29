package com.horem.parachute.mine;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.app.utils.MyEncrypt;
import com.common.HttpUrlConstant;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.login.StartRegisterActivity;
import com.horem.parachute.login.bean.UuidKey;
import com.horem.parachute.login.request.presenter.impl.LoginApiImplPresenter;
import com.horem.parachute.mine.bean.MessageBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.http.request.HttpApi;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Activity_Setting_Safety_Modify_Code extends BaseActivity {

    private EditText oldPwd;
    private EditText newPwd;

    private Map<String,String> params;
    private CustomApplication application;
    private Tracker mTracker;
    private static final String TAG = "ModifyCodeAty";
    @Override
    protected void onStart() {
        super.onStart();
        //google analytics
        application = (CustomApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(this.getPackageName()+" [Android] " + TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Share")
                .build());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity__setting__safety__modify__code);

        init();

    }

    private void init() {
        initTitleView();
        setTitleName("修改密码");
        setRightButtonText("保存");

        oldPwd = (EditText) findViewById(R.id.modify_old_pwd);
        newPwd = (EditText) findViewById(R.id.modify_new_pwd);
    }

    @Override
    protected void OnRightButtonClicked() {
        super.OnRightButtonClicked();
        modifyPwd(oldPwd.getText().toString(),newPwd.getText().toString());
    }

    private void modifyPwd(final String oldPwd, final String newPwd) {
        if(TextUtils.isEmpty(oldPwd)){
            ToastManager.show(this,"请输入原密码");
            return;
        }
        if(TextUtils.isEmpty(newPwd)){
            ToastManager.show(this,"请输入新密码");
            return;
        }
        if(newPwd.length() < 6){
            ToastManager.show(this,"密码长度不能小于6");
            return;
        }

        final String guid = UUID.randomUUID().toString();
        Log.i(getClass().getName(),"first guid value=>>>"+guid);
        params = new HashMap<>();
        params.put("memberId","0");
        params.put("lng","");
        params.put("lat","");
        params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");
        params.put("guidKey",guid);
        OkHttpClientManager.postAsyn(HttpUrlConstant.keyUrl, new OkHttpClientManager.ResultCallback<UuidKey>() {
            @Override
            public void onError(Request request, Exception e) {
//                e.printStackTrace();
//                Log.i(getClass().getName(),"错误=>>>>>"+e.toString());
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(UuidKey response) {
//                Log.i(getClass().getName(),"success=>>>>>"+new Gson().toJson(response));

                StringBuilder builder = new StringBuilder(response.getKey());
                String encryptCode = builder.append("a1").toString();
//                Log.i("加密key",encryptCode);

                //修改密码
                params.clear();
                params.put("password", new MyEncrypt(encryptCode,newPwd).encrypt());
                params.put("guidKey",guid);
                params.put("oldPassword",new MyEncrypt(encryptCode,oldPwd).encrypt());
                params.put("memberId", SharePreferencesUtils.getLong(Activity_Setting_Safety_Modify_Code.this, SharePrefConstant.MEMBER_ID,(long)0)+"");
                params.put("lng","");
                params.put("lat","");
                params.put("clientId", SharePreferencesUtils.getString(Activity_Setting_Safety_Modify_Code.this, SharePrefConstant.INSTALL_CODE,""));
                params.put("deviceType","android");

//                for(Map.Entry<String,String> entry:params.entrySet()){
//                    Log.i("修改密码",entry.getKey()+" "+entry.getValue());
//                }
                OkHttpClientManager.postAsyn(HttpUrlConstant.memberPasswordEditUrl, new OkHttpClientManager.ResultCallback<MessageBean>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                    }

                    @Override
                    public void onResponse(MessageBean response) {
                        Log.d("registerActivity",new Gson().toJson(response));
                        ToastManager.show(Activity_Setting_Safety_Modify_Code.this,response.getMessage());
                        if(response.getStatusCode() == 1){
                            Activity_Setting_Safety_Modify_Code.this.finish();
                        }else if(response.getStatusCode() ==-999){
                            exitApp();
                        }else
                            ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                    }
                },params);
            }
        },params);


    }
}
