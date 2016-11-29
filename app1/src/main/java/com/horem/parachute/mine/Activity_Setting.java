package com.horem.parachute.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.horem.parachute.R;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.common.DataCleanManager;
import com.horem.parachute.util.ToastManager;
import com.horem.parachute.util.Utils;

/**
 * Created by user on 2015/9/16.
 */
public class Activity_Setting extends BaseActivity implements View.OnClickListener{
    private TextView showCacheSize;
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
        setContentView(R.layout.activity_setting);
        init();
    }

    private void init() {
        initTitleView();
        setTitleName("设置");

        findViewById(R.id.settings_ll_download).setOnClickListener(this);
        findViewById(R.id.settings_ll_about_parachute).setOnClickListener(this);
        findViewById(R.id.settings_ll_black_list).setOnClickListener(this);
        findViewById(R.id.settings_ll_clear_cache).setOnClickListener(this);
        findViewById(R.id.settings_ll_safety).setOnClickListener(this);
        findViewById(R.id.settings_ll_user_advices).setOnClickListener(this);

        showCacheSize = (TextView) findViewById(R.id.show_cache_size);
        showCacheSize.setText(String.valueOf(Utils.getTwoLastNumber(DataCleanManager.getCacheSize(Activity_Setting.this)))+"M");
    }

    @Override
    public void onClick(View view) {
        Intent goNextIntent = null;
        switch (view.getId()){
            case R.id.settings_ll_safety:
                goNextIntent = new Intent(this,Activity_Setting_Safety.class);
                break;
            case R.id.settings_ll_black_list:
                goNextIntent = new Intent(this,SettingBlackListActivity.class);
                break;
            case R.id.settings_ll_download:
                goNextIntent = new Intent(this,SettingDownLoadOptions.class);
                break;
            case R.id.settings_ll_clear_cache:

                Glide.get(this).clearMemory();
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        Glide.get(getApplicationContext()).clearDiskCache();
                        DataCleanManager.cleanApplicationData(Activity_Setting.this);
                    }
                }.start();
                showCacheSize.setText("");
                ToastManager.show(this,"清除缓存成功");
                break;
            case R.id.settings_ll_user_advices:
                //跳转到伞来了回话界面
                Intent  intent = new Intent();
                intent.setClass(Activity_Setting.this,ChatActivity.class);
                intent.putExtra("currentId",29+"");
                intent.putExtra("type",10+"");
                intent.putExtra("name","伞来了团队");
                startActivity(intent);
                break;
            case R.id.settings_ll_about_parachute:
                goNextIntent = new Intent(this,SettingAboutParachute.class);
                break;
        }
        if(null != goNextIntent){
            startActivity(goNextIntent);
        }
    }
}
