package com.horem.parachute.pushmessage;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.common.HttpUrlConstant;
import com.google.gson.Gson;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.login.bean.upDateUIBeanNew;
import com.horem.parachute.mine.bean.upDateUIBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.internal.spdy.FrameReader;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class InstanceMessageService extends Service {

	private boolean threadDisable = false;
	private boolean firstSend = true;
//	private boolean lastTimeStatus = true;
//	private boolean currentStatus = false;
	private boolean isCanRequest = true;
	private int count = 0;
	private Timer TenSecCount = new Timer();
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == 0x1000){
				isCanRequest = true;
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return sBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		sBinder = new SimpleBinder();
//		Log.d("InstanceMessageService","onCreate start");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
//		Log.d("InstanceMessageService","onStartCommand start");
		getIsHasNewMessage();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		threadDisable = true;
	}
	public void getIsHasNewMessage() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				HashMap<String,String> map = new HashMap<String, String>();
				long memberId = SharePreferencesUtils.getLong(getApplicationContext(), SharePrefConstant.MEMBER_ID,(long)0);
				map.put("memberId",String.valueOf(memberId) );
				map.put("lat","");
				map.put("lng","");
				map.put("clientId",SharePreferencesUtils.getString(getApplicationContext(),SharePrefConstant.INSTALL_CODE,"") );
				map.put("deviceType","android" );

				String url = HttpUrlConstant.isHasNewMessage;
				sleepTenSec();
				while (!threadDisable) {
					if(isCanRequest) {
						isCanRequest = false;
						if(CustomApplication.getInstance().isLogin()) {
//							Log.d(getClass().getName(),"执行一次请求");

							IsHasNewMessage(map, url);
						}
					}
				}
			}
		}).start();
	}

	private void sleepTenSec() {
		TenSecCount.schedule(new TimerTask() {
			@Override
			public void run() {
				count++;
//				Log.d(getClass().getName(),String.valueOf(count%10));
				if( count != 0 && count%10 == 0) {
					handler.sendEmptyMessage(0x1000);
//					Log.d(getClass().getName(),"发送了一次消息");
				}
			}
		},0,1000);
	}

	public class SimpleBinder extends Binder {

		public InstanceMessageService getService() {
			return InstanceMessageService.this;
		}

	}
	public SimpleBinder sBinder;

	private void IsHasNewMessage(HashMap<String,String> map,String url) {

		OkHttpClientManager.postAsyn(url, new OkHttpClientManager.ResultCallback<upDateUIBeanNew>() {
			@Override
			public void onError(Request request, Exception e) {
				if (firstSend) {
					ToastManager.show(getApplicationContext(), "噢，网络不给力！");
					firstSend = false;
				}
			}
			@Override
			public void onResponse(upDateUIBeanNew response) {
//				Log.d("消息轮询返回结果外面",new Gson().toJson(response));

				if(response.getStatusCode() == 1) {
//					Log.d("消息轮询返回结果里面",new Gson().toJson(response));
					firstSend = true;
					//数据保存在全局变量中
					CustomApplication application = CustomApplication.getInstance();
					application.setMessageList(response.getResult().isMessageList());
					application.setSubTaskList(response.getResult().isMySubTaskList());
					application.setViewUserList(response.getResult().isViewUserList());
					application.setTaskOrderList(response.getResult().isMyTaskOrderList());
					application.setFlowersToMeList(response.getResult().isFlowersToMeList());
					application.setBeFollowList(response.getResult().isBeFollowList());
					application.setGiveLikeMeList(response.getResult().isGiveLikeMeList());

					//每请求一次发送一次广播
					Intent intent = new Intent();
					intent.setAction("com.horem.parachute.Status");
					CustomApplication.sendLocalBroadcast(intent);
//					Log.d(getClass().getName(), "发送了一广播");
				}
			}
		},map);
	}
}
