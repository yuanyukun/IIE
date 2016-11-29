package com.horem.parachute.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.common.HttpUrlConstant;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.login.Activity_Login;
import com.horem.parachute.main.AppMainActivity;
import com.horem.parachute.mine.bean.MessageBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.horem.parachute.util.Utils;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import java.util.HashMap;


public class BaseActivity extends Activity {

    private TextView Title_Back;
    private TextView Title_Name;
    private TextView Title_Next;
    private CustomApplication application;
    private CustomLoading customLoadingDialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        application = (CustomApplication) getApplication();
    }

    protected   void initTitleView() {

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.title_bar);
        Title_Back = (TextView) findViewById(R.id.title_back);
        Title_Name = (TextView) findViewById(R.id.title_name);
        Title_Next = (TextView) findViewById(R.id.title_next);

        Title_Back.setOnClickListener(new HeaderViewOnClicked());
        Title_Next.setOnClickListener(new HeaderViewOnClicked());

    }


    class HeaderViewOnClicked implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.title_back:
                    OnLeftButtonClicked();
                    break;
                case R.id.title_next:
                    OnRightButtonClicked();
                    break;
            }
        }
    }

    protected void OnRightButtonClicked() {

    }

    protected void OnLeftButtonClicked() {
        this.finish();
//        onBackPressed();
    }

    ;

    /**
     *
     * @param name
     */
    protected void setTitleName(String name){
        Title_Name.setText(name);
    }

    /**
     * //隐藏右边按钮
     */
    protected void setNextButtonHide(){
        Title_Next.setVisibility(View.GONE);
    }

    /**
     * 获取标题文本
     * @return
     */
    protected  String getTitleName(){
        return Title_Name.getText().toString();
    }
    /**
     *
     * @param text
     * 设置右边view的文字描述
     */
    protected void setRightButtonText(String text){
        Title_Next.setText(text);
        if(text != null){
            Title_Next.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置右边Button是否可见
     * @param visible
     */

    protected void setRightButtonVisible(boolean visible){
        if(visible) {
            Title_Next.setVisibility(View.VISIBLE);
            Title_Next.setClickable(true);
        }else
            Title_Next.setVisibility(View.INVISIBLE);
    }

    /**
     * 设置左边的Button是否可见
     * @param visible
     */
    protected  void setLeftButtonVisible(boolean visible){
        if(visible) {
            Title_Back.setVisibility(View.VISIBLE);
            Title_Back.setClickable(true);
        } else
            Title_Back.setVisibility(View.INVISIBLE);
    }
    protected void setLeftButtonDrawableLeft(Drawable drawable){
        Title_Back.setCompoundDrawablePadding(0);
        if(null != drawable){
            drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
            setLeftButtonVisible(true);
        }
        Title_Back.setCompoundDrawables(drawable,null,null,null);
    }

    protected void setLeftButtonText(String name){
        if(null != name ){
            Title_Back.setText(name);
        }
    }

    protected void RightButtonText(String name){
        if(null != name ){
            Title_Next.setText(name);
        }
    }

    protected void setRightButtonDrawableRight(Drawable drawable){

        if(null != drawable){
            drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
            setRightButtonVisible(true);
        }
        Title_Next.setCompoundDrawables(null,null,drawable,null);
    }

    protected void exitApp(){
        HashMap<String,String> params = new HashMap<>();
        params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lng","");
        params.put("lat","");
        params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");

        params.put("channelId",SharePreferencesUtils.getString(this,SharePrefConstant.CHANNEL_ID,""));
        params.put("userId",SharePreferencesUtils.getString(this,SharePrefConstant.USER_ID,""));

        OkHttpClientManager.postAsyn(HttpUrlConstant.LogoutUrl, new OkHttpClientManager.ResultCallback<MessageBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(MessageBean response) {
                if(response.getStatusCode() == 1){
                    SharePreferencesUtils.setString(getApplicationContext(), SharePrefConstant.MEMBER_NAME,"");
                    SharePreferencesUtils.setLong(getApplicationContext(),SharePrefConstant.MEMBER_ID,(long)0);
                    SharePreferencesUtils.setBoolean(getApplicationContext(),SharePrefConstant.USER_LOGIN,false);
                    SharePreferencesUtils.setBoolean(getApplicationContext(),SharePrefConstant.isNeedConfirmShare,false);
                    SharePreferencesUtils.setBoolean(getApplicationContext(),SharePrefConstant.isNeedUpdateVersion,false);
                    application.setLogin(false);

                    stopLoading();
                    Intent intent = new Intent(getApplicationContext(),Activity_Login.class);
//                    intent.putExtra("intentType","exist");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");

            }
        },params);
    }




    /*****************************************************************************/
    /**
     * 显示键盘
     * @param view
     */
    protected void showInputMethod(View view) {

        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }
    /**
     * 隐藏键盘
     * @param view
     */
    protected void closeInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (isOpen) {
            // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//没有显示则显示
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    /**
     * 进度加载动画
     */
    public void startLoading(){
        if(customLoadingDialog == null){
            customLoadingDialog = CustomLoading.CreateLoadingDialog(this);
            customLoadingDialog.setMessage("正在加载...");
            customLoadingDialog.setCanceledOnTouchOutside(false);//不允许点击取消

            Window wd = customLoadingDialog.getWindow();
            WindowManager.LayoutParams lp = wd.getAttributes();
            lp.alpha = 0.5f;
            wd.setAttributes(lp);
        }
        customLoadingDialog.show();
    }  /**
     * 进度加载动画
     */
    public void startLoading(String msg){
        if(customLoadingDialog == null){
            customLoadingDialog = CustomLoading.CreateLoadingDialog(this);
            customLoadingDialog.setMessage(msg);
            customLoadingDialog.setCanceledOnTouchOutside(false);//不允许点击取消

            Window wd = customLoadingDialog.getWindow();
            WindowManager.LayoutParams lp = wd.getAttributes();
            lp.alpha = 0.5f;
            wd.setAttributes(lp);
        }
        customLoadingDialog.show();
    }
    /**
     * 关闭加载动画
     */
    public void stopLoading(){
        if(customLoadingDialog != null){
            customLoadingDialog.dismiss();
            customLoadingDialog.dismiss();
        }
    }
    //谷歌分析事件
    public void onEvent(String category, String action, String label, Long value) {
        try {
          Tracker mTracker = application.getDefaultTracker();
            mTracker.send(new HitBuilders.EventBuilder().
                    setCategory(category)
                    .setAction(action)
                    .setLabel(label)
                    .setValue(value)
                    .build());
        } catch (Exception e) {
            if (e != null) {
                e.printStackTrace();
            }
        }
    }
}
