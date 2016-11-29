package com.horem.parachute.mine.bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/5/27.
 */
public class MineSeeMeSubBean implements Serializable {

    private long memberId;          //用户Id
    private String nickName;        //昵称
    private String headImg;         //头像昵称
    private String createTime;      //创建时间
    private boolean isEnterPrise;   //是否为企业账号

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public boolean isEnterPrise() {
        return isEnterPrise;
    }

    public void setEnterPrise(boolean enterPrise) {
        isEnterPrise = enterPrise;
    }
}
