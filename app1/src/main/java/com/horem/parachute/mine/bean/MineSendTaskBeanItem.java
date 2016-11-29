package com.horem.parachute.mine.bean;

import java.io.Serializable;

/**
 * Created by user on 2016/4/15.
 */
public class MineSendTaskBeanItem implements Serializable {

    /// <summary>
    /// id
    /// </summary>
    public String id;
    /// <summary>
    /// 名称
    /// </summary>
    public String name;
    /// <summary>
    /// 文件大小
    /// </summary>
    public int fileSize;
    /// <summary>
    /// 媒体类型
    /// </summary>
    public int mediaType ;
    /// <summary>
    /// 时长
    /// </summary>
    public int timeLength ;
    /// <summary>
    /// 视频预览图文件名
    /// </summary>
    public String previewImg;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public int getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(int timeLength) {
        this.timeLength = timeLength;
    }

    public String getPreviewImg() {
        return previewImg;
    }

    public void setPreviewImg(String previewImg) {
        this.previewImg = previewImg;
    }
}
