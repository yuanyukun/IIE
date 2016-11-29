package com.horem.parachute.balloon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.common.HttpUrlConstant;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.adapter.SendFlowerAdapter;
import com.horem.parachute.balloon.Bean.FlowersListBean;
import com.horem.parachute.balloon.Bean.SendFlowerBean;
import com.horem.parachute.balloon.Bean.SendFlowersGetPayIdBean;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.customview.GetFlowerNum;
import com.horem.parachute.task.TaskPayActivity;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.horem.parachute.util.Utils;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SendFlowersActivity extends BaseActivity {
    private GetFlowerNum flowerNum;
    private Button btnPay;
    private RecyclerView mRecyclerView;
    private CircleImageView userHeader;
    private TextView flowerPrice;


    private List<FlowersListBean> lists = new ArrayList<>();
    private SendFlowerAdapter mAdapter;

    private String balloonId;
    private String headUrl;
    private String sendFlowersNum = "10";

    private boolean isPersonnal = false;
    private String currentUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_flowers);

        initData();
        initView();

    }

    private void initData() {
        HashMap<String,String> params = new HashMap<>();
        isPersonnal = getIntent().getStringExtra("type").equals("appUserInfo");
        balloonId = getIntent().getStringExtra("balloonId");
        headUrl = getIntent().getStringExtra("head");
        //
        if(isPersonnal){
            params.put("userId",balloonId);
            currentUrl = HttpUrlConstant.flowersPriceToUser;
        }else{
            params.put("balloonId",balloonId);
            currentUrl = HttpUrlConstant.balloonFlowersPrices;
        }

        params.put("flowersPageSize","6");
        params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lng","");
        params.put("lat","");
        params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");

        OkHttpClientManager.postAsyn(currentUrl, new OkHttpClientManager.ResultCallback<SendFlowerBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(SendFlowerBean response) {
//                Log.d("送花",new Gson().toJson(response));
                if(response.getStatusCode() == 1){
                    lists.addAll(response.getResult().getFlowersList());
                    mAdapter.notifyDataSetChanged();
                    flowerPrice.setText(getPriceStr(Utils.getTwoLastNumber((double)response.getResult().getPrice())));
                }else if(-999 == response.getStatusCode()){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }
        },params);

    }

    private String getPriceStr(String twoLastNumber) {
        StringBuilder builder = new StringBuilder();
        builder.append("￥");
        builder.append(twoLastNumber);
        builder.append("/朵");
        return  builder.toString();
    }

    private void initView() {
        initTitleView();
        setTitleName("送花");

        btnPay = (Button) findViewById(R.id.btn_send_flowers_pay);
        btnPay.setText("支付(￥10.00)");

        userHeader = (CircleImageView) findViewById(R.id.circle_imageview_send_flowers_user_icon);
        Glide.with(this).load(Utils.getHeadeUrl(headUrl))
//                .placeholder(R.mipmap.circle_dark)
                .into(userHeader);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_send_flowers_list);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new SendFlowerAdapter(this,lists);
        mRecyclerView.setAdapter(mAdapter);
        flowerPrice = (TextView) findViewById(R.id.tv_send_balloon_flower_price);
        flowerNum = (GetFlowerNum) findViewById(R.id.custom_flower_num);
        flowerNum.setOnEditTextChanged(new GetFlowerNum.OnEditTextChangedListener() {
            @Override
            public void onEditNumChanged(String value) {

                sendFlowersNum = value;
                if(TextUtils.isEmpty(value)){
                    //button设置为支付
                    btnPay.setText("支付");
                }else{
                    //button 设置为支付+(Integer.valueOf(value))
                    btnPay.setText("支付(￥" + Utils.getTwoLastNumber(Double.valueOf(value))+")");
                }
            }
        });
    }
    public void onPay(View view){
        if(sendFlowersNum.equals("")){
            ToastManager.show(this,"请输入送花的数量");
        }else{
            if(isPersonnal){
                sendFlowersToPerson();
            }else{
                sendFlowersToBalloon();
            }



        }

    }

    private void sendFlowersToPerson() {
        HashMap<String,String> params = new HashMap<>();
        params.put("userId",balloonId);
        params.put("flowerNumber",sendFlowersNum);
        params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lng","");
        params.put("lat","");
        params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");

        OkHttpClientManager.postAsyn(HttpUrlConstant.balloonAddUsers, new OkHttpClientManager.ResultCallback<SendFlowersGetPayIdBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(SendFlowersGetPayIdBean response) {
//                Log.d(getClass().getName(),new Gson().toJson(response));
                if(response.getStatusCode() == 1){
                    Bundle bundle = new Bundle();
                    bundle.putString("taskId",response.getResult());
                    bundle.putString("intentType","sendFlowers");
                    Intent intent = new Intent(SendFlowersActivity.this, TaskPayActivity.class);
                    intent.putExtra("bundle",bundle);
                    startActivity(intent);
                    finish();
                }else if(response.getStatusCode() == -999){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }
        },params);
    }

    private void sendFlowersToBalloon() {
        HashMap<String,String> params = new HashMap<>();
        params.put("balloonId",balloonId);
        params.put("flowerNumber",sendFlowersNum);
        params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lng","");
        params.put("lat","");
        params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");

        OkHttpClientManager.postAsyn(HttpUrlConstant.balloonAddFlowers, new OkHttpClientManager.ResultCallback<SendFlowersGetPayIdBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(SendFlowersGetPayIdBean response) {
//                Log.d(getClass().getName(),new Gson().toJson(response));
                if(response.getStatusCode() == 1){
                    Bundle bundle = new Bundle();
                    bundle.putString("taskId",response.getResult());
                    bundle.putString("intentType","sendFlowers");
                    Intent intent = new Intent(SendFlowersActivity.this, TaskPayActivity.class);
                    intent.putExtra("bundle",bundle);
                    startActivity(intent);
                    finish();
                }else if(-999 == response.getStatusCode()){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }
        },params);
    }
}
