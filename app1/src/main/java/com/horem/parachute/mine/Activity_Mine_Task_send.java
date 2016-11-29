package com.horem.parachute.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.common.HttpUrlConstant;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.adapter.TaskSendRecyclerViewAdapter;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.mine.bean.MineSendTaskNewBean;
import com.horem.parachute.mine.bean.taskSendSubBean;
import com.horem.parachute.util.ScreenBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.http.request.HttpApi;
import com.http.request.OkHttpClientManager;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 2015/9/11.
 */
public class Activity_Mine_Task_send extends BaseActivity {
    private static final int TASK_CONFIRM = 1001;
    private XRecyclerView mRecyclerView;
    private TaskSendRecyclerViewAdapter mAdapter;

    private  HttpApi api;                       //网络请求接口
    private HashMap<String,String> map;


    private static final int  PAGE_SIZE = 5;    //每页请求的数据个数；
    private int currentPage = 0;                //请求的页码
    private boolean isRefreshing = true;

    //模拟数据
    private ArrayList<taskSendSubBean> lists = new ArrayList<>();

    private CustomApplication application;
    private Tracker mTracker;
    private static final String TAG = "MineTaskSend";
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
        setContentView(R.layout.activity_mine_publish_task);
        init();
        initData();
    }

    private void initData() {
        long userId = SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0);

        map = new HashMap<>();
        map.put("pageSize",String.valueOf(PAGE_SIZE));
        map.put("currentPage",String.valueOf(currentPage));

        map.put("memberId",SharePreferencesUtils.getLong(this,SharePrefConstant.MEMBER_ID,(long)0)+"");
        map.put("lng","");
        map.put("lat","");
        map.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        map.put("deviceType","android");

//        for(Map.Entry<String,String> entry:map.entrySet()){
//            Log.i(getClass().getName(),entry.getKey()+":"+entry.getValue());
//        }

        OkHttpClientManager.postAsyn(HttpUrlConstant.myIssuanceTaskList, new OkHttpClientManager.ResultCallback<MineSendTaskNewBean>() {
            @Override
            public void onError(Request request, Exception e) {
//                Log.d(getClass().getName(),e.toString());
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(MineSendTaskNewBean response) {
                if(response.getStatusCode() == 1) {
                    if (isRefreshing) {
                        lists.clear();
                        lists.addAll(response.getResult().getList());
                        mAdapter.RrefreshData(lists);
                        mRecyclerView.refreshComplete();
                    } else {

                        lists.addAll(response.getResult().getList());
                        mAdapter.RrefreshData(lists);
                        mRecyclerView.loadMoreComplete();
                    }
                }else if(response.getStatusCode() == -999){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }
        },map);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        currentPage = 0;
        isRefreshing = true;
        initData();
    }

    private void init() {
        initTitleView();
        setTitleName("我投的伞");


        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        ScreenBean.setScreenWidth(screenWidth);
        ScreenBean.setScreenHeight(screenHeight);

        mRecyclerView = (XRecyclerView) findViewById(R.id.recycler_view);
         LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                //refreshing...
                isRefreshing =true;
                currentPage = 0;
                initData();
            }

            @Override
            public void onLoadMore() {
                //load more...
                isRefreshing = false;
                currentPage++;
                initData();
            }
        });

        mAdapter = new TaskSendRecyclerViewAdapter(this,lists);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == TASK_CONFIRM && data != null){
            int position = data.getIntExtra("position",-1);
            int taskStatus = data.getIntExtra("taskStatus",0);
            if(position != -1 && taskStatus != 0){
                lists.get(position).setTaskState(taskStatus);
                mAdapter.RefreshSubTaskStatus(lists);
            }
        }
    }
}
