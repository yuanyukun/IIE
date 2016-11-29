package com.horem.parachute.util;

import android.content.Context;
import android.os.Environment;
import java.io.*;

public class SdcardUtils  {

    public static boolean isSdCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    private static File mkDir(String path) {
        File dir = new File(path);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir;
    }

    public static File createCacheFolder(Context mContext, String name) {
        String cacheDir = null;

        if (isSdCardAvailable() ) {
            cacheDir =Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + name;
//            cacheDir = mContext.getFilesDir().getAbsolutePath() + File.separator + name;
        }

        return mkDir(cacheDir);
    }

    public static File createTmpFolder(Context mContext) {
        return createCacheFolder(mContext, "tmp");
    }

    public static File createTmpSubFolder(Context mContext, String name) {
        File tmp = createTmpFolder(mContext);
        return mkDir(tmp.getAbsolutePath() + File.separator + name);
    }

    public static boolean copyFile(String oldPath, String newPath) {
        int bytes;
        boolean isOk = false;
        File oldFile = new File(oldPath);

        if (!oldFile.exists()) {
            return isOk;
        }

        try {
            InputStream in = new FileInputStream(oldPath); // 读入原文件
            FileOutputStream fos = new FileOutputStream(newPath);
            byte[] buffer = new byte[4096];

            while ((bytes = in.read(buffer)) != -1) {
                fos.write(buffer, 0, bytes);
            }

            fos.flush();
            fos.close();
            in.close();
            isOk = true;

        } catch (IOException e) {
            e.printStackTrace();
            isOk = false;
        }

        return isOk;
    }

    public static void deleteFile(String filePathAndName) {
        try {
            File file = new File(filePathAndName);
            file.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean moveFile(String oldPath, String newPath) {
        if (copyFile(oldPath, newPath)) {
            deleteFile(oldPath);
            return true;
        }

        return false;
    }

    public static File getAutoLoginFileDir(){
        return new File(Environment.getExternalStorageDirectory().toString() + File.separator + "warthog.log");
    }
}