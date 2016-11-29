package com.http.request;

import android.app.Activity;
import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by user on 2016/4/7.
 */
public interface HttpApi {
    /**
     *
     * @param activity
     * @param api
     * @param map
     */
     void httpRequest(Activity activity, IResponseApi api, Map<String,String> map);
    /**
     *
     * @param
     * @param api
     * @param map
     */
     void httpRequest(Context context, IResponseApi api, Map<String,String> map);
    /**
     *
     * @param activity
     * @param api
     * @param map
     */
     void httpRequest(Activity activity, IResponseApi api, Map<String,String> map, ArrayList<File> lists);

}
