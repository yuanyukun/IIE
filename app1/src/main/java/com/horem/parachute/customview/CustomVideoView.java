package com.horem.parachute.customview;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.common.ApplicationConstant;
import com.common.HttpUrlConstant;
import com.horem.parachute.R;
import com.horem.parachute.autoupdate.internal.NetworkUtil;
import com.horem.parachute.balloon.Bean.BaseResultBean;
import com.horem.parachute.common.CustomLoading;
import com.horem.parachute.main.ExitSystemHttpImpl;
import com.horem.parachute.menu.PopupPhotoView;
import com.horem.parachute.util.BitmapUtils;
import com.horem.parachute.util.ScreenBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.horem.parachute.util.Utils;
import com.http.request.HttpApi;
import com.http.request.IResponseApi;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yuanyukun on 2016/7/7.
 * public View (Context context)是在java代码创建视图的时候被调用，如果是从xml填充的视图，就不会调用这个
 public View (Context context, AttributeSet attrs)这个是在xml创建但是没有指定style的时候被调用
 public View (Context context, AttributeSet attrs, int defStyle)这个不用说也懂了吧
 */
public class CustomVideoView extends RelativeLayout {

    private int mediaType;
    private double fileSize;
    private long fileLength;
    private String videoName;
    private Context context;
    private String runClassName;
    private Bitmap sourceBitmap;

     private ImageView imgOriginal;
    private ImageView imgStart;
    private TextView tvTips;
    private TextView tvFileSize;
    private TextView tvFileLength;
    private TextView tvPrivateMark;
    private CustomLoading customLoadingDialog = null;


