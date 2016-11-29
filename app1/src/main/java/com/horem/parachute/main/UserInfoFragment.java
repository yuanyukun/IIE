package com.horem.parachute.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.common.HttpUrlConstant;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.balloon.BalloonRecommendAndMoreActivity;
import com.horem.parachute.balloon.FansActivity;
import com.horem.parachute.balloon.FlowersHaveSendActivity;
import com.horem.parachute.balloon.PayAttentionToActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.common.CustomLoading;
import com.horem.parachute.customview.CustomHeadView;
import com.horem.parachute.mine.Activity_Mine_Account;
import com.horem.parachute.mine.Activity_Mine_Money;
import com.horem.parachute.mine.Activity_Mine_Photos;
import com.horem.parachute.mine.Activity_Mine_See_Me;
import com.horem.parachute.mine.Activity_Mine_Task_Received;
import com.horem.parachute.mine.Activity_Mine_Task_send;
import com.horem.parachute.mine.Activity_Setting;
import com.horem.parachute.mine.GiveLikeMeListActivity;
import com.horem.parachute.mine.bean.MessageBean;
import com.horem.parachute.mine.bean.MineInfoBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.http.request.HttpApi;
import com.http.request.IResponseApi;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuanyukun on 2016/6/17.
 */
public class UserInfoFragment extends Fragment implements View.OnClickListener{

    private ScrollView rootView;
    private TextView titleName;
    private TextView titleBack;
    private TextView titleNext;

    private CustomHeadView Account_photo;
    private TextView  Account_name;
    private CustomApplication application;

    private ImageView mySubTaskdot;
    private ImageView myTaskOrderdot;
    private ImageView fansRedDot;

    private TextView flowersNum;
    private TextView followedNum;
    private TextView fansNum;
    private TextView likesNum;


