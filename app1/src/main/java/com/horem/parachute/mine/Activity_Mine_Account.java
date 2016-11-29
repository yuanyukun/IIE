package com.horem.parachute.mine;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.common.ApplicationConstant;
import com.common.HttpUrlConstant;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.horem.parachute.R;
import com.horem.parachute.balloon.Bean.BaseResultBean;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.mine.bean.AcccountSaftyBean;
import com.horem.parachute.mine.bean.MineInfoBean;
import com.horem.parachute.task.TaskSendPhotoActivity;
import com.horem.parachute.util.BitmapUtils;
import com.horem.parachute.util.SdcardUtils;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.horem.parachute.util.Utils;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Activity_Mine_Account extends BaseActivity{

    private static final int HOMETOWN_ID = 0x101;
    private static final int WORKUNITE_ID = 0x102;
    private static final int SCHOOL_ID = 0x103;
    private static final int LOCATION_ID = 0x104;

    private static  final String  TAG = "Activity_Mine_Account";
    private TextView tvUserName;
    private CircleImageView igUserPhoto;
    private TextView tvSex;
    private TextView tvHome;
    private TextView tvIntroduce;
    private TextView tvCareer;
    private TextView tvWorkPlace;
    private TextView tvSchool;
    private TextView tvLocation;
    private TextView tvUsedToGo;
    private TextView tvIntrests;

    private String cacheDirectory;  //图片缓存目录
    private File cameraFile;        //sd头像文件
    private String userName;

    private static final int PICTURE_PHOTO  = 0x1000;
    private static final int PICTURE_CAMERA = 0x1001;
    private Tracker mTracker;
    private CustomApplication application;

    private AcccountSaftyBean bean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity__mine__account);
        initView();
        getUserInfo();
    }

   //头像
    public void onPhotoClick(View view){
        popupFuncView();
    }
    //用户名
    public void onUserNameClick(View view){
        Intent intent = new Intent(this,Activity_Mine_Modify.class);
        intent.putExtra("ModifyType",ApplicationConstant.MODIFY_NAME);
        intent.putExtra("value",userName);
        startActivity(intent);
    }
    //性别
    public void onUserSexClick(View view){
        Intent intent = new Intent(this,SexOptionActivity.class);
        intent.putExtra("sex",bean.getSex());
        startActivity(intent);
    }
    //故乡
    public void onHomeTownClick(View view){
        String value = bean.getHometownCity();
        Intent intent = new Intent(this,SingleLineTextModifyActivity.class);
        intent.putExtra("type",HOMETOWN_ID);
        intent.putExtra("home",value);
        startActivity(intent);

    }
    //自我介绍
    public void onMySelfIntroduceClick(View view){
        Intent intent = new Intent(this,Mine_Account_Text_Modify.class);
        intent.putExtra("value",bean.getIntroduction());
        startActivity(intent);
    }

    //职业
    public void onCareerClick(View view){
        Intent intent = new Intent(this,ModifyProfessionActivity.class);
        intent.putExtra("value",bean.getProfessionId());
        startActivity(intent);
    }
    //工作单位
    public void onWorkPlaceClick(View view){
        String value = bean.getWorkUnit();
        Intent intent = new Intent(this,SingleLineTextModifyActivity.class);
        intent.putExtra("type",WORKUNITE_ID);
        intent.putExtra("unit",value);
        startActivity(intent);
    }
    //学校
    public void onSchoolClick(View view){
        String value = bean.getSchool();
        Intent intent = new Intent(this,SingleLineTextModifyActivity.class);
        intent.putExtra("type",SCHOOL_ID);
        intent.putExtra("school",value);
        startActivity(intent);
    }
    //所在地
    public void onLocationClick(View view){
        String value = bean.getSiteCity();
        Intent intent = new Intent(this,SingleLineTextModifyActivity.class);
        intent.putExtra("type",LOCATION_ID);
        intent.putExtra("loc",value);
        startActivity(intent);
    }
    //常去的景点
    public void onUsedToGo(View view){
        Intent intent = new Intent(this,HomeTipsActivity.class);
        intent.putExtra("count",bean.getMyAddressCount());
        startActivity(intent);
    }


    private void getUserInfo() {
        startLoading();
        HashMap<String,String> params = new HashMap<>();
        params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lng","");
        params.put("lat","");
        params.put("clientId", SharePreferencesUtils.getString(Activity_Mine_Account.this, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");

        OkHttpClientManager.postAsyn(HttpUrlConstant.memberDetailUrl, new OkHttpClientManager.ResultCallback<MineInfoBean>() {
            @Override
                public void onError(Request request, Exception e) {
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                }

            @Override
            public void onResponse(MineInfoBean response) {
                stopLoading();
//                Log.d(getClass().getName(),new Gson().toJson(response));
                if(response.getStatusCode() == 1) {
                    setupView(response);
                    bean = response.getResult();
                }else if(response.getStatusCode() == -999){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }
        },params);
    }

    private void setupView(MineInfoBean response) {
        userName = response.getResult().getNickName();
        tvUserName.setText(userName);
        Glide.with(Activity_Mine_Account.this).load(Utils.getHeadeUrl(response.getResult().getHeadImg()))
               .into(igUserPhoto);
        tvSex.setText(getSexType(response.getResult().getSex()));
        tvHome.setText(response.getResult().getHometownCity());
        tvIntroduce.setText(response.getResult().getIntroduction());
        tvCareer.setText(response.getResult().getProfession());
        tvWorkPlace.setText(response.getResult().getWorkUnit());
        tvSchool.setText(response.getResult().getSchool());
        tvLocation.setText(response.getResult().getSiteCity());
        tvUsedToGo.setText(response.getResult().getMyAddressCount()+"个");
//        tvIntrests
    }
    private String getSexType(int sexCode){
        return sexCode==0?"男":"女";
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
    private void initView() {
        initTitleView();
        setTitleName("用户信息");
        setLeftButtonText("我");
        setRightButtonText("认证申请");

        tvUserName = (TextView) findViewById(R.id.mine_account_tv_name);    //用户名
        igUserPhoto = (CircleImageView) findViewById(R.id.mine_account_ig_photo); //用户头像
        tvSex = (TextView) findViewById(R.id.tv_mine_account_sex);
        tvHome = (TextView) findViewById(R.id.tv_mine_account_hometown);
        tvIntroduce = (TextView) findViewById(R.id.tv_mine_account_introduce);
        tvCareer = (TextView) findViewById(R.id.tv_mine_account_career);
        tvWorkPlace = (TextView) findViewById(R.id.tv_mine_account_work_place);
        tvSchool = (TextView) findViewById(R.id.tv_mine_account_school);
        tvLocation = (TextView) findViewById(R.id.tv_mine_account_location);
        tvUsedToGo = (TextView) findViewById(R.id.tv_mine_account_used_to_go);
        tvIntrests = (TextView) findViewById(R.id.tv_mine_account_intrests);

        //创建文件夹
        cacheDirectory = ApplicationConstant.MEDIA_FILES;
        File fileDirectory = new File(cacheDirectory);
        if(!fileDirectory.exists()){
            fileDirectory.mkdir();
        }
        cameraFile = new File(cacheDirectory+ File.separator + "headIcon.jpg");
    }

    @Override
    protected void OnRightButtonClicked() {
        new AlertView(null, "\n为保证信息真实性，申请认证请\n同客服联系", null, null, new String[]{"确定"}, this,
                AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {

            }
        }).setCancelable(true).show();
    }

    private void popupFuncView() {
        new AlertView(null, null, "取消", null,
                new String[]{"从相册选取", "拍照"},
                this, AlertView.Style.ActionSheet, new OnItemClickListener() {

            @Override
            public void onItemClick(Object o, int position) {
                Intent intent = null;
                switch (position) {
                    case 0:
                        try{
                            intent = new Intent(Intent.ACTION_PICK);
                            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                            startActivityForResult(intent,PICTURE_PHOTO);
                        }catch (ActivityNotFoundException e){
                            ToastManager.show(Activity_Mine_Account.this,"该手机不支持该功能");
                        }
                        break;

                    case 1:
                        if(!SdcardUtils.isSdCardAvailable()){
                            ToastManager.show(Activity_Mine_Account.this,"SD卡未安装");
                        }
                        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
                        startActivityForResult(intent,PICTURE_CAMERA);
                        break;
                }
            }
        }).setCancelable(true).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            switch (requestCode){
                case PICTURE_CAMERA:
                    if(resultCode == Activity.RESULT_OK ){
                        getCameraImage();
                    }
                    break;
                case PICTURE_PHOTO:
                    if(resultCode == Activity.RESULT_OK && null != data){
                        getPhotosImage(data);
                    }
                    break;
            }
    }

    private void getCameraImage() {


        try {
            //从文件中获取bitmap引用
            FileInputStream fis = new FileInputStream(cameraFile);
            Bitmap  bitmap = BitmapFactory.decodeStream(fis);
            fis.close();
            //显示图片
            igUserPhoto.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        startLoading("正在上传头像");
        upLoadHeadIcon(cameraFile);
    }


    private void getPhotosImage(Intent data) {

        Uri selectedImage = data.getData();
        String[] filePathColumns={MediaStore.Images.Media.DATA};
        Cursor c = this.getContentResolver().query(selectedImage, filePathColumns, null,null, null);
        c.moveToFirst();
        int columnIndex = c.getColumnIndex(filePathColumns[0]);
        String picturePath= c.getString(columnIndex);
        c.close();

        File cacheFile = new File(picturePath);

        try {
            //从文件中获取bitmap引用
            FileInputStream fis = new FileInputStream(cacheFile);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            fis.close();
            igUserPhoto.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        startLoading("正在上传头像");
        upLoadHeadIcon(cacheFile);
    }

    private void upLoadHeadIcon(File cameraFile) {
        HashMap<String,String> params = new HashMap<>();
        params.put("memberId",SharePreferencesUtils.getLong(Activity_Mine_Account.this,
                SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("clientId",SharePreferencesUtils.getString(Activity_Mine_Account.this,
                SharePrefConstant.INSTALL_CODE,""));

        try {
            OkHttpClientManager.postAsyn(HttpUrlConstant.memberPortraitEditUrl, new OkHttpClientManager.ResultCallback<BaseResultBean>() {
                @Override
                public void onError(Request request, Exception e) {
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                }

                @Override
                public void onResponse(BaseResultBean response) {
                    stopLoading();
                    if(response.getStatusCode() == 1){
                        ToastManager.show(getApplicationContext(),response.getMessage());
                    }else if(response.getStatusCode() == -999){
                        exitApp();
                    }else
                        ToastManager.show(getApplicationContext(),"噢，网络不给力！");
                }
            },cameraFile,"",params);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getUserInfo();
    }
}
