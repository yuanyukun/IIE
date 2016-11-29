package com.horem.parachute.task;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.common.HttpUrlConstant;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.common.ApplicationConstant;
import com.horem.parachute.autoupdate.internal.NetworkUtil;
import com.horem.parachute.balloon.BalloonItemInfoActivity;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.customview.ActionSheetDialog;
import com.horem.parachute.customview.AlertDialog;
import com.horem.parachute.customview.CustomHeadView;
import com.horem.parachute.customview.CustomVideoView;
import com.horem.parachute.login.Activity_Login;
import com.horem.parachute.login.bean.SubTaskBean;
import com.horem.parachute.menu.PopupPhotoView;
import com.horem.parachute.mine.Activity_Setting;
import com.horem.parachute.mine.AppUserInfo;
import com.horem.parachute.mine.ChatActivity;
import com.horem.parachute.mine.HomeTipsActivity;
import com.horem.parachute.mine.bean.ConfirmBean;
import com.horem.parachute.mine.bean.MessageBean;
import com.horem.parachute.mine.bean.ShowMessageSubBean;
import com.horem.parachute.mine.bean.SubTaskInfoBean;
import com.horem.parachute.mine.bean.TaskConfirmBean;
import com.horem.parachute.util.BitmapUtils;
import com.horem.parachute.util.MyTimeUtil;
import com.horem.parachute.util.ScreenBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.horem.parachute.util.Utils;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.senab.photoview.Compat;

public class TaskInformationActivity extends BaseActivity implements View.OnClickListener,AMapLocationListener,AMap.OnMapTouchListener{

    private TextView taskFee;
    private CustomHeadView userHeader;
    private TextView userName;
    private TextView taskDescriber;
    private TextView taskCreateTime;
    private TextView taskOverTime;
    private TextureMapView mapView;
    private RelativeLayout taskMove;
    private TextView careBtn;
    private TextView tvTaskType;
    private ImageView timeOverImg;
    private LinearLayout taskinfoContainer;
    private RelativeLayout previewContainer;
    private ScrollView scrollView;

    private int shareType;
    private String subTaskId;

    private AMap aMap;
    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    private AMapLocationClientOption mLocationOption = null;
    private LatLng currentLatLng;
    private boolean isFirstEnter = true;
    private TaskConfirmBean OrderBean;
    private SubTaskInfoBean bean;
    private CustomVideoView customVideoView;

