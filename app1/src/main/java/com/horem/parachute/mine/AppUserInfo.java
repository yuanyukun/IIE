package com.horem.parachute.mine;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.common.HttpUrlConstant;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.balloon.BalloonRecommendAndMoreActivity;
import com.horem.parachute.balloon.Bean.BaseResultBean;
import com.horem.parachute.balloon.FansActivity;
import com.horem.parachute.balloon.PayAttentionToActivity;
import com.horem.parachute.balloon.SendFlowersActivity;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.customview.CustomHeadView;
import com.horem.parachute.login.Activity_Login;
import com.horem.parachute.main.bean.BalloonListSubBeanItem;
import com.horem.parachute.mine.bean.AppUserInfoNewBean;
import com.horem.parachute.util.MyTimeUtil;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AppUserInfo extends BaseActivity {

    private long userId; //用户Id
    private CustomHeadView headImg;       //用户头像
//    private TextView userName;             //用户姓名
    private TextView       chatBtn;          //聊聊按钮
    private TextView       focusBtn;          //聊聊按钮
    private TextView time;                 //时间
    private TextView doSendTask;           //投伞
    private TextView receivedNum;          //采用
    private TextView receivedRate;         //采用率
    private TextView takePhotos;           //拍摄
    private TextView beenReceivedRate;     //被采用率
    private TextView beenReceiveFlowers;   //收到鲜花的数量
    private TextView balloonNum;            //气球
    private TextView followNum;             //关注
    private TextView beFollowNum;           //粉丝
    private TextView tvUserNames;           //粉丝
    private TextView tvCertification;           //粉丝
    private TextView tvIntroduction;           //粉丝
    private TextView tvTitle;           //粉丝
    private int authType;
    private String head;
    private LinearLayout btnContainer;

    private String nickName;
    private AppUserInfoNewBean subItem;


    private CustomApplication application;
    private Tracker mTracker;
    private static final String TAG = "AppUserInfo";
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
        setContentView(R.layout.activity_app_user_info);

        init();
        initialData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initialData();
    }

    public void onAppUserInfoFlowers(View view){
        Intent intent = null;
        if(CustomApplication.getInstance().isLogin()) {
            intent = new Intent(this, SendFlowersActivity.class);
            intent.putExtra("type", "appUserInfo");
            intent.putExtra("balloonId", userId + "");
            intent.putExtra("head", head);

        }else{
            intent = new Intent(this, Activity_Login.class);
        }
        startActivity(intent);
    }
    private void init() {
        initTitleView();
        setTitleName("用户信息");

        userId = getIntent().getLongExtra("memberId",(long)0);

        headImg = (CustomHeadView) findViewById(R.id.user_info_head_icon);
//        userName = (TextView) findViewById(R.id.user_info_user_name);
        chatBtn = (TextView) findViewById(R.id.user_info_user_button);
        focusBtn = (TextView) findViewById(R.id.tv_app_user_info_focus);
        time = (TextView) findViewById(R.id.user_info_last_login_time);
        doSendTask = (TextView) findViewById(R.id.user_info_throwNum);

        receivedNum = (TextView) findViewById(R.id.user_info_thrownum_fee);
        receivedRate = (TextView) findViewById(R.id.user_info_user_percent);
        takePhotos = (TextView) findViewById(R.id.user_info_user_takePhoto);
        beenReceivedRate = (TextView) findViewById(R.id.user_info_adoptedRate);
        beenReceiveFlowers = (TextView) findViewById(R.id.tv_app_user_info_flowers);
        tvTitle = (TextView) findViewById(R.id.tv_app_user_info_title);

        balloonNum = (TextView) findViewById(R.id.tv_app_user_info_balloon);
        followNum = (TextView) findViewById(R.id.tv_app_user_info_Follow);
        beFollowNum = (TextView) findViewById(R.id.tv_app_user_info_be_follow);

        tvUserNames = (TextView) findViewById(R.id.tv_app_user_info_name);
        tvCertification = (TextView) findViewById(R.id.tv_app_user_info_certification);
        tvIntroduction = (TextView) findViewById(R.id.tv_app_user_info_introduction);

        btnContainer = (LinearLayout) findViewById(R.id.btn_container);
    }
    public void onBalloon(View view){
        Intent intent = new Intent(this, BalloonRecommendAndMoreActivity.class);
        intent.putExtra("type","appUserInfo");
        intent.putExtra("memberId",userId);
        intent.putExtra("nickName",nickName);
        startActivity(intent);
    }
    public void onFollow(View view){
        Intent intent = new Intent(this, PayAttentionToActivity.class);
        intent.putExtra("memberId",userId);
        startActivity(intent);
    }
    public void onBeFollow(View view){
        Intent intent = new Intent(this, FansActivity.class);
        intent.putExtra("type","sendBalloon");
        intent.putExtra("memberId",userId);
        startActivity(intent);
    }

    public void onFocus(TextView view){
        if (subItem.getResult().isFollow()) {
            view.setText("已关注");
        } else {
            view.setText("关注");
        }

    }
    private void initialData() {
        HashMap<String,String> map =  new HashMap<>();
        map.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)-1)+"");
        map.put("userId", userId +"");
        map.put("lng","");
        map.put("lat","");
        map.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        map.put("deviceType","android");

