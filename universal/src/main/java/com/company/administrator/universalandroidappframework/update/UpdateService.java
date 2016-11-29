package com.company.administrator.universalandroidappframework.update;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.company.administrator.universalandroidappframework.R;

import java.io.File;

/**
 * Created by Administrator on 2016/11/16.
 */

public class UpdateService extends Service{
    private String apkUrl;
    private String filePath;
    private NotificationManager notificationManager;
    private Notification notification;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        filePath = Environment.getExternalStorageDirectory()+"universal/app.apk";
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null){
            NotifyUser("下载失败","下载失败原因！",0);
            stopSelf();
        }
        apkUrl = intent.getStringExtra("url");
        NotifyUser("下载开始","下载开始",0);
        startDownload();
        return super.onStartCommand(intent,flags,startId);
    }

    private void startDownload() {
        UpdateManager.getInstance().startDownload(apkUrl, filePath, new UpdateDownloadListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onProgressChange(int progress, String downloadUrl) {
                NotifyUser("正在下载...","正在下载...",progress);
            }

            @Override
            public void onFinished(int completeSize, String downloadUrl) {
                NotifyUser("下载完成...","下载完成...",100);
                stopSelf();
            }

            @Override
            public void onFailure() {
                NotifyUser("下载失败","下载失败",0);
                stopSelf();
            }
        });

    }

    private void NotifyUser(String result, String reason,int progress) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name));
        if(progress > 0 && progress < 100){
            builder.setProgress(100,progress,false);
        }else{
            builder.setProgress(0,0,false);
        }
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        builder.setTicker(result);
        builder.setContentIntent(progress >= 100 ? getContentIntent(): PendingIntent.getActivity(
                this,0,new Intent(),PendingIntent.FLAG_CANCEL_CURRENT));
        notification  = builder.build();
        notificationManager.notify(0,notification);
    }

    private PendingIntent getContentIntent() {
        File apkFile = new File(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://"+apkFile.getAbsolutePath()),"applicatin/vnd.android.package-archive");
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent
        .FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }

}
