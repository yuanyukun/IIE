package com.horem.parachute.balloon;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.common.ApplicationConstant;
import com.common.HttpUrlConstant;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.adapter.SendFlowerAdapter;
import com.horem.parachute.autoupdate.internal.NetworkUtil;
import com.horem.parachute.balloon.Bean.BallonInfoDetailBean;
import com.horem.parachute.balloon.Bean.BalloonInfoResultBean;
import com.horem.parachute.balloon.Bean.FlowersListBean;
import com.horem.parachute.balloon.Bean.SendFlowerBean;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.customview.ActionSheetDialog;
import com.horem.parachute.customview.AlertDialog;
import com.horem.parachute.customview.CustomHeadView;
import com.horem.parachute.customview.CustomLoveBtn;
import com.horem.parachute.customview.CustomVideoView;
import com.horem.parachute.login.Activity_Login;
import com.horem.parachute.main.bean.BalloonListSubBeanItem;
import com.horem.parachute.main.bean.GetExtroInfoBean;
import com.horem.parachute.menu.PopupPhotoView;
import com.horem.parachute.menu.Screen;
import com.horem.parachute.mine.AppUserInfo;
import com.horem.parachute.mine.ChatActivity;
import com.horem.parachute.mine.bean.MessageBean;
import com.horem.parachute.task.TaskInformationActivity;
import com.horem.parachute.util.BitmapUtils;
import com.horem.parachute.util.MyTimeUtil;
import com.horem.parachute.util.ScreenBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.horem.parachute.util.Utils;
import com.http.request.OkHttpClientManager;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.squareup.okhttp.Request;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BalloonItemInfoActivity extends BaseActivity implements LocationSource,AMapLocationListener ,AMap.OnMapTouchListener{

    private static final int FLOWER_PAGE_SIZE = 10;
    private TextureMapView mapView;
    private AMap aMap;

    private OnLocationChangedListener mListener;    //地图地理位置变化监听
    private AMapLocationClient mlocationClient;     //地图定位客户端
    private AMapLocationClientOption mLocationOption;//定位客户端样式选项自定义

    private LatLng latLng;                          //坐标类
    private boolean isFirstEnter = true;            //是否第一次打开界面
    private int personalType;

    private TextView userName;     //
    private CustomHeadView userIcon;     //
    private TextView Watcher;//
    private TextView depict;
    private ImageView balloonStatusMark;

    private TextView createTime;   //
    private LinearLayout rlOnCamera;
    private LinearLayout rlOnFlowers;
    private TextView tvCameraNum;
    private TextView tvFlowerNum;
    private ScrollView scrollView;
    private TextView timeOutText;
    private CustomVideoView customVideoView;
    private CustomLoveBtn loveBtn;
    private LinearLayout balloonInfoContainer;

    private XRecyclerView mRecyclerView;
    private SendFlowerAdapter mAdapter;
    private int currentPage = 0;
    private boolean isRefresh = true;
    private List<FlowersListBean> lists = new ArrayList<>();

    private String balloonId;
    private BalloonInfoResultBean subItem;
    private int shareType;
    private View decoreView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balloon_item_info);

        init();
        setupMap(savedInstanceState);
        startLoading();
    }

    private void setupMap(Bundle savedInstanceState) {
        //获取地图控件引用
        mapView = (TextureMapView) findViewById(R.id.balloon_map);
        //在activity执行onCreate时执行mapView.onCreate(savedInstanceState)，实现地图生命周期管理
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        aMap.setOnMapTouchListener(this);
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.location_marker));// 设置小蓝点的图标
        aMap.setMyLocationStyle(myLocationStyle);
        //地图的各种属性设置
        aMap.setLocationSource(BalloonItemInfoActivity.this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);//普通2d地图
        aMap.moveCamera(CameraUpdateFactory.zoomTo(4));//设置地图初始缩放级别（4~20)
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mapView.onResume ()，实现地图生命周期管理
        mapView.onResume();
        //确认分享成功
        Log.d("界面重新启动","执行确认分享接口");
        if (SharePreferencesUtils.getBoolean(getApplicationContext(),SharePrefConstant.isNeedConfirmShare,false)) {

            SharePreferencesUtils.setBoolean(getApplicationContext(),SharePrefConstant.isNeedConfirmShare,false);
            HashMap<String, String> params = new HashMap<>();
            params.put("shareToType", shareType + "");
            params.put("balloonId", subItem.getBalloonId());
            params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID, (long) 0) + "");
            params.put("lng", "");
            params.put("lat", "");
            params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE, ""));
            params.put("deviceType", "android");
            OkHttpClientManager.postAsyn(HttpUrlConstant.balloonShare, new OkHttpClientManager.ResultCallback<MessageBean>() {
                @Override
                public void onError(Request request, Exception e) {
                    ToastManager.show(getApplicationContext(),"噢！网络不给力");
                }

                @Override
                public void onResponse(MessageBean response) {
                    Log.d(getClass().getName(), new Gson().toJson(response));
                    if (response.getStatusCode() == 1) {
                        RefreshExtroInfo();
                        ToastManager.show(BalloonItemInfoActivity.this, response.getMessage());
                    }else if(response.getStatusCode() == -999){
                        exitApp();
                    }else
                        ToastManager.show(getApplicationContext(),"噢！网络不给力");
                }
            }, params);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mapView.onPause ()，实现地图生命周期管理
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mapView.onSaveInstanceState(outState);
    }

    private void getData() {
        HashMap<String, String> params = new HashMap<>();
        params.put("balloonId", String.valueOf(balloonId));
        params.put("flowersPageSize", String.valueOf(FLOWER_PAGE_SIZE));

        params.put("memberId", SharePreferencesUtils.getLong(BalloonItemInfoActivity.this, SharePrefConstant.MEMBER_ID, (long) 0) + "");
        params.put("lng", latLng.longitude + "");
        params.put("lat", latLng.latitude + "");
        params.put("clientId", SharePreferencesUtils.getString(BalloonItemInfoActivity.this, SharePrefConstant.INSTALL_CODE, ""));
        params.put("deviceType", "android");
        OkHttpClientManager.postAsyn(HttpUrlConstant.balloonInfoDetails, new OkHttpClientManager.ResultCallback<BallonInfoDetailBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢！网络不给力");
            }

            @Override
            public void onResponse(BallonInfoDetailBean response) {
                Log.d("气球详情", new Gson().toJson(response));
                if (response.getStatusCode() == 1) {
                    subItem = response.getResult();
                    setUpView();
                    stopLoading();
                }else if(response.getStatusCode() == -999){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),"噢！网络不给力");
            }
        }, params);

    }

    private void setUpView() {
        setTitleName(subItem.getPlace());

        loveBtn.setInitData(subItem.getBalloonId(),subItem.isGiveLike(),subItem.getGiveLikeNum());
        timeOutText.setText(subItem.getTimeOutStr());
        if(subItem.isTimeOut()){
            balloonStatusMark.setImageResource(R.mipmap.red_clock_48);
            balloonInfoContainer.setBackgroundColor(ContextCompat.getColor(this,R.color.lightRed));
        }else{
            balloonStatusMark.setImageResource(R.mipmap.blue_clock_48);
            balloonInfoContainer.setBackgroundColor(ContextCompat.getColor(this,R.color.lightBlue));
        }
        //显示地图标记
        showMark();
        userName.setText(subItem.getCreatePersonName());
        if(subItem.isFollow()){
            Watcher.setText("已关注");
        }else{
            Watcher.setText("关注");
        }
        customVideoView.setData(subItem.getMediaType(),subItem.getAttList().get(0).getFileSize(),
                subItem.getAttList().get(0).getTimeLength(),subItem.getAttList().get(0).getName(),
                subItem.getAttList().get(0).getPreviewImg(),getClass().getName());

        depict.setText(subItem.getBalloonDesc());
        createTime.setText(MyTimeUtil.formatTimeZh(subItem.getCreateTime()));
       
        userIcon.showHeadView(subItem.getCreatePersonHead(),subItem.getAuthType(),subItem.getCreatePersonId(),true);

//        loveBtn.setInitData(subItem.getBalloonId(),subItem.is);
//        tvShareNum.setText(subItem.getShareNum() + "");
        tvCameraNum.setText(subItem.getRequestNum() + "");
        tvFlowerNum.setText(subItem.getFlowersNum() + "");


        if (subItem.getCreatePersonId() == SharePreferencesUtils.getLong(BalloonItemInfoActivity.this, SharePrefConstant.MEMBER_ID, (long) 0)) {
            Watcher.setVisibility(View.INVISIBLE);
        } else {
            Watcher.setVisibility(View.VISIBLE);
        }
        Watcher.setClickable(true);
        Watcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CustomApplication.getInstance().isLogin()) {
                    HashMap<String, String> params = buildRequestParams();
                    if (!subItem.isFollow()) {
                        addFollow(params);
                    } else {
                        delFollow(params);
                    }
                }else {
                    Intent intent = new Intent(BalloonItemInfoActivity.this,Activity_Login.class);
                    startActivity(intent);
                }
            }
        });


        rlOnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (!CustomApplication.getInstance().isLogin()) {
                        Intent intent = new Intent(BalloonItemInfoActivity.this, Activity_Login.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(BalloonItemInfoActivity.this, WatchingAliveActivity.class);
                        intent.putExtra("id", subItem.getBalloonId());
                        startActivity(intent);
                    }
            }
        });
        rlOnFlowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CustomApplication.getInstance().isLogin()){
                    Intent intent = new Intent(BalloonItemInfoActivity.this, Activity_Login.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(BalloonItemInfoActivity.this, SendFlowersActivity.class);
                    intent.putExtra("type","homeFragment");
                    intent.putExtra("balloonId", subItem.getBalloonId());
                    intent.putExtra("head", subItem.getCreatePersonHead());
                    startActivity(intent);
                }
            }
        });

    }
    private void RefreshExtroInfo() {
        HashMap<String,String> params = buildRequestParams();
        params.put("balloonId",subItem.getBalloonId());
        OkHttpClientManager.postAsyn(HttpUrlConstant.balloonExtroInfo, new OkHttpClientManager.ResultCallback<GetExtroInfoBean>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(GetExtroInfoBean response) {
//                Log.d("重新获取数据",new Gson().toJson(response));
                if(response.getStatusCode() == 1) {
                    tvCameraNum.setText(response.getResult().getRequestNum() + "");
                    tvFlowerNum.setText(response.getResult().getFlowersNum() + "");
                    loveBtn.setInitData(subItem.getBalloonId(),response.getResult().isGiveLike(),
                            response.getResult().getGiveLikeNum());
                }
            }
        },params);
    }
    private void showMark() {

        Glide.with(this)
                .load(getBalloonUrl())
                .asBitmap()
                .centerCrop()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        View view = getLayoutInflater().inflate(R.layout.balloon_map_icon,null);
                        ImageView icon = (ImageView) view.findViewById(R.id.balloon_icon_icon);
                        icon.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        RelativeLayout markContainer = (RelativeLayout) view.findViewById(R.id.balloon_icon_container);
                        String url;
                        switch (subItem.getMediaType()){
                            case ApplicationConstant.MEDIA_TYPE_PHOTO:
                                markContainer.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.mipmap.redpin));
                                break;
                            case ApplicationConstant.MEDIA_TYPE_VIDEO:
                                markContainer.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.mipmap.bluepin));
                                break;
                        }
                        icon.setImageBitmap(resource);
                        BitmapDescriptor markerIcon = BitmapDescriptorFactory
                                .fromView(view);
            //          markerOptions.position(latLng);
                        //添加到地图上
                        Marker marker = aMap.addMarker(new MarkerOptions().anchor(0.5f,0.5f)
                                .position(latLng)
                                .snippet("")
                                .title("")
                                .draggable(true)
                        );
                        marker.setIcon(markerIcon);
                    }
                });
