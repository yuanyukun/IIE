package com.horem.parachute.mine;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.common.HttpUrlConstant;
import com.horem.parachute.R;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.mine.bean.MessageBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import org.kymjs.chat.bean.Message;

import java.util.HashMap;

public class SexOptionActivity extends BaseActivity {

    private ImageView mark1;
    private ImageView mark2;

    private int sexId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sex_option);
        init();
    }

    private void init() {
        initTitleView();
        setLeftButtonText(SharePreferencesUtils.getString(this,SharePrefConstant.MEMBER_NAME,""));
        setTitleName("性别");
        setRightButtonText("保存");
        mark1 = (ImageView) findViewById(R.id.image_1);
        mark2 = (ImageView) findViewById(R.id.image_2);

        sexId = getIntent().getIntExtra("sex",0);
        switch (sexId){
            case 0:
                resetIcon();
                mark1.setVisibility(View.VISIBLE);
                break;
            case 1:
                resetIcon();
                mark2.setVisibility(View.VISIBLE);
                break;
        }
    }
    public void resetIcon(){
        mark1.setVisibility(View.INVISIBLE);
        mark2.setVisibility(View.INVISIBLE);
    }
    public void onMaleClicked(View view){
        resetIcon();
        mark1.setVisibility(View.VISIBLE);
        sexId = 0;
    }
    public void onFemaleClicked(View view){
        resetIcon();
        mark2.setVisibility(View.VISIBLE);
        sexId = 1;
    }

    @Override
    protected void OnRightButtonClicked() {
        super.OnRightButtonClicked();
        HashMap<String,String> params = new HashMap<>();
        params.put("sex",sexId+"");
        params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lng","");
        params.put("lat","");
        params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");
        OkHttpClientManager.postAsyn(HttpUrlConstant.memberSexEdit, new OkHttpClientManager.ResultCallback<MessageBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(MessageBean response) {
                if(response.getStatusCode() == 1){
                    SexOptionActivity.this.finish();
                }else if(response.getStatusCode() == -999){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }
        },params);
    }
}
