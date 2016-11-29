package com.horem.parachute.balloon.Bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/6/27.
 */
public class FlowersListBean implements Serializable {
    private int flowerNumber;
    private long createUserId;
    private String createUserHead;
    private String createUserName;
    private int authType;
    private boolean isFollow;
    private int count;

    public int getAuthType() {
        return authType;
    }

    public int getFlowerNumber() {
        return flowerNumber;
    }

    public void setFlowerNumber(int flowerNumber) {
        this.flowerNumber = flowerNumber;
    }

    public long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(long createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserHead() {
        return createUserHead;
    }

    public void setCreateUserHead(String createUserHead) {
        this.createUserHead = createUserHead;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }


    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
