package com.horem.parachute.mine;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLng;
import com.common.HttpUrlConstant;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.adapter.GiveLikeMeAdapter;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.mine.bean.GiveLikeMeListBean;
import com.horem.parachute.mine.bean.GiveLikeMeListItemBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.http.request.OkHttpClientManager;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GiveLikeMeListActivity extends BaseActivity implements AMapLocationListener{

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private LatLng currentLatLng;
    private boolean isFirstEnter = true;


    private XRecyclerView recyclerView;
    private GiveLikeMeAdapter mAdapter;
    private ArrayList<GiveLikeMeListItemBean> lists = new ArrayList<>();

    private boolean isRefresh = true;
    private static  final  int PageSize = 20;
    private int currentPage = 0;

    private static final int HAVE_GIVE_LIKE = 0x10;//我赞过的
    private static final int GIVE_LIKE_ME = 0x11;//赞过我的
    private String url = "";
    private int FLAG;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_like_me_list);
        init();
    }

    private void init() {
        initTitleView();

        initMap();
        initView();
    }

    private void initView() {
        FLAG = getIntent().getIntExtra("flag",0);
        switch (FLAG){
            case HAVE_GIVE_LIKE:
                url = HttpUrlConstant.balloonMyGiveLikeList;
                setTitleName("我赞过的");
                setLeftButtonText("我");
                break;
            case GIVE_LIKE_ME:
                url = HttpUrlConstant.balloonLikeMeList;
                setTitleName("赞过我的");
                setLeftButtonText("聊聊");
                break;
        }
        recyclerView = (XRecyclerView) findViewById(R.id.give_like_list_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        mAdapter = new GiveLikeMeAdapter(this,lists,FLAG);
        recyclerView.setAdapter(mAdapter);

        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
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

    private void initData() {
        OkHttpClientManager.postAsyn(url, new OkHttpClientManager.ResultCallback<GiveLikeMeListBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(GiveLikeMeListActivity.this,"噢，网络不给力");
                if(isRefresh){
                    recyclerView.refreshComplete();
                }else{
                    recyclerView.loadMoreComplete();
                }
                ToastManager.show(GiveLikeMeListActivity.this,e.toString());
            }

            @Override
            public void onResponse(GiveLikeMeListBean response) {
                Log.d(getClass().getName(),new Gson().toJson(response.getResult()));
                if(isRefresh){
                    lists.clear();
                    lists.addAll(response.getResult().getList());
                    mAdapter.RefreshData(lists);
                    recyclerView.refreshComplete();
                }else{
                    lists.addAll(response.getResult().getList());
                    mAdapter.RefreshData(lists);
                    recyclerView.loadMoreComplete();
                }
            }
        },buildParams());
    }

    private HashMap<String,String>  buildParams() {
        HashMap<String,String> params = new HashMap<>();
        params.put("pageSize",String.valueOf(PageSize));
        params.put("currentPage",String.valueOf(currentPage));
        params.put("memberId",""+ SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0));
        params.put("lat",""+currentLatLng.latitude);
        params.put("lng",""+currentLatLng.longitude);
        params.put("clientId",SharePreferencesUtils.getString(this,SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");
        return params;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mLocationClient != null && aMapLocation != null) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {

                currentLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                if (isFirstEnter) {
                    isFirstEnter = false;
                    initData();
                }

            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }
}
