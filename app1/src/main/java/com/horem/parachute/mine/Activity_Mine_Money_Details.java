package com.horem.parachute.mine;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;

import com.common.HttpUrlConstant;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.horem.parachute.R;
import com.horem.parachute.adapter.MoneyDetailAdapter;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.mine.bean.MoneyDetailBean;
import com.horem.parachute.mine.bean.MoneyDetailNewBean;
import com.horem.parachute.mine.bean.MoneyDetailSubBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.http.request.OkHttpClientManager;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.squareup.okhttp.Request;

import org.kymjs.chat.bean.Message;

import java.util.ArrayList;
import java.util.HashMap;

public class Activity_Mine_Money_Details extends BaseActivity {

    private static final String url = HttpUrlConstant.taskMyChangeListUrl;
    private XRecyclerView mRecyclerView;     //recyclerview
    private  MoneyDetailAdapter adapter;     //适配器
    private ArrayList<MoneyDetailSubBean> lists;//数据

    private int currentPage = 0;            //当前页
    private long memberId;                  //用户Id
    private boolean isRefreshing;           //是否下拉
    private static final  int pageSize = 15;//每次请求获取的数据数量

    private CustomApplication application;
    private Tracker mTracker;
    private static final String TAG = "MoneyDtail";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity__mine__money__details);
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

        HashMap<String,String> map = new HashMap<>();

        map.put("pageSize",""+pageSize);
        map.put("currentPage",""+currentPage);

        map.put("memberId",""+memberId);
        map.put("lng","");
        map.put("lat","");
        map.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        map.put("deviceType","android");
        OkHttpClientManager.postAsyn(url, new OkHttpClientManager.ResultCallback<MoneyDetailNewBean>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(MoneyDetailNewBean response) {
                if(response.getStatusCode() == 1) {
                    if (isRefreshing) {
                        lists.clear();
                        lists.addAll(response.getResult().getList());
                        mRecyclerView.refreshComplete();
                        adapter.setData(lists);
                        adapter.notifyDataSetChanged();
                    } else {
                        lists.addAll(response.getResult().getList());
                        mRecyclerView.refreshComplete();
                        adapter.setData(lists);
                        adapter.notifyDataSetChanged();
                        mRecyclerView.loadMoreComplete();
                    }
                }else if(response.getStatusCode() == -999){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }
        },map);
    }

    private void init() {
        initTitleView();
        setTitleName("零钱明细");

        lists = new ArrayList<>();
        memberId = SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0);
        mRecyclerView = (XRecyclerView) findViewById(R.id.money_recylerview);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);

        adapter = new MoneyDetailAdapter(this,lists);
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }

            @Override
            public void onLoadMore() {
                loadMoreData();
            }
        });

//        mRecyclerView.refreshDrawableState();

    }

    private void loadMoreData() {
        isRefreshing = false;
        currentPage++;
        initData();
    }

    private void refreshData() {
        isRefreshing = true;
        currentPage = 0;
        initData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent. KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN ) {
            this.finish();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
