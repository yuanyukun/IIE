package com.horem.parachute.mine;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.common.HttpUrlConstant;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.adapter.CommonAdapter;
import com.horem.parachute.adapter.TaskReceivedRecyclerViewAdapter;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.mine.bean.MineSeeMeSubBean;
import com.horem.parachute.mine.bean.MineSendTaskBean;
import com.horem.parachute.mine.bean.MineSendTaskSubBean;
import com.horem.parachute.mine.bean.TaskReceivedBean;
import com.horem.parachute.mine.bean.TaskReceivedNewBean;
import com.horem.parachute.mine.bean.TaskReceivedSubBean;
import com.horem.parachute.util.ScreenBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.http.request.OkHttpClientManager;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我接过的伞
 * Created by user on 2016/3/18.
 */
public class Activity_Mine_Task_Received extends BaseActivity implements AMapLocationListener {

    private XRecyclerView mRecyclerView;
    private TaskReceivedRecyclerViewAdapter mAdapter;
    private List<HashMap<String,Object>> mLists =new ArrayList<>();

    private List<TaskReceivedSubBean> lists = new ArrayList<>();
    private HashMap<String,String> map  = new HashMap<>();

    private LatLng currentLatlng;
    private static final int PAGE_SIZE = 10;
    private int currentPage = 0;
    private boolean isFirstEnter = true;
    private boolean isLoadingMore = false;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

    private CustomApplication application;
    private Tracker mTracker;
    private static final String TAG = "MineTaskReceived";
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
        setContentView(R.layout.activity_mine_receive_task);
        init();
    }

    private void requestData() {
        map.clear();
        map.put("memberId", SharePreferencesUtils.getLong(this,SharePrefConstant.MEMBER_ID,(long)0)+"");
        map.put("lng",currentLatlng.longitude+"");
        map.put("lat",currentLatlng.latitude+"");
        map.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        map.put("deviceType","android");
        map.put("pageSize",String.valueOf(PAGE_SIZE));
        map.put("currentPage",String.valueOf(currentPage));

//        for(Map.Entry<String,String> entry:map.entrySet()){
//            Log.i("我的接收列表",entry.getKey()+" "+entry.getValue());
//        }

        map.put("pageSize",String.valueOf(PAGE_SIZE));
        map.put("currentPage",String.valueOf(currentPage));

        OkHttpClientManager.postAsyn(HttpUrlConstant.myReceiveTaskListUrl, new OkHttpClientManager.ResultCallback<TaskReceivedNewBean>() {
            @Override
            public void onError(Request request, Exception e) {
//                Log.d(getClass().getName(),e.toString());
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }


            @Override
            public void onResponse(TaskReceivedNewBean response) {
                if(response.getStatusCode() == 1) {
                    if (isLoadingMore) {
                        lists.addAll(response.getResult().getList());
                        mAdapter.setData(lists);
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.loadMoreComplete();
                    } else {
                        lists.clear();
                        lists.addAll(response.getResult().getList());
                        mAdapter.setData(lists);
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.refreshComplete();
                    }
                }else if(response.getStatusCode() == -999){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                stopLoading();
            }
        },map);

    }

    private void init() {
        initTitleView();
        setTitleName("我接的伞");

        initMap();
        startLoading();

        mRecyclerView = (XRecyclerView) findViewById(R.id.task_received_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this){
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                // 这里要复写一下，因为默认宽高都是wrap_content
                // 这个不复写，你点击的背景色就只充满你的内容
                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new TaskReceivedRecyclerViewAdapter(this,lists);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                currentPage = 0;
                isLoadingMore = false;
                requestData();
            }

            @Override
            public void onLoadMore() {
                isLoadingMore = true;
                currentPage++;
                requestData();
            }
        });

    }

    private void initMap() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
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
                currentLatlng = new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude());

                //
                if(isFirstEnter){
                    isFirstEnter = false;
                    requestData();

                }

               /* aMapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(aMapLocation.getTime());
                df.format(date);//定位时间*/
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError","location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }
}
