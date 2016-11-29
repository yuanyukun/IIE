package com.horem.parachute.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.common.HttpUrlConstant;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.adapter.HomeFragmentAdapter;
import com.horem.parachute.balloon.WatchingAliveActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.common.CustomLoading;
import com.horem.parachute.login.Activity_Login;
import com.horem.parachute.main.bean.BalloonListBean;
import com.horem.parachute.main.bean.BalloonListSubBeanItem;
import com.horem.parachute.mine.HomeChatActivity;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.horem.parachute.util.Utils;
import com.http.request.HttpApi;
import com.http.request.IResponseApi;
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

/**
 * Created by yuanyukun on 2016/6/17.
 */
public class HomeFragment extends Fragment implements View.OnClickListener,AMapLocationListener{

    private CustomLoading customLoadingDialog;
    private TextView titleName,titleNext,userInfo;    //返回键
    private XRecyclerView xRecyclerView;
    private HomeFragmentAdapter mAdapter;
    private List<BalloonListSubBeanItem> lists = new ArrayList<>();

    private boolean isRefresh = true;
    private int currentPage;
    private static final int PageSize = 20;
    private boolean isFirstEnter = true;

    private ImageView chatReddot;
    private String address;
    private String balloonDesc;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    //
    private LatLng currentLatLng;

//    private boolean isNeedSendInfo = false;
    private int shareType;
    private String currentBalloonId;
    private CustomApplication application;
    private MyReceiver myBroadCastReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        initMap();
//        registerCustomReceiver();
        chatReddot = (ImageView) view.findViewById(R.id.nearby_fragment_red_dot);
        titleName = (TextView) view.findViewById(R.id.title_name);
        userInfo = (TextView) view.findViewById(R.id.title_back);
        titleNext = (TextView) view.findViewById(R.id.title_next);
        titleNext.setOnClickListener(this);
        titleName.setText("伞来了");
        userInfo.setVisibility(View.INVISIBLE);
        Utils.setRightDrawable(titleNext, ContextCompat.getDrawable(getContext(),R.mipmap.tab_chat_75));

        xRecyclerView = (XRecyclerView) view.findViewById(R.id.home_fragment_xrecycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        xRecyclerView.setLayoutManager(manager);
        mAdapter = new HomeFragmentAdapter(getActivity(),lists);
        xRecyclerView.setAdapter(mAdapter);

        xRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
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

        mAdapter.setOnCameraListener(new HomeFragmentAdapter.OnCameraListener() {
            @Override
            public void onCameraClicked(int position) {
                if(!CustomApplication.getInstance().isLogin()){
                    Intent intent = new Intent(getContext(), Activity_Login.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(getContext(), WatchingAliveActivity.class);
                    intent.putExtra("id", lists.get(position).getBalloonId());
                    startActivity(intent);
                }
            }
        });

//        updaUI();
    }


    private void updaUI() {
        application = (CustomApplication) getActivity().getApplication();
        if (application.isViewUserList || application.flowersToMeList || application.isMessageList) {
            chatReddot.setVisibility(View.VISIBLE);
        } else {
            chatReddot.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendToFriendCommunity(final boolean shareType, final Bitmap bitmap, String balloonId) {
        HashMap<String, String> params = new HashMap<>();

//        params.put("shareToType",type+"");
        params.put("balloonId",balloonId);

        params.put("memberId", SharePreferencesUtils.getLong(getContext(), SharePrefConstant.MEMBER_ID, (long) 0) + "");
        params.put("lng", "");
        params.put("lat", "");
        params.put("clientId", SharePreferencesUtils.getString(getContext(), SharePrefConstant.INSTALL_CODE, ""));
        params.put("deviceType", "android");

        OkHttpClientManager.postAsyn(HttpUrlConstant.balloonShareUrl, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getContext(),"噢！网络不给力");
            }

            @Override
            public void onResponse(String response) {
//                Log.d("分享气球",response);
                SharePreferencesUtils.setBoolean(getContext(), SharePrefConstant.iswxLon,false);
                String webUrl = "";
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getInt("statusCode") == 1) {
                        webUrl = jsonObject.getString("result");
                        WXWebpageObject webpage = new WXWebpageObject();
                        webpage.webpageUrl = webUrl;

                        WXMediaMessage msg = new WXMediaMessage(webpage);
                        msg.title = balloonDesc;
                        msg.description = "你的朋友在" + address;
//                msg.description = "伞来了是一款即时多媒体交易平台，全世界亿万用户的分享经济平台。";
                        Bitmap thump = null;
                        if(bitmap != null){
                            thump = bitmap;
                        }else{
                            thump = BitmapFactory.decodeResource(getResources(), R.mipmap.logo_80);
                        }
                        msg.thumbData = Utils.bmpToByteArray(thump,true);

                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = buildTransaction("webpage");

                        req.message = msg;
                        req.scene = shareType ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
                        CustomApplication.api.sendReq(req);
                    }else if(jsonObject.getInt("statusCode") == -999){
                        HttpApi httpApi = new ExitSystemHttpImpl();
                        httpApi.httpRequest(getContext(), new IResponseApi() {
                            @Override
                            public void onSuccess(Object object) {

                            }

                            @Override
                            public void onFailed(Exception e) {

                            }
                        },new HashMap<String, String>());
                    }else
                        ToastManager.show(getContext(),"噢！网络不给力");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },params);

    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerCustomReceiver();
        mAdapter.notifyDataSetChanged();
//        if(SharePreferencesUtils.getBoolean(getContext(),SharePrefConstant.isNeedConfirmShare,false)){
////            Log.d("分享成功","shareType:"+shareType+" balloonId:"+currentBalloonId);
//            SharePreferencesUtils.setBoolean(getContext(),SharePrefConstant.isNeedConfirmShare,false);
//            HashMap<String,String> params = new HashMap<>();
//            params.put("shareToType",shareType+"");
//            params.put("balloonId",currentBalloonId);
//            params.put("memberId", SharePreferencesUtils.getLong(getContext(), SharePrefConstant.MEMBER_ID, (long) 0) + "");
//            params.put("lng", "");
//            params.put("lat", "");
//            params.put("clientId", SharePreferencesUtils.getString(getContext(), SharePrefConstant.INSTALL_CODE, ""));
//            params.put("deviceType", "android");
//
//            OkHttpClientManager.postAsyn(HttpUrlConstant.balloonShare, new OkHttpClientManager.ResultCallback<MessageBean>() {
//                @Override
//                public void onError(Request request, Exception e) {
//                    ToastManager.show(getContext(),"噢！网络不给力");
//                }
//                @Override
//                public void onResponse(MessageBean response) {
////                    Log.d(getClass().getName(),new Gson().toJson(response));
//                    if(response.getStatusCode() ==1){
//                        ToastManager.show(getContext(),response.getMessage());
//                        mAdapter.notifyDataSetChanged();
//                    }else if(response.getStatusCode() == -999){
//                        HttpApi httpApi = new ExitSystemHttpImpl();
//                        httpApi.httpRequest(getContext(), new IResponseApi() {
//                            @Override
//                            public void onSuccess(Object object) {
//
//                            }
//
//                            @Override
//                            public void onFailed(Exception e) {
//
//                            }
//                        },new HashMap<String, String>());
//                    }else
//                        ToastManager.show(getContext(),"噢！网络不给力");
//                }
//            },params);
//        }
    }

