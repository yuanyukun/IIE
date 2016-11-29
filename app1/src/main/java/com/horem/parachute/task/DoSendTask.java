package com.horem.parachute.task;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.app.utils.SignatureTool;
import com.common.ApplicationConstant;
import com.common.HttpUrlConstant;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.task.bean.LocationConfirmBean;
import com.horem.parachute.task.bean.PlaceBean;
import com.horem.parachute.task.bean.SendTaskBean;
import com.horem.parachute.task.bean.SendTaskPhotoBean;
import com.horem.parachute.util.BitmapUtils;
import com.horem.parachute.util.ScreenBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.senab.photoview.PhotoView;

public class DoSendTask extends BaseActivity implements View.OnClickListener,AMapLocationListener {

    private TextView tvTaskLocation;
    private TextView tvTaskUserNum;
    private TextView tvTaskCostTotal;
    private TextView tvTaskTimeOut;
    private EditText editSendMsg;
    private String strLocationMsg;
    private ImageView taskImg;
    private VideoView mVideoView;

    private int ActivityType;
    private double Latitude;
    private double longtitude;
    private LatLng currentLatLng;


    private List<LocationConfirmBean> mList = new ArrayList<>();
    private static final int REQUEST_LOCATION = 0x1001;
    private static final int REQUEST_PERSONER = 0x1002;
    private static final int REQUEST_COSTS    = 0x1003;
    private static final int REQUEST_EXPIRATE = 0x1004;
    private int CurrentRequestCode;
    private List<LatLng> latLngs = new ArrayList<>();
    private long userId;
    private int currentNum = 1;     //(1~100)
    private int currentLevel = 0;   //(0~600)
    private int levelMin;           //最小级数
    private int currentTime = 1;    //超时时间

    private ArrayList<Bitmap> lists = new ArrayList<>();
    private String cacheDirectory;
    private File cameraFile;
    private static final int REQUEST_CODE_CAMERA = 0x1005;
    private static final int REQUEST_CODE_VIDEO = 0x1006;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private File compressFile;


    private String videoFilePath = null;
    private static  final  int RECORD_VIDEO = 0x1007;
    private  HashMap<String,String> params = new HashMap<>();

