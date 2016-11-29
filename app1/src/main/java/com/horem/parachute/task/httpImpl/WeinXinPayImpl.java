package com.horem.parachute.task.httpImpl;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.app.utils.MyEncrypt;
import com.app.utils.SignatureTool;
import com.common.HttpUrlConstant;
import com.horem.parachute.task.bean.FlowerBean;
import com.horem.parachute.task.bean.TaskPayBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.http.request.HttpApi;
import com.http.request.IResponseApi;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 2016/5/4.
 */
public class WeinXinPayImpl implements HttpApi {
    @Override
    public void httpRequest(Activity activity, final IResponseApi api, Map<String, String> map) {

        HashMap<String,String> params = new HashMap<>();
        params.clear();
        long userId = SharePreferencesUtils.getLong(activity, SharePrefConstant.MEMBER_ID,(long)0);
        String url = map.get("url");
        if(url.equals(HttpUrlConstant.flowersWeChatPay)){
            FlowerBean flowerBean = new FlowerBean();
            flowerBean.setMemberId(userId);
            flowerBean.setFlowersId(map.get("taskId"));

            params.put("flowersId",map.get("taskId"));
            params.put("sign", SignatureTool.getSignStr(flowerBean));

        }else{
            TaskPayBean bean = new TaskPayBean();
            bean.setMemberId(userId);
            bean.setTaskId(map.get("taskId"));

            params.put("taskId",map.get("taskId"));
            params.put("sign", SignatureTool.getSignStr(bean));
        }

        params.put("memberId", SharePreferencesUtils.getLong(activity, SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lng","");
        params.put("lat","");
        params.put("clientId", SharePreferencesUtils.getString(activity, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");
//        for (Map.Entry<String,String> entry:params.entrySet()){
//            Log.i(getClass().getName(),entry.getKey() +" : "+entry.getValue() );
//        }
        Log.i(getClass().getName(), "微信url : "+map.get("url") );
        OkHttpClientManager.postAsyn(map.get("url"), new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                Log.i(getClass().getName(),response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String Str = jsonObject.getJSONObject("result").getString("o");
                    String tmpKey = jsonObject.getJSONObject("result").getString("k");
                    String key = tmpKey+"a1";
                    Log.i("加密key",key);
                    String value = new MyEncrypt(key,Str).decrypt();
                    Log.i("解密后的值",value);
                    api.onSuccess(value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },params);


    }

    @Override
    public void httpRequest(Context context, IResponseApi api, Map<String, String> map) {

    }

    @Override
    public void httpRequest(Activity activity, IResponseApi api, Map<String, String> map, ArrayList<File> lists) {

    }
}
