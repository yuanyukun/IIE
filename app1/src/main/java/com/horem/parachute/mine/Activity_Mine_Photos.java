package com.horem.parachute.mine;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.common.HttpUrlConstant;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.adapter.MyAdapter;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.mine.bean.MinePhotoBean;
import com.horem.parachute.mine.bean.MinePhotoNewBean;
import com.horem.parachute.mine.bean.MinePhotoSubBean;
import com.horem.parachute.util.ScreenBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.http.request.OkHttpClientManager;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;

public class Activity_Mine_Photos extends BaseActivity {
    private XRecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private ArrayList<MinePhotoSubBean> listData = new ArrayList<>();
    private int refreshTime = 0;
    private int times = 0;

    private static final int PAGE_SIZE = 15;
    private int currentPage = 0;
    private boolean isLoadingMore = false;

    private static  final  String url = HttpUrlConstant.memberPhotoAllUrl;
    private CustomApplication application;
    private Tracker mTracker;
    private static final String TAG = "PhotosAty";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);

        init();
        initData();
    }
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

    private void initData() {
        HashMap<String,String> params = new HashMap<>();
        params.put("photoMemberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("pageSize",String.valueOf(PAGE_SIZE));
        params.put("currentPage",currentPage+"");

        params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lng","");
        params.put("lat","");
        params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");

        OkHttpClientManager.postAsyn(url, new OkHttpClientManager.ResultCallback<MinePhotoNewBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(MinePhotoNewBean response) {
//                Log.d(getClass().getName(),new Gson().toJson(response));
                if(response.getStatusCode() == 1) {
                    mAdapter.RefreshDatas(response.getResult().getAttachmentList());
                    if (!isLoadingMore) {

                        setTitleName("相册(" + response.getResult().getAttachmentNum() + ")");
                        listData.clear();
                        listData = response.getResult().getAttachmentList();
                        mAdapter.RefreshDatas(listData);
                        mRecyclerView.refreshComplete();

                    } else {
                        listData.addAll(response.getResult().getAttachmentList());
                        mAdapter.RefreshDatas(listData);
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
        setTitleName("相册");
        getSreenDimens();
        initView();
    }

    private void initView() {
        mRecyclerView = (XRecyclerView)this.findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this,3);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
                     @Override
            public void onRefresh() {
                 currentPage = 0;
                 isLoadingMore = false;
                 initData();
            }

            @Override
            public void onLoadMore() {
                currentPage++;
                isLoadingMore = true;
                initData();
            }
        });

        mAdapter = new MyAdapter(listData,this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void getSreenDimens() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        ScreenBean.setScreenWidth(screenWidth);
        ScreenBean.setScreenHeight(screenHeight);
    }


}
