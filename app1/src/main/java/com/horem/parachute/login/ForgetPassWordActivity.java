package com.horem.parachute.login;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.common.HttpUrlConstant;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.mine.bean.MessageBean;
import com.horem.parachute.util.ToastManager;
import com.horem.parachute.util.Utils;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import java.util.HashMap;

public class ForgetPassWordActivity extends BaseActivity {

    private EditText userEmail;
    private CustomApplication application;
    private Tracker mTracker;
    private static final String TAG  = "ForgetPassWord";
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
        setContentView(R.layout.activity_forget_pass_word);
        init();
    }

    private void init() {
        initTitleView();
        setTitleName("忘记密码");

        userEmail = (EditText) findViewById( R.id.forget_pwd_email_edit);
        findViewById(R.id.forget_pwd_send_email_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPwdback(userEmail.getText().toString());
            }
        });
    }

    private void getPwdback(String email) {
        if(TextUtils.isEmpty(email) && Utils.checkEmail(email)){
            ToastManager.show(ForgetPassWordActivity.this,"请输入有效的邮箱地址");
            return;
        }
        HashMap<String,String> params = new HashMap<>();
        params.put("email",email);

        OkHttpClientManager.getAsyn(HttpUrlConstant.memberPasswordForgotUrl, new OkHttpClientManager.ResultCallback<MessageBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(MessageBean response) {
//                Log.d("ForgetPwdActivity",new Gson().toJson(response));
                ToastManager.show(ForgetPassWordActivity.this,response.getMessage());
                if(response.getStatusCode() == 1){
                    ForgetPassWordActivity.this.finish();
                }else if(response.getStatusCode() == -999){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }
        },params);





    }
}
