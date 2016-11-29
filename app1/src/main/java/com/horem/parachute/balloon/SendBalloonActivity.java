package com.horem.parachute.balloon;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.alipay.apmobilesecuritysdk.face.APSecuritySdk;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.common.ApplicationConstant;
import com.common.HttpUrlConstant;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.balloon.Bean.GetAdressBean;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.task.CameraActivity;
import com.horem.parachute.task.DoSendTask;
import com.horem.parachute.task.TaskPayActivity;
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
import org.kymjs.chat.bean.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.senab.photoview.PhotoView;

public class SendBalloonActivity extends BaseActivity implements AMapLocationListener,View.OnClickListener{
    private int ActivityType;
    private long userId;

    private TextView tvTaskLocation;
    private TextView editSendMsg;
    private ImageView taskImg;


    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private String currentAddress = null;

    private LatLng currentLatLng;
    private boolean isFirstEnter = true;

    private File cameraFile;
    private File compressFile;
    private String videoFilePath = null;
    private String cacheDirectory;
    private ArrayList<Bitmap> lists = new ArrayList<>();

    private static final int REQUEST_LOCATION = 0x1001;
    private static final int REQUEST_CODE_CAMERA = 0x1002;
    private static final int RECORD_VIDEO    = 0x1003;
    private int currentRequestCode;
    private  HashMap<String,String> params = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_balloon);

        init();
    }

    private void init() {
        //初始化标题栏
        initTitleView();
        initMap();

        //创建文件夹
        cacheDirectory = ApplicationConstant.MEDIA_FILES;
        File fileDirectory = new File(cacheDirectory);
        if(!fileDirectory.exists()){
            fileDirectory.mkdir();
        }
        ActivityType = getIntent().getIntExtra("type",-1);
        userId = SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0);
        Intent goNext = null;
        switch (ActivityType){
            case ApplicationConstant.MEDIA_TYPE_PHOTO:

                setTitleName("热气球（照片）");
                if(isFirstEnter && cameraFile == null){
                    String filename = BitmapUtils.getCurDataStr();
                    cameraFile = new File(cacheDirectory+ File.separator + filename);
                    goNext = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    goNext.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
                    currentRequestCode = REQUEST_CODE_CAMERA;
                    startActivityForResult(goNext,REQUEST_CODE_CAMERA);
                }
                break;
            case ApplicationConstant.MEDIA_TYPE_VIDEO:

                setTitleName("热气球（视频）");
                if(isFirstEnter && videoFilePath == null){
                    Intent intent = new Intent(this, CameraActivity.class);
                    startActivityForResult(intent,RECORD_VIDEO);
                }

                break;
        }
        setRightButtonText("发送");
        //监听触摸事件
        findViewById(R.id.send_balloon_task_location).setOnClickListener(this);
        //显示textview
        tvTaskLocation = (TextView) findViewById(R.id.send_balloon_location_tv);
        editSendMsg = (EditText) findViewById(R.id.activity_task_send_et);
        //
        taskImg = (ImageView) findViewById(R.id.send_task_image);
        taskImg.setOnClickListener(this);

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

    /**
     * 获取视频预览图
     * @param data
     */
    private void dealVideoSource(Intent data)  {
        videoFilePath = data.getStringExtra("filePath");
        Bitmap bmp = ThumbnailUtils.createVideoThumbnail(videoFilePath, MediaStore.Video.Thumbnails.MICRO_KIND);
        taskImg.setImageBitmap(bmp);
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

        switch (ActivityType){
            case ApplicationConstant.MEDIA_TYPE_PHOTO:

                try {
                    buildPicRequestParams(describe);
                    buildPhotoTask();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case ApplicationConstant.MEDIA_TYPE_VIDEO:
                buildVideoRequestParams(describe);
                buildVideoTask();
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationOption = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    private void buildVideoRequestParams(String describe) {
        params.clear();
        params.put("balloonDesc",describe);
        params.put("balloonLatitude",currentLatLng.latitude+"");
        params.put("balloonLongitude",currentLatLng.longitude+"");
        params.put("needToPublish","1");
        params.put("balloonAddress",currentAddress);
        params.put("remindUserJson","[]");
        params.put("isChange","0");

        params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lng",currentLatLng.longitude+"");
        params.put("lat",currentLatLng.latitude+"");
        params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");
//        for(Map.Entry<String,String> entry:params.entrySet()){
//            Log.d(getClass().getName(),entry.getKey()+": "+entry.getValue());
//        }
    }

    /**
     * 发送视频任务，分有视频和无视频文件两种，根据videoFilePath是否为空进行判定
     */
    private void buildVideoTask() {

        if(videoFilePath != null){
            startLoading();
            uploadVideoFile(new File(videoFilePath));
        }else{
           ToastManager.show(this,"请添加视频");
        }
    }

    private void uploadVideoFile(File file) {
        try {
            OkHttpClientManager.postAsyn(HttpUrlConstant.balloonVideoAdd,
                    new OkHttpClientManager.ResultCallback<String>() {
                        @Override
                        public void onError(Request request, Exception e) {

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
     * 发布图片任务,必须有图片才能上传
     */
    private void buildPhotoTask() throws IOException {

        /**
         *  1.上传图片,无图片直接执行第2步 2.将上传图片返回的结果连同订单参数一同抛给服务器，无图片，订单参数为空
         */
        if(compressFile != null){
            startLoading();
            OkHttpClientManager.postAsyn(HttpUrlConstant.balloonPicUpload,
                    new OkHttpClientManager.ResultCallback<SendTaskPhotoBean>() {
                        @Override
                        public void onError(Request request, Exception e) {
                            ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                        }

                        @Override
                        public void onResponse(SendTaskPhotoBean response) {
                            Log.d(getClass().getName(),new Gson().toJson(response));
                                ArrayList<SendTaskPhotoBean> list = new ArrayList<>();
                                list.add(response);
                                params.put("attArrayJson", new Gson().toJson(list));
                                httpRequestPicTask();
                        }

                    },compressFile,"");
        }else{
                ToastManager.show(SendBalloonActivity.this,"请添加照片");
        }

    }
    private void httpRequestPicTask() {
        OkHttpClientManager.postAsyn(HttpUrlConstant.balloonPicAdd,
                new OkHttpClientManager.ResultCallback<String>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                    }
                    @Override
                    public void onResponse(String response) {
//                        Log.d(getClass().getName(),response);
                        stopLoading();
                        dealWithSendTaskResult(response);
                    }
                },params);
    }

    /**
     * 请求参数设置,发布任务的四种情况的请求参数大同小异，提取出来精简代码
     */
    private void buildPicRequestParams(String describe) {
        params.clear();
        params.put("balloonDesc",describe);
        params.put("balloonLatitude",currentLatLng.latitude+"");
        params.put("balloonLongitude",currentLatLng.longitude+"");
        params.put("needToPublish","1");
        params.put("balloonAddress",currentAddress);
        params.put("remindUserJson","[]");
        params.put("isChange","1");

        params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lng",currentLatLng.longitude+"");
        params.put("lat",currentLatLng.latitude+"");
        params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");
//        for(Map.Entry<String,String> entry:params.entrySet()){
//            Log.d(getClass().getName(),entry.getKey()+": "+entry.getValue());
//        }
    }
    /**
     * 发送任务后的事件处理，跳转到支付界面，同时传递生成的任务Id
     * @param response
     */
    private void dealWithSendTaskResult(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response.toString());
            int status = jsonObject.optInt("statusCode");
            String message = jsonObject.optString("message");
            ToastManager.show(SendBalloonActivity.this, message);
            if(1 == status){
                SharePreferencesUtils.setBoolean(SendBalloonActivity.this,SharePrefConstant.isNeedUpdateMap,true);
                SendBalloonActivity.this.finish();
            }else if(-999 == status){
                exitApp();
            }else{
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        Intent goNext = null;
        switch (v.getId()){
            case R.id.do_send_task_location:
//                currentRequestCode = REQUEST_LOCATION;
                break;

            case R.id.send_task_image:

                switch (ActivityType) {
                    case ApplicationConstant.MEDIA_TYPE_PHOTO:

                        if(cameraFile != null && lists.size() > 0){
                            PopupWindow popupWindow = initPopwindow(lists);
                            popupWindow.showAtLocation(v, Gravity.NO_GRAVITY,0,0);
                        }else{

                            String filename = BitmapUtils.getCurDataStr();
                            cameraFile = new File(cacheDirectory+ File.separator + filename);
                            goNext = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            goNext.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
                            currentRequestCode = REQUEST_CODE_CAMERA;
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
                            goNext = new Intent(this, CameraActivity.class);
//                            startActivityForResult(goNext,RECORD_VIDEO);
                            currentRequestCode = RECORD_VIDEO;
                        }
                        break;
                }
                break;
        }
        if(null != goNext){
            startActivityForResult(goNext,currentRequestCode);
        }

    }
    private void getCameraImage() {

        Bitmap bitmap =null;
        try {
            //从文件中获取bitmap引用
            FileInputStream fis = new FileInputStream(cameraFile);
            bitmap = BitmapFactory.decodeStream(fis);
            fis.close();

            Bitmap newBmp = BitmapUtils.ResizeBitmap(SendBalloonActivity.this,bitmap);
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
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                currentLatLng = new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude());
                if(isFirstEnter){
                    isFirstEnter = false;
                    getAndShowAddress(currentLatLng);
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError","location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    private void getAndShowAddress(LatLng latlng) {
        HashMap<String,String> params = new HashMap<>();
        params.put("longitude",latlng.longitude+"");
        params.put("latitude",latlng.latitude+"");

        params.put("memberId",userId+"");
        params.put("lng",latlng.longitude+"");
        params.put("lat",latlng.latitude+"");
        params.put("clientId",SharePreferencesUtils.getString(this,SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");

//        for(Map.Entry<String,String> entry:params.entrySet()){
//            Log.d(getClass().getName(),entry.getKey()+":"+entry.getValue());
//        }

        OkHttpClientManager.postAsyn(HttpUrlConstant.mapAddressUrl, new OkHttpClientManager.ResultCallback<GetAdressBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(GetAdressBean response) {
                if(response.getStatusCode() == 1) {
                    Log.d("showAddress", new Gson().toJson(response));
                    //显示当前的地址
                    currentAddress = response.getResult();
                    tvTaskLocation.setText(currentAddress);
                }else if(response.getStatusCode() == -999){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }
        },params);
    }
}
