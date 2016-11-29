package com.horem.parachute.mine.httpImpl;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.app.utils.SignatureTool;
import com.common.HttpUrlConstant;
import com.google.gson.Gson;
import com.horem.parachute.main.ExitSystemHttpImpl;
import com.horem.parachute.mine.bean.MyChange;
import com.horem.parachute.mine.bean.MyChangeBean;
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
 * Created by user on 2016/4/15.
 */
public class MyChangeImpl implements HttpApi{
//零钱提现
private static final String url = HttpUrlConstant.taskMyChangeUrl;
    @Override
    public void httpRequest(final Activity activity, final IResponseApi api, Map<String, String> map) {

        long userId = SharePreferencesUtils.getLong(activity, SharePrefConstant.MEMBER_ID,(long)0);
        if(userId != 0){

            MyChangeBean bean = new MyChangeBean();
            bean.setMemberId(userId);
            HashMap<String,String> params = new HashMap<>();
            params.put("s_createPersonId",userId+"");
            params.put("sign", SignatureTool.getSignStr(bean));

            params.put("memberId", SharePreferencesUtils.getLong(activity, SharePrefConstant.MEMBER_ID,(long)0)+"");
            params.put("lng","");
            params.put("lat","");
            params.put("clientId", SharePreferencesUtils.getString(activity, SharePrefConstant.INSTALL_CODE,""));
            params.put("deviceType","android");

            OkHttpClientManager.postAsyn(url, new OkHttpClientManager.ResultCallback<MyChange>() {
                @Override
                public void onError(Request request, Exception e) {

                }

                @Override
                public void onResponse(MyChange response) {
                    if(response.getStatusCode() == 1) {
                        Log.i(getClass().getName(), new Gson().toJson(response));
                        api.onSuccess(response);
                    }else if(response.getStatusCode() ==-999){
                        api.onFailed(new Exception("错误退出"));
                    }else
                        ToastManager.show(activity,"噢，网络不给力！");
                }
            },params);


        }







    }

    @Override
    public void httpRequest(Context context, IResponseApi api, Map<String, String> map) {

    }

    @Override
    public void httpRequest(Activity activity, IResponseApi api, Map<String, String> map, ArrayList<File> lists) {

    }
}