    private void initMap() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getContext());
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
        mLocationOption.setInterval(5000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //启动定位
        mLocationClient.startLocation();
    }
    private void initData() {

        HashMap<String,String> params = new HashMap<>();
        params.put("pageSize",String.valueOf(PageSize));
        params.put("currentPage",String.valueOf(currentPage));
        params.put("memberId", SharePreferencesUtils.getLong(getContext(), SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lat",currentLatLng.latitude+"");
        params.put("lng",currentLatLng.longitude+"");
        params.put("clientId",SharePreferencesUtils.getString(getContext(),SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");

//        for(Map.Entry<String,String> entry:params.entrySet()){
//            Log.d("获取气球列表参数",entry.getKey()+": "+entry.getValue());
//        }

        OkHttpClientManager.postAsyn(HttpUrlConstant.balloonHomeFragment, new OkHttpClientManager.ResultCallback<BalloonListBean>() {
            @Override
            public void onError(Request request, Exception e) {
                stopLoading();
                ToastManager.show(getContext(),"噢！网络不给力");
            }

            @Override
            public void onResponse(BalloonListBean response) {
                Log.d(getClass().getName(),new Gson().toJson(response));
                stopLoading();
                if(response.getStatusCode() == -999) {
                    HttpApi httpApi = new ExitSystemHttpImpl();
                    httpApi.httpRequest(getContext(), new IResponseApi() {
                        @Override
                        public void onSuccess(Object object) {

                        }

                        @Override
                        public void onFailed(Exception e) {

                        }
                    },new HashMap<String, String>());
                } else if(response.getStatusCode() == 1){
                            if (isRefresh) {
                                lists.clear();
                                lists.addAll(response.getResult().getList());
                                mAdapter.RefreshData(lists);
                                xRecyclerView.refreshComplete();
                            } else {
                                lists.addAll(response.getResult().getList());
                                mAdapter.RefreshData(lists);
                                xRecyclerView.loadMoreComplete();
                            }
                }else
                    ToastManager.show(getContext(),"噢，网络不给力！");
            }
        },params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        getActivity().unregisterReceiver(myBroadCastReceiver);
    }

    @Override
    public void onClick(View v) {
        if(v == titleNext){
            Intent intent = null;
            if(CustomApplication.getInstance().isLogin()){
                 intent = new Intent(getContext(), HomeChatActivity.class);
            }else{
                intent = new Intent(getContext(), Activity_Login.class);
            }
            startActivity(intent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(myBroadCastReceiver != null) {
            CustomApplication.unregisterLocalReceiver(myBroadCastReceiver);
        }
    }

    /**
     * 进度加载动画
     */
    protected void startLoading(){
        if(customLoadingDialog == null){
            customLoadingDialog = CustomLoading.CreateLoadingDialog(getContext());
            customLoadingDialog.setMessage("正在加载...");
            customLoadingDialog.setCanceledOnTouchOutside(false);//不允许点击取消

            Window wd = customLoadingDialog.getWindow();
            WindowManager.LayoutParams lp = wd.getAttributes();
            lp.alpha = 0.5f;
            wd.setAttributes(lp);
        }
        customLoadingDialog.show();
    }  /**
     * 进度加载动画
     */
    protected void startLoading(String msg){
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
    protected void stopLoading(){
        if(customLoadingDialog != null){
            customLoadingDialog.dismiss();
            customLoadingDialog.dismiss();
        }
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
                    startLoading();
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
    private void registerCustomReceiver() {
        if(myBroadCastReceiver == null) {
            myBroadCastReceiver = new MyReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.horem.parachute.Status");
        //注册一个接收的广播
        CustomApplication.registerLocalReceiver(myBroadCastReceiver, filter);
    }
      class MyReceiver extends BroadcastReceiver{
          @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.horem.parachute.Status")) {
                //updateUI here
//                Log.d(getClass().getName(),"首页收到广播，刷新聊聊");
                updaUI();
            }
        }
    }

}
