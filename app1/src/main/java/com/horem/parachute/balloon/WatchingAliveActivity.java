package com.horem.parachute.balloon;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.common.ApplicationConstant;
import com.common.HttpUrlConstant;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.main.bean.BalloonRequestBean;
import com.horem.parachute.main.bean.BalloonTotalFeeBean;
import com.horem.parachute.task.NumberSelectionActivity;
import com.horem.parachute.task.TaskPayActivity;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import java.util.HashMap;

public class WatchingAliveActivity extends BaseActivity {

    private EditText etDescribe;
    private TextView tvTotalFee;
    private static final int Request_Camera = 0x1001;
    private int currentLevelNum = 0;
    private int MinLevelNum;
    private  String balloonId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watching_alive);
        init();
        initData(0);
    }

    private void initData(int levelNum) {
        balloonId = getIntent().getStringExtra("id");
        HashMap<String,String> params = new HashMap<>();
        params.put("balloonId",balloonId);
        params.put("levelNum",String.valueOf(levelNum));

        params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lng","");
        params.put("lat","");
        params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");
        OkHttpClientManager.postAsyn(HttpUrlConstant.balloonGetTotalFee, new OkHttpClientManager.ResultCallback<BalloonTotalFeeBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(BalloonTotalFeeBean response) {
                Log.d("获取初始级数",new Gson().toJson(response));
                if(response.getStatusCode() == 1){
                    currentLevelNum = response.getResult().getLevelNum();
                    MinLevelNum = response.getResult().getLevelNumMin();
                    tvTotalFee.setText(response.getResult().getFeeStr());
                }else if(response.getStatusCode() == -999){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");

            }
        },params);

    }

    private void init() {
        initTitleView();
        setLeftButtonText("取消");
        setRightButtonText("发送");
        setTitleName("我想看现场");

        tvTotalFee = (TextView) findViewById(R.id.watch_alive_money_tv);
        etDescribe = (EditText) findViewById(R.id.watch_alive_edit_text);

    }
    public void onSelectMoney(View v){
        Intent intent = new Intent(this, NumberSelectionActivity.class);
        intent.putExtra("enter", ApplicationConstant.BLON_TYPE);
        intent.putExtra("level",currentLevelNum);
        intent.putExtra("minLevel",MinLevelNum);
        intent.putExtra("balloonId",balloonId);
        startActivityForResult(intent,Request_Camera);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Request_Camera && resultCode == RESULT_OK && data != null){

            int level = data.getIntExtra("progress",0);
            initData(level);
        }
    }

    @Override
    protected void OnLeftButtonClicked() {
        super.OnLeftButtonClicked();
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void OnRightButtonClicked() {
        super.OnRightButtonClicked();
        String depict = etDescribe.getText().toString().trim();
        if(TextUtils.isEmpty(depict)){
            ToastManager.show(this,"请输入拍摄要求");
            return;
        }

        HashMap<String,String> params = new HashMap<>();
        params.put("balloonId",balloonId);
        params.put("levelNum",String.valueOf(currentLevelNum));
        params.put("otherRequirements",depict);

        params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lng","");
        params.put("lat","");
        params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");

        OkHttpClientManager.postAsyn(HttpUrlConstant.balloonRequest, new OkHttpClientManager.ResultCallback<BalloonRequestBean>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(BalloonRequestBean response) {
//                Log.d(getClass().getName(),new Gson().toJson(response));
                if(response.getStatusCode() == 1) {
                    Bundle bundle = new Bundle();
                    bundle.putString("taskId", response.getResult());
                    bundle.putString("intentType", "WatchingAliveActivity");
                    Intent intent = new Intent(WatchingAliveActivity.this, TaskPayActivity.class);
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);
                    WatchingAliveActivity.this.finish();
                }else  if(response.getStatusCode() == -999){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }
        },params);

    }
}
