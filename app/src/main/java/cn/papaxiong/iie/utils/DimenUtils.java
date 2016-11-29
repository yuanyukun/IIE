package cn.papaxiong.iie.utils;

import android.content.Context;

/**
 * Created by Administrator on 2016/11/29.
 * 长度相关的工具类
 */

public class DimenUtils {

    private static float mDensity = -1;
    public static int dp2px(Context context, float dpValue) {
        float scale = getScreenDensity(context);
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        float scale = getScreenDensity(context);
        return (int) (pxValue / scale + 0.5f);
    }

    public static float getScreenDensity(Context context) {
        if (mDensity == -1) {
            mDensity = context.getResources().getDisplayMetrics().density;
        }

        return mDensity;
    }
}
