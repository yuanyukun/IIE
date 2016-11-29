package com.horem.parachute.mine;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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

public class Mine_Account_Text_Modify extends BaseActivity {

    private EditText etSelfIntroduce;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine__account__text__modify);

        init();
    }

    private void init() {
        initTitleView();
        setRightButtonText("保存");
        setTitleName("自我介绍");
        setLeftButtonText(SharePreferencesUtils.getString(this,SharePrefConstant.MEMBER_NAME,""));
        String value = getIntent().getStringExtra("value");
        etSelfIntroduce = (EditText) findViewById(R.id.mine_account_edit_modify);
        if(value != null) {
            etSelfIntroduce.setText(value);
            etSelfIntroduce.setSelection(value.length());
        }
        etSelfIntroduce.setCursorVisible(false);
        etSelfIntroduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSelfIntroduce.setCursorVisible(true);
            }
        });
    }

    @Override
    protected void OnRightButtonClicked() {
        super.OnRightButtonClicked();
        modifyInfo(etSelfIntroduce.getText().toString());
    }

    private void modifyInfo(final String s) {
        if(!TextUtils.isEmpty(s)){
            HashMap<String,String> params = new HashMap<>();
            params.put("introduction",s);
            params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
            params.put("lat","");
            params.put("lng","");
            params.put("clientId",SharePreferencesUtils.getString(this,SharePrefConstant.INSTALL_CODE,""));
            params.put("deviceType","android");

            OkHttpClientManager.postAsyn(HttpUrlConstant.memberIntroduceEdit, new OkHttpClientManager.ResultCallback<MessageBean>() {
                @Override
                public void onError(Request request, Exception e) {
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                }

                @Override
                public void onResponse(MessageBean response) {
                    if(response.getStatusCode() == 1){
                        Mine_Account_Text_Modify.this.finish();
                    }else if(response.getStatusCode() == -999){
                        exitApp();
                    }else{
                        ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                    }
                }
            },params);

        }else{
            ToastManager.show(this,"自我描述不能为空");
        }
    }
}
