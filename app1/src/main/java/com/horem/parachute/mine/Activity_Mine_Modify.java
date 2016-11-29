package com.horem.parachute.mine;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.text.style.MaskFilterSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.app.utils.MyEncrypt;
import com.common.HttpUrlConstant;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.common.ApplicationConstant;
import com.horem.parachute.adapter.CommonAdapter;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.mine.bean.MessageBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.horem.parachute.util.Utils;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Activity_Mine_Modify extends BaseActivity {

    private EditText inputResult;
    private int Type;
    private String message;

    private Tracker mTracker;
    private CustomApplication application;
    private static final String TAG = "AtyMineModify";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity__mine__account__personal);

        init();
    }
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
    protected void OnRightButtonClicked() {
        super.OnRightButtonClicked();
        modifyInfo(inputResult.getText().toString());
    }

    private void init() {
        initTitleView();
        setRightButtonText("保存");
        setLeftButtonText(SharePreferencesUtils.getString(this,SharePrefConstant.MEMBER_NAME,""));
        inputResult = (EditText) findViewById(R.id.person_et_name);
        Type = getIntent().getIntExtra("ModifyType",-1);

        switch (Type){
            case ApplicationConstant.MODIFY_PHONE:
                setTitleName("手机号");
                 message = getIntent().getStringExtra("value");
                break;
            case ApplicationConstant.MODIFY_NAME:
                setTitleName("用户名");
                 message = getIntent().getStringExtra("value");
                break;
            case ApplicationConstant.MODIFY_EMAIL:
                setTitleName("邮箱地址");
                 message = getIntent().getStringExtra("value");

                break;
        }
        if(message != null) {
            inputResult.setText(message);
            inputResult.setSelection(message.length());
        }
        inputResult.setCursorVisible(false);
        inputResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputResult.setCursorVisible(true);
            }
        });
    }

    private void modifyInfo(String modifyParams) {
        Map<String,String> params = new HashMap<String, String>();
        //网络请求
        switch (Type){
            case ApplicationConstant.MODIFY_PHONE:
                modifyUserPhone(modifyParams, params);

                break;
            case ApplicationConstant.MODIFY_NAME:
                modifyUserName(modifyParams, params);

                break;
            case ApplicationConstant.MODIFY_EMAIL:
                modifyUserEmail(modifyParams, params);

                break;
        }
    }

    private void modifyUserPhone(String modifyParams, Map<String, String> params) {
        //修改手机号
        if(TextUtils.isEmpty(modifyParams) || !Utils.checkMobileNumber(modifyParams)){
            ToastManager.show(Activity_Mine_Modify.this,
                    "请输入正确的手机号码");
            return;
        }
        params.clear();
        params.put("phone",modifyParams);
        params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lng","");
        params.put("lat","");
        params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");

//        for(Map.Entry<String,String> entry:params.entrySet()){
//            Log.i("修改手机号参数",entry.getKey()+" "+entry.getValue());
//        }
        OkHttpClientManager.postAsyn(HttpUrlConstant.memberPhoneEditUrl, new OkHttpClientManager.ResultCallback<MessageBean>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(MessageBean response) {
                Log.d(getClass().getName(),new Gson().toJson(response));
                ToastManager.show(Activity_Mine_Modify.this,response.getMessage());
                if(response.getStatusCode() ==1 ){
                    Activity_Mine_Modify.this.finish();
                }else if(response.getStatusCode() == -999){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }
        },params);
    }

    private void modifyUserEmail(final String modifyParams, Map<String, String> params) {
        //修改邮箱
        if(TextUtils.isEmpty(modifyParams) || !Utils.checkEmail(modifyParams)){
            ToastManager.show(Activity_Mine_Modify.this,
                    "请输入正确的邮箱地址");
            return;
        }
        params.clear();
        params.put("email",modifyParams);
        params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lng","");
        params.put("lat","");
        params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");

//        for(Map.Entry<String,String> entry:params.entrySet()){
//            Log.i("修改邮箱参数",entry.getKey()+" "+entry.getValue());
//        }
        OkHttpClientManager.postAsyn(HttpUrlConstant.memberEmailEditUrl, new OkHttpClientManager.ResultCallback<MessageBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(MessageBean response) {
//                Log.d(getClass().getName(),new Gson().toJson(response));
//                ToastManager.show(Activity_Mine_Modify.this,response.getMessage());
                if(response.getStatusCode() ==1 ){
                    SharePreferencesUtils.setString(Activity_Mine_Modify.this,SharePrefConstant.USER_EMAIL,modifyParams);
                    Activity_Mine_Modify.this.finish();
                }else if(response.getStatusCode() == -999){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }
        },params);
    }

    private void modifyUserName(final String modifyParams, Map<String, String> params) {
        //修改用户名
        if(TextUtils.isEmpty(modifyParams) ){
            ToastManager.show(Activity_Mine_Modify.this,
                    "请输入你想更改的用户名");
            return;
        }
        params.clear();
        params.put("nickName",modifyParams);
        params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lng","");
        params.put("lat","");
        params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");

//        for(Map.Entry<String,String> entry:params.entrySet()){
//            Log.i("修改用户名",entry.getKey()+" "+entry.getValue());
//        }
        OkHttpClientManager.postAsyn(HttpUrlConstant.memberNickNameEditUrl, new OkHttpClientManager.ResultCallback<MessageBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(MessageBean response) {
//                Log.d(getClass().getName(),new Gson().toJson(response));
//                ToastManager.show(Activity_Mine_Modify.this,response.getMessage());
                if(response.getStatusCode() ==1 ){
                    SharePreferencesUtils.setString(Activity_Mine_Modify.this,SharePrefConstant.MEMBER_NAME,modifyParams);
                    Activity_Mine_Modify.this.finish();
                }else if(response.getStatusCode() == -999){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }
        },params);
    }
}
