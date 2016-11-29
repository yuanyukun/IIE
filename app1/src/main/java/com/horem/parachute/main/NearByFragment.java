package com.horem.parachute.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.login.Activity_Login;
import com.horem.parachute.login.bean.MapTaskNewBean;
import com.horem.parachute.login.bean.SubTaskBean;
import com.horem.parachute.menu.PopupBalloonInfo;
import com.horem.parachute.menu.PopupTaskInfo;
import com.horem.parachute.pushmessage.InstanceMessageService;
import com.horem.parachute.task.DoSendTask;
import com.horem.parachute.task.bean.BalloonBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.Utils;
import com.http.request.HttpApi;
import com.http.request.IResponseApi;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yuanyukun on 2016/6/17.
 */
public class NearByFragment extends Fragment implements LocationSource,AMapLocationListener
,View.OnClickListener,AMap.InfoWindowAdapter,AMap.OnMarkerClickListener{
    private CustomApplication application;
    private TextureMapView mapView;                        //地图
    private AMap aMap;
    private int currentIndex = 0;

    private LocationSource.OnLocationChangedListener mListener;    //地图地理位置变化监听
    private AMapLocationClient mlocationClient;     //地图定位客户端
    private AMapLocationClientOption mLocationOption;//定位客户端样式选项自定义

    private LatLng latLng;                          //坐标类
    private boolean isFirstEnter = true;            //是否第一次打开界面

    private TextView titleName,titleNext,userInfo;    //返回键
    private ImageView chatReddot;

    private SubTaskBean[] subTaskList;              //伞任务列表
    private ArrayList<BalloonBean> balloonBeanLists;
    private ArrayList<BalloonBean> newBalloonLists;//异步获取完图片后重新组合气球列表
    private PopupTaskInfo taskInfo;                 //弹框列表
    private View view;
    private static NearByFragment fragment = null;
    private PopupBalloonInfo balloonInfo;


    private LinearLayout testContainer;

    private static final  int SubTaskType = 0x010;
    private static final  int BalloonType = 0x011;


    public static NearByFragment newInstance() {
        if (fragment == null) {
            synchronized (NearByFragment.class) {
                if (fragment == null) {
                    fragment = new NearByFragment();
                }
            }
        }
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (view == null) {
//             Log.d("sys", "MF onCreateView() null");
            view = inflater.inflate(R.layout.activity_main, null);
             mapView = (TextureMapView) view.findViewById(R.id.location_mapview);
             mapView.onCreate(savedInstanceState);
            if (aMap == null) {
                aMap = mapView.getMap();
            }
            // 自定义系统定位小蓝点
            MyLocationStyle myLocationStyle = new MyLocationStyle();
            myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                    .fromResource(R.drawable.location_marker));// 设置小蓝点的图标
            aMap.setMyLocationStyle(myLocationStyle);
            //地图的各种属性设置
            aMap.setLocationSource(this);// 设置定位监听
            aMap.setOnMarkerClickListener(this);
            aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
            aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            aMap.setMapType(AMap.MAP_TYPE_NORMAL);//普通2d地图
            aMap.moveCamera(CameraUpdateFactory.zoomTo(16));//设置地图初始缩放级别（4~20)
        } else {
            if (view.getParent() != null) {
                 ((ViewGroup) view.getParent()).removeView(view);
               }
        }

        return view;
    }

    private void isLoginCache() {
        application = (CustomApplication) getActivity().getApplication();
        long memberId = SharePreferencesUtils.getLong(getContext(), SharePrefConstant.MEMBER_ID,(long)-1);
        if(memberId > 0){
            application.setLogin(true);
            application.setUserId(memberId);
        }
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isLoginCache();
       titleName = (TextView) view.findViewById(R.id.title_name);
        userInfo = (TextView) view.findViewById(R.id.title_back);
        titleNext = (TextView) view.findViewById(R.id.title_next);


        testContainer = (LinearLayout) view.findViewById(R.id.test_container);


        titleNext.setVisibility(View.INVISIBLE);
        titleName.setText("附近");
        userInfo.setVisibility(View.INVISIBLE);

    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
//                mListener.onLocationChanged(aMapLocation);//这行代码就是显示系统默认图标，现在注释掉
                latLng = new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude());

//                aMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));  //定位成功中定位
                if( isFirstEnter )
                {
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
                    getTaskBeanData();
                }

