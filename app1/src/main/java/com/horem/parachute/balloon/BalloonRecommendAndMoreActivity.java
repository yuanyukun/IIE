package com.horem.parachute.balloon;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.common.HttpUrlConstant;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.adapter.HomeFragmentAdapter;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.main.bean.BalloonListBean;
import com.horem.parachute.main.bean.BalloonListSubBeanItem;
import com.horem.parachute.mine.bean.MessageBean;
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

public class BalloonRecommendAndMoreActivity extends BaseActivity implements AMapLocationListener{

    private static final  int PAGE_SIZE = 10;
    private static final  String recommendUrl = HttpUrlConstant.balloonRecommend;
    private static final  String hotBalloonUrl = HttpUrlConstant.balloonHotList;
    private static final  String myBalloonListUrl = HttpUrlConstant.flowersMyBalloonList;
    private static final  String userBalloonListUrl = HttpUrlConstant.userBalloonList;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    //
    private LatLng currentLatLng;
    private boolean isFirstEnter = true;
    private String currentRequestURL;
    //
    private XRecyclerView mRecyclerView;
    private HomeFragmentAdapter mAdapter;
    private ArrayList<BalloonListSubBeanItem> lists = new ArrayList<>();
    //
    private String balloonDesc;
    private String balloonAddress;

