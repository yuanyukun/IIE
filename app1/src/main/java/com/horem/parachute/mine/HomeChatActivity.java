package com.horem.parachute.mine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.common.HttpUrlConstant;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.balloon.FlowersToMeListActivity;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.customview.HorizontalSlideAdapter;
import com.horem.parachute.customview.HorizontalSlideDeleteListView;
import com.horem.parachute.mine.bean.GiveLikeMeListItemBean;
import com.horem.parachute.mine.bean.HomeChatBean;
import com.horem.parachute.mine.bean.HomeChatMessageBean;
import com.horem.parachute.mine.bean.HomeChatNewBean;
import com.horem.parachute.mine.bean.MessageContractBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeChatActivity extends BaseActivity {

    private HorizontalSlideDeleteListView mListView;
    private HorizontalSlideAdapter mAdapter;
    private ArrayList<HomeChatMessageBean> mLists = new ArrayList<>();
    private HashMap<String,String> params = new HashMap<>();
    private long memberId;

    private myHomeChatReceiver myBroadCastReceiver;
    private CustomApplication application;
    private Tracker mTracker;
    private static final String TAG = "HomeChat";

    private ImageView seeMeRedDot;
    private ImageView sendFlowersRedDot;
    private ImageView giveMeListRedDot;

    private static final int GIVE_LIKE_ME = 0x11;
    @Override
    protected void onStart() {
        super.onStart();
        //google analytics
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(this.getPackageName()+" [Android] " + TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Share")
                .build());
    }

    @Override
    protected void OnLeftButtonClicked() {
        super.OnLeftButtonClicked();
        CustomApplication.unregisterLocalReceiver(myBroadCastReceiver);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CustomApplication.unregisterLocalReceiver(myBroadCastReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_chat);
        registerCustomReceiver();
        init();
        initialDate();
    }

    /**
     * @我的
     * @param v
     */
    public void onTips(View v){

    }
    public void onGiveLikeMe(View v){
        CustomApplication.getInstance().setGiveLikeMeList(false);
        Intent intent = new Intent(this,GiveLikeMeListActivity.class);
        intent.putExtra("flag",GIVE_LIKE_ME);
        intent.setAction("com.horem.parachute.Status");
        CustomApplication.sendLocalBroadcast(intent);
        startActivity(intent);
    }
    /**
     * 看过我的
     * @param v
     */
    public void onSeeMe(View v){
        CustomApplication.getInstance().setViewUserList(false);
        Intent intent = new Intent(this,Activity_Mine_See_Me.class);
        intent.setAction("com.horem.parachute.Status");
        CustomApplication.sendLocalBroadcast(intent);
        startActivity(intent);
    }

    /**
     * 送花给我的
     * @param v
     */
    public void onFlowers(View v){
        CustomApplication.getInstance().setFlowersToMeList(false);
        Intent intent = new Intent(this, FlowersToMeListActivity.class);
        intent.setAction("com.horem.parachute.Status");
        CustomApplication.sendLocalBroadcast(intent);
        startActivity(intent);
    }

    private void initialDate() {
        startLoading();
        params.clear();
        params.put("memberId",String.valueOf(memberId));
        params.put("lng","");
        params.put("lat","");
        params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");

//        for(Map.Entry<String,String> entry:params.entrySet()){
//            Log.d("聊聊首页",entry.getKey()+" "+entry.getValue());
//        }
        OkHttpClientManager.postAsyn(HttpUrlConstant.chatMessageHome, new OkHttpClientManager.ResultCallback<HomeChatNewBean>() {
            @Override
            public void onError(Request request, Exception e) {
                stopLoading();
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }
            @Override
            public void onResponse(HomeChatNewBean response) {
                stopLoading();
                Log.d(TAG,new Gson().toJson(response));
                if(response.getStatusCode() == 1){
                    mLists.clear();
                    mLists = response.getResult();
                    mAdapter.SetData( mLists);
//                    mAdapter.notifyDataSetChanged();
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");

            }
        },params);
    }

    private void init() {
        application = (CustomApplication) getApplication();
        initTitleView();
        setTitleName("聊聊");
        memberId = SharePreferencesUtils.getLong(this,SharePrefConstant.MEMBER_ID,(long)-1);
        seeMeRedDot = (ImageView) findViewById(R.id.see_me_red_button);
        sendFlowersRedDot  = (ImageView) findViewById(R.id.send_flowers_red_button);
        giveMeListRedDot  = (ImageView) findViewById(R.id.give_like_red_button);

        mListView = (HorizontalSlideDeleteListView) findViewById(R.id.home_chat_list_view);
        mAdapter = new HorizontalSlideAdapter(this,mLists);
        mListView.setAdapter(mAdapter);

        mAdapter.setOnItemDelete(new HorizontalSlideAdapter.OnItemDelete() {
            @Override
            public void itemClicked(final int itemPosition, final View view) {

                params.clear();
                params.put("memberId",""+memberId);
                params.put("personalType",mLists.get(itemPosition).getPersonalType()+"");
                params.put("personalId",mLists.get(itemPosition).getPersonalId()+"");

                new AlertView(null, null, "取消",null, new String[]{"删除", "删除并加入黑名单"}, HomeChatActivity.this, AlertView.Style.ActionSheet,
                        new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position) {
                                switch (position){
                                    case 0:
                                        deleteUser(false,params,itemPosition,view);
                                        break;
                                    case 1:
                                        deleteUser(true,params,itemPosition,view);
                                        break;
                                }
                            }
                        }).setCancelable(true).show();
            }
        });
        mAdapter.setOnWholeItemClicked(new HorizontalSlideAdapter.OnWholeItemClicked() {
            @Override
            public void wholeItemClicked(int position) {
//                Log.d("聊聊首面","Item点击事件触发"+position);
                mLists.get(position).setHasNewMessage(false);
                Intent  intent = new Intent();
                intent.setClass(HomeChatActivity.this,ChatActivity.class);
                intent.putExtra("currentId",mLists.get(position).getPersonalId()+"");
                intent.putExtra("type",mLists.get(position).getPersonalType()+"");
                intent.putExtra("name",mLists.get(position).getPersonalName());
                startActivity(intent);
                finish();
            }
        });
        //新消息
        updateUI();
    }
    //新消息
    private void updateUI() {
        //送花给我
        if(application.flowersToMeList){
            sendFlowersRedDot.setVisibility(View.VISIBLE);
        }else{
            sendFlowersRedDot.setVisibility(View.INVISIBLE);
        }
        //看过我的
        if(application.isViewUserList){
            seeMeRedDot.setVisibility(View.VISIBLE);
        }else{
            seeMeRedDot.setVisibility(View.INVISIBLE);
        }
        //赞过我的
        if(application.isGiveLikeMeList()){
            giveMeListRedDot.setVisibility(View.VISIBLE);
        }else{
            giveMeListRedDot.setVisibility(View.INVISIBLE);
        }
    }

    private void deleteUser(final boolean addBlacklists, final HashMap<String,String> map,
                            final int position, final View view) {
        OkHttpClientManager.getAsyn(HttpUrlConstant.messageDeleteContracts, new OkHttpClientManager.ResultCallback<MessageContractBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(MessageContractBean response) {
                if(response.getStatusCode() == -999){
                    exitApp();
                }else if (response.getStatusCode() == 1) {
                    Animation animation = AnimationUtils.loadAnimation(HomeChatActivity.this,
                            R.anim.anim_item_delete);
                    HorizontalSlideAdapter.ViewHolder holder = (HorizontalSlideAdapter.ViewHolder) view.getTag();
                    holder.scrollView.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mLists.remove(position);
                            mAdapter.SetData(mLists);
                            mAdapter.notifyDataSetChanged();
                        }
                    });

                    if (addBlacklists && 1 == response.getStatusCode()) {
                        OkHttpClientManager.getAsyn(HttpUrlConstant.messageAddBlackLists, new OkHttpClientManager.ResultCallback<MessageContractBean>() {
                            @Override
                            public void onError(Request request, Exception e) {
                                ToastManager.show(getApplicationContext(), "噢，网络不给力！");
                            }

                            @Override
                            public void onResponse(MessageContractBean response) {
                                if (1 == response.getStatusCode()) {
                                    ToastManager.show(HomeChatActivity.this, response.getMessage());
                                } else if(response.getStatusCode() == -999){
                                    exitApp();
                                }else
                                    ToastManager.show(getApplicationContext(), "噢，网络不给力！");
                            }
                        }, map);
                    }
                }
            }
        },map);
    }
    private void registerCustomReceiver() {
        if(myBroadCastReceiver == null) {
            myBroadCastReceiver = new myHomeChatReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.horem.parachute.Status");
        //注册一个接收的广播
        CustomApplication.registerLocalReceiver(myBroadCastReceiver, filter);
    }
    public class myHomeChatReceiver extends BroadcastReceiver {
        public myHomeChatReceiver() {
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("HomeCHat 收到广播",action);
            if (action.equals("com.horem.parachute.Status")) {
                //updateUI here
                updateUI();
            }
        }
    }
}