//                currentCityCode = aMapLocation.getCityCode();

            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode()+ ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }
    }
    private void getTaskBeanData() {

        Log.i(getClass().getName(),"获取map数据");
        isFirstEnter = false;
        HashMap<String,String> map = new HashMap<>();
        map.put("lat",latLng.latitude+"");
        map.put("lng",latLng.longitude+"");

        HttpApi api = new TaskMapImpl();
        api.httpRequest(getActivity(), new IResponseApi() {
            @Override
            public void onSuccess(Object object) {
                //显示地图任务图标
                showMark(object);
            }

            @Override
            public void onFailed(Exception e) {

            }
        },map);
    }
    private void showMark(Object object){
        MapTaskNewBean bean = (MapTaskNewBean) object;
        balloonBeanLists  = bean.getResult().getBalloonList();
        showTaskMark(bean);
        showBalloonMark(bean);
    }

    private void showBalloonMark(MapTaskNewBean bean) {
         if(bean.getStatusCode() == 1 && bean.getResult().getBalloonList()!=null){
             for(int index = 0;index < balloonBeanLists.size();index++ ){
                 final BalloonBean item = balloonBeanLists.get(index);
                 final LatLng balloonPoint = new LatLng(item.getBalloonLatitude(),item.getBalloonLongitude());
                 final int currentPosition = index;
                 if(item.getAttList().size() > 0) {
                     Glide.with(getContext())
                             .load(getBitmapUrl(item))
                             .asBitmap()
                             .centerCrop()
                             .into(new SimpleTarget<Bitmap>(250, 250) {
                                 @Override
                                 public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                     super.onLoadFailed(e, errorDrawable);
                                 }

                                 @Override
                                 public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                     // Do something with bitmap here.
                                     // 初始化marker内容
                                      MarkerOptions markerOptions = new MarkerOptions();
                                     // 根据需求可以加载复杂的View
                                      RelativeLayout markViewContainer = new RelativeLayout(getContext());
                                     RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(
                                             Utils.dp2px(getContext(),50),Utils.dp2px(getContext(),55.55f));
                                     markViewContainer.setLayoutParams(layoutParams1);

                                     switch (item.getMediaType()){
                                         case ApplicationConstant.MEDIA_TYPE_PHOTO:
                                             markViewContainer.setBackground(ContextCompat.getDrawable(getContext(),R.mipmap.redpin));
                                             break;
                                         case ApplicationConstant.MEDIA_TYPE_VIDEO:
                                             markViewContainer.setBackground(ContextCompat.getDrawable(getContext(),R.mipmap.bluepin));
                                             break;
                                     }
                                     ImageView imageView = new ImageView(getContext());
                                     imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                     RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(
                                             Utils.dp2px(getContext(),45), Utils.dp2px(getContext(),45));

                                     layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                                     layoutParams2.setMargins(10,10,10,0);
                                     imageView.setLayoutParams(layoutParams2);
                                     imageView.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.white));
                                     markViewContainer.addView(imageView);

                                     imageView.setImageBitmap(resource);
                                     // 通过View获取BitmapDescriptor对象
                                     BitmapDescriptor markerIcon = BitmapDescriptorFactory
                                             .fromView(markViewContainer);
                                     markerOptions.position(balloonPoint)
                                             .icon(markerIcon);

                                     // 添加到地图上
                                     Marker mMarker = aMap.addMarker(markerOptions);
                                     markerType type = new markerType(currentPosition,BalloonType);

//                                     Log.d(getClass().getName(),String.valueOf(currentIndex-1));
//                                     Log.d(getClass().getName(),String.valueOf(currentPosition));
//                                     Log.d("验证Url",getBitmapUrl(balloonBeanLists.get(currentPosition)));
                                     mMarker.setObject(type);
                                 }

                             });
                 }
             }
         }
    }

    private String getBitmapUrl(BalloonBean item) {
        String result = "";
        if(item.getAttList().size() >  0) {
            switch (item.getMediaType()) {
                case ApplicationConstant.MEDIA_TYPE_PHOTO:
                    result = Utils.getSmalleImageUrl(item.getAttList().get(0).getName(), 45, 45, getContext());
                    break;
                case ApplicationConstant.MEDIA_TYPE_VIDEO:
                    result = Utils.getVideoPreviewImgUrl(item.getAttList().get(0).getPreviewImg());
                    break;
            }
        }
        return result;
    }

    private void showTaskMark(MapTaskNewBean bean) {
        if(bean.getStatusCode() == 1 && bean.getResult().getSubTaskList() != null){
            subTaskList = bean.getResult().getSubTaskList();
            Log.d("降落伞列表数据",new Gson().toJson(subTaskList));
            for(int i=0;i<subTaskList.length;i++){
                //显示任务图标
                int mediaType = subTaskList[i].getMediaType();
                LatLng Company = new LatLng(subTaskList[i].getLatitude(),subTaskList[i].getLongitude());

                MarkerOptions marker = new MarkerOptions();
                marker.draggable(false);
                RelativeLayout markViewContainer = new RelativeLayout(
                        getContext());
                markViewContainer.setBackground(null);
                ImageView imageView = new ImageView(getContext());
                imageView.setBackground(null);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(
                       ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                if(subTaskList[i].isPrivate()){
                    imageView.setImageResource(R.mipmap.top_parachute_1_100);
                }else{

                    switch (mediaType){
                        case ApplicationConstant.MEDIA_TYPE_PHOTO:
                           imageView.setImageResource(R.mipmap.red_parachute_1_100);
                            break;
                        case ApplicationConstant.MEDIA_TYPE_VIDEO:
                            imageView.setImageResource(R.mipmap.parachute_1_100);
                            break;
                    }

                }

                imageView.setLayoutParams(layoutParams2);
//                 imageView.setImageResource(R.mipmap.circle_light);
                markViewContainer.addView(imageView);

                RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(
                        Utils.dp2px(getContext(),60),Utils.dp2px(getContext(),75));
                markViewContainer.setLayoutParams(layoutParams1);

                // 通过View获取BitmapDescriptor对象
                BitmapDescriptor markerIcon = BitmapDescriptorFactory
                        .fromView(markViewContainer);
                marker.position(Company).icon(markerIcon);

                Marker mMarker = aMap.addMarker(marker);
                markerType type = new markerType(i,SubTaskType);
                mMarker.setObject(type);

            }
            aMap.moveCamera(CameraUpdateFactory.zoomTo(16));

        }
    }

    private void popupShowBalloon(ArrayList<BalloonBean> lists, int position) {
        balloonInfo = new PopupBalloonInfo(getActivity(),lists,position);
        balloonInfo.showAtLocation(getActivity().findViewById(R.id.main_root), Gravity.BOTTOM,0,0);
    }
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(getContext());
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            //设置定位间隔,单位毫秒,默认为2000ms
            mLocationOption.setInterval(10000);
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

    @Override
    public boolean onMarkerClick(Marker marker) {
         markerType type = (markerType) marker.getObject();
        String value = new Gson().toJson(type);
        switch (type.getType()){
            case SubTaskType:
                popupShowTask(subTaskList,type.getId());
                break;
            case BalloonType:
                int position = type.getId();//8
                String url = getBitmapUrl(balloonBeanLists.get(position));
                Log.d("气球预览图URl",url);
                popupShowBalloon(balloonBeanLists,position);
                break;
        }
        return true;
    }
    /**
     * /任务列表
     */
    private void popupShowTask(SubTaskBean[] mlists, int position){

        taskInfo = new PopupTaskInfo(getActivity(),mlists,position);
        taskInfo.showAtLocation(getActivity().findViewById(R.id.main_root), Gravity.BOTTOM,0,0);

    }

    @Override
    public void onClick(View v) {
//        if(v == titleNext){
//            showFuncMenu();
//        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("fragment","onCreate");
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.setVisibility(View.VISIBLE);
        mapView.onResume();

        if(taskInfo != null && taskInfo.isShowing()){
            taskInfo.dismiss();
        }
        if(balloonInfo != null && balloonInfo.isShowing()){
            balloonInfo.dismiss();
        }
        if(SharePreferencesUtils.getBoolean(getContext(),SharePrefConstant.isNeedUpdateMap,false)){
            SharePreferencesUtils.setBoolean(getContext(),SharePrefConstant.isNeedUpdateMap,false);
            getTaskBeanData();
        }
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        mapView.setVisibility(View.GONE);
        Log.d("fragment","onPause");
    }


    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
        Log.d("fragment","onSaveInstanceState");
    }


    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        Log.d("fragment","onDestroy");

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("fragment","onDestroyView");
    }


    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("fragment","onDetach");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("fragment","onstop");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("fragment","onDestroy");
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    class markerType{
        int id;
        int type;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public markerType() {
        }

        public markerType(int id, int type) {
            this.id = id;
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

}
