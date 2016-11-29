package com.horem.parachute.task.httpImpl;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.app.utils.SignatureTool;
import com.common.HttpUrlConstant;
import com.google.gson.Gson;
import com.horem.parachute.task.bean.UploadBackBean;
import com.horem.parachute.task.bean.UploadCompleteBean;
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
 * Created by user on 2016/4/14.
 */
public class TaskSendPhotoImpl implements HttpApi {

    private static final String url = HttpUrlConstant.taskOrderDeliveryUploadUrl;//执行任务，单张多线程上传
    private static final String completeUrl = HttpUrlConstant.taskOrderDeliveryUrl;//执行完成，确认
    private UploadBackBean[] giveBackBean;
    private int count = 0;
    private HashMap<String,String> params = new HashMap<>();

    /**
     * 分两步：1、上传图片，将返回的参数拼成一个数组json格式作为确认上传图频的参数 2、执行确认上传完成接口
     * @param activity
     * @param api
     * @param map
     * @param lists
     */
    @Override
    public void httpRequest(Activity activity, final IResponseApi api, Map<String, String> map, final ArrayList<File> lists) {

        UploadCompleteBean bean = new UploadCompleteBean();
        long userId = SharePreferencesUtils.getLong(activity, SharePrefConstant.MEMBER_ID,(long)0);

        bean.setMemberId(userId);
        bean.setSubTaskId(map.get("subTaskId"));

        params.put("subTaskId",map.get("subTaskId"));
        params.put("depict",map.get("describe"));
        params.put("sign", SignatureTool.getSignStr(bean));


//        params.put("lng","113.3668593");
//        params.put("lat","23.1285566");
        params.put("lng",map.get("lng"));
        params.put("lat",map.get("lat"));
        params.put("memberId",userId+"");
        params.put("deviceType","android");
        params.put("clientId",SharePreferencesUtils.getString(activity,SharePrefConstant.INSTALL_CODE,""));


        giveBackBean = new UploadBackBean[lists.size()];

        for(int index = 0;index < lists.size();index++){
            try {
                OkHttpClientManager.postAsyn(url, new OkHttpClientManager.ResultCallback<UploadBackBean>() {
                    @Override
                    public void onError(Request request, Exception e) {

                    }

                    @Override
                    public void onResponse(UploadBackBean response) {
                        Log.i(getClass().getName(),new Gson().toJson(response));
                        giveBackBean[count++] =response;
                        //全部上传完成后，执行
                        if(count == lists.size()){
                            params.put("attArrayJson",new Gson().toJson(giveBackBean).toString());
//                            for(Map.Entry<String,String> entry:params.entrySet()){
//                                Log.i(getClass().getName(),entry.getKey()+" "+entry.getValue());
//                            }
                            OkHttpClientManager.postAsyn(completeUrl, new OkHttpClientManager.ResultCallback<String>() {
                                @Override
                                public void onError(Request request, Exception e) {

                                }

                                @Override
                                public void onResponse(String response) {
                                    Log.i(getClass().getName(),response);
                                    api.onSuccess(response);
                                }
                            },params);

                        }
                    }
                },lists.get(index),"kkk");
            } catch (IOException e) {
                e.printStackTrace();
            }


        }



    }

    @Override
    public void httpRequest(Activity activity, IResponseApi api, Map<String, String> map) {

    }

    @Override
    public void httpRequest(Context context, IResponseApi api, Map<String, String> map) {

    }
}
