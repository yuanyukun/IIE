package com.horem.parachute.balloon.Bean;

import com.horem.parachute.main.bean.attListBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yuanyukun on 2016/6/23.
 */
public class BalloonInfoResultBean implements Serializable {
    private String balloonId;
    private String no;
    private String place;
    private double Longitude;
    private double Latitude;
    private String balloonDesc;
    private long createPersonId;
    private String createPersonName;
    private String createPersonHead;

    public int getGiveLikeNum() {
        return giveLikeNum;
    }

    public void setGiveLikeNum(int giveLikeNum) {
        this.giveLikeNum = giveLikeNum;
    }

    private int giveLikeNum;

    private boolean isGiveLike;

    public boolean isGiveLike() {
        return isGiveLike;
    }

    public void setGiveLike(boolean giveLike) {
        isGiveLike = giveLike;
    }

    private boolean  isFollow;
    private boolean isEnterprise;
    private boolean needToPublish;
    private boolean isTimeOut;

    private ArrayList<attListBean> attList;
    private double distance;
    private int mediaType;
    private String createTime;
    private String validityTime;
    private String timeOutStr;

    private ArrayList<FlowersBean> flowersList;
    private int shareNum;
    private int requestNum;
    private int flowersNum;
    private int authType;

    public int getAuthType() {
        return authType;
    }

    public String getBalloonId() {
        return balloonId;
    }

    public void setBalloonId(String balloonId) {
        this.balloonId = balloonId;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public String getBalloonDesc() {
        return balloonDesc;
    }

    public void setBalloonDesc(String balloonDesc) {
        this.balloonDesc = balloonDesc;
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

    public ArrayList<attListBean> getAttList() {
        return attList;
    }

    public void setAttList(ArrayList<attListBean> attList) {
        this.attList = attList;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public boolean isEnterprise() {
        return isEnterprise;
    }

    public void setEnterprise(boolean enterprise) {
        isEnterprise = enterprise;
    }

    public boolean isNeedToPublish() {
        return needToPublish;
    }

    public void setNeedToPublish(boolean needToPublish) {
        this.needToPublish = needToPublish;
    }

    public boolean isTimeOut() {
        return isTimeOut;
    }

    public void setTimeOut(boolean timeOut) {
        isTimeOut = timeOut;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getValidityTime() {
        return validityTime;
    }

    public void setValidityTime(String validityTime) {
        this.validityTime = validityTime;
    }

    public String getTimeOutStr() {
        return timeOutStr;
    }

    public void setTimeOutStr(String timeOutStr) {
        this.timeOutStr = timeOutStr;
    }

    public ArrayList<FlowersBean> getFlowersList() {
        return flowersList;
    }

    public void setFlowersList(ArrayList<FlowersBean> flowersList) {
        this.flowersList = flowersList;
    }

    public int getShareNum() {
        return shareNum;
    }

    public void setShareNum(int shareNum) {
        this.shareNum = shareNum;
    }

    public int getRequestNum() {
        return requestNum;
    }

    public void setRequestNum(int requestNum) {
        this.requestNum = requestNum;
    }

    public int getFlowersNum() {
        return flowersNum;
    }

    public void setFlowersNum(int flowersNum) {
        this.flowersNum = flowersNum;
    }
}