    public CustomVideoView(Context context) {
        super(context);

    }
    public void isPrivate(boolean isPrivate){
        if(isPrivate){
            tvPrivateMark.setVisibility(VISIBLE);
            Drawable drawable = ContextCompat.getDrawable(context,R.mipmap.private_48);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth() / 2, drawable.getIntrinsicHeight() / 2);
            tvPrivateMark.setCompoundDrawables(drawable,null, null, null);
        }else {
            tvPrivateMark.setVisibility(INVISIBLE);
        }
    }

    /**
     * 此方法会引起iamgeview 的scaleType无效，故需重新设置setDrawable并adjustviewBound
     * @return
     */
    public Bitmap getSourceBitmap(){
        return sourceBitmap;
    }
    public void setFileInfoHide(){
        tvTips.setVisibility(INVISIBLE);
        tvFileSize.setVisibility(INVISIBLE);
        tvFileLength.setVisibility(INVISIBLE);
    }
    public void setFileInfoShow(){
        tvFileSize.setVisibility(VISIBLE);
        tvFileLength.setVisibility(VISIBLE);
        tvTips.setVisibility(VISIBLE);
    }
    public void setData(final int mediaType, double fileSize, long fileLength, final String videoName,String previewImg,String className){
        this.mediaType = mediaType;
        this.fileSize = fileSize;
        this.fileLength = fileLength;
        this.videoName = videoName;
        this.runClassName = className;
        imgOriginal.setScaleType(ImageView.ScaleType.FIT_XY);
        switch (mediaType){
            case ApplicationConstant.MEDIA_TYPE_PHOTO:
                tvTips.setVisibility(View.INVISIBLE);
                imgStart.setVisibility(View.INVISIBLE);
                tvFileSize.setVisibility(INVISIBLE);
                tvFileLength.setVisibility(INVISIBLE);

                if(null != videoName) {
                    Glide.with(getContext())
                            .load(Utils.getSmalleImageUrl(videoName, 200, 200, context))
                            .asBitmap()
                            .centerCrop()
                            .into(new SimpleTarget<Bitmap>(ScreenBean.getScreenWidth(), ScreenBean.getScreenWidth()) {

                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                                    imgOriginal.setImageBitmap(resource);
                                    BitmapUtils.setImageViewMathParent(imgOriginal,resource);
                                    sourceBitmap = resource;
                                }
                            });
                }
                    break;
            case ApplicationConstant.MEDIA_TYPE_VIDEO:
                tvTips.setVisibility(View.VISIBLE);
                imgStart.setVisibility(View.VISIBLE);
                tvFileSize.setVisibility(VISIBLE);
                tvFileLength.setVisibility(VISIBLE);
                if(null != videoName) {
                    if (Utils.isFileCache(context, videoName)) {
                        setFileInfoHide();
                    } else {
                        setFileInfoShow();
                    }

                    tvFileLength.setText(Utils.getTwoLastNumber(fileSize / 1024) + "M");
                    tvFileSize.setText(Utils.showFileLength(fileLength));
                    imgStart.setVisibility(View.VISIBLE);
                    String url = Utils.getVideoPreviewImgUrl(previewImg);

                    Glide.with(getContext())
                            .load(url)
                            .asBitmap()
                            .centerCrop()
                            .into(new SimpleTarget<Bitmap>(ScreenBean.getScreenWidth(), ScreenBean.getScreenWidth()) {

                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                                    imgOriginal.setImageBitmap(resource);
                                    BitmapUtils.setImageViewMathParent(imgOriginal,resource);
                                    sourceBitmap = resource;
                                }
                            });
                }
                break;
        }
        imgOriginal.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dealWithClickedEvent(context,v,videoName,mediaType);
            }
        });
    }
    public  void setSmallSize(){
        LayoutParams lp = (LayoutParams) imgStart.getLayoutParams();
        lp.width = Utils.dp2px(context,30);
        lp.height = Utils.dp2px(context,30);
        imgStart.setLayoutParams(lp);
        tvTips.setTextSize(12);
    }

    private void init(final Context context) {
        LayoutInflater.from(context).inflate(R.layout.custom_video_view,this);
        imgOriginal = (ImageView) findViewById(R.id.custom_video_view_origin);
        imgStart = (ImageView) findViewById(R.id.img_mark);
        tvTips = (TextView) findViewById(R.id.tv_custom_view_mark);
        tvFileSize = (TextView) findViewById(R.id.tv_custom_view_file_size);
        tvFileLength = (TextView) findViewById(R.id.tv_custom_view_file_length);
        tvPrivateMark = (TextView) findViewById(R.id.video_preview_is_private);
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context =context;
        init(context);
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public  void dealWithClickedEvent(final Context activity, View view, final String videoName, int MediaType) {
        switch (MediaType){
            case ApplicationConstant.MEDIA_TYPE_PHOTO:
                List<String> list = new ArrayList<>();
                String url = Utils.getBigImageUrl(SharePreferencesUtils.getLong(activity, SharePrefConstant.MEMBER_ID,(long)0),videoName);
                list.add(url);
                PopupPhotoView photoView = new PopupPhotoView(activity,list,0);
                photoView.showAtLocation(view, Gravity.NO_GRAVITY,0,0);
                break;
            case ApplicationConstant.MEDIA_TYPE_VIDEO:
                boolean isCache =  Utils.isFileCache(activity,videoName);
                final String Url = Utils.getVideoFileUrl(activity,videoName);

                if(isCache){
                    Utils.playVideoFile(Utils.getVideoFileNewName(activity,videoName),activity);
                    notifyServer(activity,videoName);
                }else {
                    boolean download_3G = SharePreferencesUtils.getBoolean(activity, SharePrefConstant.DOWNLOAD_3G, false);
                    int type = NetworkUtil.getNetworkType(activity);
                    switch (type) {
                        case 1://wifi
                            downloadVideoFile(activity, videoName, Url);
                            break;
                        case 2://3G
                            if (!download_3G) {//进设置wifi状态下下载时，提醒用户流量
                                new AlertView("下载提示", "当前不在WIFI网络下，继续下载播放吗？", null, new String[]{"确定", "取消"}, null, activity,
                                        AlertView.Style.Alert, new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(Object o, int position) {
                                        switch (position) {
                                            case 0:
                                                Log.d(getClass().getName(), "position 0 just pushed");
                                                downloadVideoFile(activity, videoName, Url);
                                                break;
                                            case 1:
                                                Log.d(getClass().getName(), "position 1 just pushed");
                                                break;
                                        }
                                    }
                                }).show();
                            } else {
                                downloadVideoFile(activity, videoName, Url);
                            }
                            break;
                    }
                    break;
                }
                break;
        }
    }

    public  void notifyServer(final Context context, String videoName) {

        HashMap<String,String> params = new HashMap<>();
        params.put("memberId",SharePreferencesUtils.getLong(context,SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lat","");
        params.put("lng","");
        params.put("clientId",SharePreferencesUtils.getString(context,SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");

        params.put("fileName",videoName);
        String url = HttpUrlConstant.balloonVideoView;
        Log.d(getClass().getName(),url);
        OkHttpClientManager.postAsyn(url, new OkHttpClientManager.ResultCallback<BaseResultBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(context,"噢，网络不给力！");
            }
            @Override
            public void onResponse(BaseResultBean response) {
//                Log.d(getClass().getName(),response);//{"message":"查看气球视频","statusCode":1}
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
        },params);

    }

    public void downloadVideoFile(final Context activity, final String videoName, String url) {
        startLoading("正在下载...");
        OkHttpClientManager.downloadAsyn(url, ApplicationConstant.MEDIA_FILES, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
//                Log.d("件下载错误", e.toString() + "");
//                activity.stopLoading();
                ToastManager.show(context,"噢，网络不给力！");
            }

            @Override
            public void onResponse(String response) {
                stopLoading();
                //response 为文件的下载文件的绝对路径
//                Log.d("下载文件的绝对路径", Utils.getFileNameSplit(response));
                String oldName = Utils.getFileNameSplit(response);
                String newName =Utils.getVideoFileNewName( activity,videoName);
                //耗时任务
                boolean rename = Utils.renameFile(oldName,newName);
                if (rename) {
                    if(isTopActivity(getTopTask(activity),"com.horem.parachute",runClassName)) {
                        notifyServer(activity, videoName);
                        setFileInfoHide();
                        Utils.playVideoFile(newName, activity);
                    }
                }else{
                    ToastManager.show( activity,"文件错误，不能播放");
                }
            }
        });
    }
    /**
     * 进度加载动画
     */
    public void startLoading(String msg){
        if(customLoadingDialog == null){
            customLoadingDialog = CustomLoading.CreateLoadingDialog(getContext());
            customLoadingDialog.setMessage(msg);
            customLoadingDialog.setCanceledOnTouchOutside(false);//不允许点击取消

            Window wd = customLoadingDialog.getWindow();
            WindowManager.LayoutParams lp = wd.getAttributes();
            lp.alpha = 0.5f;
            wd.setAttributes(lp);
        }
        customLoadingDialog.show();
    }
    /**
     * 关闭加载动画
     */
    public void stopLoading(){
        if(customLoadingDialog != null){
            customLoadingDialog.dismiss();
            customLoadingDialog.dismiss();
        }
    }



    public ActivityManager.RunningTaskInfo getTopTask(Context mContext) {
        ActivityManager mActivityManager;

        mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = mActivityManager.getRunningTasks(1);
        if (tasks != null && !tasks.isEmpty()) {
            return tasks.get(0);
        }

        return null;
    }
    public boolean isTopActivity(
            ActivityManager.RunningTaskInfo topTask,
            String packageName,
            String activityName) {
        if (topTask != null) {
            ComponentName topActivity = topTask.topActivity;
            String currentPackageName = topActivity.getPackageName();
            String currentActyName = removePackageInfo(topActivity.getClassName());
            if (currentPackageName.equals(packageName) &&
                    currentActyName.equals(activityName)) {
                return true;
            }
        }

        return false;
    }
    public String removePackageInfo(String wholePackageName){
        String[] infos = wholePackageName.split("\\.");
        return  infos[infos.length-1];
    }
}
