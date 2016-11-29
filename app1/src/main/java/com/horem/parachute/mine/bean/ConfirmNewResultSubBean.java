package com.horem.parachute.mine.bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/8/10.
 */
public class ConfirmNewResultSubBean  implements Serializable{
    private String id;
    private String name;
    private String taskOrderId;
    private boolean isPrivate;
    private int taskOrderstate;
    private long createPersonId;
    private String createPersonName;
    private String createPersonHead;
    private float fileSize;
    private int mediaType;
    private long timeLength;
    private String previewImg;

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

    public String getTaskOrderId() {
        return taskOrderId;
    }

    public void setTaskOrderId(String taskOrderId) {
        this.taskOrderId = taskOrderId;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public int getTaskOrderstate() {
        return taskOrderstate;
    }

    public void setTaskOrderstate(int taskOrderstate) {
        this.taskOrderstate = taskOrderstate;
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

    public float getFileSize() {
        return fileSize;
    }

    public void setFileSize(float fileSize) {
        this.fileSize = fileSize;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public long getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(long timeLength) {
        this.timeLength = timeLength;
    }

    public String getPreviewImg() {
        return previewImg;
    }

    public void setPreviewImg(String previewImg) {
        this.previewImg = previewImg;
    }
}
