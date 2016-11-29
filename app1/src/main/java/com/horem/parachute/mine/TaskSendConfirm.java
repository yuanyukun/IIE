package com.horem.parachute.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.app.utils.SignatureTool;
import com.common.HttpUrlConstant;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.adapter.confirmTaskAdapter;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.mine.bean.ConfirmBean;
import com.horem.parachute.mine.bean.ConfirmNewResultBean;
import com.horem.parachute.mine.bean.IsCheckedBean;
import com.horem.parachute.mine.bean.OrderConfirmBean;
import com.horem.parachute.mine.bean.OrderListBean;
import com.horem.parachute.mine.bean.TaskConfirmBean;
import com.horem.parachute.mine.bean.taskSendSubBean;
import com.horem.parachute.util.MyTimeUtil;
import com.horem.parachute.util.ScreenBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.horem.parachute.util.Utils;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskSendConfirm extends BaseActivity implements View.OnClickListener{

    private static final String url = HttpUrlConstant.subTaskInfoUrl;//子任务详细信息
    private static final String confirmUrl = HttpUrlConstant.taskOrderConfirmUrl;
    private static final String notConfirmUrl = HttpUrlConstant.taskOrderNotConfirmUrl;

    private TextView tvTaskNum;         //伞号
    private TextView tvMoneyCost;       //费用
    private TextView tvDepict;          //任务描述
    private TextView tvAddress;         //地址
    private TextView tvOrderCreateTime;
    private TextView tvTimeOut;         //超时时间
    private TextView tvMessage;         //信息
    private TextView confirmBtn;        //确认按钮
    private TextView useNoneBtn;        //全部不采用按钮

    private RecyclerView mRecycler;
    private confirmTaskAdapter adapter;
    private ArrayList<OrderListBean> lists;
    private ArrayList<IsCheckedBean> mlists;
    private ArrayList<String> orderIdsList;
    private LayoutInflater inflater;
    private View headerView;

    private taskSendSubBean bean;
    private TaskConfirmBean confirmBean;

    private int position;
    private CustomApplication application;
    private Tracker mTracker;
    private static final String TAG = "TaskSendConfirm";
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
        setContentView(R.layout.mine_task_confirm);
        initView();
        initData();
    }

    private void initView() {
        initTitleView();
        setTitleName("确认");
        bean = (taskSendSubBean) getIntent().getSerializableExtra("value");
        position = getIntent().getIntExtra("position",-1);



        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        ScreenBean.setScreenWidth(screenWidth);
        ScreenBean.setScreenHeight(screenHeight);



        lists = new ArrayList<>();
        mlists = new ArrayList<>();
        orderIdsList = new ArrayList<>();
        inflater = LayoutInflater.from(this);
        headerView = inflater.inflate(R.layout.task_confirm_header_view,null);

        tvTaskNum = (TextView)headerView.findViewById(R.id.task_send_task_confirm_id);
        tvMoneyCost = (TextView)headerView.findViewById(R.id.task_send_money_confirm);
        tvDepict = (TextView)headerView.findViewById(R.id.send_task_depict_confirm);
        tvAddress = (TextView)headerView.findViewById(R.id.send_task_address_confirm);
        tvTimeOut = (TextView)headerView.findViewById(R.id.task_confirm_timeout);
        tvMessage = (TextView)headerView.findViewById(R.id.task_confirm_message);
        tvOrderCreateTime = (TextView)headerView.findViewById(R.id.send_task_create_time_confirm);

        confirmBtn = (TextView) findViewById(R.id.confirm_task_button);
        confirmBtn.setOnClickListener(this);
        confirmBtn.setClickable(false);
        confirmBtn.setAlpha(0.5f);

        useNoneBtn = (TextView) findViewById(R.id.confirm_task_no_use);
        useNoneBtn.setOnClickListener(this);

        mRecycler = (RecyclerView) findViewById(R.id.task_confirm_recycler_view);
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
        mRecycler.setLayoutManager(layoutManager);
        adapter = new confirmTaskAdapter(this,lists,mlists);
        mRecycler.setAdapter(adapter);

        //订单点击事件处理
        adapter.setOnItemClickListener(new confirmTaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //如果订单一个都没有选中
                if(getCheckedNum(adapter) == 0){
                    confirmBtn.setClickable(false);
                    confirmBtn.setAlpha(1f);
                }else {//如果有选中的订单
                    confirmBtn.setClickable(true);
                    confirmBtn.setAlpha(0.5f);
                }
                //如果选中的订单数小于设置接收到的最大订单数，则还可以继续选中订单
                if(getCheckedNum(adapter) <= confirmBean.getResult().getSubTaskInfo().getReceiveNum()){
                    if(adapter.getIsChecked(position)) {//获取当前位置订单的状态，如果选中设为不选中
                        adapter.setIsChecked(position,false);
                    }else{
                        adapter.setIsChecked(position,true);//如果没选中，设置为选中
                        confirmBtn.setClickable(true);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * 获取选中的个数
     * @param adapter
     * @return
     */
    private int getCheckedNum(confirmTaskAdapter adapter){
        int num = 0;
        ArrayList<IsCheckedBean> list = adapter.getIsCheckedList();
        for(int i = 0;i<list.size();i++){
            if(list.get(i).isChecked()){
                num++;
            }
        }
        return num;
    }
    private void initData() {
        startLoading();
        HashMap<String,String> map = new HashMap<>();
        map.put("memberId",bean.getCreatePersonId()+"");
        map.put("subTaskId",bean.getSubTaskId()+"");
        map.put("lng",bean.getLongitude()+"");
        map.put("lat",bean.getLatitude()+"");
        map.put("deviceType","android");
        map.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));

        OkHttpClientManager.postAsyn(url, new OkHttpClientManager.ResultCallback<TaskConfirmBean>() {
            @Override
            public void onError(Request request, Exception e) {
                stopLoading();
//                Log.d(getClass().getName(),e.toString());
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(TaskConfirmBean response) {
//                Log.d(getClass().getName(),new Gson().toJson(response));
                stopLoading();
                confirmBean = response;
                if(response.getStatusCode() == 1) {
                    initialView();
                }else if(response.getStatusCode() == -999){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),response.getMessage());
            }
        },map);
    }

    private void initialView() {

        if(confirmBean.getResult().getSubTaskInfo().isReceiveTimeOver()){
            useNoneBtn.setVisibility(View.VISIBLE);
        }

        tvTaskNum.setText(confirmBean.getResult().getSubTaskInfo().getNo());
        tvMoneyCost.setText(confirmBean.getResult().getSubTaskInfo().getSymbol()+ Utils.getTwoLastNumber(confirmBean.getResult().getSubTaskInfo().getRemuneration()));
        tvDepict.setText(confirmBean.getResult().getSubTaskInfo().getOtherRequirements());
        tvAddress.setText(confirmBean.getResult().getSubTaskInfo().getPlace());
        tvOrderCreateTime.setText(MyTimeUtil.formatTimeZh(confirmBean.getResult().getSubTaskInfo().getCreateTime()));
        tvTimeOut.setText(confirmBean.getResult().getSubTaskInfo().getTimeOutStr());

        switch (confirmBean.getResult().getSubTaskInfo().getMediaType()){
            case 10:
                tvMessage.setText("收到"+confirmBean.getResult().getSubTaskInfo().getDeliveryOrderNum()+"张照片");
                break;
            case 30:
                tvMessage.setText("收到"+confirmBean.getResult().getSubTaskInfo().getDeliveryOrderNum()+"段视频");
                break;
        }

        adapter.addHeader(headerView);
        //创建一个数组，订单状态数组和订单对应
        for(int i = 0;i<confirmBean.getResult().getOrderList().size();i++){
            IsCheckedBean bean = new IsCheckedBean();
            bean.setChecked(false);
            mlists.add(bean);
        }
        adapter.setData(confirmBean.getResult().getOrderList(),mlists);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.confirm_task_button:
                dealWithConfrimBtn();
                break;
            case R.id.confirm_task_no_use:
                dealWithNoneUseBtn();
                break;
        }
    }

    private void dealWithNoneUseBtn() {
        getAllOrderIdList();
        ConfirmBean bean = new ConfirmBean();
        bean.setMemberId(confirmBean.getResult().getSubTaskInfo().getCreatePersonId());
        bean.setSubTaskId(confirmBean.getResult().getSubTaskInfo().getSubTaskId());

        HashMap<String,String> map = new HashMap<>();
        map.put("subTaskId",confirmBean.getResult().getSubTaskInfo().getSubTaskId());
        map.put("taskOrderJson",new Gson().toJson(orderIdsList));
        map.put("sign", SignatureTool.getSignStr(bean));

        map.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        map.put("lng","");
        map.put("lat","");
        map.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        map.put("deviceType","android");

        OkHttpClientManager.postAsyn(notConfirmUrl, new OkHttpClientManager.ResultCallback<OrderConfirmBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(TaskSendConfirm.this,"噢，网络不给力！");
            }

            @Override
            public void onResponse(OrderConfirmBean response) {
                if(response.getStatusCode() == 1){
                    ToastManager.show(TaskSendConfirm.this,response.getMessage());
                    Intent intent = new Intent(TaskSendConfirm.this,Activity_Mine_Task_send.class);
                    intent.putExtra("position",position);
                    intent.putExtra("taskStatue",40);
                    setResult(RESULT_OK,intent);
                    TaskSendConfirm.this.finish();
                }else if(response.getStatusCode() == -999){
                    exitApp();
                }else
                    ToastManager.show(TaskSendConfirm.this,response.getMessage());
            }
        },map);
    }

    private void dealWithConfrimBtn() {
        getOrderIdList();
        ConfirmBean bean = new ConfirmBean();
        bean.setMemberId(confirmBean.getResult().getSubTaskInfo().getCreatePersonId());
        bean.setSubTaskId(confirmBean.getResult().getSubTaskInfo().getSubTaskId());

        HashMap<String,String> map = new HashMap<>();
        map.put("subTaskId",confirmBean.getResult().getSubTaskInfo().getSubTaskId());
        map.put("taskOrderJson",new Gson().toJson(orderIdsList));
        map.put("sign", SignatureTool.getSignStr(bean));

        map.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        map.put("lng","");
        map.put("lat","");
        map.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        map.put("deviceType","android");

        OkHttpClientManager.postAsyn(confirmUrl, new OkHttpClientManager.ResultCallback<ConfirmNewResultBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }
            @Override
            public void onResponse(ConfirmNewResultBean response) {
                if(response.getStatusCode() == 1){
                    Intent intent = new Intent(TaskSendConfirm.this,Activity_Mine_Task_send.class);
                    intent.putExtra("position",position);
                    intent.putExtra("taskStatue",50);
                    setResult(RESULT_OK,intent);
                    TaskSendConfirm.this.finish();
                    ToastManager.show(getApplicationContext(),response.getMessage());
                }else if(response.getStatusCode() == -999){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }
        },map);
    }

    private void getOrderIdList() {
        orderIdsList.clear();
        ArrayList<IsCheckedBean> list =  adapter.getIsCheckedList();
        for(int i = 0;i<list.size();i++){
            if(list.get(i).isChecked()){
                orderIdsList.add(confirmBean.getResult().getOrderList().get(i).getId());
            }
        }
    }
    private void getAllOrderIdList() {
        orderIdsList.clear();
        ArrayList<IsCheckedBean> list =  adapter.getIsCheckedList();
        for(int i = 0;i<list.size();i++){
            orderIdsList.add(confirmBean.getResult().getOrderList().get(i).getId());
        }
    }
}
