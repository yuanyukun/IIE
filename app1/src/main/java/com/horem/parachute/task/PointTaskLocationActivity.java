package com.horem.parachute.task;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.SearchView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.common.HttpUrlConstant;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.balloon.Bean.GetAdressBean;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.mine.HomeTipsActivity;
import com.horem.parachute.task.bean.GetTaskFeeBean;
import com.horem.parachute.task.bean.GetTaskFeeBeanNew;
import com.horem.parachute.task.bean.LocationConfirmBean;
import com.horem.parachute.util.AMapUtil;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.horem.parachute.util.Utils;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import org.kymjs.kjframe.database.utils.ClassUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PointTaskLocationActivity extends BaseActivity implements View.OnClickListener,LocationSource
        ,AMapLocationListener {

    private AMap aMap;
    private MapView mapView;
    private static final int requestPoiKey = 1001;
    private boolean isFirstEnter = true;

    private LatLng currentLatLng;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private SearchView searchEdit;
    private String addressName;
    private int TaskType;

    private static final int  CONFIRM_LOCATION_FLAG = 0x101;
    private static final int  HOME_TIPS_FLAG = 0x102;
    private int Flag;
    private CustomApplication application;
    private Tracker mTracker;
    private static  final  String TAG = "PointLocation";

    private boolean getRefresh = false;
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
        setContentView(R.layout.activity_point_task_location);
        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {

        initTitleView();
        setTitleName("投伞地点");
        setRightButtonText("完成");


        Flag = getIntent().getIntExtra("flag",0);
        switch (Flag){
            case CONFIRM_LOCATION_FLAG:
                TaskType = getIntent().getIntExtra("enter",-1);
                break;
            case HOME_TIPS_FLAG:
                break;
        }
        searchEdit = (SearchView) findViewById(R.id.point_location_edit);
        searchEdit.setOnClickListener(this);
        mapView = (MapView) findViewById(R.id.point_location_mapview);
        mapView.onCreate(savedInstanceState);//必须调用


        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }

    private void setUpMap() {
        aMap.setLocationSource(this);// 设置定位监听
//        aMap.setOnCameraChangeListener(this);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.MAP_TYPE_NORMAL);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));//设置地图初始缩放级别（4~20)
    }

    @Override
    protected void OnRightButtonClicked() {
        LatLng currentLoc = aMap.getCameraPosition().target;
//        super.OnRightButtonClicked();
        if(null != currentLatLng){
            switch (Flag){
                case CONFIRM_LOCATION_FLAG:
                    startLoading();
                    getTaskFee(currentLoc);
                    break;
                case HOME_TIPS_FLAG:
                    startLoading();
                    getAndShowAddress(currentLoc);
                    break;
            }
        }

    }
    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if(null != mlocationClient){
            mlocationClient.onDestroy();
        }
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点

                if(isFirstEnter){
                    isFirstEnter = false;
                    currentLatLng = new LatLng(amapLocation.getLatitude(),amapLocation.getLongitude());
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(currentLatLng));
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(18));//设置地图初始缩放级别（4~20)
                    Log.d("第一次定位成功",currentLatLng+"");
                }
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }
    }

    private void showMark(LatLng target) {
        aMap.clear();
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(target));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));//设置地图初始缩放级别（4~20)

        MarkerOptions marker = new MarkerOptions();
        marker.draggable(false);
        marker.position(target);
        marker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.red_parachute_1_100));
        aMap.addMarker(marker);
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
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

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }
    private void getAndShowAddress(final LatLng latlng) {
        HashMap<String,String> params = new HashMap<>();
        params.put("longitude",latlng.longitude+"");
        params.put("latitude",latlng.latitude+"");

        params.put("memberId",SharePreferencesUtils.getLong(this,SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lng",latlng.longitude+"");
        params.put("lat",latlng.latitude+"");
        params.put("clientId",SharePreferencesUtils.getString(this,SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");

//        for(Map.Entry<String,String> entry:params.entrySet()){
//            Log.d(getClass().getName(),entry.getKey()+":"+entry.getValue());
//        }

        OkHttpClientManager.postAsyn(HttpUrlConstant.mapAddressUrl, new OkHttpClientManager.ResultCallback<GetAdressBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(GetAdressBean response) {
                if(response.getStatusCode() == 1) {
                    //显示当前的地址
                    addressName = response.getResult();
                    Intent intent = new Intent(PointTaskLocationActivity.this,HomeTipsActivity.class);
                    intent.putExtra("address",addressName);
                    intent.putExtra("latlng",latlng);
                    setResult(RESULT_OK,intent);
                    finish();
                }else if(response.getStatusCode() == -999){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }
        },params);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onGoBack(null);
    }

    private void onGoBack(LocationConfirmBean name) {
        Intent intent = new Intent(PointTaskLocationActivity.this,ConfirmLocationAddress.class);
        if(name != null){
            intent.putExtra("TaskInfo",name);
            intent.putExtra("latlng",currentLatLng);
        }
        setResult(RESULT_OK,intent);
        this.finish();
    }

    @Override
    protected void OnLeftButtonClicked() {
//        super.OnLeftButtonClicked();
        switch (Flag){
            case CONFIRM_LOCATION_FLAG:
                onGoBack(null);
                break;
            case HOME_TIPS_FLAG:
                Intent intent = new Intent(PointTaskLocationActivity.this,HomeTipsActivity.class);
                setResult(RESULT_OK,intent);
                finish();
                break;
        }

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(PointTaskLocationActivity.this,PoiKeySearchActivity.class);
        startActivityForResult(intent,requestPoiKey);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestPoiKey == requestCode && RESULT_OK == resultCode && null != data){

            currentLatLng = data.getParcelableExtra("latlng");
            if(currentLatLng != null) {
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(currentLatLng));
                aMap.moveCamera(CameraUpdateFactory.zoomTo(18));//设置地图初始缩放级别（4~20)
            }
        }
    }

    private void getTaskFee(LatLng currentLoc) {
        HashMap<String,String> map = new HashMap<>();
        map.put("mediaType",TaskType+"");
        map.put("longitude",currentLoc.longitude+"");
        map.put("latitude", currentLoc.latitude+"");

        map.put("memberId", SharePreferencesUtils.getLong(PointTaskLocationActivity.this, SharePrefConstant.MEMBER_ID,(long)-1)+"");
        map.put("lng",currentLoc.longitude+"");
        map.put("lat",currentLoc.latitude+"");
        map.put("clientId",SharePreferencesUtils.getString(PointTaskLocationActivity.this,SharePrefConstant.INSTALL_CODE,""));
        map.put("deviceType","android");

        OkHttpClientManager.postAsyn(HttpUrlConstant.mapAddressPayStandardsUrl, new OkHttpClientManager.ResultCallback<GetTaskFeeBeanNew>() {
            @Override
            public void onError(Request request, Exception e) {
                stopLoading();
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(GetTaskFeeBeanNew response) {
                stopLoading();
                if(response.getStatusCode() == 1){
                    LocationConfirmBean bean = new LocationConfirmBean();
                    bean.setLocationDescribe(response.getResutl().getAddress());
                    bean.setTaskFee(response.getResutl().getSymbol()+ Utils.getTwoLastNumber(response.getResutl().getBenchmark()));
                    bean.setTaskOrderFee(response.getResutl().getPoundageBenchmark()+"");
                    String value = new Gson().toJson(bean);
                    onGoBack(bean);
                }else if(response.getStatusCode() == -999){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }
        },map);
    }

}
