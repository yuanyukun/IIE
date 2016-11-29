/*
 * Copyright (c) 2015, 张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.horem.parachute.mine;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.common.HttpUrlConstant;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.main.ExitSystemHttpImpl;
import com.horem.parachute.mine.bean.ChatMessageBean;
import com.horem.parachute.mine.bean.ChatMessageListBean;
import com.horem.parachute.mine.bean.ChatMessageListNewBean;
import com.horem.parachute.mine.bean.ChatMessagePersonBean;
import com.horem.parachute.mine.bean.ChatUserListBean;
import com.horem.parachute.mine.bean.HomeChatMessageBean;

import org.json.JSONObject;
import org.kymjs.chat.OnOperationListener;
import com.horem.parachute.adapter.ChatAdapter;
import com.horem.parachute.mine.bean.MessageBean;
import com.horem.parachute.util.MyTimeUtil;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.horem.parachute.util.Utils;
import com.http.request.HttpApi;
import com.http.request.IResponseApi;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import org.kymjs.chat.bean.Emojicon;
import org.kymjs.chat.bean.Faceicon;
import org.kymjs.chat.bean.Message;
import org.kymjs.chat.emoji.DisplayRules;
import org.kymjs.chat.widget.KJChatKeyboard;
import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.ViewInject;
import org.kymjs.kjframe.utils.FileUtils;
import org.kymjs.kjframe.utils.KJLoger;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 聊天主界面
 */
public class ChatActivity extends KJActivity{

    public static final int REQUEST_CODE_GETIMAGE_BYSDCARD = 0x1;
    private static final  int PAGE_SIZE = 10;
    private KJChatKeyboard box;
    private ListView mRealListView;

    private TextView titleBack;
    private TextView titleName;
    private TextView titleNext;
    private TextView chatHistory;

//    private HomeChatMessageBean homeChatMessageBean;
    private String personalId;
    private String personalType;
    private String userName;

    private HashMap<String,String> params;
    private long memberId;
    private String currentTime = "";
    private String theLatestTime = "";               //定时查询新的消息
    private Timer myTimer;
    private View view;      //listviw 头文件

    private int senderType;
    private int receiveperType;

    List<Message> datas = new ArrayList<>();
    List<Message> tmp = new ArrayList<>();
    List<ChatMessageBean> messages = new ArrayList<>();
    private ChatAdapter adapter;
    private boolean getHistoryMessage = false;

    private CustomApplication application;
    private Tracker mTracker;
    private static final String TAG = "ChatAty";
    private boolean shouldClosed = false;

