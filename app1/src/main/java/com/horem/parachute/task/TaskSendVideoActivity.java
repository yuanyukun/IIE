package com.horem.parachute.task;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.horem.parachute.R;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.main.AppMainActivity;
import com.horem.parachute.task.httpImpl.TaskSendVideoImpl;
import com.horem.parachute.util.ToastManager;
import com.http.request.HttpApi;
import com.http.request.IResponseApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskSendVideoActivity extends BaseActivity implements IResponseApi{

    private VideoView videoView;
    private ImageView imageView;
    private SeekBar seekBar;
    private EditText mEdit;
    private static final  int RECORD_VIDEO = 1001;
    private int currentVoiceValue;
    private AudioManager audioManager;
    private String videoFilePath = null;

    private String subTaskId;
    private double lat;
    private double lng;
    private HashMap<String,String> map = new HashMap<>();

    private CustomApplication application;
    private Tracker mTracker;
    private static final String TAG  = "SendVideoAty";
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
        setContentView(R.layout.activity_task_take_video);
        init();
    }

    private void init() {
        initTitleView();
        setTitleName("拍视频");
        setRightButtonText("发送");

        subTaskId = getIntent().getStringExtra("subTaskId");
        lat = getIntent().getDoubleExtra("lat",0.0);
        lng = getIntent().getDoubleExtra("lng",0.0);
        Log.i(getClass().getName(),subTaskId+"++++ "+lat+" ____"+lng);
        map.put("subTaskId",subTaskId);
        map.put("lat",lat+"");
        map.put("lng",lng+"");

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mEdit = (EditText) findViewById(R.id.send_photo_et);
        videoView = (VideoView) findViewById(R.id.show_video_view);
        imageView = (ImageView) findViewById(R.id.plus_video_image);
        seekBar = (SeekBar) findViewById(R.id.sound_seek);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(TaskSendVideoActivity.this, CameraActivity.class);
                startActivityForResult(intent,RECORD_VIDEO);
            }
        });

    }

    @Override
    protected void OnRightButtonClicked() {

        if(mEdit.getText().toString().equals("")){
            ToastManager.show(this,"请输入您的视频简要说明");
            return;
        }
        ArrayList<File> files = new ArrayList<>();
        if(!videoFilePath.equals("")||videoFilePath != null){
            File file = new File(videoFilePath);
            files.add(file);
        }else {
            return;
        }
        startLoading();
        map.put("describe",mEdit.getText().toString().trim());
        HttpApi api = new TaskSendVideoImpl();
        api.httpRequest(this,this,map,files);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == RECORD_VIDEO && resultCode == RESULT_OK && data != null){

            videoFilePath = data.getStringExtra("filePath");
            imageView.setVisibility(View.GONE);
            LinearLayout videoViewParent = (LinearLayout) findViewById(R.id.play_video_view);
            videoViewParent.setVisibility(View.VISIBLE);

            videoView.setVideoPath(videoFilePath);
            initAudioSound();

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    mp.setLooping(true);

                }
            });

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            videoView.setVideoPath(videoFilePath);
                            videoView.start();

                        }
            });
        }
    }

    private void initAudioSound() {
        int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        seekBar.setMax(max);
        currentVoiceValue = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(videoView !=null){
            videoView.stopPlayback();
            videoView = null;
        }
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVoiceValue,0);
    }

    @Override
    public void onSuccess(Object object) {
        stopLoading();
        try {
            JSONObject jsonObject = new JSONObject(object.toString());
            String msg = jsonObject.getString("message");
            ToastManager.show(this,msg);
            int status = jsonObject.getInt("statusCode");
            if(status == 1){
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onFailed(Exception e) {

    }

    @Override
    public void onBackPressed() {
        if(videoView !=null){
            videoView.stopPlayback();
            videoView = null;
        }
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVoiceValue,0);
        super.onBackPressed();
    }
}
