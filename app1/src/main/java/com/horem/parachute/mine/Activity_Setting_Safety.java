package com.horem.parachute.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.common.HttpUrlConstant;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.common.ApplicationConstant;
import com.horem.parachute.balloon.Bean.BaseResultBean;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.mine.bean.AcccountSaftyBean;
import com.horem.parachute.mine.bean.MineInfoBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;
import com.tencent.mm.sdk.modelmsg.SendAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Activity_Setting_Safety extends BaseActivity implements View.OnClickListener{

    private TextView tvPhone;
    private TextView tvEmail;

    private ImageView arrowMark;
    private TextView weixinBind;

    private String userPhoneNum;
    private String userEmailAddress;

    private boolean isBindWeChat;

    private CustomApplication application;
    private Tracker mTracker;
    private static final String TAG = "SettingSafe";
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
        setContentView(R.layout.activity_activty__setting__safety);

        init();
    }

    private void init() {
        initTitleView();
        setTitleName("账户安全");

        findViewById(R.id.safety_code).setOnClickListener(this);
        findViewById(R.id.safety_phone_number).setOnClickListener(this);
        findViewById(R.id.safety_email).setOnClickListener(this);

        tvEmail = (TextView) findViewById(R.id.tv_safety_email);
        tvPhone = (TextView) findViewById(R.id.tv_safety_phone);

        arrowMark = (ImageView) findViewById(R.id.mark2);
        weixinBind = (TextView) findViewById(R.id.safety_weixin_account);
        getUserInfo();
    }

    private void getUserInfo() {
        HashMap<String,String> params = new HashMap<>();
        params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lng","");
        params.put("lat","");
        params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");
        OkHttpClientManager.postAsyn(HttpUrlConstant.memberDetailUrl, new OkHttpClientManager.ResultCallback<MineInfoBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(MineInfoBean response) {
                if(response.getStatusCode() == 1){
                        userPhoneNum = response.getResult().getPhone();
                        userEmailAddress = response.getResult().getEmail();

                        tvEmail.setText(userEmailAddress);
                        tvPhone.setText(userPhoneNum);

                        if(response.getResult().isBindWeChat()){
                            isBindWeChat  = true;
                            weixinBind.setClickable(false);
                            weixinBind.setText("已绑定");
                            arrowMark.setVisibility(View.INVISIBLE);
                        }else{
                                isBindWeChat = false;
                                weixinBind.setText("未绑定");
                                findViewById(R.id.rl_weixin_info).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(!isBindWeChat) {
                                            new AlertView(null, null, "取消", null, new String[]{"绑定"}, Activity_Setting_Safety.this, AlertView.Style.ActionSheet, new OnItemClickListener() {
                                                @Override
                                                public void onItemClick(Object o, int position) {
                                                    switch (position) {
                                                        case 0:
                                                            getWeiXinAuth();
                                                            break;
                                                        case 1:
                                                            break;
                                                    }

                                                }
                                            }).setCancelable(true).show();
                                        }
                                    }
                                });

                        }
                }else if(response.getStatusCode() == -999){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }
        },params);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(isBindWeChat && SharePreferencesUtils.getBoolean(this,SharePrefConstant.wx_callback_OK,false)){
            Log.d("绑定微信接口","开始执行");
            SharePreferencesUtils.setBoolean(this,SharePrefConstant.iswxBinding,false);
            isBindWeChat = false;
            bindWeChat();
        }
        getUserInfo();
    }

    private void getWeiXinAuth() {
        if(CustomApplication.api == null){

            Log.i(getClass().getName(),"微信api对象为空");
        }
        if(!CustomApplication.api.isWXAppInstalled())
        {
            ToastManager.show(this,"检测到您的手机未安装微信客户端");
            return;
        }
        isBindWeChat = true;
        SharePreferencesUtils.setBoolean(this,SharePrefConstant.iswxBinding,true);
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "com_horem_parachute";
        CustomApplication.api.sendReq(req);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void bindWeChat() {
//        memberWeChatBindUrl
            HashMap<String,String> params = new HashMap<>();
            params .put("memberId",SharePreferencesUtils.getLong(this,SharePrefConstant.MEMBER_ID,(long)-1)+"");
            params.put("code",SharePreferencesUtils.getString(this,SharePrefConstant.WX_CODE,"")); params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
            params.put("lng","");
            params.put("lat","");
            params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
            params.put("deviceType","android");
            Log.d("bindWeChat","params");
            for(Map.Entry<String,String> entry: params.entrySet()){
                Log.d(getClass().getName(),entry.getKey()+": "+entry.getValue());
            }

        OkHttpClientManager.postAsyn(HttpUrlConstant.memberWeChatBindUrl, new OkHttpClientManager.ResultCallback<BaseResultBean>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(BaseResultBean response) {
                Log.d("绑定微信接口返回结果",new Gson().toJson(response));
                        if(1 == response.getStatusCode()) {
                            weixinBind.setText("已绑定");
                            isBindWeChat = true;
                        }else if(response.getStatusCode() == -999)
                            exitApp();
                        else
                            ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }
        },params);

    }

    @Override
    public void onClick(View view) {

        Intent goNextIntent = null;
        switch (view.getId()){
            case R.id.safety_code:
                goNextIntent = new Intent(this,Activity_Setting_Safety_Modify_Code.class);
                break;
            case R.id.safety_phone_number:
                goNextIntent = new Intent(this,Activity_Mine_Modify.class);
                goNextIntent.putExtra("ModifyType",ApplicationConstant.MODIFY_PHONE);
                goNextIntent.putExtra("value",userPhoneNum);
                break;
            case R.id.safety_email:
                goNextIntent = new Intent(this,Activity_Mine_Modify.class);
                goNextIntent.putExtra("ModifyType", ApplicationConstant.MODIFY_EMAIL);
                goNextIntent.putExtra("value",userEmailAddress);
                break;
        }
        if(null != goNextIntent){
            startActivity(goNextIntent);
        }
    }
}
