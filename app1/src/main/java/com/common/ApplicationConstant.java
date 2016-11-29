package com.common;

import android.os.Environment;

import java.io.File;

/**
 * Created by user on 2016/3/17.
 */
public class ApplicationConstant {
    /**
     * 修改类型
     **/
    public static final int MODIFY_PHONE = 1001;
    public static final int MODIFY_EMAIL = 1002;
    public static final int MODIFY_NAME = 1003;
    //进入类型
    public static final int USER_TYPE = 1;
    public static final int COST_TYPE = 2;
    public static final int TIME_TYPE = 3;
    public static final int BLON_TYPE = 4;

    /**
     * 媒体类型
     */
    public static final int MEDIA_TYPE_PHOTO = 10;
    public static final int MEDIA_TYPE_AUDIO = 20;
    public static final int MEDIA_TYPE_VIDEO = 30;
    /**
     * 加密签名key
     */
    public  static final String signKey="Jiang1Luo5San0Web2Api15Get02Sign";
    /**
     * 设置上传图片的最大值
     */
    public static final int MAX_IMAGE_NUM = 4;
    /**
     * 手机相册的临时目录
     */
    public static final String MEDIA_FILES = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"DCIM"+File.separator+"伞来了";
    /**
     * 视频录制相关的设置
     */
    public static final int VIDEO_FPS = 24; //视频帧率

    /**
     * 用户类型
     */
    public static  final int ENTERPRISE = 10;
    public static  final int NORMALUSER = 20;

}
