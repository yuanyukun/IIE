package com.horem.parachute.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.app.utils.MyEncrypt;
import com.common.HttpUrlConstant;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.horem.parachute.R;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.login.bean.LoginBean;
import com.horem.parachute.login.bean.UuidKey;
import com.horem.parachute.login.request.presenter.impl.LoginApiImplPresenter;
import com.horem.parachute.main.AppMainActivity;
import com.horem.parachute.mine.UserGuidActivity;
import com.horem.parachute.mine.bean.MessageBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.horem.parachute.util.Utils;
import com.http.request.HttpApi;
import com.http.request.IResponseApi;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StartRegisterActivity extends BaseActivity implements IResponseApi {

    private EditText userName;
    private EditText userEmail;
    private EditText passWord;
    private TextView registerContacts;

    private CustomApplication application;
    private Tracker mTracker;
    private static final String TAG  = "StartRegiAty";
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
        setContentView(R.layout.activity_start_register);
        init();
    }

    private void init() {
        initTitleView();
        setTitleName("注册");
        setRightButtonText("完成");

        userName = (EditText) findViewById(R.id.register_username);
        userEmail = (EditText) findViewById(R.id.register_user_email);
        passWord = (EditText) findViewById(R.id.register_user_passwod);

        registerContacts = (TextView) findViewById(R.id.register_user_contacts);
        String Txt = "《用户服务协议》";
        SpannableStringBuilder builder = new SpannableStringBuilder(Txt);
        ForegroundColorSpan whiteSpan = new ForegroundColorSpan(Color.WHITE);
        ForegroundColorSpan blueSpan = new ForegroundColorSpan(Color.BLUE);
        builder.setSpan(whiteSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(blueSpan, 1, Txt.length()-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(whiteSpan, Txt.length()-1, Txt.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        registerContacts.setText(builder);

        registerContacts.setClickable(true);
        registerContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartRegisterActivity.this, UserGuidActivity.class);
                intent.putExtra("value",1);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void OnRightButtonClicked() {
        super.OnRightButtonClicked();

        RegisterApp(userName.getText().toString(),userEmail.getText().toString(),passWord.getText().toString());

    }

    private void RegisterApp(final String userName, final String userEmail, final String password) {
        if(TextUtils.isEmpty(userName)){
            ToastManager.show(this,"用户名不能为空");
            return;
        }
        if(TextUtils.isEmpty(userEmail) && Utils.checkEmail(userEmail)){
            ToastManager.show(this,"请输入有效邮箱");
            return;
        }
        if(TextUtils.isEmpty(password)){
            ToastManager.show(this,"请输入密码");
            return;
        }
        //获取加密key
        final String guid = UUID.randomUUID().toString();
//        Log.i(getClass().getName(),"first guid value=>>>"+guid);
        Map<String,String> params = new HashMap<>();
        params.put("memberId","0");
        params.put("lng","");
        params.put("lat","");
        params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");
        params.put("guidKey",guid);
//        Log.i(getClass().getName(),"first guid value=>>>"+guid);
        OkHttpClientManager.postAsyn(HttpUrlConstant.keyUrl, new OkHttpClientManager.ResultCallback<UuidKey>() {
            @Override
            public void onError(Request request, Exception e) {
                e.printStackTrace();
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(UuidKey response) {

                StringBuilder builder = new StringBuilder(response.getKey());
                String encryptCode = builder.append("a1").toString();
//                Log.i("加密key",encryptCode);

                //普通注册
                Map<String,String> params = new HashMap<String, String>();
                params.put("memberId","0");
                params.put("lng","");
                params.put("lat","");
                params.put("clientId", SharePreferencesUtils.getString(StartRegisterActivity.this, SharePrefConstant.INSTALL_CODE,""));
                params.put("deviceType","android");

                params.put("password", new MyEncrypt(encryptCode,password).encrypt());
                params.put("guidKey",guid);
                params.put("nickName",userName);
                params.put("email",userEmail);
                params.put("memberType",10+"");

                OkHttpClientManager.postAsyn(HttpUrlConstant.memberRegisterUrl, new OkHttpClientManager.ResultCallback<MessageBean>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                    }

                    @Override
                    public void onResponse(MessageBean response) {
//                        Log.d("registerActivity",new Gson().toJson(response));
                        ToastManager.show(StartRegisterActivity.this,response.getMessage());
                        if(response.getStatusCode() == 1){
                            SharePreferencesUtils.setString(StartRegisterActivity.this,
                                    SharePrefConstant.USER_EMAIL,userEmail);
                            HashMap<String,String> map = new HashMap<String, String>();
                            map.put("userName",userName);
                            map.put("password",password);
                            map.put("lat","");
                            map.put("lng","");
                            HttpApi api = new LoginApiImplPresenter();
                            startLoading();
                            // 调用接口请求服务器数据，服务器返回的数据回调在ResponseApiImpl的onSuccess（）方法里面,可以根据自己的需求来实现onStart（）和ondoinBack（）方法
                            api.httpRequest(StartRegisterActivity.this,StartRegisterActivity.this,map);

                        }else if(-999 == response.getStatusCode()){
                            exitApp();
                        }else
                            ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                    }
                },params);
            }
        },params);
    }

    @Override
    public void onSuccess(Object object) {
        stopLoading();
        LoginBean bean = (LoginBean) object;
        int status = bean.getStatusCode();
        //状态为1时为成功
        if(1 == status){

            long userId = bean.getUser().getUid();
            SharePreferencesUtils.setLong(StartRegisterActivity.this, SharePrefConstant.MEMBER_ID,userId);
            SharePreferencesUtils.setBoolean(StartRegisterActivity.this,SharePrefConstant.USER_LOGIN,true);
            SharePreferencesUtils.setString(StartRegisterActivity.this,SharePrefConstant.MEMBER_NAME,userName.getText().toString());

            Intent intent = new Intent(StartRegisterActivity.this, AppMainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onFailed(Exception e) {

    }
}
