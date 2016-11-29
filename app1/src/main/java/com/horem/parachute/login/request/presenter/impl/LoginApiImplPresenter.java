package com.horem.parachute.login.request.presenter.impl;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.app.utils.MyEncrypt;
import com.common.HttpUrlConstant;
import com.google.gson.Gson;
import com.horem.parachute.login.bean.LoginBean;
import com.horem.parachute.login.bean.UuidKey;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.http.request.HttpApi;
import com.http.request.IResponseApi;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by user on 2016/4/7.
 */
public class LoginApiImplPresenter implements HttpApi {

    private String getKeyUrl = HttpUrlConstant.keyUrl;
    private String loginSerUrl = HttpUrlConstant.LoginUrl;

    @Override
    public void httpRequest(final Activity activity, final IResponseApi api, final Map<String,String> map) {
        final String guid = UUID.randomUUID().toString();
//        Log.i(getClass().getName(),"first guid value=>>>"+guid);
        Map<String,String> params = new HashMap<>();
        params.put("memberId","0");
        params.put("lng","");
        params.put("lat","");
        params.put("clientId", SharePreferencesUtils.getString(activity, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");
        params.put("guidKey",guid);
        OkHttpClientManager.postAsyn(getKeyUrl, new OkHttpClientManager.ResultCallback<UuidKey>() {
            @Override
            public void onError(Request request, Exception e) {
//                            e.printStackTrace();
//                            Log.i(getClass().getName(),"错误=>>>>>"+e.toString());
                ToastManager.show(activity, "噢，网络不给力！");
            }

            @Override
            public void onResponse(UuidKey response) {
//                            Log.i(getClass().getName(),"success=>>>>>"+new Gson().toJson(response));

                StringBuilder builder = new StringBuilder(response.getKey());
                String encryptCode = builder.append("a1").toString();
//                            Log.i("加密key",encryptCode);

                Map<String, String> params = new HashMap<>();
                params.put("password", new MyEncrypt(encryptCode, map.get("password")).encrypt());
                params.put("guidKey", guid);
                params.put("loginName", map.get("userName"));
                params.put("channelId",SharePreferencesUtils.getString(activity,SharePrefConstant.CHANNEL_ID,""));
                params.put("userId",SharePreferencesUtils.getString(activity,SharePrefConstant.USER_ID,""));
                params.put("lng", map.get("lng"));
                params.put("lat", map.get("lat"));
                params.put("clientId", SharePreferencesUtils.getString(activity, SharePrefConstant.INSTALL_CODE, ""));
                params.put("deviceType", "android");
                params.put("guidKey", guid);
                params.put("memberId", "0");

                for(Map.Entry<String,String> entry:params.entrySet()){
                    Log.i("登录参数",entry.getKey()+" "+entry.getValue());
                }

                OkHttpClientManager.postAsyn(loginSerUrl, new OkHttpClientManager.ResultCallback<LoginBean>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        ToastManager.show(activity, "噢，网络不给力！");
                    }

                    @Override
                    public void onResponse(LoginBean response) {
                        api.onSuccess(response);
                    }
                }, params);
            }
        },params);
    }

    @Override
    public void httpRequest(final Context context, final IResponseApi api, final Map<String, String> map) {

    }

    @Override
    public void httpRequest(final Activity activity, final IResponseApi api, final Map<String, String> map, ArrayList<File> lists) {
    }
}
