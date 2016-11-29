package com.horem.parachute.mine;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.app.utils.SignatureTool;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.common.HttpUrlConstant;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.balloon.Bean.BaseResultBean;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.mine.bean.MessageBean;
import com.horem.parachute.mine.bean.MyChange;
import com.horem.parachute.mine.bean.MyChangeBean;
import com.horem.parachute.mine.httpImpl.MyChangeImpl;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.http.request.HttpApi;
import com.http.request.IResponseApi;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import java.util.HashMap;

/**
 * create by yuanyukun 2016/3/18
 */
public class Activity_Mine_Money extends BaseActivity {

    private static final String url = HttpUrlConstant.taskWithdrawalWeChatUrl;
    private HashMap<String,String> map;
    private TextView myChange;

    private CustomApplication application;
    private Tracker mTracker;
    private static final String TAG = "AtyMineMoney";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_parachute);
        init();
        initMyChangeData();
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
    private void init() {
        initTitleView();
        setTitleName("零钱");
        setRightButtonText("零钱明细");

        map = new HashMap<>();

        myChange = (TextView) findViewById(R.id.tv_my_chanage);
        findViewById(R.id.my_change_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertView(null,null,"取消",null,new String[]{"微信支付提现"},
                       Activity_Mine_Money.this,AlertView.Style.ActionSheet,new OnItemClickListener(){

                    @Override
                    public void onItemClick(Object o, int position) {
                        switch (position){

                            case 0:
                                translateMoney();
                                break;
                        }
                    }
                }).setCancelable(true).show();
            }
        });
    }

    //零钱提现
    private void translateMoney() {
        long userId = SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0);
        MyChangeBean bean = new MyChangeBean();
        bean.setMemberId(userId);
        HashMap<String,String> map = new HashMap<>();
        map.put("sign", SignatureTool.getSignStr(bean));

        map.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        map.put("lng","");
        map.put("lat","");
        map.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        map.put("deviceType","android");
        OkHttpClientManager.postAsyn(url, new OkHttpClientManager.ResultCallback<BaseResultBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(BaseResultBean response) {
                Log.d(getClass().getName(),new Gson().toJson(response));
                if(response.getStatusCode() == 1)
                     myChange.setText("￥0.00");
                else if(response.getStatusCode() == -999){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),response.getMessage());
            }
        },map);

    }

    @Override
    protected void OnRightButtonClicked() {
        Intent intent = new Intent();
        intent.setClass(this,Activity_Mine_Money_Details.class);
        startActivity(intent);
    }
    //获取我的零钱余额
    private void initMyChangeData() {
        HttpApi api = new MyChangeImpl();
        api.httpRequest(Activity_Mine_Money.this, new IResponseApi() {
            @Override
            public void onSuccess(Object object) {

                MyChange bean = (MyChange) object;
                myChange.setText(bean.getResult());
            }

            @Override
            public void onFailed(Exception e) {
                exitApp();
            }
        },map);
    }

}
