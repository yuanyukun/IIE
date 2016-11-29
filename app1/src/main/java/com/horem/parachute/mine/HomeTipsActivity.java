package com.horem.parachute.mine;

import android.content.Intent;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

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
import com.horem.parachute.adapter.HomeTipsRecyclerViewAdapter;
import com.horem.parachute.balloon.Bean.BaseResultBean;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.mine.bean.AddressBean;
import com.horem.parachute.mine.bean.AddressBeanNew;
import com.horem.parachute.mine.bean.AddressSubBean;
import com.horem.parachute.task.PointTaskLocationActivity;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Address;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeTipsActivity extends BaseActivity implements AMapLocationListener{

    private static final int HOME_TIPS_FLAG = 0x102;
    private static final int HOME_TIPS_REQUEST = 0x1002;
    private ImageView confirmBtn;
    private RecyclerView mRecyclerView;
    private HomeTipsRecyclerViewAdapter mAdapter;
    private List<AddressSubBean> addressBeanLists = new ArrayList<>();

    private boolean isFirstEnter = true;
    //声明AMapLocationClient类对象
    private  AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    private  AMapLocationClientOption mLocationOption = null;
    private LatLng currentLatLng;

    private CustomApplication application;
    private Tracker mTracker;
    private static final String TAG = "HomeTips";


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
        setContentView(R.layout.activity_home_tips);
        initMap();
        init();
    }

    private void init() {
        initTitleView();//初始化标题栏
        setLeftButtonText("用户信息");
        setTitleName("常去景点/地标");
        setRightButtonText("确定");

        int count = getIntent().getIntExtra("count",0);
        if(count == 0){
            Intent intent = new Intent(HomeTipsActivity.this,PointTaskLocationActivity.class);
            intent.putExtra("flag",HOME_TIPS_FLAG);
            startActivityForResult(intent,HOME_TIPS_REQUEST);
        }
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_home_tips);
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

        mAdapter = new HomeTipsRecyclerViewAdapter(this,addressBeanLists);

        mRecyclerView.setAdapter(mAdapter);
        confirmBtn = (ImageView) findViewById(R.id.home_tips_button);

        mAdapter.setOnItemDelete(new HomeTipsRecyclerViewAdapter.OnItemDelete() {
            @Override
            public void OnDelete(int positon) {
                addressBeanLists.remove(positon);
                mAdapter.setData(addressBeanLists);
                mAdapter.notifyDataSetChanged();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeTipsActivity.this,PointTaskLocationActivity.class);
                intent.putExtra("flag",HOME_TIPS_FLAG);
                startActivityForResult(intent,HOME_TIPS_REQUEST);
            }
        });
    }


    private void initData() {
        //获取我的常用地址
            HashMap<String,String> params = new HashMap<>();
            params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
            params.put("lng","");
            params.put("lat","");
            params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
            params.put("deviceType","android");

            OkHttpClientManager.postAsyn(HttpUrlConstant.myAddressListUrl, new OkHttpClientManager.ResultCallback<AddressBeanNew>() {
                @Override
                public void onError(Request request, Exception e) {
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                }

                @Override
                public void onResponse(AddressBeanNew response) {
                    Log.d(getClass().getName(),new Gson().toJson(response) );
                    if(response.getStatusCode() == 1 && response.getResult() != null){
                        addressBeanLists = response.getResult();
                        mAdapter.setData(addressBeanLists);
                        mAdapter.notifyDataSetChanged();
                    }else
                        ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                }
            },params);
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
        mLocationClient.setLocationListener(HomeTipsActivity.this);
        //启动定位
        mLocationClient.startLocation();
    }


    @Override
    protected void OnRightButtonClicked() {
        updateAddressList();
    }

    private void updateAddressList() {
        startLoading("保存中");
        HashMap<String,String> params = new HashMap<>();
        params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lng","");
        params.put("lat","");
        params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");

        params.put("addressJson",new Gson().toJson(addressBeanLists));
//        for(Map.Entry<String,String> entry:params.entrySet()){
//            Log.d(getClass().getName(),entry.getKey()+": "+entry.getValue());
//        }

        OkHttpClientManager.postAsyn(HttpUrlConstant.addMyAddressUrl, new OkHttpClientManager.ResultCallback<BaseResultBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(BaseResultBean response) {
                stopLoading();
                ToastManager.show(HomeTipsActivity.this,response.getMessage());
                if(response.getStatusCode() == 1){
                    finish();
                }else if(response.getStatusCode() == -999){
                    exitApp();
                }else{
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                }
            }
        },params);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == HOME_TIPS_REQUEST && data != null){
                String address = data.getStringExtra("address");
                LatLng latLng = data.getParcelableExtra("latlng");
                if(latLng != null && address != null) {


                    AddressSubBean bean = new AddressSubBean();
                    bean.setAddressName(address);
                    bean.setId("");
                    bean.setLatitude(latLng.latitude);
                    bean.setLongitude(latLng.longitude);
                    bean.setRadius(0);
                    addressBeanLists.add(bean);
                    mAdapter.setData(addressBeanLists);
                    mAdapter.notifyDataSetChanged();
                }
        }
    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(0,R.anim.push_up_out);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                currentLatLng = new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude());
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
