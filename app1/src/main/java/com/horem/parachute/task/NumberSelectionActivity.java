package com.horem.parachute.task;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.common.HttpUrlConstant;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.common.ApplicationConstant;
import com.horem.parachute.balloon.WatchingAliveActivity;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.main.bean.BalloonTotalFeeBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;

public class NumberSelectionActivity extends BaseActivity {

    private TextView showTheme;
    private TextView seekBarProgress;
    private TextView seekBarMin;
    private TextView seekBarMax;
    private SeekBar  seekBar;
    private TextView minus,plus;
    private TextView autoUsed;

    private LinearLayout informationTips;
    private LinearLayout timeOutTips;
    private Bundle bundle;
    private  int enterType;
    private HashMap<String,String> params;
    private int currentProgress;
    private String placeArr;
    private long memberId;
    private int receivedNum;

    private String balloonId;
    private int currentMinLevel;


    private CustomApplication application;
    private Tracker mTracker;
    private static  final  String TAG = "NumberSeleAty";
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
        setContentView(R.layout.activity_number_selection);
        init();
    }

    private void init() {
        initTitleView();
        setRightButtonText("确定");

        informationTips = (LinearLayout) findViewById(R.id.theme_information);
        timeOutTips = (LinearLayout) findViewById(R.id.ll_time_out_info_tips);
        autoUsed = (TextView) findViewById(R.id.auto_used_tips);
        showTheme = (TextView) findViewById(R.id.tv_theme);
        seekBarProgress = (TextView) findViewById(R.id.seek_bar_progress);
        seekBarMin = (TextView) findViewById(R.id.seek_bar_min);
        seekBarMax = (TextView) findViewById(R.id.seek_bar_max);
        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        minus = (TextView) findViewById(R.id.minus);
        plus = (TextView) findViewById(R.id.plus);

        bundle = new Bundle();
        params = new HashMap<>();
        initLayout();
    }

    private void initLayout() {
        enterType = getIntent().getIntExtra("enter", -1);
        memberId = SharePreferencesUtils.getLong(NumberSelectionActivity.this, SharePrefConstant.MEMBER_ID,(long)0);
        switch (enterType) {
            case ApplicationConstant.USER_TYPE:
                setTitleName("最多采用人数");
                showTheme.setText("确认时可选择的人数");
                int number = getIntent().getIntExtra("chooseNum",0);
                seekBar.setProgress(number);
                seekBar.setMax(99);
                seekBarProgress.setText(number+"人");
                seekBarMin.setText("1人");
                seekBarMax.setText("100人");
                break;
            case ApplicationConstant.COST_TYPE:
                setTitleName("总费用");
                showTheme.setText("需支付");
                int level = getIntent().getIntExtra("level",1);
                placeArr = getIntent().getStringExtra("params");
                int minLevel = getIntent().getIntExtra("minLevel",1);
                receivedNum = getIntent().getIntExtra("currentNum",1);
                currentProgress = level;
                currentMinLevel = minLevel;

                seekBar.setMax(599);
                seekBar.setProgress(level);
                seekBarMin.setText(minLevel+"");
                seekBarMax.setText("600");
                getTotalFeeImpl(memberId,level,receivedNum);

                minus.setVisibility(View.VISIBLE);
                plus.setVisibility(View.VISIBLE);
                minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(currentProgress <= currentMinLevel-1){
                            currentProgress = currentMinLevel;
                        }else
                            currentProgress = currentProgress + 1;
                        getTotalFeeImpl(memberId,currentProgress,receivedNum);
                    }
                });
                plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentProgress = currentProgress + 1;
                        getTotalFeeImpl(memberId, currentProgress, receivedNum);
                    }
                });

                break;
            case ApplicationConstant.TIME_TYPE:
                setTitleName("拍摄有效期");
                showTheme.setText("付款后可以拍摄的有效时间");
                informationTips.setVisibility(View.INVISIBLE);
                autoUsed.setVisibility(View.INVISIBLE);
                timeOutTips.setVisibility(View.VISIBLE);
                int time = getIntent().getIntExtra("time",1);
                seekBar.setProgress(time);
                seekBarProgress.setText(time+"小时");
                seekBar.setMax(71);
                seekBarMin.setText("1小时");
                seekBarMax.setText("72小时");
                break;
            case ApplicationConstant.BLON_TYPE:
                setTitleName("打赏");
                showTheme.setVisibility(View.INVISIBLE);
                informationTips.setVisibility(View.INVISIBLE);
                autoUsed.setVisibility(View.INVISIBLE);
                int blonLevel = getIntent().getIntExtra("level",1);
                int blon_MinLevel = getIntent().getIntExtra("minLevel",1);
                currentMinLevel = blon_MinLevel;
                balloonId = getIntent().getStringExtra("balloonId");
                currentProgress = blon_MinLevel;

                seekBar.setMax(599);

                seekBar.setProgress(blonLevel-1);
                seekBarMin.setText(blon_MinLevel+"");
                seekBarMax.setText("600");
                getBalloonTotalFee(balloonId,currentProgress);

                minus.setVisibility(View.VISIBLE);
                plus.setVisibility(View.VISIBLE);
                minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            if(currentProgress <= currentMinLevel){
                                currentProgress = currentMinLevel;
                            }
                            currentProgress = currentProgress -1;
                            getBalloonTotalFee(balloonId,currentProgress);
                    }
                });
                plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        currentProgress = currentProgress + 1;
                        getBalloonTotalFee(balloonId,currentProgress);

                    }
                });
                break;
        }
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress  <=  currentMinLevel-1)
                    currentProgress = currentMinLevel;
                else
                    currentProgress = progress+1;
                switch (enterType) {
                    case ApplicationConstant.USER_TYPE:
                        setTitleName("最多采用人数");
                        showTheme.setText("确认时可选择的人数");
                        seekBarProgress.setText(currentProgress+"人");
                        break;
                    case ApplicationConstant.COST_TYPE:
                        setTitleName("总费用");
                        showTheme.setText("需支付");

                        break;
                    case ApplicationConstant.TIME_TYPE:
                        setTitleName("拍摄有效期");
                        showTheme.setText("付款后可以拍摄的有效时间");
                        seekBarProgress.setText(currentProgress+"小时");
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(enterType == ApplicationConstant.COST_TYPE){
                    getTotalFeeImpl(memberId,currentProgress,receivedNum);
                }
                if (enterType == ApplicationConstant.BLON_TYPE){
                    getBalloonTotalFee(balloonId,currentProgress);
                }
            }
        });
    }

    private void getBalloonTotalFee(String balloonId, int currentProgress) {
        startLoading();
        HashMap<String,String> params = new HashMap<>();
        params.put("balloonId",balloonId);
        params.put("levelNum",currentProgress+"");

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
//                Log.d("获取打赏费用",new Gson().toJson(response));
                stopLoading();
                if(response.getStatusCode() == 1){
                    seekBarProgress.setText(response.getResult().getFeeStr());
                }else if(response.getStatusCode() ==-999){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }
        },params);
    }

    private void getTotalFeeImpl(long memberId,int levelNum,int receiveNum) {
        startLoading();
        HashMap<String,String> map = new HashMap<>();
        map.put("mediaType",enterType+"");
        map.put("receiveNum",receiveNum+"");
        map.put("placeJson",placeArr);
        map.put("levelNum",levelNum+"");

        map.put("memberId",SharePreferencesUtils.getLong(this,SharePrefConstant.MEMBER_ID,(long)0)+"");
        map.put("lat","");
        map.put("lng","");
        map.put("clientId",SharePreferencesUtils.getString(this,SharePrefConstant.INSTALL_CODE,""));
        map.put("deviceType","android");

        OkHttpClientManager.postAsyn(HttpUrlConstant.taskGetTotalFee, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                stopLoading();
                try {
                    JSONObject json = new JSONObject(response);
                    JSONObject subJson = json.getJSONObject("result");
                    if(json.getInt("statusCode") == 1) {
                        seekBarProgress.setText(subJson.optString("feeStr"));
                    }else if(json.getInt("statusCode") == -999){
                        exitApp();
                    }else
                        ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },map);
    }
    @Override
    protected void OnRightButtonClicked() {
        super.OnRightButtonClicked();
        Intent intent = null;
        if(enterType == ApplicationConstant.BLON_TYPE){
            intent= new Intent(this, WatchingAliveActivity.class);
            intent.putExtra("progress",currentProgress);
        }else{
             intent = new Intent(NumberSelectionActivity.this,DoSendTask.class);
            intent.putExtra("progress",currentProgress);
        }
        setResult(RESULT_OK,intent);
        this.finish();

    }

}