    private String fromUserHeadUrl = null;
    private String toUserHeadUrl = null;
    private String fromUserHeadName;
    private String toUserHeadName;
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
    public void setRootView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(org.kymjs.chat.R.layout.activity_chat);
    }

    @Override
    public void initWidget() {
        super.initWidget();

        Intent intent = getIntent();
        personalId  = intent.getStringExtra("currentId");
        personalType = intent.getStringExtra("type");
        userName = intent.getStringExtra("name");

        params = new HashMap<>();
        memberId = SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)-1);
        //初始化标题栏
        titleBack = (TextView) findViewById(R.id.title_back);
        titleName = (TextView) findViewById(R.id.title_name);
        titleNext = (TextView) findViewById(R.id.title_next);
        titleBack.setClickable(true);
        titleBack.setText("聊聊");
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shouldClosed = true;
                Intent intent = new Intent(ChatActivity.this,HomeChatActivity.class);
                startActivity(intent);
                finish();
            }
        });
        titleName.setText(userName);
        titleNext.setVisibility(View.INVISIBLE);

        //输入框
        box = (KJChatKeyboard) findViewById(org.kymjs.chat.R.id.chat_msg_input_box);
        mRealListView = (ListView) findViewById(org.kymjs.chat.R.id.chat_listview);
        mRealListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        mRealListView.setSelector(android.R.color.transparent);

        view = LayoutInflater.from(this).inflate(R.layout.chat_message_header_view,null);
        chatHistory = (TextView) view.findViewById(R.id.chat_message_history);

        chatHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHistoryMessage = true;
                httpRequestData(currentTime);
            }
        });
        mRealListView.addHeaderView(view);
        initMessageInputToolBox();
        initListView();
        getUserList();
       
    }

    private void getUserList() {
        HashMap<String,String> params = new HashMap<>();
        params.put("personalType",personalType);
        params.put("personalId",personalId);

        params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lat","");
        params.put("lng","");
        params.put("deviceType","android");
        params.put("clientId", SharePreferencesUtils.getString(this,SharePrefConstant.INSTALL_CODE,""));

        OkHttpClientManager.postAsyn(HttpUrlConstant.messageUserList, new OkHttpClientManager.ResultCallback<ChatUserListBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(ChatUserListBean response) {
//                Log.d("获取聊聊用户列表",new Gson().toJson(response));
                if(response.getStatusCode() == 1) {
                    for (ChatMessagePersonBean bean : response.getResult()) {
                        if (bean.getMemberId() == SharePreferencesUtils.getLong(ChatActivity.this,
                                SharePrefConstant.MEMBER_ID, (long) 0)) {
                            fromUserHeadUrl = Utils.getHeadeUrl(bean.getMemberHead());
//                            Log.d(getClass().getName(),"自己的头像URl"+fromUserHeadUrl);
                            fromUserHeadName = bean.getMemberName();
                            senderType = bean.getAuthType();
                        } else {
                            toUserHeadUrl = Utils.getHeadeUrl(bean.getMemberHead());
                            toUserHeadName  = bean.getMemberName();
                            receiveperType = bean.getAuthType();
//                            Log.d(getClass().getName(),"对方的头像URl"+toUserHeadUrl);
                        }
                    }
                    initialData();
                }else if(response.getStatusCode() == -999){
                    HttpApi httpApi = new ExitSystemHttpImpl();
                    httpApi.httpRequest(getApplicationContext(), new IResponseApi() {
                        @Override
                        public void onSuccess(Object object) {

                        }

                        @Override
                        public void onFailed(Exception e) {

                        }
                    },new HashMap<String, String>());
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }
        },params);

    }


    private void initialData() {
        boolean debug = false;
        if(debug){
            debugData();
        }else{
            httpRequestData("");
        }

    }

    private void httpRequestData(String currentStr) {
        params.clear();
        params.put("memberId",String.valueOf(memberId));
        params.put("lng","");
        params.put("lat","");
        params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");

        params.put("personalType",personalType);
        params.put("personalId",personalId);
        params.put("pageSize",String.valueOf(PAGE_SIZE));
        params.put("currentTime",currentStr);
        //currentTime传"" 获取最新的十条消息
        OkHttpClientManager.postAsyn(HttpUrlConstant.messageDetailForPage,
                new OkHttpClientManager.ResultCallback<ChatMessageListNewBean>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                    }
                    @Override
                    public void onResponse(ChatMessageListNewBean response) {
                        Log.d("获取最新十条消息",new Gson().toJson(response));
                        if(response.getStatusCode() ==1) {
                            if (getHistoryMessage) {
                                getHistoryResponed(response);
                            } else {
                                //进入聊天界面时的获取的数据处理
                                initDataResponse(response);
                            }
                        }else if(response.getStatusCode() == -999){
                            HttpApi httpApi = new ExitSystemHttpImpl();
                            httpApi.httpRequest(getApplicationContext(), new IResponseApi() {
                                @Override
                                public void onSuccess(Object object) {

                                }

                                @Override
                                public void onFailed(Exception e) {

                                }
                            },new HashMap<String, String>());
                        }else
                            ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                    }
                },params);
    }

    private void initDataResponse(ChatMessageListNewBean response) {
        if(response != null && response.getResult().getList() != null){
            if(response.getResult().getList().size() > 0){
                messages = response.getResult().getList();
                for(ChatMessageBean bean:response.getResult().getList()){
                    Message message = null;
                    if(memberId == bean.getSendMemberId()){
                        message = new Message(Message.MSG_TYPE_TEXT,
                                Message.MSG_STATE_SUCCESS,userName, fromUserHeadUrl, "Jerry", toUserHeadUrl,
                                new String(bean.getMessageContent()), true, true,  MyTimeUtil.toDate(MyTimeUtil.toDateStr(bean.getCreateTime())));
                    }else {
                        message = new Message(Message.MSG_TYPE_TEXT,
                                Message.MSG_STATE_SUCCESS,userName, fromUserHeadUrl, "Jerry", toUserHeadUrl,
                                new String(bean.getMessageContent()), false, true,  MyTimeUtil.toDate(MyTimeUtil.toDateStr(bean.getCreateTime())));
                    }
                    datas.add(message);
                }
                for(Message message: datas){
                    Log.d("第一次进入聊天界面的消息列表",new Gson().toJson(message));
                }
                adapter.refresh(datas);
                theLatestTime = response.getResult().getList().get(response.getResult().getList().size()-1).getCreateTime();
                currentTime = response.getResult().getList().get(0).getCreateTime();
                mRealListView.setSelection(response.getResult().getList().size()-1);
            }

        }
        SearchingNewMessage();//定时查询有无最新的消息

    }

    private void getHistoryResponed(ChatMessageListNewBean response) {
        tmp.clear();
        Log.d("获取较早历史记录",new Gson().toJson(response));
        if(response.getResult().getList().size() < PAGE_SIZE){
            mRealListView.removeHeaderView(view);
        }
        messages.addAll(0,response.getResult().getList());
        //数组越界
        if(response.getResult().getList().size() > 1){
            for(ChatMessageBean bean:response.getResult().getList()){
                Message message = null;
                if(memberId == bean.getSendMemberId()){
                    message = new Message(Message.MSG_TYPE_TEXT,
                            Message.MSG_STATE_SUCCESS,userName, fromUserHeadUrl, "Jerry", toUserHeadUrl,
                            new String(bean.getMessageContent()), true, true,  MyTimeUtil.toDate(MyTimeUtil.toDateStr(bean.getCreateTime())));
                }else {
                    message = new Message(Message.MSG_TYPE_TEXT,
                            Message.MSG_STATE_SUCCESS,userName, fromUserHeadUrl, "Jerry", toUserHeadUrl,
                            new String(bean.getMessageContent()), false, true,  MyTimeUtil.toDate(MyTimeUtil.toDateStr(bean.getCreateTime())));
                }
                tmp.add(message);
            }
            datas.addAll(0,tmp);
            adapter.refresh(datas);
            adapter.notifyDataSetChanged();
            currentTime = response.getResult().getList().get(0).getCreateTime();
            mRealListView.setSelection(0);
        }
    }

    private void SearchingNewMessage() {
        Log.d("最新消息时间",theLatestTime);
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                httpSearch();
                if(shouldClosed){
                   this.cancel();
                    myTimer = null;
                    finish();
                }
            }
        },1000,10000);
    }

    /**
     * 刷新界面
     */

    private void httpSearch(){
        params.clear();
        params.put("memberId",String.valueOf(memberId));
        params.put("lng","");
        params.put("lat","");
        params.put("clientId", SharePreferencesUtils.getString(ChatActivity.this, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");

        params.put("personalType",String.valueOf(receiveperType));
        params.put("personalId",personalId);
        params.put("currentTime",theLatestTime);
//        Log.d("最新消息时间",theLatestTime);
        OkHttpClientManager.postAsyn(HttpUrlConstant.messageDetailCurrent, new OkHttpClientManager.ResultCallback<ChatMessageListNewBean>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(ChatMessageListNewBean response) {
                Log.d(getClass().getName(),new Gson().toJson(response));
                if(response.getStatusCode() == 1) {
                    if (response.getResult().getList() != null) {
                        whetherHasNewMessage(response.getResult().getList());
                    }
                }else if(response.getStatusCode() == -999){
                    HttpApi httpApi = new ExitSystemHttpImpl();
                    httpApi.httpRequest(getApplicationContext(), new IResponseApi() {
                        @Override
                        public void onSuccess(Object object) {

                        }

                        @Override
                        public void onFailed(Exception e) {

                        }
                    },new HashMap<String, String>());
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");

            }
        },params);
    }

    private void whetherHasNewMessage(List<ChatMessageBean> list) {
        boolean flag;
        for(ChatMessageBean bean:list){
            String ID = bean.getId();
            flag = false;
            for(ChatMessageBean bean1:messages){
                if(ID.equals(bean1.getId())){
                    flag = true;
                }else{ }
            }
            if(!flag){
                Log.d("有最新消息",new Gson().toJson(bean));
                messages.add(bean);
                theLatestTime = bean.getCreateTime();
                Message msg;
                if(bean.getSenderId() == memberId){
                     msg = new Message(Message.MSG_TYPE_TEXT,
                             Message.MSG_STATE_SUCCESS,userName, fromUserHeadUrl, "Jerry", toUserHeadUrl,
                             new String(bean.getMessageContent()), true, true, MyTimeUtil.toDate(MyTimeUtil.toDateStr(bean.getCreateTime())));
                }else{
                    msg = new Message(Message.MSG_TYPE_TEXT,
                            Message.MSG_STATE_SUCCESS,userName, fromUserHeadUrl, "Jerry", toUserHeadUrl,
                            new String(bean.getMessageContent()), false, true,  MyTimeUtil.toDate(MyTimeUtil.toDateStr(bean.getCreateTime())));
                }
                datas.add(msg);
            }
        }
        adapter.refresh(datas);
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(myTimer != null){
            myTimer.cancel();
            myTimer.cancel();
        }
        shouldClosed = true;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(myTimer != null){
            myTimer.cancel();
            myTimer = null;
        }
        shouldClosed = true;
        this.finish();
    }

    private void debugData() {
        AssetManager am  = getAssets();
        InputStream ins ;
        try{
            ins = am.open("1.txt");
            InputStreamReader insReader = new InputStreamReader(ins);
            BufferedReader bufferedReader = new BufferedReader(insReader);
            String line = "";
            String lineUnicode;
            StringBuilder Result = new StringBuilder();
            while((line = bufferedReader.readLine()) != null){
//                    lineUnicode = URLDecoder.decode(line,"UTF-8");
                lineUnicode = URLDecoder.decode(line,"GBK");
                Result.append(lineUnicode);
            }
            String jsonStr = Result.toString();
            JSONObject json = new JSONObject(jsonStr);

            ChatMessageListBean response = new Gson().fromJson(jsonStr,ChatMessageListBean.class);

            for(ChatMessageBean bean:response.getList()){
                Message message;
                if(memberId == bean.getSendMemberId()){
                    message = new Message(Message.MSG_TYPE_TEXT,
                            Message.MSG_STATE_SUCCESS,"Tom", fromUserHeadUrl, "Jerry", toUserHeadUrl,
                            new String(bean.getMessageContent()), true, true, new Date(System.currentTimeMillis()
                            - (1000 * 60 * 60 * 24) * 8));
                }else {
                    message = new Message(Message.MSG_TYPE_TEXT,
                            Message.MSG_STATE_SUCCESS,"Tom", fromUserHeadUrl, "Jerry", toUserHeadUrl,
                            new String(bean.getMessageContent()), false, true, new Date(System.currentTimeMillis()
                            - (1000 * 60 * 60 * 24) * 8));
                }
                datas.add(message);
            }
            adapter.refresh(datas);
            adapter.notifyDataSetChanged();


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initMessageInputToolBox() {
        box.setOnOperationListener(new OnOperationListener() {
            @Override
            public void send(String content) {
//                createReplayMsg(message);
                mRealListView.setSelection(datas.size() - 1);
                sendMessageToServer(content);
            }

            @Override
            public void selectedFace(Faceicon content) {
                Message message = new Message(Message.MSG_TYPE_FACE, Message.MSG_STATE_SUCCESS,
                        "Tom", fromUserHeadUrl, "Jerry", toUserHeadUrl, content.getPath(), true, true, new
                        Date());
                datas.add(message);
                adapter.refresh(datas);
//                createReplayMsg(message);
            }

            @Override
            public void selectedEmoji(Emojicon emoji) {
                box.getEditTextBox().append(emoji.getValue());
            }

            @Override
            public void selectedBackSpace(Emojicon back) {
                DisplayRules.backspace(box.getEditTextBox());
            }

            @Override
            public void selectedFunction(int index) {
                switch (index) {
                    case 0:
                        goToAlbum();
                        break;
                    case 1:
                        ViewInject.toast("跳转相机");
                        break;
                }
            }
        });

        List<String> faceCagegory = new ArrayList<>();
//        File faceList = FileUtils.getSaveFolder("chat");
        File faceList = new File("");
        if (faceList.isDirectory()) {
            File[] faceFolderArray = faceList.listFiles();
            for (File folder : faceFolderArray) {
                if (!folder.isHidden()) {
                    faceCagegory.add(folder.getAbsolutePath());
                }
            }
        }

        box.setFaceData(faceCagegory);
        mRealListView.setOnTouchListener(getOnTouchListener());
    }

    private void sendMessageToServer(final String content) {
        params.clear();
        params.put("senderType",String.valueOf(senderType));    //发送者类型
        params.put("messageContent",content);           //发送内容
        params.put("recipientType",String.valueOf(receiveperType)); //接收者类型
        params.put("senderId",memberId+"");             //发送者Id
        params.put("recipientId",personalId);           //接收者Id

        params.put("memeberId",SharePreferencesUtils.getLong(this,SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lat","");
        params.put("lng","");
        params.put("clientId",SharePreferencesUtils.getString(this,SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");

        OkHttpClientManager.postAsyn(HttpUrlConstant.messageSend, new OkHttpClientManager.ResultCallback<MessageBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(MessageBean response) {
                Log.d("chatActivity",new Gson().toJson(response));
                if(1 == response.getStatusCode()){
                    //主动调用刷新借口
                    httpSearch();
                }else if(response.getStatusCode() == -999){
                    HttpApi httpApi = new ExitSystemHttpImpl();
                    httpApi.httpRequest(getApplicationContext(), new IResponseApi() {
                        @Override
                        public void onSuccess(Object object) {

                        }

                        @Override
                        public void onFailed(Exception e) {

                        }
                    },new HashMap<String, String>());
                }else{
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                }
            }
        },params);


    }

    private void initListView() {
        adapter = new ChatAdapter(this, datas,getOnChatItemClickListener());
        mRealListView.setAdapter(adapter);
    }

//    private void createReplayMsg(Message message) {
//        final Message reMessage = new Message(message.getType(), Message.MSG_STATE_SUCCESS, "Tom",
//                fromUserHeadUrl, "Jerry", toUserHeadUrl, message.getType() == Message.MSG_TYPE_TEXT ? "返回:"
//                + message.getContent() : message.getContent(), false,
//                true, new Date());
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(1000 * (new Random().nextInt(3) + 1));
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            datas.add(reMessage);
//                            adapter.refresh(datas);
//                        }
//                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && box.isShow()) {
            box.hideLayout();
            return true;
        } else {
            shouldClosed = true;
            if(myTimer != null) {
                myTimer.cancel();
                myTimer = null;
            }
            return true;
        }
    }

    /**
     * 跳转到选择相册界面
     */
    private void goToAlbum() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"),
                    REQUEST_CODE_GETIMAGE_BYSDCARD);
        } else {
            intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"),
                    REQUEST_CODE_GETIMAGE_BYSDCARD);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_GETIMAGE_BYSDCARD) {
            Uri dataUri = data.getData();
            if (dataUri != null) {
                File file = FileUtils.uri2File(aty, dataUri);
                Message message = new Message(Message.MSG_TYPE_PHOTO, Message.MSG_STATE_SUCCESS,
                        "Tom", "avatar", "Jerry",
                        "avatar", file.getAbsolutePath(), true, true, new Date());
                datas.add(message);
                adapter.refresh(datas);
            }
        }
    }

    /**
     * 若软键盘或表情键盘弹起，点击上端空白处应该隐藏输入法键盘
     *
     * @return 会隐藏输入法键盘的触摸事件监听器
     */
    private View.OnTouchListener getOnTouchListener() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                box.hideLayout();
                box.hideKeyboard(aty);
                return false;
            }
        };
    }

    /**
     * @return 聊天列表内存点击事件监听器
     */
    private OnChatItemClickListener getOnChatItemClickListener() {
        return new OnChatItemClickListener() {
            @Override
            public void onPhotoClick(int position) {
                KJLoger.debug(datas.get(position).getContent() + "点击图片的");
                ViewInject.toast(aty, datas.get(position).getContent() + "点击图片的");
            }

            @Override
            public void onTextClick(int position) {

            }

            @Override
            public void onFaceClick(int position) {

            }
        };
    }

    /**
     * 聊天列表中对内容的点击事件监听
     */
    public interface OnChatItemClickListener {
        void onPhotoClick(int position);

        void onTextClick(int position);

        void onFaceClick(int position);
    }
}
