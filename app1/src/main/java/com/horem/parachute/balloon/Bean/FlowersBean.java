package com.horem.parachute.balloon.Bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/6/23.
 */
public class FlowersBean implements Serializable {
    private int flowerNumber;
    private long createUserId;
    private String createUserName;
    private String createUserHead;

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

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getCreateUserHead() {
        return createUserHead;
    }

    public void setCreateUserHead(String createUserHead) {
        this.createUserHead = createUserHead;
    }
}
