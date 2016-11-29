package com.horem.parachute.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.common.HttpUrlConstant;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.login.bean.LoginBean;
import com.horem.parachute.login.request.presenter.impl.LoginApiImplPresenter;
import com.horem.parachute.main.AppMainActivity;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.http.request.HttpApi;
import com.http.request.IResponseApi;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;
import com.tencent.mm.sdk.modelmsg.SendAuth;

import java.util.HashMap;
import java.util.Map;

public class Activity_Login extends BaseActivity implements View.OnClickListener,IResponseApi,
		AMapLocationListener {

	private EditText editUserName;
	private EditText editUserPassword;
	private boolean isWXLogin = false;
	private LatLng currentLatLng;
	//声明AMapLocationClient类对象
	public AMapLocationClient mLocationClient = null;
	//声明mLocationOption对象
	public AMapLocationClientOption mLocationOption = null;

	private CustomApplication application;
	private Tracker mTracker;
	private static final String TAG  = "LoginAty";


	@Override
	public void onBackPressed() {
//		super.onBackPressed();
//		this.finish();
	}

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
	protected void onCreate(Bundle bundle) {		
		super.onCreate(bundle);
		setContentView(R.layout.activity_mine_login);
		init();
		initMap();
	}
	private void initMap() {
		//初始化定位
		mLocationClient = new AMapLocationClient(getApplicationContext());
		mLocationOption = new AMapLocationClientOption();
		//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
		mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
		//设置是否返回地址信息（默认返回地址信息）
		mLocationOption.setNeedAddress(true);
		//设置是否只定位一次,默认为false
		mLocationOption.setOnceLocation(false);
		//设置是否强制刷新WIFI，默认为强制刷新
		mLocationOption.setWifiActiveScan(true);
		//设置是否允许模拟位置,默认为false，不允许模拟位置
		mLocationOption.setMockEnable(false);
		//设置定位间隔,单位毫秒,默认为2000ms
		mLocationOption.setInterval(2000);
		//给定位客户端对象设置定位参数
		mLocationClient.setLocationOption(mLocationOption);
		//设置定位回调监听
		mLocationClient.setLocationListener(this);
		//启动定位
		mLocationClient.startLocation();
	}
	//latitude=23.126613longitude=113.372374 公司经纬度
	private void init() {
		initTitleView();
		setTitleName("登录");

//		latLng = new LatLng(23.126613,113.372374);
		editUserName = (EditText) findViewById(R.id.login_user_name);
		editUserPassword = (EditText) findViewById(R.id.login_user_password);

		findViewById(R.id.tv_forget_pwd).setOnClickListener(this);
		findViewById(R.id.btn_login).setOnClickListener(this);
		findViewById(R.id.btn_register).setOnClickListener(this);
		findViewById(R.id.login_weixin_btn).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent goNext = null;
		switch (v.getId()){
			case R.id.tv_forget_pwd:
				goNext = new Intent(this,ForgetPassWordActivity.class);
				break;
			case R.id.btn_login:
				login();
				break;
			case R.id.btn_register:
				goNext = new Intent(this,StartRegisterActivity.class);
				break;
			case R.id.login_weixin_btn:
				weixinLogin();
				break;
		}
		if(null != goNext){
			startActivity(goNext);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i("Activity_Login","OnResume triggered");
		weChatLogin();
	}

	private void weChatLogin() {
		if(SharePreferencesUtils.getBoolean(this, SharePrefConstant.iswxLon,false &&
			SharePreferencesUtils.getBoolean(this,SharePrefConstant.wx_callback_OK,false))){

			startLoading();
			SharePreferencesUtils.setBoolean(this,SharePrefConstant.iswxLon,false);
			String code = SharePreferencesUtils.getString(this,SharePrefConstant.WX_CODE,"");
			if(currentLatLng == null){
				ToastManager.show(getApplicationContext(),"噢，网络错误");
				return;
			}

			HashMap<String,String> map = new HashMap<>();
			map.put("code",code);
			map.put("channelId",SharePreferencesUtils.getString(this,SharePrefConstant.CHANNEL_ID,""));
			map.put("userId",SharePreferencesUtils.getString(this,SharePrefConstant.USER_ID,""));
			map.put("memberId",SharePreferencesUtils.getLong(this,SharePrefConstant.MEMBER_ID,(long)0)+"");
			map.put("deviceType","android");
			map.put("clientId",SharePreferencesUtils.getString(this,SharePrefConstant.INSTALL_CODE,""));
			map.put("lng",currentLatLng.longitude+"");
			map.put("lat",currentLatLng.latitude+"");
			for(Map.Entry<String,String> entry:map.entrySet()){
				Log.d(getClass().getName(),entry.getKey()+" : "+entry.getValue());
			}
			if(!TextUtils.isEmpty(code)){

				OkHttpClientManager.postAsyn(HttpUrlConstant.memberWeChatLoginUrl, new OkHttpClientManager.ResultCallback<LoginBean>() {
					@Override
					public void onError(Request request, Exception e) {
						ToastManager.show(getApplicationContext(),"噢，网络不给力！");
					}

					@Override
					public void onResponse(LoginBean response) {
						stopLoading();
						Log.i(getClass().getName(),new Gson().toJson(response));
						if(response.getStatusCode() == 1){
							ToastManager.show(Activity_Login.this,response.getMessage());
							CustomApplication.getInstance().setLogin(true);
							SharePreferencesUtils.setLong(Activity_Login.this,SharePrefConstant.MEMBER_ID,response.getUser().getUid());
							SharePreferencesUtils.setString(Activity_Login.this,SharePrefConstant.MEMBER_NAME,response.getUser().getName());
							SharePreferencesUtils.setBoolean(Activity_Login.this,SharePrefConstant.isNeedUpdateChannelId,true);

//							Intent intent = new Intent(Activity_Login.this,AppMainActivity.class);
//							intent.putExtra("intentType","login");
//							startActivity(intent);
							Activity_Login.this.finish();

						}else if(response.getStatusCode() == -999){
							exitApp();
						}else
							ToastManager.show(getApplicationContext(),"噢，网络不给力！");
					}

				},map);
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i("Activity_Login","onPause triggered");
	}

	@Override
	protected void onRestart() {
		super.onRestart();

	}

	private void weixinLogin() {
		// send oauth request
		SharePreferencesUtils.setBoolean(this,SharePrefConstant.iswxLon,true);
		if(CustomApplication.api == null){

			Log.i(getClass().getName(),"微信api对象为空");
		}
		if(!CustomApplication.api.isWXAppInstalled())
		{
			ToastManager.show(this,"检测到您的手机未安装微信客户端");
			return;
		}
		SendAuth.Req req = new SendAuth.Req();
		req.scope = "snsapi_userinfo";
		req.state = "com_horem_parachute";
		boolean isSuccess = CustomApplication.api.sendReq(req);

	}
	private void login() {
		if(null != currentLatLng){
			final String name = editUserName.getText().toString();
			final String password = editUserPassword.getText().toString();
			final Map<String,String > map = new HashMap<>();
			map.put("userName",name);
			map.put("password",password);
			map.put("lat",currentLatLng.latitude+"");
			map.put("lng",currentLatLng.longitude+"");

//			for(Map.Entry<String,String> entry:map.entrySet()){
//				Log.i(getClass().getName(),entry.getKey()+" "+entry.getValue());
//			}

			if(TextUtils.isEmpty(name)||TextUtils.isEmpty(password)){
				Toast.makeText(this,"用户名或者密码不能为空",Toast.LENGTH_SHORT).show();
			}

			// 实例化接口，MVP的核心思想就是针对接口编程而不是针对实现编程
			HttpApi api = new LoginApiImplPresenter();
			startLoading();
			// 调用接口请求服务器数据，服务器返回的数据回调在ResponseApiImpl的onSuccess（）方法里面,可以根据自己的需求来实现onStart（）和ondoinBack（）方法
			api.httpRequest(Activity_Login.this,this,map);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mLocationClient.onDestroy();
		mLocationClient = null;
	}

	@Override
	public void onSuccess(Object object) {
		stopLoading();
		LoginBean bean = (LoginBean) object;
		int status = bean.getStatusCode();
		ToastManager.show(this,bean.getMessage());
		//状态为1时为成功
		if(1 == status){

			long userId = bean.getUser().getUid();
			CustomApplication.getInstance().setLogin(true);
			SharePreferencesUtils.setLong(Activity_Login.this, SharePrefConstant.MEMBER_ID,userId);
			SharePreferencesUtils.setBoolean(Activity_Login.this,SharePrefConstant.USER_LOGIN,true);
			SharePreferencesUtils.setString(Activity_Login.this,SharePrefConstant.MEMBER_NAME,editUserName.getText().toString());
			SharePreferencesUtils.setBoolean(Activity_Login.this,SharePrefConstant.isNeedUpdateChannelId,true);

//			Intent intent = new Intent(Activity_Login.this, AppMainActivity.class);
			SharePreferencesUtils.setBoolean(Activity_Login.this,SharePrefConstant.isFromLoginAty,true);
			finish();

		}
	}

	@Override
	protected void OnLeftButtonClicked() {
//		Intent intent = new Intent(Activity_Login.this, AppMainActivity.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		intent.putExtra("intentType","login");
//		startActivity(intent);
		finish();
//		super.OnLeftButtonClicked();
	}

	@Override
	public void onFailed(Exception e) {

	}

	@Override
	public void onLocationChanged(AMapLocation aMapLocation) {
//		Log.d("Login","定位回调");
		if (aMapLocation != null) {
			if (aMapLocation.getErrorCode() == 0) {
				//定位成功回调信息，设置相关消息
				aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
				currentLatLng = new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude());

			} else {
				//显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
				Log.e("AmapError","location Error, ErrCode:"
						+ aMapLocation.getErrorCode() + ", errInfo:"
						+ aMapLocation.getErrorInfo());
			}
		}
	}


}
