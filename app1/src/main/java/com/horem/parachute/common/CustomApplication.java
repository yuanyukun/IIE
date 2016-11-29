package com.horem.parachute.common;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.horem.parachute.R;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by user on 2016/3/14.
 */
public class CustomApplication extends Application {

    private static CustomApplication instance;

    private long userId;         //用户Id
    private boolean isLogin;    //登录状态

    private boolean isFirstLoad;

    public boolean isFirstLoad() {
        return isFirstLoad;
    }

    public void setFirstLoad(boolean firstLoad) {
        isFirstLoad = firstLoad;
    }

    //消息轮询机制
    public boolean isSubTaskList;
    public boolean isTaskOrderList;
    public boolean isMessageList;
    public boolean isViewUserList;
    public boolean beFollowList;
    public boolean flowersToMeList;

    public boolean isGiveLikeMeList() {
        return giveLikeMeList;
    }

    public void setGiveLikeMeList(boolean giveLikeMeList) {
        this.giveLikeMeList = giveLikeMeList;
    }

    public boolean giveLikeMeList;

    public boolean isBeFollowList() {
        return beFollowList;
    }

    public void setBeFollowList(boolean beFollowList) {
        this.beFollowList = beFollowList;
    }

    public boolean isFlowersToMeList() {
        return flowersToMeList;
    }

    public void setFlowersToMeList(boolean flowersToMeList) {
        this.flowersToMeList = flowersToMeList;
    }

    public boolean isSubTaskList() {
        return isSubTaskList;
    }

    public void setSubTaskList(boolean subTaskList) {
        isSubTaskList = subTaskList;
    }

    public boolean isTaskOrderList() {
        return isTaskOrderList;
    }

    public void setTaskOrderList(boolean taskOrderList) {
        isTaskOrderList = taskOrderList;
    }

    public boolean isMessageList() {
        return isMessageList;
    }

    public void setMessageList(boolean messageList) {
        isMessageList = messageList;
    }

    public boolean isViewUserList() {
        return isViewUserList;
    }

    public void setViewUserList(boolean viewUserList) {
        isViewUserList = viewUserList;
    }


    //自动升级相关
    public  static  boolean isRegistered = false;
    public  static  boolean updateFirstLogin = true;
    // 微信登陆
    public static IWXAPI api;
    public static final String AppID = "wx97cabf1d699d1de7";
    public static final String AppSecret = "9239cf136cba2f9aebae948d2ba6b6ef";
    public String appCode;

    //google analytics
    private Tracker mTracker;

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }


    //在application中使用
    public static void sendLocalBroadcast(Intent intent) {
        LocalBroadcastManager.getInstance(getInstance()).sendBroadcastSync(intent);
    }
    public static void registerLocalReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        LocalBroadcastManager.getInstance(getInstance()).registerReceiver(receiver, filter);
    }

    public static void unregisterLocalReceiver(BroadcastReceiver receiver) {
            LocalBroadcastManager.getInstance(getInstance()).unregisterReceiver(receiver);
    }



    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }


    // 版本号
    public static int version;

    // 用户上传头像
    static Stack<Activity> statck = new Stack<>();

    public static void addActivity(Activity activity) {
        statck.add(activity);
    }

    public static void exitSystem() {
        for (Activity activity : statck) {
            if (activity != null) {
                activity.finish();
            }
        }
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.exit(0);
            }
        };
        timer.schedule(task, 1000);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

//        ViewTarget.setTagId(R.id.glide_tag);
        // 微信登陆
        api = WXAPIFactory.createWXAPI(this,AppID,true);
        api.registerApp(AppID);


        // 获取当前本应用的版本号
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            String versionName = packInfo.versionName;

            String[] arr = versionName.split("\\.");
            String str = "";
            if (arr.length > 0) {
                for (int i = 0; i < arr.length; i++) {
                    str = str + arr[i];
                }
            }
            CustomApplication.version = Integer.valueOf(str);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static CustomApplication getInstance(){
        return instance;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
