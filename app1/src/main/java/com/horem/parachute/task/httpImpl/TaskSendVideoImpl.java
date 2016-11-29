package com.horem.parachute.task.httpImpl;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.app.utils.SignatureTool;
import com.common.HttpUrlConstant;
import com.horem.parachute.task.ConfirmLocationAddress;
import com.horem.parachute.task.bean.UploadCompleteBean;
import com.horem.parachute.task.bean.UploadVideoBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.http.request.HttpApi;
import com.http.request.IResponseApi;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 2016/4/19.
 */
public class TaskSendVideoImpl implements HttpApi {
    private static final String url = HttpUrlConstant.taskVideoUploadUrl;

    public void httpRequest(Activity activity, IResponseApi api, Map<String, String> map) {


    }

    @Override
    public void httpRequest(Context context, IResponseApi api, Map<String, String> map) {

    }

    @Override
    public void httpRequest(Activity activity, final IResponseApi api, Map<String, String> map, ArrayList<File> lists) {

        long userId = SharePreferencesUtils.getLong(activity, SharePrefConstant.MEMBER_ID,(long)0);
        UploadCompleteBean bean = new UploadCompleteBean();

        bean.setMemberId(userId);
        bean.setSubTaskId(map.get("subTaskId"));

        HashMap<String,String> params = new HashMap<>();
        params.put("subTaskId",map.get("subTaskId"));
        params.put("attArrayJson","[]");

        params.put("memberId",userId+"");
        params.put("lng",map.get("lng"));
        params.put("lat",map.get("lat"));
//        params.put("lng","113.3668593");
//        params.put("lat","23.1285566");
        params.put("depict",map.get("describe"));
        params.put("sign", SignatureTool.getSignStr(bean));
        params.put("deviceType","android");
        params.put("clientId",SharePreferencesUtils.getString(activity,SharePrefConstant.INSTALL_CODE,""));

//        for(Map.Entry<String,String> entry:params.entrySet()){
//            Log.i(getClass().getName(),entry.getKey()+" "+entry.getValue());
//        }

        try {
            OkHttpClientManager.postAsyn(url, new OkHttpClientManager.ResultCallback<String>() {
                @Override
                public void onError(Request request, Exception e) {

                }

                @Override
                public void onResponse(String response) {

                    Log.d(getClass().getName(),response);
                    api.onSuccess(response);
                }
            },lists.get(0),"",params);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
