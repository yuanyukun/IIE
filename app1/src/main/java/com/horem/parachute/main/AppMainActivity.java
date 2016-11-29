package com.horem.parachute.main;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager.OnPageChangeListener;


import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.common.ApplicationConstant;
import com.common.HttpUrlConstant;
import com.horem.parachute.R;
import com.horem.parachute.autoupdate.AppUpdateInterface;
import com.horem.parachute.autoupdate.AppUpdateService;
import com.horem.parachute.autoupdate.internal.ResponseParser;
import com.horem.parachute.balloon.Bean.BaseResultBean;
import com.horem.parachute.balloon.SendBalloonActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.customview.AlertDialog;
import com.horem.parachute.customview.CustomViewPager;
import com.horem.parachute.login.Activity_Login;
import com.horem.parachute.mine.Activity_Mine_Task_Received;
import com.horem.parachute.mine.Activity_Mine_Task_send;
import com.horem.parachute.mine.HomeChatActivity;
import com.horem.parachute.mine.TaskSendConfirm;
import com.horem.parachute.pushmessage.InstanceMessageService;
import com.horem.parachute.task.DoSendTask;
import com.horem.parachute.util.ScreenBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.horem.parachute.util.Utils;
import com.http.request.HttpApi;
import com.http.request.IResponseApi;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class AppMainActivity extends FragmentActivity implements View.OnClickListener{

    private CustomViewPager mViewPager;

    private ImageView homeFragmentDot;
    private ImageView userInfoFragmentDot;

    private FrameLayout btnHome;
    private FrameLayout btnNearby;
    private FrameLayout btnCircle;
    private FrameLayout btnDiscover;
    private FrameLayout btnUserInfo;

    private ImageView   imgHome;
    private ImageView   imgNearby;
    private ImageView   imgDiscover;
    private ImageView   imgUserInfo;

    private TextView    tvHome;
    private TextView    tvNearby;
    private TextView    tvDiscover;
    private TextView    tvUserInfo;

    private HomeFragment tab1;
    private NearByFragment tab2;
    private DiscoverFragment tab3;
    private UserInfoFragment tab4;

    private List<Fragment> mFragments = new ArrayList<>();
    private FragmentPagerAdapter mAdapter;
    private FragmentManager fragmentManger;

    private int currentIndex;

    private mySubTaskListReceiver myBroadCastReceiver;//接受消息轮询返回结果
    private CustomApplication application;

    private AppUpdateInterface appUpdate;              //自动升级

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_app_home);
        //是否登陆缓存
        isLoginCache();
        //绑定app所需服务
        bindCustomService();
        //注册广播接收器
        registerCustomReceiver();
        //
        initView();
        //自动升级
        appAutoUpdate();
    }
    private void appAutoUpdate() {
        //自动升级
        appUpdate = AppUpdateService.getAppUpdate(this);
        if (appUpdate != null) {
            //升级url
            String updateUrl = HttpUrlConstant.androidVersion;
//            Log.d("MainActivity",updateUrl);
            appUpdate.checkLatestVersion(updateUrl, new ResponseParser());
        }

    }
    private void isLoginCache() {
        application = (CustomApplication) getApplication();
        long memberId = SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)-1);
        if(memberId > 0){
            application.setLogin(true);
            application.setUserId(memberId);
        }
    }
    private void bindCustomService() {
        //绑定百度消息推送服务
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,
                Utils.getMetaValue(this,"api_key"));
        //启动消息轮询服务
        this.startService(new Intent(AppMainActivity.this,
                InstanceMessageService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CustomApplication.unregisterLocalReceiver(myBroadCastReceiver);
    }

    private void registerCustomReceiver() {
        if(myBroadCastReceiver == null) {
            myBroadCastReceiver = new mySubTaskListReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.horem.parachute.Status");
        //注册一个接收的广播
        CustomApplication.registerLocalReceiver(myBroadCastReceiver, filter);
    }
    public class mySubTaskListReceiver extends BroadcastReceiver {
        public mySubTaskListReceiver() {
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.horem.parachute.Status")) {
                //updateUI here
                updaUI();
            }
        }
    }
    private void updaUI() {
        application = (CustomApplication) getApplication();
        //还差一个
        if(application.isMessageList || application.isViewUserList|| application.flowersToMeList){
            homeFragmentDot.setVisibility(View.VISIBLE);
        }else{
            homeFragmentDot.setVisibility(View.INVISIBLE);
        }
        //
        if(application.beFollowList || application.isTaskOrderList || application.isSubTaskList){
            userInfoFragmentDot.setVisibility(View.VISIBLE);
        }else{
            userInfoFragmentDot.setVisibility(View.INVISIBLE);
        }
    }

    private void initView() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        ScreenBean.setScreenWidth(screenWidth);
        ScreenBean.setScreenHeight(screenHeight);


        fragmentManger = getSupportFragmentManager();
        mViewPager = (CustomViewPager) findViewById(R.id.main_home_viewpager);

        btnHome = (FrameLayout) findViewById(R.id.frame_home_btn);
        btnNearby = (FrameLayout) findViewById(R.id.frame_nearby_btn);
        btnCircle = (FrameLayout) findViewById(R.id.frame_circle_btn);
        btnDiscover= (FrameLayout) findViewById(R.id.frame_discovery);
        btnUserInfo = (FrameLayout) findViewById(R.id.frame_user_info);

        btnHome.setOnClickListener(this);
        btnNearby.setOnClickListener(this);
        btnCircle.setOnClickListener(this);
        btnDiscover.setOnClickListener(this);
        btnUserInfo.setOnClickListener(this);

        homeFragmentDot = (ImageView) findViewById(R.id.img_fragment_home_red_dot);
        userInfoFragmentDot = (ImageView) findViewById(R.id.img_user_info_fragment_red_dot);
//        chatBtnRedDot = findViewById(R.id.img)

        imgHome = (ImageView) findViewById(R.id.tab_home_click);
        imgNearby = (ImageView) findViewById(R.id.tab_nearby);
        imgDiscover = (ImageView) findViewById(R.id.img_discovery);
        imgUserInfo = (ImageView) findViewById(R.id.img_user_info);

        tvHome = (TextView) findViewById(R.id.tab_home_text);
        tvNearby = (TextView) findViewById(R.id.tab_nearby_text);
        tvDiscover = (TextView) findViewById(R.id.tv_discovery);
        tvUserInfo = (TextView) findViewById(R.id.tv_user_info);

         tab1= new HomeFragment();
         tab2= new NearByFragment();
         tab3= new DiscoverFragment();
         tab4= new UserInfoFragment();

        mFragments.add(tab1);
        mFragments.add(tab2);
        mFragments.add(tab3);
        mFragments.add(tab4);

        //通过改变跳转页面动画解决viewPager与mapView黑边问题,不是很好，但是解决了
//        mViewPager.setPageTransformer(true, new DepthPageTransformer());
       //设置是否滑动
        mViewPager.setPagingEnabled(false);
        //左右页面缓存个数为2
        mViewPager.setOffscreenPageLimit(2);

        mAdapter= new FragmentPagerAdapter(fragmentManger) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }
        };

        mViewPager.setAdapter(mAdapter);
        setTabSelection(0);
        mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                resetTabBtn();
                switch (position)
                {
                    case 0:
                        imgHome.setImageResource(R.mipmap.tab_parachute2_75);
                        tvHome.setTextColor(ContextCompat.getColor(AppMainActivity.this,R.color.material_blue));
                        break;
                    case 1:
                        imgNearby.setImageResource(R.mipmap.tab_map2_75);
                        tvNearby.setTextColor(ContextCompat.getColor(AppMainActivity.this,R.color.material_blue));
                        break;
                    case 2:
                        imgDiscover.setImageResource(R.mipmap.tab_discovery2_75);
                        tvDiscover.setTextColor(ContextCompat.getColor(AppMainActivity.this,R.color.material_blue));

                        break;
                    case 3:
                        if(application.isLogin()){
                            imgUserInfo.setImageResource(R.mipmap.tab_me2_75);
                            tvUserInfo.setTextColor(ContextCompat.getColor(AppMainActivity.this,R.color.material_blue));
                        }else{
                            Intent intent = new Intent(AppMainActivity.this,Activity_Login.class);
                            startActivity(intent);
                        }

                        break;
                }

                currentIndex = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void resetTabBtn() {
        imgHome.setImageResource(R.mipmap.tab_parachute1_75);
        imgNearby.setImageResource(R.mipmap.tab_map1_75);
        imgDiscover.setImageResource(R.mipmap.tab_discovery1_75);
        imgUserInfo.setImageResource(R.mipmap.tab_me1_75);

        tvHome.setTextColor(ContextCompat.getColor(this,R.color.text_grey));
        tvNearby.setTextColor(ContextCompat.getColor(this,R.color.text_grey));
        tvDiscover.setTextColor(ContextCompat.getColor(this,R.color.text_grey));
        tvUserInfo.setTextColor(ContextCompat.getColor(this,R.color.text_grey));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.frame_home_btn:
                setTabSelection(0);
                currentIndex = 0;
                break;
            case R.id.frame_nearby_btn:
                setTabSelection(1);
                currentIndex =1;
                break;
            case R.id.frame_circle_btn:
                showAlertView();

                break;
            case R.id.frame_discovery:
                currentIndex = 2;
                setTabSelection(2);
                break;
            case R.id.frame_user_info:
                currentIndex = 3;
                if(application.isLogin()){
                    setTabSelection(3);
                }else{
                   Intent intent = new Intent(this, Activity_Login.class);
                    startActivity(intent);
                }

                break;

        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    private void showAlertView() {
        new AlertView(null,null,"取消",null,new String[]{"热气球(视频)","热气球(照片)","降落伞 (视频)","降落伞 (照片)"},this, AlertView.Style.ActionSheet
            ,new OnItemClickListener(){
            @Override
            public void onItemClick(Object o, int position) {

                Intent intent = null;
                if(!Utils.GPSIsOPen(AppMainActivity.this)){
                    popupDialog();
                }else{
                    switch (position) {
                        case 0:
                            if(!application.isLogin()){
                                intent = new Intent(AppMainActivity.this,Activity_Login.class);
                            }else {
                                intent = new Intent(AppMainActivity.this, SendBalloonActivity.class);
                                intent.putExtra("type", ApplicationConstant.MEDIA_TYPE_VIDEO);
                            }
                            break;
                        case 1:
                            if(!application.isLogin()){
                                intent = new Intent(AppMainActivity.this,Activity_Login.class);
                            }else {
                                intent = new Intent(AppMainActivity.this, SendBalloonActivity.class);
                                intent.putExtra("type", ApplicationConstant.MEDIA_TYPE_PHOTO);
                            }

                            break;
                        case 2:
                            if(!application.isLogin()){
                                intent = new Intent(AppMainActivity.this,Activity_Login.class);
                            }else {
                                intent = new Intent(AppMainActivity.this, DoSendTask.class);
                                intent.putExtra("enterType", ApplicationConstant.MEDIA_TYPE_VIDEO);
                            }

                            break;
                        case 3:

                            if(!application.isLogin()){
                                intent = new Intent(AppMainActivity.this,Activity_Login.class);
                            }else {
                                intent = new Intent(AppMainActivity.this, DoSendTask.class);
                                intent.putExtra("enterType", ApplicationConstant.MEDIA_TYPE_PHOTO);
                            }
                            break;
                    }
                }
                if(intent != null){
                    startActivity(intent);
                }
            }
        }).setCancelable(true).show();
    }

    private void popupDialog() {
        new AlertDialog(this).builder()
                .setMsg("请开启定位！")
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).setPositiveButton("确定",new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).show();
    }

    /**
     2.      * 根据传入的index参数来设置选中的tab页。
     3.      *
     4.      */
    @SuppressLint("NewApi")
         private void setTabSelection(int index)
         {
             // 重置按钮
             resetTabBtn();
              // 开启一个Fragment事务
              switch (index)
              {
                 case 0:
                         // 当点击了消息tab时，改变控件的图片和文字颜色
                     imgHome.setImageResource(R.mipmap.tab_parachute2_75);
                     tvHome.setTextColor(ContextCompat.getColor(AppMainActivity.this,R.color.material_blue));
                     mViewPager.setCurrentItem(0,false);
                break;
            case 1:
                    // 当点击了消息tab时，改变控件的图片和文字颜色
                imgNearby.setImageResource(R.mipmap.tab_map2_75);
                tvNearby.setTextColor(ContextCompat.getColor(AppMainActivity.this,R.color.material_blue));
                mViewPager.setCurrentItem(1,false);
                break;
              case 2:
                  // 当点击了动态tab时，改变控件的图片和文字颜色
                  imgDiscover.setImageResource(R.mipmap.tab_discovery2_75);
                  tvDiscover.setTextColor(ContextCompat.getColor(AppMainActivity.this,R.color.material_blue));
                  mViewPager.setCurrentItem(2,false);
                  break;
            case 3:
                    // 当点击了设置tab时，改变控件的图片和文字颜色
                imgUserInfo.setImageResource(R.mipmap.tab_me2_75);
                tvUserInfo.setTextColor(ContextCompat.getColor(AppMainActivity.this,R.color.material_blue));
                mViewPager.setCurrentItem(3,false);
                break;
            }
         }
    /**
     * popupWindow解决方法
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            showIntroduceView();
        }
    }
    private void showIntroduceView() {
        /**
         * 程序安装后的第一次启动显示指导界面
         */
        if(CustomApplication.getInstance().isFirstLoad()){
            View view = LayoutInflater.from(this).inflate(R.layout.main_introduce_popup,null);
            final PopupWindow popupIntroduce = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomApplication.getInstance().setFirstLoad(false);
                    popupIntroduce.dismiss();
                }
            });
            popupIntroduce.setFocusable(true);
            ColorDrawable cd = new ColorDrawable(0x000000);
            popupIntroduce.setBackgroundDrawable(cd);
            popupIntroduce.showAtLocation(this.findViewById(R.id.main_root), Gravity.BOTTOM,0,0);

        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (CustomApplication.isRegistered) {
            appUpdate.callOnPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!CustomApplication.isRegistered) {
            appUpdate.callOnResume();
        }
    }

    //退出程序监听
    int keyBackClickCount = 0;
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent. KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN ) {

            switch ( keyBackClickCount++) {
                case 0:
                    Toast. makeText(this, "再按一次退出程序 ", Toast.LENGTH_SHORT).show();
                    Timer timer = new Timer();

                    timer.schedule( new TimerTask() {
                        @Override
                        public void run() {
                            keyBackClickCount = 0;
                        }
                    }, 3000);//延迟三秒
                    break;
                case 1:
                    this.finish();
                    if(myBroadCastReceiver != null){

                        CustomApplication.unregisterLocalReceiver(myBroadCastReceiver);
                        myBroadCastReceiver = null;
                    }
                    this.stopService(new Intent(AppMainActivity.this,InstanceMessageService.class));
                    System.exit(0);
                    break;
                default:
                    break;
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
