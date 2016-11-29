package cn.papaxiong.iie.utils;

import android.util.Log;

/**
 * Created by Administrator on 2016/11/29.
 * 打印类
 */

public class L {
    private boolean isDebug = true;
    private static final String TAG = "appLog";
    public static  void v(String msg){
        Log.v(TAG,msg);
    }
    public static void d(String msg){
        Log.d(TAG,msg);
    }
    public static void e(String msg){
        Log.e(TAG,msg);
    }
    public static void i(String msg){ Log.i(TAG,msg);}
    public static void w(String msg){
        Log.w(TAG,msg);
    }
}
