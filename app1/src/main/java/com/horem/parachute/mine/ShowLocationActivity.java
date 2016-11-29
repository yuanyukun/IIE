package com.horem.parachute.mine;

import android.app.Activity;
import android.os.Bundle;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.horem.parachute.R;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;

public class ShowLocationActivity extends BaseActivity {

    private AMap aMap;
    private MapView mapView;
    private LatLng currentLatLng;

    private CustomApplication application;
    private Tracker mTracker;
    private static final String TAG = "ShowLocation";
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
        setContentView(R.layout.activity_show_location);
        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        initTitleView();
        setTitleName("地点");
        setLeftButtonText("我拍的伞");

        mapView = (MapView) findViewById(R.id.show_location_mapview);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        if(null == aMap){
            aMap = mapView.getMap();
        }
        currentLatLng = getIntent().getParcelableExtra("latlng");
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(currentLatLng));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(20));


        MarkerOptions marker = new MarkerOptions();
        marker.draggable(false);
        marker.position(currentLatLng);
        marker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.red_parachute_1_100));
        aMap.addMarker(marker);

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
    }
}
