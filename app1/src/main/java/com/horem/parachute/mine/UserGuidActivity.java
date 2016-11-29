package com.horem.parachute.mine;

import android.app.Activity;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.common.HttpUrlConstant;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.horem.parachute.R;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class UserGuidActivity extends BaseActivity {
    static final int CONTRACT_FLAG = 1;
    static final int GUID_FLAG = 2;
    static final int PROMOTION_FLAG= 3;
    private String url = "";

    private WebView webView;
    private ProgressBar mProgressBar;

    private CustomApplication application;
    private Tracker mTracker;
    private static final String TAG = "UserGuidAty";
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
        setContentView(R.layout.activity_user_guid);
        init();
//        initialDate();
    }

    private void initialDate() {
        HashMap<String,String> map = new HashMap<>();
        map.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        map.put("lat","");
        map.put("lng","");
        map.put("clientId",SharePreferencesUtils.getString(this,SharePrefConstant.INSTALL_CODE,""));
        map.put("deviceType","android");


        OkHttpClientManager.postAsyn(url, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力");
            }

            @Override
            public void onResponse(String response) {
                Log.d(getClass().getName(),response);
                try {
                    JSONObject json = new JSONObject(response);
                    String url = json.getString("result");
                    webView.loadUrl(url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },map);
    }

    private void init() {
        initTitleView();
        setLeftButtonText("关于伞来了");

        webView = (WebView) findViewById(R.id.web_view);

        if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT < 17) {
            try {
                webView.removeJavascriptInterface("searchBoxJavaBridge_");
                webView.removeJavascriptInterface("accessibility");
                webView.removeJavascriptInterface("accessibilityTraversal");
            } catch (Throwable tr) {
                tr.printStackTrace();
            }
        }

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.cancel();//默认的处理方式
                //handler.proceed();//忽略错误继续loading

            }
        });
        WebSettings webSettings = webView.getSettings();
        //设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        mProgressBar = (ProgressBar) findViewById(R.id.my_progress_bar);
        webView.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mProgressBar.setProgress(newProgress);
                Log.d("userGuidActivity",newProgress+"");
            }
        });

        int type = getIntent().getIntExtra("value",0);
        switch (type){
            case CONTRACT_FLAG:
                setTitleName("用户服务协议");
                url = HttpUrlConstant.getClauseUrl;
                initialDate();
                break;
            case GUID_FLAG:
                setTitleName("操作指引");
                url = HttpUrlConstant.getGuideUrl;
                initialDate();
                break;
            case PROMOTION_FLAG:

                setLeftButtonText("发现");
                String  loadUrl = getIntent().getStringExtra("url");
                String name = getIntent().getStringExtra("name");
                setTitleName(name);
                webView.loadUrl(loadUrl);
                break;
        }


    }
}