//        for(Map.Entry<String,String> entry:map.entrySet()){
//            Log.d(getClass().getName(),entry.getKey()+":"+entry.getValue());
//        }

        OkHttpClientManager.postAsyn(HttpUrlConstant.taskMemberUserInfo, new OkHttpClientManager.ResultCallback<AppUserInfoNewBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(AppUserInfoNewBean response) {
                Log.d(getClass().getName(),new Gson().toJson(response));
                if(response.getStatusCode() == 1) {
                    subItem = response;
                    userId = response.getResult().getMemberId();
                    authType = response.getResult().getAuthType();
                    head = response.getResult().getHeadImg();
                    nickName = response.getResult().getNickName();
                    initialView(response);
                }else if(response.getStatusCode() == -999){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }
        },map);

    }

    private void initialView(final AppUserInfoNewBean bean) {

        if(bean.getResult().getMemberId() != SharePreferencesUtils.getLong(this,SharePrefConstant.MEMBER_ID,(long)0)){
            btnContainer.setVisibility(View.VISIBLE);
        }
        if(bean.getResult().getHeadImg() != null) {
            headImg.showHeadView(bean.getResult().getHeadImg(), bean.getResult().getAuthType(), bean.getResult().getMemberId(), false);
//        userName.setText(bean.getResult().getNickName());
        }
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(CustomApplication.getInstance().isLogin()){
                    if(bean.getResult().isCanChat()) {
                        Intent intent = new Intent(AppUserInfo.this, ChatActivity.class);
                        intent.putExtra("currentId", userId + "");
                        intent.putExtra("type", authType + "");
                        intent.putExtra("name", bean.getResult().getNickName());
                        startActivity(intent);
                    }else{


                        new AlertView(null, "\n未进行过交易无法聊天,送花可以聊天", null, null, new String[]{"取消", "送花"},
                                AppUserInfo.this, AlertView.Style.Alert, new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position) {
                                switch (position){
                                    case 0:

                                        break;
                                    case 1:
                                        Intent intent = new Intent(AppUserInfo.this, SendFlowersActivity.class);
                                        intent.putExtra("type","appUserInfo");
                                        intent.putExtra("balloonId",userId+"");
                                        intent.putExtra("head",head);
                                        startActivity(intent);
                                        break;
                                }
                            }
                        }).setCancelable(true).show();
                    }

                }else{
                    Intent intent = new Intent(AppUserInfo.this, Activity_Login.class);
                    startActivity(intent);
                }
            }
        });

        time.setText(MyTimeUtil.formatTimeZh(bean.getResult().getLastLoginTime()));
        doSendTask.setText(bean.getResult().getThrowNum()+"次"+"(预付"+bean.getResult().getThrowFee()+")");
        receivedNum.setText(bean.getResult().getUsingNum()+"次"+"(支出"+bean.getResult().getUsingFee()+")");
        receivedRate.setText(bean.getResult().getUsingPercentage());
        takePhotos .setText(bean.getResult().getReceiveNum()+"次"+"(收入"+bean.getResult().getReceiveFee()+")");
        beenReceivedRate.setText(bean.getResult().getAdoptedRate());
        beenReceiveFlowers.setText("已收到"+bean.getResult().getFlowersNum()+"朵鲜花");
        balloonNum.setText(String.valueOf(bean.getResult().getBalloonNum()));
        followNum.setText(String.valueOf(bean.getResult().getFollowNum()));
        beFollowNum.setText(String.valueOf(bean.getResult().getBeFollowNum()));
        tvUserNames.setText(bean.getResult().getNickName());
        tvCertification.setText(bean.getResult().getAuthName());
        tvIntroduction.setText(bean.getResult().getIntroduction());
        tvTitle.setText("送花给"+bean.getResult().getNickName());
        if(bean.getResult().isFollow()){
            focusBtn.setText("已关注");
        }else{
            focusBtn.setText("关注");
        }
        focusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CustomApplication.getInstance().isLogin()){
                    HashMap<String,String> params = buildRequestParams(bean);
                    if(!subItem.getResult().isFollow()){
                        addFollow(params);
                    }else {
                        delFollow(params);

                    }
                }else{
                    Intent intent = new Intent(AppUserInfo.this,Activity_Login.class);
                    AppUserInfo.this.startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onDestroy();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Glide.get(AppUserInfo.this).pauseRequest();
        headImg = null;
    }

    private void delFollow(final HashMap<String, String> params) {

        new AlertView("确定取消关注吗？", null, "取消", new String[]{"取消关注"},null, this, AlertView.Style.ActionSheet,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, final int position) {
                        OkHttpClientManager.postAsyn(HttpUrlConstant.balloonFollowCancel, new OkHttpClientManager.ResultCallback<BaseResultBean>() {
                            @Override
                            public void onError(Request request, Exception e) {
                                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                            }

                            @Override
                            public void onResponse(BaseResultBean response) {
                                        if(1 == response.getStatusCode()){
                                            focusBtn.setText("关注");
                                            subItem.getResult().setFollow(false);
//                                            lists.get(dataPosition).setFollow(false);
//                                            holder.Watcher.setText("关注");
//                                            notifyDataSetChanged();
                                        }else if(response.getStatusCode() == -999){
                                            exitApp();
                                        }else
                                            ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                            }
                        },params);
                    }
                }).setCancelable(true).show();

    }

    private void addFollow(HashMap<String, String> params) {
        OkHttpClientManager.postAsyn(HttpUrlConstant.balloonFollowAdd, new OkHttpClientManager.ResultCallback<BaseResultBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(BaseResultBean response) {
                        if(1 == response.getStatusCode()){
//                            lists.get(position).setFollow(true);
                            subItem.getResult().setFollow(true);
                              focusBtn.setText("已关注");
//                            notifyDataSetChanged();
                        }else if(response.getStatusCode() == -999){
                            exitApp();
                        }else
                            ToastManager.show(getApplicationContext(),"噢，网络不给力！");

            }
        },params);
    }

    private HashMap<String,String> buildRequestParams(AppUserInfoNewBean subItem) {
        HashMap<String,String> params = new HashMap<>();
        params.put("followUserId",subItem.getResult().getMemberId()+"");
        params.put("memberId",SharePreferencesUtils.getLong(this,SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lat","");
        params.put("lng","");
        params.put("clientId",SharePreferencesUtils.getString(this,SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");
        return  params;
    }
}