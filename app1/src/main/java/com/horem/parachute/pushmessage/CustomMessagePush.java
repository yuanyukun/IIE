package com.horem.parachute.pushmessage;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.common.HttpUrlConstant;
import com.horem.parachute.R;
import com.horem.parachute.balloon.Bean.BaseResultBean;
import com.horem.parachute.main.AppMainActivity;
import com.horem.parachute.main.ExitSystemHttpImpl;
import com.horem.parachute.mine.Activity_Mine_Task_Received;
import com.horem.parachute.mine.Activity_Mine_Task_send;
import com.horem.parachute.mine.HomeChatActivity;
import com.horem.parachute.mine.TaskSendConfirm;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.http.request.HttpApi;
import com.http.request.IResponseApi;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 2016/5/6.
 */
public class CustomMessagePush extends PushMessageReceiver {

    /**
     * 调用PushManager.startWork后，sdk将对push
     * server发起绑定请求，这个过程是异步的。绑定请求的结果通过onBind返回。 如果您需要用单播推送，需要把这里获取的channel
     * id和user id上传到应用server中，再调用server接口用channel id和user id给单个手机或者用户推送。
     *
     * @param context
     *            BroadcastReceiver的执行Context
     * @param errorCode
     *            绑定接口返回值，0 - 成功
     * @param appid
     *            应用id。errorCode非0时为null
     * @param userId
     *            应用user id。errorCode非0时为null
     * @param channelId
     *            应用channel id。errorCode非0时为null
     * @param requestId
     *            向服务端发起的请求id。在追查问题时有用；
     * @return none
     */
    @Override
    public void onBind(Context context, int errorCode, String appid,
                       String userId, String channelId, String requestId) {
        String result = "errorCode = "+errorCode+" appid = "+appid+" userId = "+userId
                +"ChannelId = "+channelId+" requestId = "+requestId;
        Log.d("baidu_Push_service",result);


        if (errorCode == 0) {// 绑定成功
            Log.d("百度推送","绑定成功了");
            SharePreferencesUtils.setString(context, SharePrefConstant.CHANNEL_ID,channelId);
            SharePreferencesUtils.setString(context,SharePrefConstant.USER_ID,userId);
            updateChannelId(SharePreferencesUtils.getLong(context,SharePrefConstant.MEMBER_ID,(long)0),context);
        }

    }

