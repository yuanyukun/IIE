package com.horem.parachute.balloon;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.common.HttpUrlConstant;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.adapter.UserInfoFlowersAdapter;
import com.horem.parachute.balloon.Bean.FlowersHaveSendBean;
import com.horem.parachute.balloon.Bean.FlowersHaveSendSubItem;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.http.request.OkHttpClientManager;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;

public class FlowersHaveSendActivity extends BaseActivity implements AMapLocationListener{

    private static  final  int PAGE_SIZE = 10;
    private int currentPage;
    private boolean isRefresh = true;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private LatLng currentLatLng;
    private boolean isFirstEnter = true;

    private XRecyclerView mRecyclerView;
    private UserInfoFlowersAdapter mAdapter;
    private ArrayList<FlowersHaveSendSubItem> lists = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flowers_have_send);

        init();
//        initData();
    }

    private void initData() {

        HashMap<String,String> params = new HashMap<>();
        params.put("pageSize",String.valueOf(PAGE_SIZE));
        params.put("currentPage",String.valueOf(currentPage));

        params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lng",currentLatLng.longitude+"");
        params.put("lat",currentLatLng.latitude+"");
        params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");

        OkHttpClientManager.postAsyn(HttpUrlConstant.flowersToMemberList, new OkHttpClientManager.ResultCallback<FlowersHaveSendBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(FlowersHaveSendBean response) {
//                Log.d(getClass().getName(),new Gson().toJson(response));
                if(response.getStatusCode() == 1) {
                    if (isRefresh) {
                        lists.clear();
                        lists.addAll(response.getResult().getList());
                        mAdapter.RefreshData(lists);
                        mRecyclerView.refreshComplete();
                    } else {
                        lists.addAll(response.getResult().getList());
                        mAdapter.RefreshData(lists);
                        mRecyclerView.loadMoreComplete();
                    }
                }else if(response.getStatusCode() == -999){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }
        },params);
    }

    private void init() {
        initTitleView();
        setTitleName("我送的花");
        setLeftButtonText("我");
        setRightButtonVisible(false);

        initMap();
        mRecyclerView = (XRecyclerView) findViewById(R.id.recycler_user_info_flowers);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new UserInfoFlowersAdapter(this,lists);

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
                currentPage++;
                isRefresh = false;
                initData();
            }
        });
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
}
