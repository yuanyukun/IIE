package com.horem.parachute.mine.httpImpl;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.common.HttpUrlConstant;
import com.google.gson.Gson;
import com.horem.parachute.mine.bean.MineSendTaskBean;
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
 * Created by user on 2016/4/15.
 */
public class MineTaskImpl implements HttpApi {


    @Override
    public void httpRequest(Context context, IResponseApi api, Map<String, String> map) {

    }

    private static final String url = HttpUrlConstant.myIssuanceTaskList;

    private HashMap<String,String> params = new HashMap<>();
    @Override
    public void httpRequest(Activity activity, final IResponseApi api, Map<String, String> map) {

        long userId = SharePreferencesUtils.getLong(activity, SharePrefConstant.MEMBER_ID,(long)0);
        params.put("memberId",userId+"");
        params.put("pageSize",map.get("pageSize"));
        params.put("currentPage",map.get("currentPage"));
//        for(Map.Entry<String,String> entry:params.entrySet()){
//            Log.i(getClass().getName(),entry.getKey()+":"+entry.getValue());
//        }

        OkHttpClientManager.getAsyn(url, new OkHttpClientManager.ResultCallback<MineSendTaskBean>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(MineSendTaskBean response) {
//                Log.i(getClass().getName(),new Gson().toJson(response));)
                api.onSuccess(response);
            }
        },params);



    }

    @Override
    public void httpRequest(Activity activity, IResponseApi api, Map<String, String> map, ArrayList<File> lists) {

    }
}
