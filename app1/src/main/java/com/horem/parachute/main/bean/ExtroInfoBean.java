package com.horem.parachute.main.bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/6/22.
 */
public class ExtroInfoBean implements Serializable {
    private boolean isGiveLike;
    private boolean isFollow;
    private int flowersNum;
    private int shareNum;
    private int requestNum;
    private int giveLikeNum;

    public int getGiveLikeNum() {
        return giveLikeNum;
    }

    public void setGiveLikeNum(int giveLikeNum) {
        this.giveLikeNum = giveLikeNum;
    }

    public boolean isGiveLike() {
        return isGiveLike;
    }

    public void setGiveLike(boolean giveLike) {
        isGiveLike = giveLike;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public int getFlowersNum() {
        return flowersNum;
    }

    public void setFlowersNum(int flowersNum) {
        this.flowersNum = flowersNum;
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
}
