package com.horem.parachute.mine.bean;

import com.horem.parachute.main.bean.attListBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yuanyukun on 2016/8/8.
 */
public class GiveLikeMeListItemBean implements Serializable {
    private String giveLikeId;
    private String balloonId;
    private int mediaType;
    private String balloonAddress;
    private String balloonCreateTime;
    private ArrayList<attListBean> attList;
    private boolean isFollow;
    private int authType;
    private String createTime;
    private long userId;
    private String userName;
    private String userHead;

    public String getGiveLikeId() {
        return giveLikeId;
    }

    public void setGiveLikeId(String giveLikeId) {
        this.giveLikeId = giveLikeId;
    }

    public String getBalloonId() {
        return balloonId;
    }

    public void setBalloonId(String balloonId) {
        this.balloonId = balloonId;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public String getBalloonAddress() {
        return balloonAddress;
    }

    public void setBalloonAddress(String balloonAddress) {
        this.balloonAddress = balloonAddress;
    }

    public String getBalloonCreateTime() {
        return balloonCreateTime;
    }

    public void setBalloonCreateTime(String balloonCreateTime) {
        this.balloonCreateTime = balloonCreateTime;
    }

    public ArrayList<attListBean> getAttList() {
        return attList;
    }

    public void setAttList(ArrayList<attListBean> attList) {
        this.attList = attList;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public int getAuthType() {
        return authType;
    }

    public void setAuthType(int authType) {
        this.authType = authType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserHead() {
        return userHead;
    }

    public void setUserHead(String userHead) {
        this.userHead = userHead;
    }
}
