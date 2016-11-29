package com.horem.parachute.task;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.horem.parachute.R;
import com.common.ApplicationConstant;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.util.SdcardUtils;
import com.horem.parachute.common.BaseActivity;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CameraActivity extends BaseActivity implements SurfaceHolder.Callback, View.OnClickListener{

    private static final String TAG = "CameraActivity";
    private static  final int TIMER_CLOCK = 0X123;
    private SurfaceView surfaceView;
    private Button mStartRecordBtn;
    private ProgressBar recordingProgress;

    File videoTmpFile;
    private Camera mCamera;
    private int CurrentCameraId = 0;//默认是后置摄像头
    private SurfaceHolder mHolder;
    private int mCameraOrientation = 0;
    private int mCameraFacing = 0;
    private int mDeviceOrientation = 0;
    private final int iCaptureBuffers = 3;

    private int settingsWidth = 480,screenWidht = 640;
    private int settingsHeight = 480,screenheight = 480;

    private boolean bIfPreview = false;
    private boolean isRecording = false;

    private PowerManager.WakeLock mWakeLock;
    private static final  String CLASS_LABEL = "CameraActivity";

    private MediaRecorder mRecoder;
    private Handler mHandler;
    private int count = 0;

    private Timer timeCount;
    private FFmpeg ffmpeg;
    private String  tmpVideoDir;
    private File output;
//    private CustomAlertDialog.Builder progressDialog;

    private CustomApplication application;
    private Tracker mTracker;
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        //保持屏幕常亮
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, CLASS_LABEL);
        mWakeLock.acquire();

        ffmpeg = FFmpeg.getInstance(CameraActivity.this);
        loadFFMpegBinary();
        init();
        initHandler();
    }

    private void initHandler() {
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int tmp;
                switch (msg.what){
                    case TIMER_CLOCK:
                         tmp = count++;
                        if(tmp%10 == 0){
                            setTitleName(getTimeString(tmp));
                        }
                        recordingProgress.setProgress(tmp);
                        break;
                }
            }
        };
    }

    /**
     *count <= 300
     * @param count
     * @return
     */
    protected String getTimeString(int count){

        StringBuilder mTimeStr = new StringBuilder();
        mTimeStr.append("00:");
        int tmp = count/10 % 60;
        if(tmp<10){
            mTimeStr.append("0"+tmp);
        }else
            mTimeStr.append(tmp+"");


        return mTimeStr.toString();
    }
    private void init() {
        initTitleView();
        setTitleName("00:00");
        setRightButtonDrawableRight(ContextCompat.getDrawable(this,R.mipmap.camera_switch));

        //获取屏幕尺寸参数
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        //屏幕宽高设置1：1（屏幕的宽度为边长）
        surfaceView = new SurfaceView(this);
        LinearLayout surfaceLayout = (LinearLayout) findViewById(R.id.preview_view_container);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) surfaceLayout.getLayoutParams();
        params.width = displayMetrics.widthPixels;
        params.height = displayMetrics.widthPixels;
        Log.i(getClass().getName(),"屏幕宽："+params.width+" 屏幕高："+displayMetrics.heightPixels);
        surfaceLayout.setLayoutParams(params);
        surfaceLayout.addView(surfaceView);
        //设置
        SurfaceHolder mHolder= surfaceView.getHolder();
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHolder.addCallback(CameraActivity.this);


        mStartRecordBtn = (Button) findViewById(R.id.start_record_button);
        mStartRecordBtn.setOnClickListener(this);
        recordingProgress = (ProgressBar) findViewById(R.id.recording_progress_show);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWakeLock == null) {
            //获取唤醒锁,保持屏幕常亮
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, CLASS_LABEL);
            mWakeLock.acquire();
        }
    }

    @Override
    protected void OnRightButtonClicked() {
        //转换摄像头
        SwitchCamera();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try{
            mCamera = Camera.open(CurrentCameraId);
            mHolder = holder;
            mCamera.setPreviewDisplay(holder);
            initCamera();

        }catch (Exception e){}
    }

    private void initCamera() {

        if (null == mCamera)
            return;
        try {
            if (bIfPreview) {
                mCamera.stopPreview();// stopCamera();
                mCamera.setPreviewCallbackWithBuffer(null);
            }
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(CurrentCameraId, cameraInfo);
            mCameraOrientation = cameraInfo.orientation;
            mCameraFacing = cameraInfo.facing;
//            mDeviceOrientation = getDeviceOrientation();
//            Log.i(getClass().getName(), "allocate: device orientation=" + mDeviceOrientation + ", camera orientation=" + mCameraOrientation + ", facing=" + mCameraFacing);

            setCameraDisplayOrientation();

			/* Camera Service settings */
            Camera.Parameters parameters = mCamera.getParameters();

            // 获取camera支持的相关参数，判断是否可以设置
            List<Camera.Size> previewSizes = mCamera.getParameters().getSupportedPreviewSizes();

            // 获取当前设置的分辩率参数
            boolean bSetPreviewSize = false;
            for (int i = 0; i < previewSizes.size(); i++) {
                Camera.Size s = previewSizes.get(i);
                Log.i(getClass().getName(),"supprotedPreviewSizes width==>"+s.width +"height==>"+s.height);
                if(s.width == screenWidht && s.height == screenheight) {
                    bSetPreviewSize = true;
                    parameters.setPreviewSize(screenWidht, screenheight);
                    break;
                }
            }
            // 指定的分辩率不支持时，用默认的分辩率替代
            if(!bSetPreviewSize)
                parameters.setPreviewSize(640, 480);

            // 设置视频采集帧率
            List<int[]> fpsRange = parameters.getSupportedPreviewFpsRange();
            for(int i=0; i<fpsRange.size(); i++) {
                int[] r = fpsRange.get(i);
                if(r[0] >= 25000 && r[1] >= 25000) {
                    parameters.setPreviewFpsRange(r[0], r[1]);
                    Log.i(getClass().getName(),"getsupportedPreviewFpsRange==>"+r[0]+"~"+r[1]);
                    break;
                }
            }
            parameters.setPreviewFrameRate(ApplicationConstant.VIDEO_FPS);//设置帧率
            // 设置视频数据格式
            parameters.setPreviewFormat(ImageFormat.NV21);

            //设置自动对焦
            if(Build.VERSION.SDK_INT >  Build.VERSION_CODES.FROYO)
            {
                List<String> focusModes = parameters.getSupportedFocusModes();
                if(focusModes != null){
                    Log.i("video", Build.MODEL);
                    if (((Build.MODEL.startsWith("GT-I950"))
                            || (Build.MODEL.endsWith("SCH-I959"))
                            || (Build.MODEL.endsWith("MEIZU MX3")))&&focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)){

                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    }else if(focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)){
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                    }else
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
                }
            }
            // 参数设置生效
            try {
                mCamera.setParameters(parameters);
            } catch(Exception e){

            }
            Camera.Size captureSize = mCamera.getParameters().getPreviewSize();
            int bufSize = captureSize.width * captureSize.height * ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8;
            for (int i = 0; i < iCaptureBuffers; i++) {
                mCamera.addCallbackBuffer(new byte[bufSize]);
            }

            mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    mCamera.addCallbackBuffer(data);
                }
            });
            mCamera.startPreview(); // 打开预览画面
            bIfPreview = true;


            int iCurPreviewRange[] = new int[2];
            parameters.getPreviewFpsRange(iCurPreviewRange);
        } catch (Exception e) {

            if(null != mCamera){
                mCamera.release();
                mCamera = null;
            }
        }
    }

    private void setCameraDisplayOrientation() {
        try {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(CurrentCameraId, cameraInfo);

            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            int rotation = wm.getDefaultDisplay ().getRotation ();
            int degrees = 0 ;
            switch ( rotation ) {
                case Surface.ROTATION_0 : degrees = 0 ; break ;
                case Surface.ROTATION_90 : degrees = 90 ; break ;
                case Surface.ROTATION_180 : degrees = 180 ; break ;
                case Surface.ROTATION_270 : degrees = 270 ; break ;
            }

            int result;
            if ( cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT ) {
                result = ( cameraInfo.orientation + degrees ) % 360 ;
                result = ( 360 - result ) % 360 ;   // compensate the mirror
            } else {   // back-facing
                result = ( cameraInfo.orientation - degrees + 360 ) % 360 ;
            }

            mCamera.setDisplayOrientation ( result );
        } catch (Exception ex) {

        }
    }

    private int getDeviceOrientation() {
        int orientation = 0;
            WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
            //Log.i(TAG, "wm.getDefaultDisplay().getRotation():" + wm.getDefaultDisplay().getRotation());
            switch(wm.getDefaultDisplay().getRotation()) {
                case Surface.ROTATION_90:
                    orientation = 90;
                    break;
                case Surface.ROTATION_180:
                    orientation = 180;
                    break;
                case Surface.ROTATION_270:
                    orientation = 270;
                    break;
                case Surface.ROTATION_0:
                default:
                    orientation = 0;
                    break;
            }
        return orientation;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        if(null != mCamera)	{
            try {
                mCamera.stopPreview();
                mCamera.setPreviewCallbackWithBuffer(null);
                bIfPreview = false;
                mCamera.release();
                mCamera = null;
            } catch (Exception ex) {
                mCamera = null;
                bIfPreview = false;
            }
        }
        mHolder = null;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != mCamera)	{
            try {
                mCamera.stopPreview();
                mCamera.setPreviewCallbackWithBuffer(null);
                bIfPreview = false;
                mCamera.release();
                mCamera = null;
            } catch (Exception ex) {
                mCamera = null;
                bIfPreview = false;
            }
        }
        mHolder = null;
        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    // 切换摄像头
    public void SwitchCamera() {
        try {
            if(Camera.getNumberOfCameras() == 1 || mHolder == null)
                return;
            CurrentCameraId = (CurrentCameraId==0) ? 1 : 0;
            if(null != mCamera)	{
                mCamera.stopPreview();
                mCamera.setPreviewCallbackWithBuffer(null);
                bIfPreview = false;
                mCamera.release();
                mCamera = null;
            }

            mCamera = Camera.open(CurrentCameraId);
            mCamera.setPreviewDisplay(mHolder);
            initCamera();
        } catch (Exception ex) {
            if(null != mCamera) {
                mCamera.release();
                mCamera = null;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_record_button:
                if(!isRecording){

                    mStartRecordBtn.setText("点击停");
                    mStartRecordBtn.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.color_e64));
                    //开始录制视频
                    startRecord();

                }else{

                    mStartRecordBtn.setText("点击拍");
                    mStartRecordBtn.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.color_333));
                    mStartRecordBtn.setTextColor(ContextCompat.getColor(this,R.color.color_e64));
                    //停止录制视频
                    stopRecord();
                }
                break;
        }
    }

    private void startRecord() {
        if(SdcardUtils.isSdCardAvailable()){

            //创建视频保存文件
            createTmpVideo();

            //设置参数
            mRecoder = new MediaRecorder();
            mRecoder.reset();

            mCamera.stopPreview();
            mCamera.unlock();
            mRecoder.setCamera(mCamera);

            mRecoder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecoder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mRecoder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecoder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mRecoder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mRecoder.setVideoFrameRate(ApplicationConstant.VIDEO_FPS);
            mRecoder.setVideoSize(640,480);
            mRecoder.setVideoEncodingBitRate(1024*1024);
            mRecoder.setPreviewDisplay(mHolder.getSurface());
            mRecoder.setOrientationHint(mCameraOrientation);
//            mRecoder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
            mRecoder.setOutputFile(videoTmpFile.getAbsolutePath());
            mRecoder.setMaxDuration(60*1000);//设置30s
            mRecoder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mr, int what, int extra) {
                    if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {

                       stopRecord();
                    }
                }
            });



            try {
                mRecoder.prepare();
                mRecoder.start();
                isRecording = true;

                setTitleName("00:00");
                timeCount = new Timer();
                timeCount.schedule(new TimerTask() {
                  @Override
                  public void run() {

                      mHandler.sendEmptyMessage(TIMER_CLOCK);

                  }
              },1000,100);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            Toast.makeText(this,"没有检测到SD卡存在",Toast.LENGTH_LONG).show();

    }

    private void createTmpVideo() {

        tmpVideoDir = ApplicationConstant.MEDIA_FILES;
        File file = new File(tmpVideoDir);
        if(!file.exists()){
            boolean mark = file.mkdir();
            if(!mark) Log.i(getClass().getName(),"创建失败或者已经存在");
        }

        String tmpVideoName = tmpVideoDir +File.separator+System.currentTimeMillis()+System.nanoTime()+".mp4";
        videoTmpFile = new File(tmpVideoName);
    }

    private void stopRecord() {
        if(isRecording){
            mRecoder.stop();
            mRecoder.release();
            mRecoder = null;
            isRecording = false;
            timeCount.cancel();

            output = new File(tmpVideoDir+File.separator+System.currentTimeMillis()+".mp4");
            String cmd = " -i "+videoTmpFile.getAbsolutePath()+" -preset ultrafast -strict -2 -vf crop=480:480:0:80 -c:v libx264 -c:a copy -c:s copy -r 24 "+output;
            execFFmpegBinary(cmd);

        }
    }

    private void loadFFMpegBinary() {

        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    showUnsupportedExceptionDialog();
                }
            });
        } catch (FFmpegNotSupportedException e) {
            showUnsupportedExceptionDialog();
        }
    }

    private void execFFmpegBinary(final String command) {

        try {

            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    Log.i(TAG,"convert failure"+s);
                }

                @Override
                public void onSuccess(String s) {
                    Intent intent = new Intent();
                    intent.putExtra("filePath",output.getPath());
                    setResult(RESULT_OK, intent);
                    CameraActivity.this.stopLoading();
                    finish();
                    Log.i(TAG,"convert success"+s);
                }

                @Override
                public void onProgress(String s) {
                    startLoading("生成中...");
//                    Log.d(TAG, "Started command : ffmpeg "+command);
                    Log.d(TAG, "onProgress: ffmpeg "+s);
            }

                @Override
                public void onStart() {
                    Log.d(TAG, "Started command : ffmpeg " + command);
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "Finished command : ffmpeg "+command);
//                    stopLoading();
//                    videoTmpFile.delete();

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }
    private void showUnsupportedExceptionDialog() {
        new AlertDialog.Builder(CameraActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("错误提示")
                .setMessage("必须android 4.0 以上系统才能使用")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CameraActivity.this.finish();
                    }
                })
                .create()
                .show();

    }
}
