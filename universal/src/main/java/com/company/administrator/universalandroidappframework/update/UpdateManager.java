package com.company.administrator.universalandroidappframework.update;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Administrator on 2016/11/16.
 */

public class UpdateManager {
    private static UpdateManager manager;
    private ThreadPoolExecutor threadPoolExecutor;
    private UpdateDownloadRequest request = null;

    private UpdateManager(){
        threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    static {
        manager = new UpdateManager();
    }
    public static UpdateManager getInstance(){
        return manager;
    }

    public void startDownload(String downloadUrl,String localPath,UpdateDownloadListener listener){
        if(request != null){
            return;
        }
        checkLocalFilePath(localPath);
        //开始去下载
        request = new UpdateDownloadRequest(downloadUrl,localPath,listener);
        Future<?> future = threadPoolExecutor.submit(request);
    }
    //用来检查当前文件路径是否已经存在
    private void checkLocalFilePath(String localPath) {
        File dir = new File(localPath.substring(0,localPath.lastIndexOf("/")+1));
        if(!dir.exists()){
            dir.mkdir();
        }
        File file = new File(localPath);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {

            }
        }
    }


}
