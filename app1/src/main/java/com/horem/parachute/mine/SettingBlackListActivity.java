package com.horem.parachute.mine;

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
import com.horem.parachute.adapter.RecyclerAdapter;
import com.horem.parachute.adapter.RecyclerViewAdapter;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.mine.bean.BlackListBean;
import com.horem.parachute.mine.bean.BlackListNewBean;
import com.horem.parachute.mine.bean.BlackListSubBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.http.request.OkHttpClientManager;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SettingBlackListActivity extends BaseActivity {

    private XRecyclerView mRecyclerView;
    private RecyclerAdapter adapter;

    private List<BlackListSubBean> lists = new ArrayList<>();
    private static final int PAGE_SIZE = 10;
    private int currentPage = 0;
    private boolean isRefreshing;

    private CustomApplication application;
    private Tracker mTracker;
    private static final String TAG = "BlackList";
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
        setContentView(R.layout.activity_setting_black_list);
        init();
        initialData();
    }

    private void initialData() {

        HashMap<String,String> params = new HashMap<>();
        params.put("pageSize",String.valueOf(PAGE_SIZE));
        params.put("currentPage",String.valueOf(currentPage));

        params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lng","");
        params.put("lat","");
        params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");

        OkHttpClientManager.postAsyn(HttpUrlConstant.messageMyBlackLists, new OkHttpClientManager.ResultCallback<BlackListNewBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(BlackListNewBean response) {
                if (response.getStatusCode() == 1) {
                    if (isRefreshing) {
                        lists.clear();
                        lists.addAll(response.getResult().getList());
                        adapter.setData(lists);
                        adapter.notifyDataSetChanged();
                        mRecyclerView.refreshComplete();
                    } else {
                        lists.addAll(response.getResult().getList());
                        adapter.setData(lists);
                        adapter.notifyDataSetChanged();
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
        setTitleName("黑名单");

        mRecyclerView = (XRecyclerView) findViewById(R.id.recycler_view_black_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerAdapter(this,lists);
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                currentPage = 0;
                isRefreshing = true;
                initialData();
            }

            @Override
            public void onLoadMore() {
                currentPage++;
                isRefreshing = false;
                initialData();
            }
        });
        adapter.setOnItemDelete(new RecyclerAdapter.OnItemDelete() {
            @Override
            public void OnDelete(int positon) {
                lists.remove(positon);
                adapter.setData(lists);
                adapter.notifyDataSetChanged();
            }
        });

    }
}
