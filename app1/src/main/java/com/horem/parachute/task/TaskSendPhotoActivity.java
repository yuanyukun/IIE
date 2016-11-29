package com.horem.parachute.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.common.ApplicationConstant;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.adapter.HackyViewPagerAdapter;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.customview.HackyViewPager;
import com.horem.parachute.main.AppMainActivity;
import com.horem.parachute.mine.bean.MessageBean;
import com.horem.parachute.task.httpImpl.TaskSendPhotoImpl;
import com.horem.parachute.util.BitmapUtils;
import com.horem.parachute.util.ScreenBean;
import com.horem.parachute.util.ToastManager;
import com.http.request.HttpApi;
import com.http.request.IResponseApi;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskSendPhotoActivity extends BaseActivity {
    private static String Tag = "TaskSendPhotoActivity";
    private boolean isFirst = true;
    private GridView mGridView;

    private static final int REQUEST_CODE_CAMERA = 0x1001;
    private File cameraFile;

    private MyBaseAdapter adapter;
    private ArrayList<Drawable> lists;
    private ArrayList<File> files;
    private EditText mEdit;
    private String cacheDirectory;

    private String subTaskId;
    private double lat;
    private double lng;

    private CustomApplication application;
    private Tracker mTracker;
    private static final String TAG = "SendPhotoAty";
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
        setContentView(R.layout.activity_task_take_photo);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(lists.size() == 1 && isFirst){

            String filename = BitmapUtils.getCurDataStr();
            cameraFile = new File(cacheDirectory+ File.separator + filename);
            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
            startActivityForResult(camera,REQUEST_CODE_CAMERA);
        }
        isFirst = false;
    }

    private void init() {
        initTitleView();
        setTitleName("拍照片");
        setRightButtonText("发送");

        //获取子任务Id
        subTaskId = getIntent().getStringExtra("subTaskId");
        lat = getIntent().getDoubleExtra("lat",0.0);
        lng = getIntent().getDoubleExtra("lng",0.0);
        Log.i(getClass().getName(),subTaskId+"++++ "+lat+" ____"+lng);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        ScreenBean.setScreenWidth(screenWidth);
        ScreenBean.setScreenHeight(screenHeight);

        mEdit = (EditText) findViewById(R.id.send_photo_et);
//        mEdit.clearFocus();
//        mEdit.setCursorVisible(false);
        lists = new ArrayList<>();
        files = new ArrayList<>();
        Drawable drawable = ContextCompat.getDrawable(this,R.mipmap.plus_512);
        lists.add(drawable);

        mGridView = (GridView) findViewById(R.id.task_send_photo_grid_view);
        adapter = new MyBaseAdapter(this,lists);

        //创建文件夹
        cacheDirectory = ApplicationConstant.MEDIA_FILES;
        File fileDirectory = new File(cacheDirectory);
        if(!fileDirectory.exists()){
            fileDirectory.mkdir();
        }

        adapter.setOnAddImageItemListener(new MyBaseAdapter.OnAddImageItemListener() {
            @Override
            public void onTakeCamera() {

                String filename = BitmapUtils.getCurDataStr();
                //创建文件夹
                cameraFile = new File(cacheDirectory+ File.separator + filename);
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
                startActivityForResult(camera,REQUEST_CODE_CAMERA);
            }

        });
        mGridView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK ){

                Bitmap bitmap =null;
                try {
                    //从文件中获取bitmap引用
                    FileInputStream fis = new FileInputStream(cameraFile);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    bitmap = BitmapFactory.decodeStream(fis, null, options);
                    fis.close();
//                  cameraFile.delete();
                    //对引用的图片进行裁剪
                    Bitmap newBmp = BitmapUtils.ResizeBitmap(TaskSendPhotoActivity.this,bitmap);
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    Log.i("TaskSendPhotoActivity",width+"|"+height+"");
                    Drawable drawable =new  BitmapDrawable(newBmp);

                    if(  lists.size() <= ApplicationConstant.MAX_IMAGE_NUM-1){
                        lists.add(0,drawable);
                    }
                    Log.i(Tag,"lists大小"+lists.size());
                    adapter.notifyDataSetChanged();
                    //压缩保存
                    String filename = BitmapUtils.getCurDataStr();
                    File compressBitmap = new File(cacheDirectory+ File.separator +"zip"+filename);
                    if(BitmapUtils.saveBitmap2File(newBmp,compressBitmap)){
                        files.add(compressBitmap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * 网络接口
     */
    @Override
    protected void OnRightButtonClicked() {
//        super.OnRightButtonClicked();
        if(files.size() <= 0)
        {
            ToastManager.show(this,"必须拍摄至少一张照片");
            return;
        }
        if(TextUtils.isEmpty(mEdit.getText().toString().trim())){
            ToastManager.show(this,"请输入照片描述说明");
            return;
        }

        startLoading();
        HashMap<String,String> map = new HashMap<>();
        map.put("subTaskId",subTaskId);
        map.put("lat",lat+"");
        map.put("lng",lng+"");
        map.put("describe",mEdit.getText().toString().trim());

        HttpApi api = new TaskSendPhotoImpl();
        api.httpRequest(TaskSendPhotoActivity.this, new IResponseApi() {
            @Override
            public void onSuccess(Object object) {
                stopLoading();
                MessageBean bean = new Gson().fromJson((String)object,MessageBean.class);
                ToastManager.show(TaskSendPhotoActivity.this,bean.getMessage());
                if(bean.getStatusCode() == 1){
                    TaskSendPhotoActivity.this.finish();
                }
            }

            @Override
            public void onFailed(Exception e) {
            }
        },map,files);

    }
}

/**
 * GridView 的自定义适配器
 */
class MyBaseAdapter extends BaseAdapter{

    private Context mContext;
    private List mLists;
    private LayoutInflater mInflater;

    public interface OnAddImageItemListener{
        void onTakeCamera();
    }
    private OnAddImageItemListener listener;
    public void setOnAddImageItemListener(OnAddImageItemListener itemListener){
        this.listener = itemListener;
    }

    public MyBaseAdapter(Context context, List lists) {
        this.mContext = context;
        this.mLists = lists;
        mInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return mLists.size();
    }

    @Override
    public Object getItem(int position) {
        return mLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.take_photo_grid_view_item,null);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image_item);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.image.setImageDrawable((Drawable) mLists.get(position));
        //当为最后一个时
        if(position == ApplicationConstant.MAX_IMAGE_NUM -1){
            viewHolder.image.setVisibility(View.INVISIBLE);
        }

        //当是最后一个值时
        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLists.size()-1 == position ){
                    if(mLists.size() == ApplicationConstant.MAX_IMAGE_NUM ){
//                        listener.onPhotoReView();
                      PopupWindow  pop =  initPopwindow(mLists,position);
                      pop.showAtLocation(v, Gravity.NO_GRAVITY,0,0);
                    }else
                        listener.onTakeCamera();
                }else{
//                    listener.onPhotoReView();
                    PopupWindow  pop =  initPopwindow(mLists,position);
                    pop.showAtLocation(v, Gravity.NO_GRAVITY,0,0);
                }
            }
        });
        return convertView;
    }
    class ViewHolder {
        private ImageView image;
    }
    private PopupWindow initPopwindow(List lists,int positon) {
        LayoutInflater mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = mInflater
                .inflate(R.layout.popwindow_show_image, null);
        HackyViewPager mViewPager = (HackyViewPager) view.findViewById(R.id.my_view_pager);
        mViewPager.setAdapter(new HackyViewPagerAdapter(lists));
        mViewPager.setCurrentItem(positon);

        final PopupWindow popwindow = new PopupWindow(view,
                ScreenBean.getScreenWidth(), ScreenBean.getScreenHeight());
        popwindow.setFocusable(true);
        popwindow.setBackgroundDrawable(new BitmapDrawable());
        popwindow.setTouchable(true);
        popwindow.setOutsideTouchable(true);
        popwindow.setAnimationStyle(R.style.PopDownMenu);

        return popwindow;
    }
}