package com.company.administrator.universalandroidappframework.update;

/**
 * Created by Administrator on 2016/11/16.
 */

public interface UpdateDownloadListener {
    /**
     * 下载开始回调
     */
     void onStart();

    /**
     * 进度更新回调
     * @param progress
     * @param downloadUrl
     */
     void onProgressChange(int progress,String downloadUrl);

    /**
     * 下载完成回调
     * @param completeSize
     * @param downloadUrl
     */
     void onFinished(int completeSize,String downloadUrl);

    /**
     * 下载失败回调
     */
     void onFailure();
}
