package com.horem.parachute.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by yuanyukun on 2016/8/1.
 */
public class BootClass extends BroadcastReceiver {
    //启动应用，参数为需要自动启动的应用的包名
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(ACTION))
        {
//            context.startService(new Intent(context,
//                    com.baidu.android.pushservice.PushService.class));//启动倒计时服务
//            Toast.makeText(context, "OlympicsReminder service has started!", Toast.LENGTH_LONG).show();
            //这边可以添加开机自动启动的应用程序代码
            Intent i = new Intent(context,SplashActivity .class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
