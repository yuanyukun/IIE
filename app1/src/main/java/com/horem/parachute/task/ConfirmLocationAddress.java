package com.horem.parachute.task;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.amap.api.maps.model.LatLng;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.adapter.LocationConfirmAdapter;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.customview.HorizontalSlideAdapter;
import com.horem.parachute.customview.HorizontalSlideDeleteListView;
import com.horem.parachute.task.bean.LocationConfirmBean;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ConfirmLocationAddress extends BaseActivity {

    private static final  int CONFIRM_LOCATION_FLAG = 0x101;
    private ImageView confirmBtn;
    private RecyclerView mSlideListView;
    private LocationConfirmAdapter mAdapter;
    private List<LocationConfirmBean> mLists = new ArrayList<>();
    private List<LatLng> latLngs = new ArrayList<>();
    private static final  int POINT_LOCATION = 1001;
    private static  final  String TAG = "confirmLocationAddress";
    private boolean isFirstEnter = true;

    private int TaskType;
    private CustomApplication application;
    private Tracker mTracker;
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
        setContentView(R.layout.activity_confirm_location_address);
        init();
    }


    private void init() {
        initTitleView();
        setTitleName("投伞地点");
        setRightButtonText("确定");

        Intent intent = getIntent();
        TaskType = intent.getIntExtra("enter",-1);
        mLists = (List<LocationConfirmBean>) intent.getSerializableExtra("data");
        latLngs = (List<LatLng>) intent.getSerializableExtra("latlngs");
//        {"locationDescribe":"广东省广州市天河区棠下街道棠下南闸大街3巷附近","taskFee":"￥1.0","taskOrderFee":"0.06"}

        mSlideListView = (RecyclerView) findViewById(R.id.confirm_location_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mSlideListView.setLayoutManager(manager);
        mAdapter = new LocationConfirmAdapter(this,mLists);

        mSlideListView.setAdapter(mAdapter);
        confirmBtn = (ImageView) findViewById(R.id.confirm_location_button);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConfirmLocationAddress.this,PointTaskLocationActivity.class);
                intent.putExtra("flag",CONFIRM_LOCATION_FLAG);
                intent.putExtra("enter",TaskType);
                startActivityForResult(intent,POINT_LOCATION);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mLists.size() == 0 && isFirstEnter){
            Intent intent = new Intent(this,PointTaskLocationActivity.class);
            intent.putExtra("enter",TaskType);
            intent.putExtra("flag",CONFIRM_LOCATION_FLAG);
            startActivityForResult(intent,POINT_LOCATION);
        }
        isFirstEnter = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.d(TAG,"结果返回回调触发");

        if(resultCode == RESULT_OK && requestCode == POINT_LOCATION && data !=null){
            LocationConfirmBean bean = (LocationConfirmBean) data.getSerializableExtra("TaskInfo");
            Log.d(getClass().getName(),new Gson().toJson(bean));
            LatLng latLng = data.getParcelableExtra("latlng");
            latLngs.add(latLng);
           if(null != bean){
               mLists.add(bean);
               mAdapter.SetData(mLists);
               mAdapter.notifyDataSetChanged();
           }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void OnRightButtonClicked() {
//        super.OnRightButtonClicked();
        Intent intent = new Intent(this, DoSendTask.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", (Serializable) mLists);
        bundle.putSerializable("latlng", (Serializable) latLngs);
        intent.putExtra("bundle",bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        setResult(RESULT_OK,intent);
        this.finish();
    }
}