    private  myMessageReceiver myReceiver;
    private CustomLoading customLoadingDialog;
    private static final int HAVE_GIVE_LIKE = 0x10;

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getUserInfo();
            // load data here
        }else{
            // fragment is no longer visible
//            rootView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_mine, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

            initialView(view);

    }
    private void initialView(View view) {

        titleName = (TextView) view.findViewById(R.id.title_name);
        titleBack = (TextView) view.findViewById(R.id.title_back);
        titleNext = (TextView) view.findViewById(R.id.title_next);
        titleBack.setVisibility(View.INVISIBLE);
        titleNext.setVisibility(View.INVISIBLE);
        titleName.setText("我");

        rootView = (ScrollView) view.findViewById(R.id.root_scroll_view);
        flowersNum = (TextView) view.findViewById(R.id.tv_user_info_flowers_flower_num);
        followedNum = (TextView) view.findViewById(R.id.tv_user_info_flowers_attention_num);
        fansNum = (TextView) view.findViewById(R.id.tv_user_info_flowers_fans_num);
        likesNum = (TextView) view.findViewById(R.id.tv_user_info_give_like_num);

        Account_photo = (CustomHeadView) view.findViewById(R.id.mine_user_photo);
        Account_name  = (TextView) view.findViewById(R.id.mine_user_name);
        mySubTaskdot = (ImageView) view.findViewById(R.id.my_subtask_dot);
        myTaskOrderdot = (ImageView) view.findViewById(R.id.my_task_order_dot);
        fansRedDot = (ImageView) view.findViewById(R.id.fans_num_red_dot);

        updateUI();
        view.findViewById(R.id.mine_rl_been_seen).setOnClickListener(this);
        view.findViewById(R.id.mine_rl_get).setOnClickListener(this);
        view.findViewById(R.id.mine_rl_go_back).setOnClickListener(this);
        view.findViewById(R.id.mine_rl_money).setOnClickListener(this);
        view.findViewById(R.id.mine_rl_photos).setOnClickListener(this);
        view.findViewById(R.id.mine_rl_settings).setOnClickListener(this);
        view.findViewById(R.id.mine_rl_account).setOnClickListener(this);
        view.findViewById(R.id.rl_user_info_balloon_send).setOnClickListener(this);
        view.findViewById(R.id.mine_rl_send).setOnClickListener(this);

        view.findViewById(R.id.rl_user_info_send_flowers).setOnClickListener(this);
        view.findViewById(R.id.rl_user_info_care_about).setOnClickListener(this);
        view.findViewById(R.id.rl_user_info_fans).setOnClickListener(this);
        view.findViewById(R.id.rl_user_info_give_like).setOnClickListener(this);

    }
    private void updateUI() {
        application = (CustomApplication) getActivity().getApplication();
        if(application.isSubTaskList){
            mySubTaskdot.setVisibility(View.VISIBLE);
        }else
            mySubTaskdot.setVisibility(View.INVISIBLE);
        if(application.isTaskOrderList){
            myTaskOrderdot.setVisibility(View.VISIBLE);
        }else myTaskOrderdot.setVisibility(View.INVISIBLE);

        if(application.beFollowList){
            fansRedDot.setVisibility(View.VISIBLE);
        }else{
            fansRedDot.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerCustomReceiver();
        getUserInfo();
    }

    @Override
    public void onClick(View view) {
        Intent goNextIntent = null;
        switch (view.getId()){
            //账户信息
            case R.id.mine_rl_account:
                goNextIntent = new Intent(getContext(),Activity_Mine_Account.class);
                break;
            //零钱
            case R.id.mine_rl_money:
                goNextIntent = new Intent(getContext(),Activity_Mine_Money.class);
                break;
            //相册
            case R.id.mine_rl_photos:
                goNextIntent = new Intent(getContext(),Activity_Mine_Photos.class);
                break;
            //我的气球

            case R.id.rl_user_info_balloon_send:
                goNextIntent = new Intent(getContext(),BalloonRecommendAndMoreActivity.class);
                goNextIntent.putExtra("type","sendBalloon");
                goNextIntent.putExtra("memberId",SharePreferencesUtils.getLong(getContext(),SharePrefConstant.MEMBER_ID,(long)0));
                break;
            //我投的伞
            case R.id.mine_rl_send:
                CustomApplication.getInstance().setSubTaskList(false);
                goNextIntent = new Intent(getContext(),Activity_Mine_Task_send.class);
                goNextIntent.setAction("com.horem.parachute.Status");
                CustomApplication.sendLocalBroadcast(goNextIntent);
                break;
            //我接的伞
            case R.id.mine_rl_get:
                CustomApplication.getInstance().setTaskOrderList(false);
                goNextIntent = new Intent(getContext(),Activity_Mine_Task_Received.class);
                goNextIntent.setAction("com.horem.parachute.Status");
                CustomApplication.sendLocalBroadcast(goNextIntent);
                break;
            //谁看过我
            case R.id.mine_rl_been_seen:
                goNextIntent = new Intent(getContext(),Activity_Mine_See_Me.class);
                break;
            //设置
            case R.id.mine_rl_settings:
                goNextIntent = new Intent(getContext(),Activity_Setting.class);
                break;
            //退出服务
            case R.id.mine_rl_go_back:
                exitServer();
                break;
            case R.id.rl_user_info_send_flowers:
                onSendFlowers();
                break;
            case R.id.rl_user_info_care_about:
                onCareAbout();
                break;
            case R.id.rl_user_info_fans:
                onFans();
                break;
            case R.id.rl_user_info_give_like:
                onGiveLike();
                break;
        }

        if(null != goNextIntent && view.getId() != R.id.mine_rl_go_back ){
            startActivity(goNextIntent);
        }
    }

    private void onGiveLike() {
        Intent intent = new Intent(getContext(),GiveLikeMeListActivity.class);
        intent.putExtra("flag", HAVE_GIVE_LIKE);
        startActivity(intent);
    }

    private void onFans() {
        CustomApplication.getInstance().setBeFollowList(false);
        Intent intent = new Intent(getContext(), FansActivity.class);
        intent.setAction("com.horem.parachute.Status");
        CustomApplication.sendLocalBroadcast(intent);
        intent.putExtra("memberId",SharePreferencesUtils.getLong(getContext(),SharePrefConstant.MEMBER_ID,(long)0));
        startActivity(intent);
    }

    private void onCareAbout() {
        if(Integer.valueOf(followedNum.getText().toString()) > 0) {
            Intent intent = new Intent(getContext(), PayAttentionToActivity.class);
            intent.putExtra("memberId",SharePreferencesUtils.getLong(getContext(),SharePrefConstant.MEMBER_ID,(long)0));
            startActivity(intent);
        }
    }

    private void onSendFlowers() {
        Intent intent = new Intent(getContext(), FlowersHaveSendActivity.class);
        startActivity(intent);
    }

    private void getUserInfo() {
        startLoading();
        HashMap<String,String> params = new HashMap<>();
        params.put("memberId", SharePreferencesUtils.getLong(getContext(), SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lng","");
        params.put("lat","");
        params.put("clientId", SharePreferencesUtils.getString(getContext(), SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");
        for(Map.Entry<String,String> entry:params.entrySet()){
            Log.d("参数：",entry.getKey()+":"+entry.getValue());
        }
        OkHttpClientManager.postAsyn(HttpUrlConstant.memberDetailUrl, new OkHttpClientManager.ResultCallback<MineInfoBean>() {
            @Override
            public void onError(Request request, Exception e) {
                stopLoading();
                ToastManager.show(getContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(MineInfoBean response) {
                stopLoading();
                Log.d("获取用户信息",new Gson().toJson(response));
                if(response.getStatusCode() == 1) {
                    rootView.setVisibility(View.VISIBLE);
                    Account_name.setText(response.getResult().getNickName());
                    Account_photo.showHeadView(response.getResult().getHeadImg(),response.getResult().getAuthType(),
                            response.getResult().getMemberId(),true);

                    fansNum.setText(response.getResult().getBeFollowNum()+"");
                    followedNum.setText(response.getResult().getFollowNum()+"");
                    flowersNum.setText(response.getResult().getFlowersNum()+"");
                    likesNum.setText(response.getResult().getMyGiveLikeNum()+"");
                }else if(response.getStatusCode() == -999){
                    HttpApi httpApi = new ExitSystemHttpImpl();
                    httpApi.httpRequest(getContext(), new IResponseApi() {
                        @Override
                        public void onSuccess(Object object) {

                        }

                        @Override
                        public void onFailed(Exception e) {

                        }
                    },new HashMap<String, String>());
                }else
                    ToastManager.show(getContext(),"噢，网络不给力！");

            }
        },params);
    }

    private void exitServer() {

        new AlertView(null, null, "取消", null, new String[]{"确认退出"}, getContext(), AlertView.Style.ActionSheet, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                        startLoading();
                        HashMap<String,String> params = new HashMap<>();
                        params.put("memberId",SharePreferencesUtils.getLong(getContext(),SharePrefConstant.MEMBER_ID,(long)0)+"");
                        params.put("lng","");
                        params.put("lat","");
                        params.put("clientId", SharePreferencesUtils.getString(getContext(), SharePrefConstant.INSTALL_CODE,""));
                        params.put("deviceType","android");

                        params.put("channelId",SharePreferencesUtils.getString(getContext(),SharePrefConstant.CHANNEL_ID,""));
                        params.put("userId",SharePreferencesUtils.getString(getContext(),SharePrefConstant.USER_ID,""));

                        OkHttpClientManager.postAsyn(HttpUrlConstant.LogoutUrl, new OkHttpClientManager.ResultCallback<MessageBean>() {
                            @Override
                            public void onError(Request request, Exception e) {
                                ToastManager.show(getContext(),"噢，网络不给力！");
                            }

                            @Override
                            public void onResponse(MessageBean response) {

                                if(response.getStatusCode() == 1){
                                    ToastManager.show(getContext(),response.getMessage());
                                    SharePreferencesUtils.setString(getContext(), SharePrefConstant.MEMBER_NAME,"");
                                    SharePreferencesUtils.setLong(getContext(),SharePrefConstant.MEMBER_ID,(long)0);
                                    SharePreferencesUtils.setBoolean(getContext(),SharePrefConstant.USER_LOGIN,false);
                                    application.setLogin(false);

                                    stopLoading();
                                    Intent intent = new Intent(getContext(),AppMainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }else
                                    ToastManager.show(getContext(),"噢，网络不给力！");
                                stopLoading();
                            }
                        },params);
            }
        }).setCancelable(true).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public class myMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
//            Log.d("个人用户信息收到广播",action);
            if (action.equals("com.horem.parachute.Status")) {
                //updateUI here
                updateUI();
            }
        }
    }
    private void registerCustomReceiver() {
        if(myReceiver == null) {
            myReceiver = new myMessageReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.horem.parachute.Status");
        //注册一个接收的广播
        CustomApplication.registerLocalReceiver(myReceiver,filter);
//        getActivity().registerReceiver(myReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        CustomApplication.unregisterLocalReceiver(myReceiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * 进度加载动画
     */
    protected void startLoading(){
        if(customLoadingDialog == null){
            customLoadingDialog = CustomLoading.CreateLoadingDialog(getContext());
            customLoadingDialog.setMessage("正在加载...");
            customLoadingDialog.setCanceledOnTouchOutside(false);//不允许点击取消

            Window wd = customLoadingDialog.getWindow();
            WindowManager.LayoutParams lp = wd.getAttributes();
            lp.alpha = 0.5f;
            wd.setAttributes(lp);
        }
        customLoadingDialog.show();
    }  /**
     * 进度加载动画
     */
    protected void startLoading(String msg){
        if(customLoadingDialog == null){
            customLoadingDialog = CustomLoading.CreateLoadingDialog(getContext());
            customLoadingDialog.setMessage(msg);
            customLoadingDialog.setCanceledOnTouchOutside(false);//不允许点击取消

            Window wd = customLoadingDialog.getWindow();
            WindowManager.LayoutParams lp = wd.getAttributes();
            lp.alpha = 0.5f;
            wd.setAttributes(lp);
        }
        customLoadingDialog.show();
    }
    /**
     * 关闭加载动画
     */
    protected void stopLoading(){
        if(customLoadingDialog != null){
            customLoadingDialog.dismiss();
            customLoadingDialog.dismiss();
        }
    }
}