//
    }

    private String getBalloonUrl() {
        String url = "";
        if(subItem.getAttList().size() > 0) {
            switch (subItem.getMediaType()) {
                case ApplicationConstant.MEDIA_TYPE_PHOTO:
                    url = Utils.getSmalleImageUrl(subItem.getAttList().get(0).getName()
                            , Utils.px2dp(this, ScreenBean.getScreenWidth())
                            , Utils.px2dp(this, ScreenBean.getScreenWidth()), this);
                    break;
                case ApplicationConstant.MEDIA_TYPE_VIDEO:
                    url = Utils.getVideoPreviewImgUrl(subItem.getAttList().get(0).getPreviewImg());
                    break;
            }
        }
        return url;
    }


    private void sendToFriendCommunity(final boolean shareType, String balloonId) {
        HashMap<String, String> params = new HashMap<>();

//        params.put("shareToType",type+"");
        params.put("balloonId", balloonId);

        params.put("memberId", SharePreferencesUtils.getLong(BalloonItemInfoActivity.this, SharePrefConstant.MEMBER_ID, (long) 0) + "");
        params.put("lng", "");
        params.put("lat", "");
        params.put("clientId", SharePreferencesUtils.getString(BalloonItemInfoActivity.this, SharePrefConstant.INSTALL_CODE, ""));
        params.put("deviceType", "android");

        OkHttpClientManager.postAsyn(HttpUrlConstant.balloonShareUrl, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢！网络不给力");
            }

            @Override
            public void onResponse(String response) {
//                Log.d("分享气球", response);
                SharePreferencesUtils.setBoolean(BalloonItemInfoActivity.this, SharePrefConstant.iswxLon, false);
                String webUrl = "";
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getInt("statusCode") == -999){
                        exitApp();
                    }else if(jsonObject.getInt("statusCode") == 1){

                        webUrl = jsonObject.getString("result");
//                        drawable = customVideoView.getDrawable();

                        WXWebpageObject webpage = new WXWebpageObject();
                        webpage.webpageUrl = webUrl;

                        WXMediaMessage msg = new WXMediaMessage(webpage);
                        msg.title = subItem.getBalloonDesc();
                        msg.description = "你的朋友在" + subItem.getPlace();
                        Bitmap thump = null;
                        if(subItem.getAttList() != null){
                                Bitmap bitmap = customVideoView.getSourceBitmap();
                                Bitmap newBitmap = BitmapUtils.ChangeBitmapSize(100f/ bitmap.getHeight(), bitmap);
                                thump = newBitmap;
                        }else{
                            thump = BitmapFactory.decodeResource(getResources(), R.mipmap.logo_80);
                        }
                        msg.thumbData = Utils.bmpToByteArray(thump, true);

                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = buildTransaction("webpage");

                        req.message = msg;
                        req.scene = shareType ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
                        CustomApplication.api.sendReq(req);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, params);

    }
    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private void delFollow(final HashMap<String, String> params) {

        new AlertView("确定取消关注吗？", null, "取消", new String[]{"取消关注"}, null, BalloonItemInfoActivity.this, AlertView.Style.ActionSheet,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, final int position) {
                        OkHttpClientManager.postAsyn(HttpUrlConstant.balloonFollowCancel, new OkHttpClientManager.ResultCallback<String>() {
                            @Override
                            public void onError(Request request, Exception e) {
                                ToastManager.show(getApplicationContext(),"噢！网络不给力");
                            }

                            @Override
                            public void onResponse(String response) {
//                                Log.d("加关注", response);
                                try {
                                    JSONObject object = new JSONObject(response);
                                    if (!object.isNull("statusCode")) {
                                        int errCode = object.optInt("statusCode");
                                        if (1 == errCode) {
                                            subItem.setFollow(false);
                                            Watcher.setText("关注");
                                        }else if(-999 == errCode){
                                            exitApp();
                                        }else{
                                            ToastManager.show(getApplicationContext(),"噢！网络不给力");
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
                ToastManager.show(getApplicationContext(),"噢！网络不给力");
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (!object.isNull("statusCode")) {
                        int errCode = object.optInt("statusCode");
                        if (1 == errCode) {
                            subItem.setFollow(true);
                            Watcher.setText("已关注");
                        }else if(-999 ==errCode){
                            exitApp();
                        }else{
                            ToastManager.show(getApplicationContext(),"噢！网络不给力");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }

    private HashMap<String, String> buildRequestParams() {
        HashMap<String, String> params = new HashMap<>();
        params.put("followUserId", subItem.getCreatePersonId() + "");
        params.put("memberId", SharePreferencesUtils.getLong(BalloonItemInfoActivity.this, SharePrefConstant.MEMBER_ID, (long) 0) + "");
        params.put("lat", "");
        params.put("lng", "");
        params.put("clientId", SharePreferencesUtils.getString(BalloonItemInfoActivity.this, SharePrefConstant.INSTALL_CODE, ""));
        params.put("deviceType", "android");
        return params;
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
                            sendToFriendCommunity(true, subItem.getBalloonId());
                        }
                    })
                    .addSheetItem("分享到朋友", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                        @Override
                        public void onClick(int which) {
                            shareType = 20;
                            sendToFriendCommunity(false, subItem.getBalloonId());
                        }
                    })
                    .addSheetItem("举报", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                        @Override
                        public void onClick(int which) {
                            Intent intent = new Intent(BalloonItemInfoActivity.this, ChatActivity.class);
                            intent.putExtra("currentId",29+"");
                            intent.putExtra("type",10+"");
                            intent.putExtra("name","伞来了团队");
                            startActivity(intent);
                        }
                    }).show();

        }else {
            Intent  intent = new Intent(BalloonItemInfoActivity.this,Activity_Login.class);
            startActivity(intent);
        }

    }

    private void init() {
        initTitleView();
        setLeftButtonText("伞来了");
        setRightButtonDrawableRight(ContextCompat.getDrawable(this,R.mipmap.more_60));
        balloonId = getIntent().getStringExtra("balloonId");
        //获取列表数据
        getBalloonList();
        customVideoView = (CustomVideoView) findViewById(R.id.custom_view);
        loveBtn = (CustomLoveBtn) findViewById(R.id.home_fragment_balloon_custom_love_btn);

        balloonInfoContainer = (LinearLayout) findViewById(R.id.balloon_info_container);
        balloonStatusMark = (ImageView) findViewById(R.id.balloon_info_timeout_img);

        userIcon = (CustomHeadView) findViewById(R.id.home_fragment_user_head);
        userName = (TextView) findViewById(R.id.home_fragment_user_name);
        Watcher = (TextView) findViewById(R.id.home_fragment_button);
        depict = (TextView) findViewById(R.id.home_fragment_balloon_describe);
        createTime = (TextView) findViewById(R.id.home_fragment_balloon_create_time);
        rlOnCamera = (LinearLayout) findViewById(R.id.home_fragment_on_camera);
        rlOnFlowers = (LinearLayout) findViewById(R.id.home_fragment_on_flowers);
        decoreView = findViewById(R.id.divider_line_horizontal);

        tvCameraNum = (TextView) findViewById(R.id.home_fragment_balloon_camera_tv);
        tvFlowerNum = (TextView) findViewById(R.id.home_fragment_balloon_flower_tv);

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        timeOutText = (TextView) findViewById(R.id.balloon_info_timeout_str);


        mRecyclerView = (XRecyclerView) findViewById(R.id.balloon_info_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new SendFlowerAdapter(this,lists);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                currentPage = 0;
                isRefresh = true;
                getBalloonList();
            }

            @Override
            public void onLoadMore() {
                currentPage++;
                isRefresh = false;
                getBalloonList();
            }
        });
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());

                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
//                aMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));  //定位成功中定位
                if (isFirstEnter) {
                    isFirstEnter = false;
                    getData();
                }

            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(BalloonItemInfoActivity.this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(BalloonItemInfoActivity.this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
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


    public void getBalloonList() {
    HashMap<String,String> params = new HashMap<>();
    params.put("balloonId",balloonId);
    String  currentUrl = HttpUrlConstant.balloonFlowersPrices;

    params.put("flowersPageSize","6");
    params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
    params.put("lng","");
    params.put("lat","");
    params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
    params.put("deviceType","android");

    OkHttpClientManager.postAsyn(currentUrl, new OkHttpClientManager.ResultCallback<SendFlowerBean>() {
        @Override
        public void onError(Request request, Exception e) {
            ToastManager.show(getApplicationContext(),"噢！网络不给力");
        }

        @Override
        public void onResponse(SendFlowerBean response) {
            if(response.getStatusCode() == 1){
                if(isRefresh){
                    lists.clear();
                    if(response.getResult().getFlowersList().size() > 0) {
                        lists.addAll(response.getResult().getFlowersList());
                        mAdapter.notifyDataSetChanged();
                    }else{
                        mRecyclerView.setVisibility(View.GONE);
                        decoreView.setVisibility(View.GONE);
                    }
                    mRecyclerView.refreshComplete();
                }else{
                    lists.addAll(response.getResult().getFlowersList());
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.loadMoreComplete();
                }
            }else if(-999 == response.getStatusCode()){

            }else
                ToastManager.show(getApplicationContext(),"噢！网络不给力");
        }
    },params);

}
}
