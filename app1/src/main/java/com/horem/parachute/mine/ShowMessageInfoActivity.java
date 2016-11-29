package com.horem.parachute.mine;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import com.common.ApplicationConstant;
import com.common.HttpUrlConstant;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.adapter.ShowMessageAdapter;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.mine.bean.MineSendTaskSubBean;
import com.horem.parachute.mine.bean.OrderListBean;
import com.horem.parachute.mine.bean.ShowMessageMainBean;
import com.horem.parachute.mine.bean.ShowMessageMainBeanNew;
import com.horem.parachute.mine.bean.ShowMessageSubBean;
import com.horem.parachute.mine.bean.TaskConfirmBean;
import com.horem.parachute.mine.bean.taskSendSubBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.http.request.OkHttpClientManager;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowMessageInfoActivity extends BaseActivity {
    private static final String url = HttpUrlConstant.subTaskOrderListUrl;//子任务所有订单

    private XRecyclerView xRecyclerView;
    private taskSendSubBean bean;
    private ShowMessageAdapter adapter;
    private ArrayList<ShowMessageSubBean> orderLists = new ArrayList<>();

    private int currentPage = 0;
    private static final int PAGESIZE = 3;
    private boolean isRefresh = true;

    private CustomApplication application;
    private Tracker mTracker;
    private static final String TAG = "ShowMessageInfo";
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
        setContentView(R.layout.activity_show_message_info);
        init();
        initData();
    }

    private void initData() {
        startLoading();
        HashMap<String,String> map = new HashMap<>();
        map.put("pageSize",String.valueOf(PAGESIZE));
        map.put("subTaskId",bean.getSubTaskId()+"");
        map.put("currentPage",String.valueOf(currentPage));

        map.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        map.put("lng","");
        map.put("lat","");
        map.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        map.put("deviceType","android");

        OkHttpClientManager.postAsyn(url, new OkHttpClientManager.ResultCallback<ShowMessageMainBeanNew>() {
            @Override
            public void onError(Request request, Exception e) {
                stopLoading();
//                Log.d(getClass().getName(),e.toString());
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(ShowMessageMainBeanNew response) {
                stopLoading();
                if(response.getStatusCode() == 1) {
                    initialView(response);
                }else if(response.getStatusCode() == -999){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }
        },map);
    }

    private void initialView(ShowMessageMainBeanNew mainBean) {
        switch (bean.getMediaType()){
            case ApplicationConstant.MEDIA_TYPE_PHOTO:
                setTitleName("收到"+mainBean.getResult().getList().size()+"份照片");
                break;
            case ApplicationConstant.MEDIA_TYPE_VIDEO:
                setTitleName("收到"+mainBean.getResult().getList().size()+"段视频");
                break;
        }
        if(isRefresh){
            adapter.setData(mainBean.getResult().getList());
            adapter.notifyDataSetChanged();
            xRecyclerView.refreshComplete();
        }else{
            orderLists.clear();
            orderLists.addAll(mainBean.getResult().getList());
            adapter.notifyDataSetChanged();
            xRecyclerView.loadMoreComplete();
        }
        adapter.setData(mainBean.getResult().getList());
        adapter.notifyDataSetChanged();
    }

    private void init() {
        initTitleView();
        setLeftButtonText("我投的伞");
        bean = (taskSendSubBean) getIntent().getSerializableExtra("value");

        xRecyclerView = (XRecyclerView) findViewById(R.id.show_message_activity_recycler);
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
        xRecyclerView.setLayoutManager(layoutManager);
        adapter = new ShowMessageAdapter(this,orderLists);
        xRecyclerView.setAdapter(adapter);

        xRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                currentPage = 0;
                isRefresh = true;
                initData();
            }

            @Override
            public void onLoadMore() {
                currentPage++;
                isRefresh = false;
                initData();

            }
        });


    }
}
