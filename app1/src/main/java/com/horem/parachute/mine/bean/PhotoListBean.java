package com.horem.parachute.mine.bean;

import java.io.Serializable;

/**
 * Created by user on 2016/4/26.
 */
public class PhotoListBean implements Serializable {
    private String id;
    private String name;
    private boolean isPrivate;
    private String taskOrderId;
    private int   taskOrderState;
    private long  createPersonId;
    private String createPersonName;
    private String createPersonHead;
    private int    fileSize;
    private int    mediaType;
    private int    timeLength;
    private String previewImg;

    public String getPreviewImg() {
        return previewImg;
    }

    public void setPreviewImg(String previewImg) {
        this.previewImg = previewImg;
    }

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

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public String getTaskOrderId() {
        return taskOrderId;
    }

    public void setTaskOrderId(String taskOrderId) {
        this.taskOrderId = taskOrderId;
    }

    public int getTaskOrderState() {
        return taskOrderState;
    }

    public void setTaskOrderState(int taskOrderState) {
        this.taskOrderState = taskOrderState;
    }

    public long getCreatePersonId() {
        return createPersonId;
    }

    public void setCreatePersonId(long createPersonId) {
        this.createPersonId = createPersonId;
    }

    public String getCreatePersonName() {
        return createPersonName;
    }

    public void setCreatePersonName(String createPersonName) {
        this.createPersonName = createPersonName;
    }

    public String getCreatePersonHead() {
        return createPersonHead;
    }

    public void setCreatePersonHead(String createPersonHead) {
        this.createPersonHead = createPersonHead;
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
}
