package com.horem.parachute.balloon.Bean;

import com.horem.parachute.main.bean.attListBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yuanyukun on 2016/6/28.
 */
public class FlowersToMeSubItem implements Serializable {

    private String flowersId;
    private String balloonId;
    private String balloonCreateTime;
    private String balloonDesc;
    private int mediaType;
    private String balloonAddress;
    private ArrayList<attListBean> attList;
    private boolean isFollow;
    private String createTime;
    private int flowersNum;
    private long userId;
    private String userName;
    private String userHead;
    private int authType;


    public int getAuthType() {
        return authType;
    }

    public void setAuthType(int authType) {
        this.authType = authType;
    }

    public String getFlowersId() {
        return flowersId;
    }

    public void setFlowersId(String flowersId) {
        this.flowersId = flowersId;
    }

    public String getBalloonId() {
        return balloonId;
    }

    public void setBalloonId(String balloonId) {
        this.balloonId = balloonId;
    }

    public String getBalloonCreateTime() {
        return balloonCreateTime;
    }

    public void setBalloonCreateTime(String balloonCreateTime) {
        this.balloonCreateTime = balloonCreateTime;
    }

    public String getBalloonDesc() {
        return balloonDesc;
    }

    public void setBalloonDesc(String balloonDesc) {
        this.balloonDesc = balloonDesc;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getFlowersNum() {
        return flowersNum;
    }

    public void setFlowersNum(int flowersNum) {
        this.flowersNum = flowersNum;
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
