package com.horem.parachute.mine.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yuanyukun on 2016/5/31.
 */
public class MinePhotoBean implements Serializable {
    private String memberName;
    private String headImg;
    private String message;
    private int stausCode;
    private int attachmentNum;
    private ArrayList<MinePhotoSubBean> attachmentList;

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStausCode() {
        return stausCode;
    }

    public void setStausCode(int stausCode) {
        this.stausCode = stausCode;
    }

    public ArrayList<MinePhotoSubBean> getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(ArrayList<MinePhotoSubBean> attachmentList) {
        this.attachmentList = attachmentList;
    }

    public int getAttachmentNum() {
        return attachmentNum;
    }

    public void setAttachmentNum(int attachmentNum) {
        this.attachmentNum = attachmentNum;
    }
}
