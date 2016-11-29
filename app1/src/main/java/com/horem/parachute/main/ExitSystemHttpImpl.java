package com.horem.parachute.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.common.HttpUrlConstant;
import com.google.gson.Gson;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.login.Activity_Login;
import com.horem.parachute.mine.bean.MessageBean;
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

/**
 * Created by yuanyukun on 2016/7/20.
 */
public class ExitSystemHttpImpl implements HttpApi {
    @Override
    public void httpRequest(final Activity activity, IResponseApi api, Map<String, String> map) {

    }

    @Override
    public void httpRequest(final Context activity, IResponseApi api, Map<String, String> map) {
        HashMap<String,String> params = new HashMap<>();
        params.put("memberId", SharePreferencesUtils.getLong(activity, SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lng","");
        params.put("lat","");
        params.put("clientId", SharePreferencesUtils.getString(activity, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");

        params.put("channelId",SharePreferencesUtils.getString(activity,SharePrefConstant.CHANNEL_ID,""));
        params.put("userId",SharePreferencesUtils.getString(activity,SharePrefConstant.USER_ID,""));

        OkHttpClientManager.postAsyn(HttpUrlConstant.LogoutUrl, new OkHttpClientManager.ResultCallback<MessageBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(activity,"噢，网络不给力！");
            }

            @Override
            public void onResponse(MessageBean response) {
                Log.d(getClass().getName(),new Gson().toJson(response));
//                ToastManager.show(getApplicationContext(),response.getMessage());
                if(response.getStatusCode() == 1){
                    SharePreferencesUtils.setString(activity, SharePrefConstant.MEMBER_NAME,"");
                    SharePreferencesUtils.setLong(activity,SharePrefConstant.MEMBER_ID,(long)0);
                    SharePreferencesUtils.setBoolean(activity,SharePrefConstant.USER_LOGIN,false);
                    SharePreferencesUtils.setBoolean(activity,SharePrefConstant.isNeedConfirmShare,false);
                    SharePreferencesUtils.setBoolean(activity,SharePrefConstant.isNeedUpdateVersion,false);
                    CustomApplication.getInstance().setLogin(false);

                    Intent intent = new Intent(activity,Activity_Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(intent);

                }else
                    ToastManager.show(activity,"噢，网络不给力！");

            }
        },params);
    }

    @Override
    public void httpRequest(Activity activity, IResponseApi api, Map<String, String> map, ArrayList<File> lists) {

    }
}
