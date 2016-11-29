package com.horem.parachute.mine;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.common.HttpUrlConstant;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.mine.bean.MessageBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import java.util.HashMap;

public class SingleLineTextModifyActivity extends BaseActivity {

    private static final int HOMETOWN_ID = 0x101;
    private static final int WORKUNITE_ID = 0x102;
    private static final int SCHOOL_ID = 0x103;
    private static final int LOCATION_ID = 0x104;
    private int enterType;
    private String message;


    private TextView tvItemTips;
    private EditText etItemDescribe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_line_text_modify);

        init();
    }

    private void init() {
        initTitleView();
        setLeftButtonText(SharePreferencesUtils.getString(this,SharePrefConstant.MEMBER_NAME,""));
        setRightButtonText("保存");

        tvItemTips = (TextView) findViewById(R.id.tv_single_line_text_modify);
        etItemDescribe = (EditText) findViewById(R.id.et_single_line_text_modify);

        enterType = getIntent().getIntExtra("type",0);
        switch (enterType){
            case HOMETOWN_ID:
                setTitleName("故乡");
                message = getIntent().getStringExtra("home");
                break;
            case WORKUNITE_ID:
                setTitleName("工作单位");
                message = getIntent().getStringExtra("unit");

                break;
            case SCHOOL_ID:
                setTitleName("学校");
                message = getIntent().getStringExtra("school");
                break;
            case LOCATION_ID:
                setTitleName("所在地");
                message = getIntent().getStringExtra("loc");
                break;
        }
        if(message != null) {
            etItemDescribe.setText(message);
            etItemDescribe.setSelection(message.length());
        }
        etItemDescribe.setCursorVisible(false);
        etItemDescribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etItemDescribe.setCursorVisible(true);
            }
        });
    }

    @Override
    protected void OnRightButtonClicked() {
        super.OnRightButtonClicked();
        String message = etItemDescribe.getText().toString();
        if(TextUtils.isEmpty(message)) return;
        HashMap<String, String> params = new HashMap<>();
        params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID, (long) 0) + "");
        params.put("lng", "");
        params.put("lat", "");
        params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE, ""));
        params.put("deviceType", "android");
       switch (enterType){
           case HOMETOWN_ID:
               params.put("hometownCity", message);
               OkHttpClientManager.postAsyn(HttpUrlConstant.memberHomeEdit, new OkHttpClientManager.ResultCallback<MessageBean>() {
                   @Override
                   public void onError(Request request, Exception e) {
                       ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                   }

                   @Override
                   public void onResponse(MessageBean response) {
                       if (response.getStatusCode() == 1) {
                           SingleLineTextModifyActivity.this.finish();
                       }else if(response.getStatusCode() == -999 ){
                           exitApp();
                       }else
                           ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                   }
               }, params);
               break;
           case WORKUNITE_ID:
               params.put("workUnitName", message);
               OkHttpClientManager.postAsyn(HttpUrlConstant.memberWorkUnitEdit, new OkHttpClientManager.ResultCallback<MessageBean>() {
                   @Override
                   public void onError(Request request, Exception e) {
                       ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                   }

                   @Override
                   public void onResponse(MessageBean response) {
                       ToastManager.show(getApplicationContext(), response.getMessage());
                       if (response.getStatusCode() == 1) {
                           SingleLineTextModifyActivity.this.finish();
                       }else if(response.getStatusCode() == -999){
                           exitApp();
                       }else
                           ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                   }
               }, params);
               break;
           case SCHOOL_ID:
               params.put("schoolName", message);
               OkHttpClientManager.postAsyn(HttpUrlConstant.memberSchoolEdit, new OkHttpClientManager.ResultCallback<MessageBean>() {
                   @Override
                   public void onError(Request request, Exception e) {
                       ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                   }

                   @Override
                   public void onResponse(MessageBean response) {
                       ToastManager.show(getApplicationContext(), response.getMessage());
                       if (response.getStatusCode() == 1) {
                           SingleLineTextModifyActivity.this.finish();
                       }else if(response.getStatusCode() == -999){
                           exitApp();
                       }else
                           ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                   }
               }, params);
               break;
           case LOCATION_ID:
               params.put("siteCity", message);
               OkHttpClientManager.postAsyn(HttpUrlConstant.memberSiteCityEdit, new OkHttpClientManager.ResultCallback<MessageBean>() {
                   @Override
                   public void onError(Request request, Exception e) {
                       ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                   }

                   @Override
                   public void onResponse(MessageBean response) {
//                       ToastManager.show(getApplicationContext(), response.getMessage());
                       if (response.getStatusCode() == 1) {
                           SingleLineTextModifyActivity.this.finish();
                       }else if(response.getStatusCode() == -999){
                           exitApp();
                       }else
                           ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                   }
               }, params);
               break;

       }
    }
}
