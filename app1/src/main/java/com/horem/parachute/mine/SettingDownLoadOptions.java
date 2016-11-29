package com.horem.parachute.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.horem.parachute.R;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;

public class SettingDownLoadOptions extends BaseActivity implements View.OnClickListener{
    private ImageView img3G;
    private ImageView imgWifi;

    private CustomApplication application;
    private Tracker mTracker;
    private static final String TAG = "DownloadOption";
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
        setContentView(R.layout.activity_setting_down_load_options);

        init();
    }

    private void init() {
        initTitleView();
        setTitleName("下载设置");

        findViewById(R.id.download_3g).setOnClickListener(this);
        findViewById(R.id.download_wifi).setOnClickListener(this);

        img3G = (ImageView) findViewById(R.id.image_1);
        imgWifi = (ImageView) findViewById(R.id.image_2);


        resetIcon();
       if(SharePreferencesUtils.getBoolean(this,SharePrefConstant.DOWNLOAD_3G,false)) {
           img3G.setVisibility(View.VISIBLE);
           SharePreferencesUtils.setBoolean(this, SharePrefConstant.DOWNLOAD_3G,true);
       }else{
           imgWifi.setVisibility(View.VISIBLE);
           SharePreferencesUtils.setBoolean(this, SharePrefConstant.DOWNLOAD_3G,false);
       }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.download_3g:
                resetIcon();
                img3G.setVisibility(View.VISIBLE);
                SharePreferencesUtils.setBoolean(this, SharePrefConstant.DOWNLOAD_3G,true);
                break;
            case R.id.download_wifi:
                resetIcon();
                imgWifi.setVisibility(View.VISIBLE);
                SharePreferencesUtils.setBoolean(this, SharePrefConstant.DOWNLOAD_3G,false);
                break;
        }
    }
    protected  void resetIcon(){
        img3G.setVisibility(View.INVISIBLE);
        imgWifi.setVisibility(View.INVISIBLE);
    }
}
