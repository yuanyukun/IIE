package com.horem.parachute.task.bean;

import com.horem.parachute.main.bean.attListBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yuanyukun on 2016/6/14.
 */
public class BalloonBean implements Serializable {

        private String balloonId;
        private String balloonNumber;
        private String balloonDesc;
        private int mediaType;
        private String balloonAddress;
        private double balloonLongitude;
        private double balloonLatitude;
        private long createPersonId;
        private String createPersonName;
        private String createPersonHead;
        private double  distance;
        private boolean isEnterprise ;
        private boolean needToPublish ;
        private boolean isFollow ;
        private boolean isTimeOut ;
        private boolean isRecommend ;
        private boolean isGiveLike ;
        private String createTime;
        private int giveLikeNum;
        private int shareNum;
        private int requestNum;
        private int authType;

        private ArrayList<FileInfoBean> attList;

    public int getAuthType() {
        return authType;
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

    public double getDistance() {
        return distance;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public boolean isEnterprise() {
        return isEnterprise;
    }

    public boolean isNeedToPublish() {
        return needToPublish;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public boolean isTimeOut() {
        return isTimeOut;
    }

    public boolean isRecommend() {
        return isRecommend;
    }

    public boolean isGiveLike() {
        return isGiveLike;
    }

    public String getCreateTime() {
        return createTime;
    }

    public int getGiveLikeNum() {
        return giveLikeNum;
    }

    public int getShareNum() {
        return shareNum;
    }

    public int getRequestNum() {
        return requestNum;
    }

    public ArrayList<FileInfoBean> getAttList() {
        return attList;
    }

}
