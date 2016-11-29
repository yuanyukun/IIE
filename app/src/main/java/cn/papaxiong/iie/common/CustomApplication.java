package cn.papaxiong.iie.common;

import android.app.Application;

/**
 * Created by Administrator on 2016/11/29.
 */

public class CustomApplication extends Application {
    public static CustomApplication sCustomApplication;


    public static CustomApplication getInstance(){
        return sCustomApplication;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        sCustomApplication = this;
    }
}
