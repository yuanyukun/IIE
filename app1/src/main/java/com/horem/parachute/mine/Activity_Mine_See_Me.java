package com.horem.parachute.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.common.HttpUrlConstant;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.adapter.CommonAdapter;
import com.horem.parachute.adapter.SeeMeRecyclerViewAdapter;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.mine.bean.MineSeeMeBeanNew;
import com.horem.parachute.mine.bean.MineSeeMeSubBean;
import com.horem.parachute.mine.bean.MineSeeMebBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.http.request.OkHttpClientManager;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Activity_Mine_See_Me extends BaseActivity {

    private XRecyclerView mRecyclerView;
    private SeeMeRecyclerViewAdapter mAdapter;
    private List<MineSeeMeSubBean> mLists = new ArrayList<>();

    private HashMap<String ,String> params = new HashMap<>();
    private static final int PAGE_SIZE = 10;
    private int currentPage = 0;
    private boolean isLoadMore;
    private CustomApplication application;
    private Tracker mTracker;
    private static final String TAG = "MineSeeMe";
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
        setContentView(R.layout.activity_activity__mine__see__me);

        init();
        initialData();
    }


    private void initialData() {
        params.clear();
        params.put("pageSize",String.valueOf(PAGE_SIZE));
        params.put("currentPage",String.valueOf(currentPage));

        params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lng","");
        params.put("lat","");
        params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");

        OkHttpClientManager.postAsyn(HttpUrlConstant.memberViewUserListUrl, new OkHttpClientManager.ResultCallback<MineSeeMeBeanNew>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(MineSeeMeBeanNew response) {
                if(response.getStatusCode() == 1) {
                    if (!isLoadMore) {
                        mLists.clear();
                        mLists = response.getResult().getList();
                        mAdapter.setData(mLists);
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.refreshComplete();
                    } else {
                        mLists.addAll(response.getResult().getList());
                        mAdapter.setData(mLists);
                        mAdapter.notifyDataSetChanged();
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
        setTitleName("谁看过我");

        mRecyclerView = (XRecyclerView) findViewById(R.id.recycler_view_see_me);
        /*// 如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);*/
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


        mAdapter = new SeeMeRecyclerViewAdapter(this,mLists);
        mAdapter.setOnItemClickListener(new CommonAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent intent = new Intent();
                intent.setClass(Activity_Mine_See_Me.this,AppUserInfo.class);
                intent.putExtra("memberId",mLists.get(position).getMemberId());
                startActivity(intent);

            }
        });

        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                isLoadMore = false;
                currentPage = 0;
                initialData();

            }

            @Override
            public void onLoadMore() {
                isLoadMore = true;
                currentPage++;
                initialData();
            }
        });
        mRecyclerView.setAdapter(mAdapter);


    }

}
