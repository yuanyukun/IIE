package com.horem.parachute.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.app.utils.Md5Encrypt;
import com.common.ApplicationConstant;
import com.common.HttpUrlConstant;
import com.horem.parachute.main.AppMainActivity;
import com.horem.parachute.mine.bean.MessageBean;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kenny on 2015/09/06.
 */
public class Utils {
    private static float mDensity = -1;
    private Context mContext;

    public final static DecimalFormat ONE_DECIMAL_POINT_DF = new DecimalFormat("0.0");
    private final static Pattern sReplaceHtmlTagsPattern = Pattern.compile("<.[^>]+>");

    public static String v(String s) {
        return s == null ? "" : s;
    }

    public static <T> T v(T o, T defaultValue) {
        return o == null ? defaultValue : o;
    }

    public static int dp2px(Context context, float dpValue) {
        float scale = getScreenDensity(context);
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        float scale = getScreenDensity(context);
        return (int) (pxValue / scale + 0.5f);
    }
    /**
     * 获取时间，格式yyMMdd
     */
    public static String getCodeTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        return sdf.format(new Date());
    }

    /**
     * 生成随机数32位编码
     * @return
     */
    public static String getUUID(){
        String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
        return uuid;
    }

    // 获取百度ApiKey
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("获取百度推送ApiKey", "error " + e.getMessage());
        }
        return apiKey;
    }

    public static float getScreenDensity(Context context) {
        if (mDensity == -1) {
            mDensity = context.getResources().getDisplayMetrics().density;
        }

        return mDensity;
    }

    /**
     * /TextView设置左边的图片
     */
    public static void setLeftDrawable(TextView view, Drawable drawable){
        if(null != drawable){
            drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
//            drawable.setBounds(0, 0, 80, 80);//第一0是距左边距离，第二0是距上边距离，40分别是长宽(指定长宽）
        }
        view.setCompoundDrawables(drawable,null,null,null);
    }
    /**
     * /TextView设置左边的图片
     */
    public static void setRightDrawable(TextView view, Drawable drawable){
        if(null != drawable){
//            drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
            drawable.setBounds(0, 0, 100, 100);//第一0是距左边距离，第二0是距上边距离，40分别是长宽(指定长宽）
        }
        view.setCompoundDrawables(null,null,drawable,null);
    }

    /**
     * 字符串只包含字符
     * @param s
     * @return
     */
    public static boolean stringContainsOnlySpaces(String s) {
        if (s == null || s.length() == 0) {
            return false;
        }

        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);

            if (c != ' ' && c != '　' && c != '\n' && c != '\r') {
                return false;
            }
        }

        return true;
    }

    public static boolean stringContainsOnlyLetterAndNumeric(String s) {
        if (s == null || s.length() == 0) {
            return false;
        }
        return s.matches("^[a-zA-Z0-9]*");
    }

    public static boolean stringCanBeYyid(String s) {
        if (s == null || s.length() == 0) {
            return false;
        }
        return s.matches("^[a-zA-Z][a-zA-Z0-9]{3,19}$");
    }

 /*   public static boolean isIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.GET_ACTIVITIES);
        return list.size() > 0;
    }*/

    private final static int ONE_GIGABYTE = 1024 * 1024 * 1024;
    private final static int ONE_MEGABYTE = 1024 * 1024;
    private final static int ONE_KILOBYTE = 1024;

    /**
     * 格式化容量大小
     *
     * @param sizeInByte long类型的字节数
     * @return 格式后的大小
     * *
     */
    public static String formatSizeInByte(long sizeInByte) {
        if (sizeInByte >= ONE_GIGABYTE)
            return ONE_DECIMAL_POINT_DF.format((double) sizeInByte / ONE_GIGABYTE) + "G";

        else if (sizeInByte >= ONE_MEGABYTE)
            return ONE_DECIMAL_POINT_DF.format((double) sizeInByte / ONE_MEGABYTE) + "M";

        else if (sizeInByte >= ONE_KILOBYTE)
            return ONE_DECIMAL_POINT_DF.format((double) sizeInByte / ONE_KILOBYTE) + "K";

        else
            return sizeInByte + "B";
    }

    public static String urlEncode(String str) {
        try {
            return java.net.URLEncoder.encode(str, "utf-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return str;
    }

    public static String urlDecode(String str) {
        try {
            return java.net.URLDecoder.decode(str, "utf-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return str;
    }

    public static String removeHtmlTags(String html) {
        return sReplaceHtmlTagsPattern.matcher(html).replaceAll("");
    }

    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;

        } catch (Exception e) {
        }

        return null;
    }

    public static int getVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;

        } catch (Exception e) {
        }

        return 0;
    }

    public static int parseIntQuietly(String s, int defaultValue) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public static void openApp(Context context, String packageName) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(packageName, 0);
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            resolveIntent.setPackage(pi.packageName);
            List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(resolveIntent, 0);

            if ((apps == null) || (apps.size() == 0)) {
                return;
            }
            ResolveInfo ri = apps.get(0);
            if (ri != null) {
                String className = ri.activityInfo.name;
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName cn = new ComponentName(ri.activityInfo.packageName, className);
                intent.setComponent(cn);
                context.startActivity(intent);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static PackageInfo getPackageInfo(Context context, String packageName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
        }
        return packageInfo;
    }

    public static boolean isPackageInstalled(Context context, String packageName) {
        PackageInfo packageInfo = getPackageInfo(context, packageName);
        return (packageInfo != null);
    }

    /**
     * 用来判断服务是否运行.
     *
     * @param context
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRunning(Context context, String className) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        try {
            List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(100);
            if (serviceList != null && serviceList.size() > 0) {
                for (int i = 0; i < serviceList.size(); i++) {
                    if (className.equals(serviceList.get(i).service.getClassName())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getFilterTextFromEditText(EditText editText) {
        if (editText == null) return "";

        final Editable editable = editText.getText();
        if (editable == null) return "";

        String text = editable.toString().trim();
        text = text.replace('\n', ' ').replace('\r', ' ');
        return text;
    }

    public static CharSequence replaceTextWithDrawable(Context context, String src, String keyword, int drawableRes) {
        if (TextUtils.isEmpty(src)) {
            return src;
        }
        if (TextUtils.isEmpty(keyword) || drawableRes <= 0) {
            return src;
        }

        SpannableString spannableString = new SpannableString(src);
        ImageSpan span = new ImageSpan(context, drawableRes);
        final int start = src.indexOf(keyword);
        spannableString.setSpan(span, start, start + keyword.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public static String getRealPathFromURI(Context context, Uri contentURI) {
        String result = null;
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentURI, proj, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = contentURI.getPath();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return result;
    }


    public static final String PATTERN_URL_STRING = "^((http(s)?|ftp):\\/\\/[a-zA-Z0-9\\-_]+\\.[a-zA-Z0-9]+(.)+)+";
    public static final Pattern PATTERN_URL = Pattern.compile(PATTERN_URL_STRING);

    public static boolean isUrlText(String text) {
        if (text == null || text.length() == 0) {
            return false;
        }
        Matcher matcher = PATTERN_URL.matcher(text);
        if (matcher != null && matcher.find()
                && matcher.start() == 0 && matcher.end() == text.length()) {
            return true;
        }
        return false;
    }


    private static final String SALT_DIGEST = "2njn@dj5j2";


    /**
     * 判断当前进程是否是主进程
     */
    public static boolean isMainProcess(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.pid == android.os.Process.myPid()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String generate4RandomPwd() {
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        sb.append(r.nextInt(10000));
        while (sb.length() < 4) {
            sb.insert(0, 0);
        }
        return sb.toString();
    }

    public static void uninstallPkg(Activity activity, String pkgName) {
        Intent intent = new Intent(Intent.ACTION_DELETE, Uri.fromParts("package", pkgName, null));
        activity.startActivity(intent);
    }

    public static void startTargetApp(Context context, String pkgName) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(pkgName, 0);
            Intent targetIntent = new Intent(Intent.ACTION_MAIN, null);
            targetIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            targetIntent.setPackage(info.packageName);
            List<ResolveInfo> appsList = context.getPackageManager().queryIntentActivities(targetIntent, 0);
            if ((appsList == null) || (appsList.size() == 0)) {
                return;
            }
            ResolveInfo targetApp = appsList.get(0);
            if (targetApp != null) {
                String packageName = targetApp.activityInfo.packageName;
                String className = targetApp.activityInfo.name;
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(new ComponentName(packageName, className));
                context.startActivity(intent);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取屏幕分辨率
     * @param context
     * @return
     */
    public static int[] getScreenDispaly(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = windowManager.getDefaultDisplay().getWidth();// 手机屏幕的宽度
        int height = windowManager.getDefaultDisplay().getHeight();// 手机屏幕的高度
        int result[] = { width, height };
        return result;
    }

    /**
     * 获取竖直屏幕长宽比
     * @param context
     * @return
     */
    public static float getScreenRatePortrait(Context context){
        int[] P = getScreenDispaly(context);
        float W = P[0];
        float H = P[1];
        return (H/W);
    }
    /**
    * 验证邮箱
    * @param email
    * @return
            */
    public static boolean checkEmail(String email){
        boolean flag = false;
        try{
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        }catch(Exception e){
            flag = false;
        }
        return flag;
    }

    /**
     * 验证手机号码
     * @param mobileNumber
     * @return
     */
    public static boolean checkMobileNumber(String mobileNumber){
        boolean flag = false;
        try{
            Pattern regex = Pattern.compile("^(((13[0-9])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$");
            Matcher matcher = regex.matcher(mobileNumber);
            flag = matcher.matches();
        }catch(Exception e){
            flag = false;
        }
        return flag;
    }

    /**
     * 获取横屏屏幕长宽比
     * @param context
     * @return
     */
    public static float getScreenRateLandscape(Context context){
        int[] P = getScreenDispaly(context);
        float W = P[0];
        float H = P[1];
        return (W/H);
    }

    /**
     * 保留小数点后两位数
     * @param value
     * @return
     */
    public static String getTwoLastNumber(double value){
        DecimalFormat    df   = new DecimalFormat("######0.00");
        return df.format(value);
    }

    /**
     * 获取用户头像
     * @return
     */
    public static String getHeadeUrl(String ImageName) {

        return HttpUrlConstant.getBigImageUrl+"&imgName="+ImageName
                +"&memberId=" + 0;
    }
    public static String getHeadeUrl(long memberId) {

        return HttpUrlConstant.getHeadImageUrl+"&type=10"
                +"&memberId=" + memberId
                +"&width=0"
                +"&height=0";
    }

    /**
     * 接获取小图 url
     * @param
     * @return
     */
    public static String getSmalleImageUrl(String imageName,float dpWidth,float dpHeight,Context context,String oid,int ost) {

        return  HttpUrlConstant.getSmallImageUrl+"?imgName="+imageName
                +"&width="+dp2px(context,dpWidth)+"&height="+dp2px(context,dpHeight)
                +"&oid="+oid+"&ost="+ost;
    }
    /**
     * 接获取小图 url
     * @param
     * @return
     */
    public static String getSmalleImageUrl(String imageName,float dpWidth,float dpHeight,Context context) {

        return  HttpUrlConstant.getSmallImageUrl+"?imgName="+imageName
                +"&width="+dp2px(context,dpWidth)+"&height="+dp2px(context,dpHeight);
    }
 /*   *//**
     *拼接获取大图url
     *//*
    public static String getBigImageUrl(long memberId,String ImageName) {

        return HttpUrlConstant.getBigImageUrl+"&imgName="+ImageName
                +"&memberId=" + memberId;
    }*/
    /**
     *拼接获取大图url
     */
    public static String getBigImageUrl(long memberId,String ImageName,String oid,int ost) {

        return HttpUrlConstant.getBigImageUrl+"&imgName="+ImageName
                +"&memberId=" + memberId
                +"&oid=" + oid
                +"&ost=" + ost;
    }
    /**
     *拼接获取大图url
     */
    public static String getBigImageUrl(long memberId,String ImageName) {

        return HttpUrlConstant.getBigImageUrl+"&imgName="+ImageName
                +"&memberId=" + memberId;
    }

    /**
     * 获取视频预览图
     * @return
     */
    public static String getVideoPreviewImgUrl(String imageName) {

        return HttpUrlConstant.getVideoPreviewImgUrl+imageName
                + HttpUrlConstant.key + "&imageName="+imageName;
    }

    /**
     *rename the file
     * @param oldFile
     * @param newFilePath
     * @return
     */
    public static boolean renameFile(String oldFile, String newFilePath) {

        File toBeRenamed = new File(oldFile);
        //检查要重命名的文件是否存在，是否是文件
        if (!toBeRenamed.exists() || toBeRenamed.isDirectory()) {

            System.out.println("File does not exist: " + oldFile);
            return false;
        }

        File newFile = new File(ApplicationConstant.MEDIA_FILES + File.separator + newFilePath);

        //修改文件名
        if (toBeRenamed.renameTo(newFile)) {
            return true;
        } else {
            return false;
        }

    }

    public static String getDistance(double p_distance) {
        if(p_distance < 1){
            return Math.round(1000*p_distance)+"m";
        }
        DecimalFormat    df   = new DecimalFormat("######0.0");

        return df.format(p_distance)+"km";
    }

    public static byte[] bmpToByteArray(Bitmap thump, boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        thump.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            thump.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
    public static String getFileNameSplit(String path)
    {
        return path.split("\\?")[0];
    }

    public static String getVideoFileUrl(Context context,String videoName,String TaskOrderId,int TaskOrderStatus){
         String Url = HttpUrlConstant.getVideoUrl+videoName+
                HttpUrlConstant.key+"&videoName="+videoName+"&memberId="
                +SharePreferencesUtils.getLong(context,SharePrefConstant.MEMBER_ID,(long)0)
                +"&oid="+TaskOrderId+"&ost="+TaskOrderStatus;
        return Url;
    }
    public static String getVideoFileUrl(Context context,String videoName){
         String Url = HttpUrlConstant.videoDownloadUrl+videoName+
                HttpUrlConstant.key+"&videoName="+videoName+"&memberId="
                +SharePreferencesUtils.getLong(context,SharePrefConstant.MEMBER_ID,(long)0);
        return Url;
    }
    //将服务器的文件转改名成本地具有唯一性的文件
    public static String getVideoFileNewName(Context context,String videoName,String TaskOrderId,int TaskOrderStatus){
         String Url = HttpUrlConstant.getVideoUrl+videoName+
                HttpUrlConstant.key+"&videoName="+videoName+"&memberId="
                +SharePreferencesUtils.getLong(context,SharePrefConstant.MEMBER_ID,(long)0)
                +"&oid="+TaskOrderId+"&ost="+TaskOrderStatus;
        return  Md5Encrypt.MD5(Url)+".mp4";
    }
    //将服务器的文件转改名成本地具有唯一性的文件
    public static String getVideoFileNewName(Context context,String videoName){
         String Url = HttpUrlConstant.getVideoUrl+videoName+
                HttpUrlConstant.key+"&videoName="+videoName+"&memberId="
                +SharePreferencesUtils.getLong(context,SharePrefConstant.MEMBER_ID,(long)0);
        return  Md5Encrypt.MD5(Url)+".mp4";
    }
//    //是否在本地有缓存文件
//    public static  boolean isFileCache(Context context,String videoName,String TaskOrderId,int TaskOrderStatus ){
//        final String newName = getVideoFileNewName(context,videoName,TaskOrderId,TaskOrderStatus);
//        File file = new File(ApplicationConstant.MEDIA_FILES);
//        File[] files = file.listFiles();
//
//        boolean isCache = false;
//        for(File mFile:files){
//            if(mFile.getName().equals(newName)){
//                isCache = true;
//                break;
//            }
//        }
//        return isCache;
//    }
    //是否在本地有缓存文件
    public static  boolean isFileCache(Context context,String videoName ){
        final String newName = getVideoFileNewName(context,videoName);
        boolean isCache = false;
        File file = new File(ApplicationConstant.MEDIA_FILES);
        if(!file.exists()){
            file.mkdir();
            isCache = false;
        } else {

            File[] files = file.listFiles();
            if(files != null) {
                for (File mFile : files) {
                    if (mFile.getName().equals(newName)) {
                        isCache = true;
                        break;
                    }
                }
            }
        }
        return isCache;
    }
    public static void  playVideoFile(String newName,Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("file://"+ ApplicationConstant.MEDIA_FILES+ File.separator+newName);
        intent.setDataAndType(uri, "video/mp4");
        context.startActivity(intent);
    }

    public static boolean isFirstEnter(Context activity) {

        boolean isFirst = false;
        PackageInfo info = null;
        //获取上一版本的版本号
        int lastVersion = SharePreferencesUtils.getInt(activity, SharePrefConstant.VERSION_CODE, -1);
        int currentVersion = -1;
        try {
            info = activity.getPackageManager().getPackageInfo("com.horem.parachute", 0);
            currentVersion = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //如果数据库中没有保存，则生成一个安装唯一码
        if (lastVersion == -1) {
            isFirst = true;
            String code = Utils.getCodeTime() + Utils.getUUID();
            Log.d("安装唯一码: ", code);
            SharePreferencesUtils.setString(activity, SharePrefConstant.INSTALL_CODE, code);
            SharePreferencesUtils.setInt(activity, SharePrefConstant.VERSION_CODE, currentVersion);
        } else if (lastVersion > 0) {

            if (currentVersion > lastVersion) {
                //第一次启动
                isFirst = true;
                SharePreferencesUtils.setInt(activity, SharePrefConstant.VERSION_CODE, currentVersion);
            }
        }
        return isFirst;
    }
    public static String showFileLength(long length){
        if(length < 10)
            return "00:0"+length;
        else if(length == 60)
            return "01:00";
        else
            return "00:"+length;
    }

    public static final boolean GPSIsOPen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
//        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps ) {
            return true;
        }
        return false;
    }
    public static  String getRunningActivityName(Context context) {
        String contextString = context.toString();
        return contextString.substring(contextString.lastIndexOf(".") + 1, contextString.indexOf("@"));
    }
}