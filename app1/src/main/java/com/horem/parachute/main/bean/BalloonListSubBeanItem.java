package com.horem.parachute.main.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yuanyukun on 2016/6/21.
 */
public class BalloonListSubBeanItem implements Serializable {
    private String balloonId;
    private String balloonNumber;
    private String balloonDesc;
    private int mediaType;
    private int authType;

    public int getAuthType() {
        return authType;
    }

    private String balloonAddress;
    private double balloonLongitude;
    private double balloonLatitude;
    private long createPersonId;
    private String createPersonName;
    private String createPersonHead;
    private ArrayList<attListBean> attList;
    private double distance;
    private boolean  isFollow;
    private boolean isEnterprise;
    private boolean needToPublish;
    private boolean isTimeOut;
    private String createTime;
    private boolean isRecommend;
    private int giveLikeNum;
    private boolean isGiveLike;
    private int shareNum;
    private int requestNum;
    private int playNum;
    private int flowersNum;

    public void setShareNum(int shareNum) {
        this.shareNum = shareNum;
    }

    public void setRequestNum(int requestNum) {
        this.requestNum = requestNum;
    }

    public void setFlowersNum(int flowersNum) {
        this.flowersNum = flowersNum;
    }

    public void setGiveLike(boolean giveLike) {
        isGiveLike = giveLike;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public String getBalloonId() {
        return balloonId;
    }

    public String getBalloonNumber() {
        return balloonNumber;
    }

    public String getBalloonDesc() {
        return balloonDesc;
    }

    public int getMediaType() {
        return mediaType;
    }

    public String getBalloonAddress() {
        return balloonAddress;
    }

    public double getBalloonLongitude() {
        return balloonLongitude;
    }

    public double getBalloonLatitude() {
        return balloonLatitude;
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

    public ArrayList<attListBean> getAttList() {
        return attList;
    }

    public double getDistance() {
        return distance;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public boolean isEnterprise() {
        return isEnterprise;
    }

    public boolean isNeedToPublish() {
        return needToPublish;
    }

    public boolean isTimeOut() {
        return isTimeOut;
    }

    public String getCreateTime() {
        return createTime;
    }

    public boolean isRecommend() {
        return isRecommend;
    }

    public int getGiveLikeNum() {
        return giveLikeNum;
    }

    public boolean isGiveLike() {
        return isGiveLike;
    }

    public int getShareNum() {
        return shareNum;
    }

    public int getRequestNum() {
        return requestNum;
    }

    public int getPlayNum() {
        return playNum;
    }

    public int getFlowersNum() {
        return flowersNum;
    }
}
