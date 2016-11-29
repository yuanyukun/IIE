package com.horem.parachute.customview;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.HttpUrlConstant;
import com.horem.parachute.R;
import com.horem.parachute.balloon.Bean.BaseResultBean;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.login.Activity_Login;
import com.horem.parachute.task.ConfirmLocationAddress;
import com.horem.parachute.util.ScreenBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.Utils;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import java.util.HashMap;

/**
 * Created by yuanyukun on 2016/8/5.
 */
public class CustomLoveBtn extends LinearLayout {
    private ImageView imageView;
    private TextView likeNums;
    private Context context;

    private boolean markItLove;
    private int currentNum;
    private String balloonId;

    public CustomLoveBtn(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public CustomLoveBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);
    }

    public CustomLoveBtn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        this.context = context;
    }


    private void init(Context context) {

        LayoutInflater.from(context).inflate(R.layout.custom_love_btn,this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.setLayoutParams(lp);

        imageView = (ImageView) findViewById(R.id.custom_love_btn_image);
        likeNums = (TextView) findViewById(R.id.custom_love_btn_num);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewClicked();
            }
        });
    }

    private void onViewClicked() {
        buildParams();
        if(CustomApplication.getInstance().isLogin()) {
            if (markItLove) {
                setItUnLove();
            } else {
                setItLove();
            }
        }else{
            Intent intent  = new Intent(context, Activity_Login.class);
            context.startActivity(intent);
        }
    }

    private HashMap<String, String> buildParams() {
        HashMap<String,String> params = new HashMap<>();
        params.put("balloonId",balloonId);
        params.put("memberId",""+ SharePreferencesUtils.getLong(context, SharePrefConstant.MEMBER_ID,(long)0));
        params.put("lat","");
        params.put("lng","");
        params.put("deviceType","android");
        params.put("clientId",SharePreferencesUtils.getString(context,SharePrefConstant.INSTALL_CODE,""));
        return params;
    }

    private void setItUnLove() {
        OkHttpClientManager.postAsyn(HttpUrlConstant.balloonLikeCancle, new OkHttpClientManager.ResultCallback<BaseResultBean>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(BaseResultBean response) {
                if(response.getStatusCode() == 1){
                    markItLove = false;
                    currentNum = currentNum -1;
                    setInitData(balloonId,false,currentNum);
                }
            }
        }, buildParams());
    }

    private void setItLove() {
        OkHttpClientManager.postAsyn(HttpUrlConstant.balloonLikeAdd, new OkHttpClientManager.ResultCallback<BaseResultBean>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(BaseResultBean response) {
                if(response.getStatusCode() == 1){
                    markItLove = true;
                    currentNum = currentNum +1;
                    setInitData(balloonId,true,currentNum);
                }
            }
        }, buildParams());
    }

    public void setInitData(String balloonId,boolean isSetLove,int loveNums){
        this.balloonId = balloonId;
        this.currentNum = loveNums;
        this.markItLove = isSetLove;
        if(isSetLove){
            imageView.setImageResource(R.mipmap.red_like_32);
            likeNums.setTextColor(ContextCompat.getColor(context,R.color.color_e64));
        }else{
            imageView.setImageResource(R.mipmap.gray_like_32);
            likeNums.setTextColor(ContextCompat.getColor(context,R.color.color_666));
        }
        likeNums.setText(String.valueOf(loveNums));
    }






}
