package com.horem.parachute.mine.bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/5/31.
 */
public class MinePhotoSubBean implements Serializable {
    private String id;
    private String name;
    private boolean isPrivate;
    private String taskOrderId;
    private int taskOrderState;
    private long createPersonId;
    private String createPersonName;
    private String createPersonHead;
    private long fileSize;
    private int mediaType;
    private long timeLength;
    private String previewImg;
    private String refGuid;
    private String createTime;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public String getTaskOrderId() {
        return taskOrderId;
    }

    public int getTaskOrderState() {
        return taskOrderState;
    }

    public long getCreatePersonId() {
        return createPersonId;
    }

    public String getCreatePersonName() {
        return createPersonName;
    }

    public String getCreatePersonHead() {
        return createPersonHead;
    }

    public long getFileSize() {
        return fileSize;
    }

    public int getMediaType() {
        return mediaType;
    }

    public long getTimeLength() {
        return timeLength;
    }

    public String getPreviewImg() {
        return previewImg;
    }

    public String getRefGuid() {
        return refGuid;
    }

    public String getCreateTime() {
        return createTime;
    }
}