    private CustomApplication application;
    private Tracker mTracker;
    private static  final  String TAG = "DoSendTask";
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
        setContentView(R.layout.activity_do_send_task);
        init();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mLocationClient.stopLocation();
        mLocationClient = null;
    }

    private void init() {
        //初始化标题栏
        initTitleView();
        initMap();


        ActivityType = getIntent().getIntExtra("enterType",-1);
        userId = SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0);
        switch (ActivityType){
            case ApplicationConstant.MEDIA_TYPE_PHOTO:setTitleName("投照片伞");
                break;
            case ApplicationConstant.MEDIA_TYPE_VIDEO:setTitleName("投视频伞");
                break;
        }
        setRightButtonText("发送");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        ScreenBean.setScreenWidth(screenWidth);
        ScreenBean.setScreenHeight(screenHeight);
       //监听触摸事件
       findViewById(R.id.do_send_task_location).setOnClickListener(this);
       findViewById(R.id.do_send_task_user_number).setOnClickListener(this);
       findViewById(R.id.do_send_task_cost).setOnClickListener(this);
       findViewById(R.id.do_send_task_timeout).setOnClickListener(this);
       //显示textview
       tvTaskLocation = (TextView) findViewById(R.id.do_send_task_location_tv);
       tvTaskUserNum = (TextView) findViewById(R.id.do_send_task_user_number_tv);
       tvTaskCostTotal = (TextView) findViewById(R.id.do_send_task_cost_tv);
       tvTaskTimeOut = (TextView) findViewById(R.id.do_send_task_timeout_tv);
        editSendMsg = (EditText) findViewById(R.id.activity_task_send_et);

        taskImg = (ImageView) findViewById(R.id.send_task_image);
        mVideoView = (VideoView) findViewById(R.id.send_task_video_view);
        taskImg.setOnClickListener(this);

        tvTaskUserNum.setText("1人");
        tvTaskTimeOut.setText("1小时");

        //创建文件夹
        cacheDirectory = ApplicationConstant.MEDIA_FILES;
        File fileDirectory = new File(cacheDirectory);
        if(!fileDirectory.exists()){
            fileDirectory.mkdir();
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_LOCATION:
                if(RESULT_OK == resultCode && data != null){
                    mList = (List<LocationConfirmBean>) data.getBundleExtra("bundle").getSerializable("data");
                    latLngs = (List<LatLng>) data.getBundleExtra("bundle").getSerializable("latlng");
                    if(mList.size() == 1){
                        tvTaskLocation.setText(mList.get(0).getLocationDescribe());
                    }else if(mList.size() > 1){
                        tvTaskLocation.setText(String.valueOf(mList.size())+"地点");
                    }else {
                        tvTaskCostTotal.setText(" ");
                    }

                    getTotalFeeImpl(userId,currentLevel,currentNum);

                }
                break;
            case REQUEST_PERSONER:
                if(RESULT_OK == resultCode && data != null){
                    currentNum = data.getIntExtra("progress",0);
                    tvTaskUserNum.setText(currentNum+"人");
                    getTotalFeeImpl(userId,currentLevel,currentNum);
                }
                break;
            case REQUEST_COSTS:
                if(RESULT_OK == resultCode && data != null){
                    currentLevel = data.getIntExtra("progress",0);
                    getTotalFeeImpl(userId,currentLevel,currentNum);
                }
                break;
            case REQUEST_EXPIRATE:
                if(RESULT_OK == resultCode && data != null){
                    currentTime = data.getIntExtra("progress",0);//(1~72)
                    tvTaskTimeOut.setText(""+currentTime+"小时");
                }
                break;
            case REQUEST_CODE_CAMERA:

                if(RESULT_OK == resultCode && cameraFile != null ) {
                    getCameraImage();
                }
                break;
            case RECORD_VIDEO:

                if(RESULT_OK == resultCode && data != null){
                    dealVideoSource(data);
                }

                break;
        }
    }

    private void dealVideoSource(Intent data)  {
            videoFilePath = data.getStringExtra("filePath");
            Bitmap  bmp = ThumbnailUtils.createVideoThumbnail(videoFilePath, MediaStore.Video.Thumbnails.MICRO_KIND);
            taskImg.setImageBitmap(bmp);
    }


    private void getTotalFeeImpl(long memberId,int levelNum,int receiveNum) {

        HashMap<String,String> map = new HashMap<>();
        map.put("mediaType",ActivityType+"");
        map.put("receiveNum",receiveNum+"");
        map.put("placeJson",new Gson().toJson(getPlaceArr()));
        map.put("levelNum",levelNum+"");

        map.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        map.put("lng","");
        map.put("lat","");
        map.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        map.put("deviceType","android");

//        for(Map.Entry<String,String> entry:map.entrySet()){
//            Log.d("获取总费用参数",entry.getKey()+" : "+entry.getValue());
//        }

        OkHttpClientManager.postAsyn(HttpUrlConstant.taskGetTotalFee, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(String response) {
                    try {
                        JSONObject json = new JSONObject(response);
                        if(json.optInt("statusCode") == 1) {
                            tvTaskCostTotal.setText(json.getJSONObject("result").optString("feeStr"));
                            levelMin = json.getJSONObject("result").optInt("levelNumMin");
                            currentLevel = json.getJSONObject("result").optInt("levelNum");
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

    /**
     * 发送按钮的点击事件处理
     */
    @Override
    protected void OnRightButtonClicked() {

        String describe = editSendMsg.getText().toString().trim();
        if(TextUtils.isEmpty(describe)){
            Toast.makeText(this,"请输入您的拍摄要求",Toast.LENGTH_SHORT).show();
            return;
        }
        if(currentLatLng == null){
            ToastManager.show(this,"定位失败，请确认您的网络是否连接正常");
            return;
        }
        if(latLngs.size() == 0){
            ToastManager.show(this,"请选择投伞地点");
            return;
        }
        buildRequestParams(describe);
        switch (ActivityType){
            case ApplicationConstant.MEDIA_TYPE_PHOTO:

                try {
                    buildPhotoTask();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case ApplicationConstant.MEDIA_TYPE_VIDEO:

                buildVideoTask();
                break;
        }

    }

    /**
     * 发送视频任务，分有视频和无视频文件两种，根据videoFilePath是否为空进行判定
     */
    private void buildVideoTask() {
        startLoading();
        if(videoFilePath != null){
            uploadVideoFile(new File(videoFilePath));
        }else{
            uploadVideoFile(null);
        }
    }

    private void uploadVideoFile(File file) {
        try {
            OkHttpClientManager.postAsyn(HttpUrlConstant.taskAddVideoUrl,
                    new OkHttpClientManager.ResultCallback<String>() {
                        @Override
                        public void onError(Request request, Exception e) {
                            ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                        }

                        @Override
                        public void onResponse(String response) {
//                            Log.d(getClass().getName(),new Gson().toJson(response));
                            stopLoading();
                            dealWithSendTaskResult(response);

                        }
                    },file,"",params);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发布图片任务,分带图片和不带图片两种情况
     */
    private void buildPhotoTask() throws IOException {

        /**
         *  1.上传图片,无图片直接执行第2步 2.将上传图片返回的结果连同订单参数一同抛给服务器，无图片，订单参数为空
         */
         startLoading();
        if(compressFile != null){
                OkHttpClientManager.postAsyn(HttpUrlConstant.taskAddPicUploadUrl,
                        new OkHttpClientManager.ResultCallback<SendTaskPhotoBean>() {
                            @Override
                            public void onError(Request request, Exception e) {

                            }

                            @Override
                            public void onResponse(SendTaskPhotoBean response) {

                                    ArrayList<SendTaskPhotoBean> list = new ArrayList<>();
                                    list.add(response);
                                    params.put("attArrayJson", new Gson().toJson(list));
                                    httpRequestPicTask();
                            }

                        },compressFile,"");
        }else{
                    params.put("attArrayJson","");
                    httpRequestPicTask();
        }

    }
    private void httpRequestPicTask() {
        OkHttpClientManager.postAsyn(HttpUrlConstant.taskBuildPhotUrl,
                new OkHttpClientManager.ResultCallback<String>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                    }
                    @Override
                    public void onResponse(String response) {
                        stopLoading();
                        dealWithSendTaskResult(response);
                    }
                },params);
    }

    /**
     * 请求参数设置,发布任务的四种情况的请求参数大同小异，提取出来精简代码
     */
    private void buildRequestParams(String describe) {
        PlaceBean[] placeArr = getPlaceArr();
        SendTaskBean bean  = new SendTaskBean();
        bean.setMemberId(SharePreferencesUtils.getLong(DoSendTask.this, SharePrefConstant.MEMBER_ID,(long)0));
        bean.setLimitDays(currentTime);
        bean.setReceiveNum(currentNum);

        params.clear();
        params.put("receiveNum",currentNum+"");
        params.put("placeJson",new Gson().toJson(placeArr));
        params.put("otherRequirements",describe);
        params.put("isPrivate","0");
        params.put("limitDays",currentTime+"");
        params.put("isAutoUsing","1");
        params.put("sign", SignatureTool.getSignStr(bean));
        params.put("mediaType",ActivityType+"");
        params.put("levelNum",currentLevel+"");

        params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lng",currentLatLng.longitude+"");
        params.put("lat",currentLatLng.latitude+"");
        params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");
    }
    /**
     * 发送任务后的事件处理，跳转到支付界面，同时传递生成的任务Id
     * @param response
     */
    private void dealWithSendTaskResult(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response.toString());
            int status = jsonObject.optInt("statusCode");
            String taskId = jsonObject.optString("result");
//            Log.i("DoSendTaskActivity",taskId);
            if(1 == status){
                SharePreferencesUtils.setBoolean(DoSendTask.this,SharePrefConstant.isNeedUpdateMap,true);
                Intent intent = new Intent(DoSendTask.this, TaskPayActivity.class);
                Bundle bundle =new Bundle();
                bundle.putString("taskId",taskId);
                bundle.putString("intentType","DoSendTask");
                intent.putExtra("bundle",bundle);
                startActivity(intent);
                DoSendTask.this.finish();
            }else if(status == -999){
                exitApp();
            }else{
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @NonNull
    private PlaceBean[] getPlaceArr() {
        PlaceBean[] placeArr = new PlaceBean[latLngs.size()];
        for(int i = 0;i<mList.size();i++){
            PlaceBean placeJson = new PlaceBean();
            placeJson.setAddressId(0);
            placeJson.setLatitude(latLngs.get(i).latitude);
            placeJson.setLongitude(latLngs.get(i).longitude);
            placeJson.setPlace(mList.get(i).getLocationDescribe());
            placeJson.setRadius(0);
            placeArr[i] = placeJson;
        }
        return placeArr;
    }

    @Override
    public void onClick(View v) {
        Intent goNext = null;
        switch (v.getId()){
            case R.id.do_send_task_location:
                goNext = new Intent(this,ConfirmLocationAddress.class);
                goNext.putExtra("enter",ActivityType);
                goNext.putExtra("data", (Serializable) mList);
                goNext.putExtra("latlngs", (Serializable) latLngs);
                CurrentRequestCode = REQUEST_LOCATION;
                break;
            case R.id.do_send_task_user_number:
                if(mList.size() == 0){
                    ToastManager.show(this,"请先选择投伞地点");
                }else{
                    goNext = new Intent(this,NumberSelectionActivity.class);
                    goNext.putExtra("enter",ApplicationConstant.USER_TYPE);
                    goNext.putExtra("chooseNum",currentNum);
                    CurrentRequestCode = REQUEST_PERSONER;
                }

                break;
            case R.id.do_send_task_cost:
                if(mList.size() == 0){
                    ToastManager.show(this,"请先选择投伞地点");
                }else{
                    goNext = new Intent(this,NumberSelectionActivity.class);
                    goNext.putExtra("enter",ApplicationConstant.COST_TYPE);
                    goNext.putExtra("params",new Gson().toJson(getPlaceArr()));
                    goNext.putExtra("currentNum",currentNum);
                    goNext.putExtra("level",currentLevel);
                    goNext.putExtra("minLevel",levelMin);
                    CurrentRequestCode = REQUEST_COSTS;
                }

                break;
            case R.id.do_send_task_timeout:
                goNext = new Intent(this,NumberSelectionActivity.class);
                goNext.putExtra("enter",ApplicationConstant.TIME_TYPE);
                goNext.putExtra("time",currentTime);
                CurrentRequestCode = REQUEST_EXPIRATE;
                break;
            case R.id.send_task_image:

                switch (ActivityType) {
                    case ApplicationConstant.MEDIA_TYPE_PHOTO:

                        if(cameraFile != null && lists.size() >= 0){
                            PopupWindow popupWindow = initPopwindow(lists);
                            popupWindow.showAtLocation(v, Gravity.NO_GRAVITY,0,0);
                        }else{

                            String filename = BitmapUtils.getCurDataStr();
                            cameraFile = new File(cacheDirectory+ File.separator + filename);
                            goNext = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            goNext.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
                            CurrentRequestCode = REQUEST_CODE_CAMERA;
                        }
                        break;
                    case ApplicationConstant.MEDIA_TYPE_VIDEO:
                        if(videoFilePath != null){
                            File file = new File(videoFilePath);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            Uri uri = Uri.parse("file://"+ file.getAbsolutePath());
                            intent.setDataAndType(uri, "video/mp4");
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(this, CameraActivity.class);
                            startActivityForResult(intent,RECORD_VIDEO);
                        }
                        break;
                }
                break;
        }
        if(null != goNext){
            startActivityForResult(goNext,CurrentRequestCode);
        }

    }
    private void getCameraImage() {

        Bitmap bitmap =null;
        try {
            //从文件中获取bitmap引用
            FileInputStream fis = new FileInputStream(cameraFile);
            bitmap = BitmapFactory.decodeStream(fis);
            fis.close();

            Bitmap newBmp = BitmapUtils.ResizeBitmap(DoSendTask.this,bitmap);
            lists.add(newBmp);
            //显示图片
            taskImg.setImageBitmap(newBmp);

            //压缩保存
            String filename = BitmapUtils.getCurDataStr();
            compressFile = new File(cacheDirectory+ File.separator +"zip"+filename);
            if(BitmapUtils.saveBitmap2File(newBmp,compressFile)){
               cameraFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private PopupWindow initPopwindow(List lists) {
        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = mInflater
                .inflate(R.layout.do_send_task_popup_img, null);
        PhotoView Image = (PhotoView) view.findViewById(R.id.photo_view);
        Image.setImageBitmap((Bitmap) lists.get(0));

        final PopupWindow popwindow = new PopupWindow(view,
                ScreenBean.getScreenWidth(), ScreenBean.getScreenHeight());
        popwindow.setFocusable(true);
        popwindow.setBackgroundDrawable(new BitmapDrawable());
        popwindow.setTouchable(true);
        popwindow.setOutsideTouchable(true);
        popwindow.setAnimationStyle(R.style.PopDownMenu);

        return popwindow;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
//        Log.d("DosSendTask","发布任务定位回调");
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                Latitude = aMapLocation.getLatitude();//获取纬度
                longtitude = aMapLocation.getLongitude();//获取经度
                currentLatLng = new LatLng(Latitude,longtitude);

            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError","location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }
}