    private int currentPage;
    private boolean isRefresh;
    private int shareType;
    private String currentBalloonId;
    private long currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balloon_recommend);

        initMap();
        initTitleView();

        setRightButtonVisible(false);
        setLeftButtonText("我");
        String type = getIntent().getStringExtra("type");
        currentUserId  = getIntent().getLongExtra("memberId",0);
        if(type.equals("recommend")){
            setTitleName("推荐");
            currentRequestURL = recommendUrl;
        }
        if(type.equals("more")) {
            setTitleName("热门视频");
            currentRequestURL = hotBalloonUrl;
        }
        if(type.equals("sendBalloon")) {
            setTitleName("热气球");
            currentRequestURL = myBalloonListUrl;
        }
        if(type.equals("appUserInfo")){
            setTitleName("气球");
            setLeftButtonText(getIntent().getStringExtra("nickName"));

            currentRequestURL = userBalloonListUrl;

        }
        mRecyclerView = (XRecyclerView) findViewById(R.id.xrecycler_view_recommend_and_hot_balloon);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new HomeFragmentAdapter(this,lists);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                currentPage = 0;
                initData();
            }

            @Override
            public void onLoadMore() {
                isRefresh = false;
                currentPage++;
                initData();
            }
        });
        mAdapter.setOnShareListenner(new HomeFragmentAdapter.OnShareListener() {
            @Override
            public void onShareClicked(int position,Bitmap bitmap) {
                currentBalloonId = lists.get(position).getBalloonId();
                balloonDesc = lists.get(position).getBalloonDesc();
                balloonAddress = lists.get(position).getBalloonAddress();
                new AlertView(null, null, "取消", null,new String[]{"分享到微信朋友圈", "分享到微信朋友"}, BalloonRecommendAndMoreActivity.this, AlertView.Style.ActionSheet,
                        new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position) {
                                switch (position){
                                    case 0:
                                        shareType = 10;
                                        sendToFriendCommunity(true,lists.get(position).getBalloonId());

                                        break;
                                    case 1:
                                        shareType = 20;
                                        sendToFriendCommunity(false,lists.get(position).getBalloonId());

                                        break;
                                }
                            }
                        }).setCancelable(true).show();
            }
        });
        mAdapter.setOnCameraListener(new HomeFragmentAdapter.OnCameraListener() {
            @Override
            public void onCameraClicked(int position) {
                Intent intent = new Intent(BalloonRecommendAndMoreActivity.this, WatchingAliveActivity.class);
                intent.putExtra("id",lists.get(position).getBalloonId());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(SharePreferencesUtils.getBoolean(getApplicationContext(),SharePrefConstant.isNeedConfirmShare,false)){
            Log.d("分享成功","shareType:"+shareType+" balloonId:"+currentBalloonId);
            SharePreferencesUtils.setBoolean(getApplicationContext(),SharePrefConstant.isNeedConfirmShare,false);
            HashMap<String,String> params = new HashMap<>();
            params.put("shareToType",shareType+"");
            params.put("balloonId",currentBalloonId);
            params.put("memberId", SharePreferencesUtils.getLong(BalloonRecommendAndMoreActivity.this, SharePrefConstant.MEMBER_ID, (long) 0) + "");
            params.put("lng", "");
            params.put("lat", "");
            params.put("clientId", SharePreferencesUtils.getString(BalloonRecommendAndMoreActivity.this, SharePrefConstant.INSTALL_CODE, ""));
            params.put("deviceType", "android");
            OkHttpClientManager.postAsyn(HttpUrlConstant.balloonShare, new OkHttpClientManager.ResultCallback<MessageBean>() {
                @Override
                public void onError(Request request, Exception e) {
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                }

                @Override
                public void onResponse(MessageBean response) {
                    Log.d(getClass().getName(),new Gson().toJson(response));
                    if(response.getStatusCode() ==1){
                        ToastManager.show(BalloonRecommendAndMoreActivity.this,response.getMessage());
                        mAdapter.notifyDataSetChanged();
                    }else if(response.getStatusCode() == -999){
                        exitApp();
                    }else
                        ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                }
            },params);
        }
    }

    private void sendToFriendCommunity(final boolean shareType, String balloonId) {
        HashMap<String, String> params = new HashMap<>();

//        params.put("shareToType",type+"");
        params.put("balloonId",balloonId);
        params.put("memberId", SharePreferencesUtils.getLong(BalloonRecommendAndMoreActivity.this, SharePrefConstant.MEMBER_ID, (long) 0) + "");
        params.put("lng", "");
        params.put("lat", "");
        params.put("clientId", SharePreferencesUtils.getString(BalloonRecommendAndMoreActivity.this, SharePrefConstant.INSTALL_CODE, ""));
        params.put("deviceType", "android");

        OkHttpClientManager.postAsyn(HttpUrlConstant.balloonShareUrl, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(String response) {
//                Log.d("分享气球",response);
                SharePreferencesUtils.setBoolean(BalloonRecommendAndMoreActivity.this, SharePrefConstant.iswxLon,false);
                String webUrl = "";
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getInt("statusCode") == 1) {
                        webUrl = jsonObject.getString("result");
                        WXWebpageObject webpage = new WXWebpageObject();
                        webpage.webpageUrl = webUrl;

                        WXMediaMessage msg = new WXMediaMessage(webpage);
                        msg.title = balloonDesc;
                        msg.description = "你的朋友在" + balloonAddress;
                        Bitmap thump = BitmapFactory.decodeResource(getResources(),R.mipmap.logo_80);
                        msg.thumbData = Utils.bmpToByteArray(thump,true);

                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = buildTransaction("webpage");

                        req.message = msg;
                        req.scene = shareType ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
                        CustomApplication.api.sendReq(req);
                    }else if(jsonObject.getInt("statusCode")== -999){
                        exitApp();
                    }else {
                        ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        },params);

    }
    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
    private void initMap() {
        //初始化定位
        mLocationClient = new AMapLocationClient(this);
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //启动定位
        mLocationClient.startLocation();
    }
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                currentLatLng = new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude());
                if(isFirstEnter){
                    isFirstEnter = false;
                    initData();
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError","location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    private void initData() {
        startLoading();
        HashMap<String,String> params = new HashMap<>();
        params.put("memberId",SharePreferencesUtils.getLong(this,SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("pageSize",String.valueOf(PAGE_SIZE));
        params.put("currentPage",String.valueOf(currentPage));
        params.put("userId", currentUserId+"");
        params.put("lat",currentLatLng.latitude+"");
        params.put("lng",currentLatLng.longitude+"");
        params.put("clientId",SharePreferencesUtils.getString(BalloonRecommendAndMoreActivity.this,SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");

//        for(Map.Entry<String,String> entry:params.entrySet()){
//            Log.d("获取气球列表参数",entry.getKey()+": "+entry.getValue());
//        }

        OkHttpClientManager.postAsyn(currentRequestURL, new OkHttpClientManager.ResultCallback<BalloonListBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(BalloonListBean response) {
                Log.d("获取气球列表URl",currentRequestURL);
                Log.d("获取气球列表",new Gson().toJson(response));
                stopLoading();
                if(response.getStatusCode() == 1){
                    dealWithData(response);
                }else if(response.getStatusCode() == -999 ){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");


            }
        },params);
    }

    private void dealWithData(BalloonListBean response) {
        if(isRefresh ){
            lists.clear();
            lists.addAll(response.getResult().getList());
            mAdapter.RefreshData(lists);
            mRecyclerView.refreshComplete();
        }else {
            lists.addAll(response.getResult().getList());
            mAdapter.RefreshData(lists);
            mRecyclerView.loadMoreComplete();
        }
    }
}
