package com.horem.parachute.mine;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.cjj.Util;
import com.common.HttpUrlConstant;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.mine.bean.MessageBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.horem.parachute.util.Utils;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SettingAboutParachute extends BaseActivity implements View.OnClickListener{

     static final int CONTRACT_FLAG = 1;
     static final int GUID_FLAG = 2;

    private TextView versioncode;

    private CustomApplication application;
    private Tracker mTracker;
    private static final String TAG = "AboutParachute";
    private int shareType;
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
        setContentView(R.layout.activity_setting_about_parachute);
        init();
    }

    private void init() {
        initTitleView();
        setTitleName("关于伞来了");
        versioncode = (TextView) findViewById(R.id.parachute_versioncode);
        versioncode.setText("伞来了"+Utils.getVersionName(this));

        findViewById(R.id.about_parachute_contracts).setOnClickListener(this);
        findViewById(R.id.about_parachute_use).setOnClickListener(this);
        findViewById(R.id.about_parachute_share).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.about_parachute_contracts:
                intent = new Intent(this,UserGuidActivity.class);
                intent.putExtra("value",CONTRACT_FLAG);
                break;
            case R.id.about_parachute_use:
                intent = new Intent(this,UserGuidActivity.class);
                intent.putExtra("value",GUID_FLAG);
                break;
            case R.id.about_parachute_share:
                new AlertView(null, null, "取消", null,new String[]{"分享到微信朋友圈", "分享到微信朋友"}, this, AlertView.Style.ActionSheet,
                        new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position) {
                                switch (position){
                                    case 0:
                                        sendToFriendCommunity(true);
                                        shareType = 10;
                                        break;
                                    case 1:
                                        sendToFriendCommunity(false);
                                        shareType = 20;
                                        break;
                                }
                            }
                        }).setCancelable(true).show();
                break;
        }
        if(null != intent){
            startActivity(intent);
        }


    }

    private void sendToFriendCommunity(final boolean shareType) {
        HashMap<String,String> params = new HashMap<>();
        params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lng","");
        params.put("lat","");
        params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");
        OkHttpClientManager.postAsyn(HttpUrlConstant.getShareAppUrl, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
//                Log.d(getClass().getName(),response);
                SharePreferencesUtils.setBoolean(SettingAboutParachute.this, SharePrefConstant.iswxLon,false);
                String webUrl = "";
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getInt("statusCode") == 1) {
                        webUrl = jsonObject.getString("result");
                        WXWebpageObject   webpage = new WXWebpageObject();
                        webpage.webpageUrl = webUrl;

                        WXMediaMessage msg = new WXMediaMessage(webpage);
                        msg.title = "伞来了-即时多媒体交易平台"+"伞来了是一款即时多媒体交易平台，全世界亿万用户的分享经济平台。";
//                msg.description = "伞来了是一款即时多媒体交易平台，全世界亿万用户的分享经济平台。";
                        Bitmap thump = BitmapFactory.decodeResource(getResources(),R.mipmap.logo_80);
                        msg.thumbData = Utils.bmpToByteArray(thump,true);

                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = buildTransaction("webpage");

                        req.message = msg;
                        req.scene = shareType ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
                        CustomApplication.api.sendReq(req);
                    }else if(jsonObject.getInt("statusCode ") == -999){
                        exitApp();
                    }else
                        ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },params);

    }
    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(SharePreferencesUtils.getBoolean(getApplicationContext(),SharePrefConstant.isNeedConfirmShare,false)){
            SharePreferencesUtils.setBoolean(getApplicationContext(),SharePrefConstant.isNeedConfirmShare,false);
            sendShareInfoToServe();
        }

    }

    private void sendShareInfoToServe() {
        HashMap<String,String> params = new HashMap<>();
        params.put("shareToType",shareType+"");
        params.put("memberId",SharePreferencesUtils.getLong(this,SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lng","");
        params.put("lat","");
        params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");


        OkHttpClientManager.postAsyn(HttpUrlConstant.ShareAppUrl, new OkHttpClientManager.ResultCallback<MessageBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(MessageBean response) {
                if(1 == response.getStatusCode()){
                    ToastManager.show(SettingAboutParachute.this,response.getMessage());
                }else if(response.getStatusCode() == -999){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }
        },params);
    }
}
