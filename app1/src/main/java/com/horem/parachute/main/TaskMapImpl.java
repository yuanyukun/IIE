package com.horem.parachute.main;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.common.HttpUrlConstant;
import com.google.gson.Gson;
import com.horem.parachute.login.bean.MapTaskNewBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.http.request.HttpApi;
import com.http.request.IResponseApi;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 2016/4/12.
 */
public class TaskMapImpl implements HttpApi {

    private static final  String url = HttpUrlConstant.mapAreaSubTaskScopeUrl;
    @Override
    public void httpRequest(final Activity activity, final IResponseApi api, Map<String, String> map) {
        HashMap<String,String>  params = new HashMap<>();
        params.put("memberId", SharePreferencesUtils.getLong(activity, SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lng",map.get("lng"));
        params.put("lat",map.get("lat"));
        params.put("clientId", SharePreferencesUtils.getString(activity, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");

//        for(Map.Entry<String,String> entry:params.entrySet()){
//            Log.d(getClass().getName(),entry.getKey()+" "+entry.getValue());
//        }

        OkHttpClientManager.postAsyn(url, new OkHttpClientManager.ResultCallback<MapTaskNewBean>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(MapTaskNewBean response) {
                if(response.getStatusCode() == 1){
                    api.onSuccess(response);
                }else if(response.getStatusCode() == -999){
                    HttpApi httpApi = new ExitSystemHttpImpl();
                    httpApi.httpRequest(activity, new IResponseApi() {
                        @Override
                        public void onSuccess(Object object) {

                        }

                        @Override
                        public void onFailed(Exception e) {

                        }
                    },new HashMap<String, String>());
                }
            }
        },params);

    }

    @Override
    public void httpRequest(final Context context, final IResponseApi api, Map<String, String> map) {

    }

    @Override
    public void httpRequest(Activity activity, IResponseApi api, Map<String, String> map, ArrayList<File> lists) {

    }
}
