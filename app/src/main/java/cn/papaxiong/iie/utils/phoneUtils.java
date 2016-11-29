package cn.papaxiong.iie.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by Administrator on 2016/11/28.
 */

public class phoneUtils {
    /**
     * 获取手机串号（IMEI(International Mobile Equipment Identity)是国际移动设备身份码的缩写）
     * @param context
     * @return
     */
    public static String getIMEI(Context context){
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSimSerialNumber();
    }

    /**
     * 获取手机当前是否联网
     * @param context
     * @return
     */
    public static boolean isOnNetWork(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo == null || !networkInfo.isAvailable()) {
            return false;
        } else {
            //当前联网
            return true;
        }
    }

}