    /**
     * 接收透传消息的函数。
     *
     * @param context
     *            上下文
     * @param message
     *            推送的消息
     * @param customContentString
     *            自定义内容,为空或者json字符串
     */
    @Override
    public void onMessage(Context context, String message,
                          String customContentString) {
        String messageString = "透传消息 message=\"" + message
                + "\" customContentString=" + customContentString;
//        Log.d(TAG, messageString);
        String title = null;
        String description = null;
        String custom_content = null;
        String id = null;// 消息的id
        int type = 0;
        // 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
        if (!TextUtils.isEmpty(message)) {
            JSONObject customJson;
            JSONObject customJson_Content;
            try {
                customJson = new JSONObject(message);
                if (!customJson.isNull("title")) {
                    title = customJson.getString("title");
                }
                if (!customJson.isNull("description")) {
                    description = customJson.getString("description");
                }
                if (!customJson.isNull("custom_content")) {
                    custom_content = customJson.getString("custom_content");
                }
                if (custom_content != null) {
                    customJson_Content = new JSONObject(custom_content);
                    if (!customJson_Content.isNull("id")) {
                        id = customJson_Content.getString("id");
                    }

                    if (!customJson_Content.isNull("type")) {
                        type = customJson_Content.getInt("type");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (!isAppRun(context)) {// 如果运行则不显示，不运行则显示
            Intent kvIntent = new Intent(context,
                    MyTouChuanMessage_Receiver.class);
            kvIntent.putExtra("id", id);
            kvIntent.putExtra("type", type);

            NotificationUtil.sendNotificationBuilder(context, kvIntent, title,
                    description, type);
        }
    }

    /**
     * 接收通知点击的函数。注：推送通知被用户点击前，应用无法通过接口获取通知的内容。
     *
     * @param context
     *            上下文
     * @param title
     *            推送的通知的标题
     * @param description
     *            推送的通知的描述
     * @param customContentString
     *            自定义内容，为空或者json字符串
     */
    @Override
    public void onNotificationClicked(Context context, String title,
                                      String description, String customContentString) {
        String notifyString = "通知点击 title=\"" + title + "\" description=\""
                + description + "\" customContent=" + customContentString;
        Log.d(TAG, notifyString);
        showActivity(context, customContentString);

    }

    /**
     * 接收通知到达的函数。
     *
     * @param context
     *            上下文
     * @param title
     *            推送的通知的标题
     * @param description
     *            推送的通知的描述
     * @param customContentString
     *            自定义内容，为空或者json字符串
     */

    @Override
    public void onNotificationArrived(Context context, String title,
                                      String description, String customContentString) {
        Log.e("CustomApp","有信息到达");
    }

    /**
     * setTags() 的回调函数。
     *
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示某些tag已经设置成功；非0表示所有tag的设置均失败。
     * @param sucessbaiduTags
     *            设置成功的tag
     * @param failTags
     *            设置失败的tag
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onSetTags(Context context, int errorCode,
                          List<String> sucessbaiduTags, List<String> failTags,
                          String requestId) {
        String responseString = "onSetTags errorCode=" + errorCode
                + " sucessTags=" + sucessbaiduTags + " failTags=" + failTags
                + " requestId=" + requestId;
        Log.d(TAG, responseString);
    }

    /**
     * delTags() 的回调函数。
     *
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示某些tag已经删除成功；非0表示所有tag均删除失败。
     * @param sucessBaiDuTags
     *            成功删除的tag
     * @param failTags
     *            删除失败的tag
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onDelTags(Context context, int errorCode,
                          List<String> sucessBaiDuTags, List<String> failTags,
                          String requestId) {
        String responseString = "onDelTags errorCode=" + errorCode
                + " sucessBaiDuTags=" + sucessBaiDuTags + " failTags="
                + failTags + " requestId=" + requestId;
        Log.e(TAG, responseString);
    }

    /**
     * listTags() 的回调函数。
     *
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示列举tag成功；非0表示失败。
     * @param tags
     *            当前应用设置的所有tag。
     * @param requestId
     *             分配给对云推送的请求的id
     */
    @Override
    public void onListTags(Context context, int errorCode, List<String> tags,
                           String requestId) {
        String responseString = "onListTags errorCode=" + errorCode + " tags="
                + tags;
        Log.e(TAG, responseString);
    }

    /**
     * PushManager.stopWork() 的回调函数。
     *
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示从云推送解绑定成功；非0表示失败。
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onUnbind(Context context, int errorCode, String requestId) {
        String responseString = "onUnbind errorCode=" + errorCode
                + " requestId = " + requestId;
//        Log.e(TAG, responseString);

        // 解绑定成功，设置未绑定flag，
        if (errorCode == 0) {
            // PreUtils.unbind(context);
            Log.d(getClass().getName(),"解绑百度推送");
        }
    }

    /**
     * 当点击notification的时候的处理方式
     *
     * @param context
     * @param customContentString
     */
    private void showActivity(Context context, String customContentString) {
        String id = null;// 消息的id
        int type = 0;
        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                if (!customJson.isNull("id")) {
                    id = customJson.getString("id");
                }
                if (!customJson.isNull("type")) {
                    type = customJson.getInt("type");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        boolean isRun = isAppRun(context);
        if (!isRun) {// 如果没有运行重启

            PackageManager packageManager = context.getPackageManager();
            Intent intent =packageManager.getLaunchIntentForPackage("com.horem.parachute");
            intent.putExtra("id", id);
            intent.putExtra("type", type);
            context.getApplicationContext().startActivity(intent);

        } else {// 如果在运行就只是打开页面
             Intent intent = null;
            switch (type){
                case 10:
                    intent = new Intent(context.getApplicationContext(),AppMainActivity.class);
                    break;
                case 20:
//                    intent.setClass(context.getApplicationContext(),MainActivity.class);
                    break;
                case 30:
                    intent = new Intent(context.getApplicationContext(), AppMainActivity.class);
                    break;
                case 40:
                    intent = new Intent(context.getApplicationContext(), AppMainActivity.class);
                    break;
                case 50:
                    intent = new Intent(context.getApplicationContext(), AppMainActivity.class);
                    break;
            }
            if(intent != null) {
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(intent);
            }
        }
    }


    private boolean isAppRun(Context context) {
        boolean isInBackground = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                //前台程序
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals("com.horem.parachute")) {
                            isInBackground = true;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals("com.horem.parachute")) {
                isInBackground = true;
            }
        }

        return isInBackground;
    }

    private void updateChannelId(long memberId, final Context context) {

        HashMap<String,String> map = new HashMap<>();

        map.put("channelId",SharePreferencesUtils.getString(context,SharePrefConstant.CHANNEL_ID,""));//绑定百度推送返回的channelId
        map.put("userId",SharePreferencesUtils.getString(context,SharePrefConstant.USER_ID,""));//百度推送返回的应用userId

        map.put("memberId",memberId+"");//用户Id
        map.put("lng","");
        map.put("lat","");
        map.put("clientId", SharePreferencesUtils.getString(context, SharePrefConstant.INSTALL_CODE,""));
        map.put("deviceType","android");

        OkHttpClientManager.postAsyn(HttpUrlConstant.updateChannelId, new OkHttpClientManager.ResultCallback<BaseResultBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(context,"噢，网络不给力！");
            }

            @Override
            public void onResponse(BaseResultBean response) {
                Log.e("更新ChannelId返回结果：",response.getMessage());
                if(response.getStatusCode() == -999){
                    HttpApi httpApi = new ExitSystemHttpImpl();
                    httpApi.httpRequest(context, new IResponseApi() {
                        @Override
                        public void onSuccess(Object object) {

                        }

                        @Override
                        public void onFailed(Exception e) {

                        }
                    },new HashMap<String, String>());
                }
            }
        },map);
    }
}