    private static  final  String TAG = "TaskInfoActy";
    private CustomApplication application;
    private Tracker mTracker;
    @Override
    protected void onStart() {
        super.onStart();
        //google analytics
        application = (CustomApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(this.getPackageName()+" [Android] " + TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Share")
                .build());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_information);
        init(savedInstanceState);
        startLoading();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (SharePreferencesUtils.getBoolean(getApplicationContext(),SharePrefConstant.isNeedConfirmShare,false)) {
            Log.d("分享成功", "shareType:" + shareType + " balloonId:" + bean.getSubTaskId());
            SharePreferencesUtils.setBoolean(getApplicationContext(),SharePrefConstant.isNeedConfirmShare,false);
            HashMap<String, String> params = new HashMap<>();
            params.put("shareToType", shareType + "");
            params.put("subTaskId", bean.getSubTaskId());
            params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID, (long) 0) + "");
            params.put("lng", "");
            params.put("lat", "");
            params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE, ""));
            params.put("deviceType", "android");
            OkHttpClientManager.postAsyn(HttpUrlConstant.subTaskShare, new OkHttpClientManager.ResultCallback<MessageBean>() {
                @Override
                public void onError(Request request, Exception e) {
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                }

                @Override
                public void onResponse(MessageBean response) {
//                    Log.d(getClass().getName(), new Gson().toJson(response));
                    if (response.getStatusCode() == 1) {
                        ToastManager.show(TaskInformationActivity.this, response.getMessage());
//                        getData();
                    }else if(response.getStatusCode() == -999){
                        exitApp();
                    }else
                        ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                }
            }, params);
        }
    }

    private void init(Bundle savedInstanceState) {
        initTitleView();
        setTitleName("");
        setRightButtonDrawableRight(ContextCompat.getDrawable(this,R.mipmap.more_60));
        subTaskId = getIntent().getStringExtra("subTaskId");
        application = (CustomApplication) getApplication();

        scrollView = (ScrollView) findViewById(R.id.task_scrollView);
        customVideoView = (CustomVideoView) findViewById(R.id.task_information_preview);

        previewContainer = (RelativeLayout) findViewById(R.id.preview_image_container);
        taskinfoContainer = (LinearLayout) findViewById(R.id.balloon_info_container);
        timeOverImg = (ImageView) findViewById(R.id.balloon_info_timeout_img);
        careBtn = (TextView) findViewById(R.id.home_fragment_button);
        taskFee = (TextView) findViewById(R.id.task_info_fee);
        userHeader = (CustomHeadView) findViewById(R.id.task_info_user_icon);
        userName = (TextView) findViewById(R.id.task_info_user_name);
        taskDescriber = (TextView) findViewById(R.id.task_info_task_describer);
        taskCreateTime = (TextView) findViewById(R.id.task_info_create_time);
        tvTaskType = (TextView) findViewById(R.id.tv_task_type);
        taskMove = (RelativeLayout)findViewById(R.id.task_info_move);
        taskOverTime = (TextView) findViewById(R.id.task_info_over_time);
        taskMove.setOnClickListener(this);

        initMap(savedInstanceState);
        initMapSetting();
    }

    @Override
    protected void OnRightButtonClicked() {
        super.OnRightButtonClicked();
        if(CustomApplication.getInstance().isLogin()) {
            new ActionSheetDialog(this).builder()
                    .setCancelable(true)
                    .setCanceledOnTouchOutside(false)
                    .addSheetItem("分享到朋友圈", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                        @Override
                        public void onClick(int which) {
                            shareType = 10;
                            sendToFriendCommunity(true, bean.getSubTaskId());
                        }
                    })
                    .addSheetItem("分享到朋友", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                        @Override
                        public void onClick(int which) {
                            shareType = 20;
                            sendToFriendCommunity(false, bean.getSubTaskId());
                        }
                    })
                    .addSheetItem("举报", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                        @Override
                        public void onClick(int which) {
                            Intent intent = new Intent(TaskInformationActivity.this, ChatActivity.class);
                            intent.putExtra("currentId",29+"");
                            intent.putExtra("type",10+"");
                            intent.putExtra("name","伞来了团队");
                            startActivity(intent);
                        }
                    }).show();
        }else{
            Intent intent = new Intent(TaskInformationActivity.this,Activity_Login.class);
            startActivity(intent);
        }
    }
    private void sendToFriendCommunity(final boolean shareType, String taskId) {
        HashMap<String, String> params = new HashMap<>();

//        params.put("shareToType",type+"");
        params.put("subTaskId", taskId);
        params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID, (long) 0) + "");
        params.put("lng", "");
        params.put("lat", "");
        params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE, ""));
        params.put("deviceType", "android");

        OkHttpClientManager.postAsyn(HttpUrlConstant.subTaskShareUrl, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                SharePreferencesUtils.setBoolean(TaskInformationActivity.this, SharePrefConstant.iswxLon, false);
                String webUrl = "";
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("statusCode") == 1) {
                        webUrl = jsonObject.getString("result");
                        SharePreferencesUtils.setBoolean(getApplicationContext(),SharePrefConstant.isNeedConfirmShare,true);
                        WXWebpageObject webpage = new WXWebpageObject();
                        webpage.webpageUrl = webUrl;

                        WXMediaMessage msg = new WXMediaMessage(webpage);
                        msg.title ="你的朋友在"+bean.getPlace();
                        msg.description = "伞来了-即时多媒体交易平台" + "伞来了是一款即时多媒体交易平台，全世界亿万用户的分享经济平台。";
//                msg.description = "伞来了是一款即时多媒体交易平台，全世界亿万用户的分享经济平台。";
                        Bitmap thump;
                        if(bean.getAttList() != null){
                            Bitmap bitmap =  customVideoView.getSourceBitmap();
                            Bitmap newBitmap = BitmapUtils.ChangeBitmapSize(100/bitmap.getHeight(),bitmap);
                            thump = newBitmap;
                        }else{
                            thump = BitmapFactory.decodeResource(getResources(), R.mipmap.logo_80);
                        }
//
                        msg.thumbData = Utils.bmpToByteArray(thump, true);

                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = buildTransaction("webpage");

                        req.message = msg;
                        req.scene = shareType ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
                        CustomApplication.api.sendReq(req);
                    }else if(jsonObject.getInt("statusCode")== -999){
                        exitApp();
                    }else
                        ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, params);

    }
    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private void initData() {
        HashMap<String,String> map = new HashMap<>();
        map.put("memberId",SharePreferencesUtils.getLong(this,SharePrefConstant.MEMBER_ID,(long)0)+"");
        map.put("subTaskId",subTaskId);
        map.put("lng",currentLatLng.longitude+"");
        map.put("lat",currentLatLng.latitude+"");
        map.put("deviceType","android");
        map.put("clientId",SharePreferencesUtils.getString(this,SharePrefConstant.INSTALL_CODE,""));

        OkHttpClientManager.postAsyn(HttpUrlConstant.subTaskInfoUrl, new OkHttpClientManager.ResultCallback<TaskConfirmBean>() {
            @Override
            public void onError(Request request, Exception e) {
                stopLoading();
            }

            @Override
            public void onResponse(TaskConfirmBean response) {
                stopLoading();
//                Log.d(getClass().getName(),new Gson().toJson(response));
                if(response.getStatusCode() ==1) {
                    OrderBean = response;
                    initView();
                }else if(response.getStatusCode() == -999){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }
        },map);

    }
    private void initMapSetting() {
        aMap.setOnMapTouchListener(this);
        //初始化定位
        mLocationClient = new AMapLocationClient(this);
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //隐藏定位
        aMap.getUiSettings().setZoomControlsEnabled(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(10000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //设置定位回调监听
        mLocationClient.setLocationListener(TaskInformationActivity.this);
        //启动定位
        mLocationClient.startLocation();
    }
    private void initView() {

         bean = OrderBean.getResult().getSubTaskInfo();


        taskOverTime.setText(
                bean.getTimeOutStr());
        if(bean.isTimeOut()){
            timeOverImg.setImageResource(R.mipmap.red_clock_48);
            taskinfoContainer.setBackgroundColor(ContextCompat.getColor(this,R.color.lightRed));
        }else{
            timeOverImg.setImageResource(R.mipmap.blue_clock_48);
            taskinfoContainer.setBackgroundColor(ContextCompat.getColor(this,R.color.lightBlue));
        }
//        taskNo.setText("伞号："+bean.getNo());
        taskFee.setText(bean.getSymbol()+Utils.getTwoLastNumber(bean.getRemuneration()));
        userHeader.showHeadView(bean.getCreatePersonHead(),bean.getAuthType(),bean.getCreatePersonId(),true);

        userName.setText(bean.getCreatePersonName());
        taskDescriber.setText(bean.getOtherRequirements());

        taskCreateTime.setText(MyTimeUtil.friendlyTime(MyTimeUtil.formatTimeWhole(bean.getCreateTime())));
        switch (bean.getMediaType()){
            case ApplicationConstant.MEDIA_TYPE_PHOTO:
                tvTaskType.setText("拍照片");
                break;
            case ApplicationConstant.MEDIA_TYPE_VIDEO:
                tvTaskType.setText("拍视频");
                break;
        }

        if(bean.isFollow()){
            careBtn.setText("已关注");
        }else{
            careBtn.setText("关注");
        }
        careBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CustomApplication.getInstance().isLogin()){
                    HashMap<String,String> params = buildRequestParams();
                    if(!bean.isFollow()){
                        addFollow(params);
                    }else {
                        delFollow(params);

                    }
                }else{
                    Intent intent = new Intent(getApplicationContext(),Activity_Login.class);
                    startActivity(intent);
                }
            }
        });

        MarkerOptions marker = new MarkerOptions();
        switch (bean.getMediaType()){
            case ApplicationConstant.MEDIA_TYPE_PHOTO:
                marker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.red_parachute_1_100));
                break;
            case ApplicationConstant.MEDIA_TYPE_VIDEO:
                marker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.parachute_1_100));
                break;
        }
        marker.draggable(false);
        marker.position(currentLatLng);
        aMap.addMarker(marker);
        Log.d("预览图的信息：",new Gson().toJson(bean.getAttList().toString()));
        if(bean.getAttList() !=null && bean.getAttList().size() > 0){
            LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(ScreenBean.getScreenWidth()-40,ScreenBean.getScreenWidth()-40);
            lllp.setMargins(20,20,20,20);
            previewContainer.setLayoutParams(lllp);
            customVideoView.setData(bean.getMediaType(),bean.getAttList().get(0).getFileSize()
                    ,bean.getAttList().get(0).getTimeLength(),bean.getAttList().get(0).getName()
                    ,bean.getAttList().get(0).getPreviewImg(),getClass().getName());
        }else {
            previewContainer.setVisibility(View.GONE);
        }
    }
    private void delFollow(final HashMap<String, String> params) {

        new AlertView("确定取消关注吗？", null, "取消", new String[]{"取消关注"}, null, this, AlertView.Style.ActionSheet,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, final int position) {
                        OkHttpClientManager.postAsyn(HttpUrlConstant.balloonFollowCancel, new OkHttpClientManager.ResultCallback<String>() {
                            @Override
                            public void onError(Request request, Exception e) {
                                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                            }

                            @Override
                            public void onResponse(String response) {
//                                Log.d("加关注", response);
                                try {
                                    JSONObject object = new JSONObject(response);
                                    if (!object.isNull("statusCode")) {
                                        int errCode = object.optInt("statusCode");
                                        if (1 == errCode) {
                                            bean.setFollow(false);
                                            careBtn.setText("关注");
                                        }else if(-999 == errCode){
                                            exitApp();
                                        }else{
                                            ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, params);
                    }
                }).setCancelable(true).show();

    }

    private void addFollow(HashMap<String, String> params) {
        OkHttpClientManager.postAsyn(HttpUrlConstant.balloonFollowAdd, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(String response) {
//                Log.d("加关注", response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (!object.isNull("statusCode")) {
                        int errCode = object.optInt("statusCode");
                        if (1 == errCode) {
                            bean.setFollow(true);
                            careBtn.setText("已关注");
                        }else if(-999 == errCode){
                            exitApp();
                        }else
                            ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }

    private HashMap<String, String> buildRequestParams() {
        HashMap<String, String> params = new HashMap<>();
        params.put("followUserId", bean.getCreatePersonId() + "");
        params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID, (long) 0) + "");
        params.put("lat", "");
        params.put("lng", "");
        params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE, ""));
        params.put("deviceType", "android");
        return params;
    }

    private void initMap(Bundle savedInstanceState) {
        mapView = (TextureMapView) findViewById(R.id.task_info_map_view);
        if(aMap == null){
            aMap = mapView.getMap();
            mapView.onCreate(savedInstanceState);// 此方法必须重写
        }
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);

    }

   @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mapView.onSaveInstanceState(outState);
    }
    @Override
    public void onClick(View v) {
        if(v == taskMove){
            Intent intent = null;
            if(application.isLogin()){
                switch (bean.getMediaType()){
                    case ApplicationConstant.MEDIA_TYPE_PHOTO:
                        intent = new Intent(TaskInformationActivity.this,TaskSendPhotoActivity.class);
                        intent.putExtra("subTaskId",bean.getSubTaskId());
                        intent.putExtra("lat",bean.getLatitude());
                        intent.putExtra("lng",bean.getLongitude());
                        break;
                    case ApplicationConstant.MEDIA_TYPE_VIDEO:
                        intent = new Intent(this,TaskSendVideoActivity.class);
                        intent.putExtra("subTaskId",bean.getSubTaskId());
                        intent.putExtra("lat",bean.getLatitude());
                        intent.putExtra("lng", bean.getLongitude());
                        break;
                }
            }else{
                intent = new Intent(this, Activity_Login.class);
                this.finish();
            }
            if(intent != null){
                startActivity(intent);
            }
        }
    }
    //处理scrollView与mapView的冲突
    @Override
    public void onTouch(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                scrollView.requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                scrollView.requestDisallowInterceptTouchEvent(true);
                break;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                Log.d(getClass().getName(),"定位回调成功");
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                currentLatLng = new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude());
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,18));


                //第一次定位成功调用
                if(isFirstEnter){
                    initData();
                    isFirstEnter = false;
                }

            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError","location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }
}
