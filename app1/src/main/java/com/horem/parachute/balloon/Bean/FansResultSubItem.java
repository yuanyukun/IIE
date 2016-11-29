package com.horem.parachute.balloon.Bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/6/30.
 */
public class FansResultSubItem implements Serializable{
    private long userId;
    private String userName;
    private String userHead;
    private boolean isFollow;
    private int  authType;

    public int getAuthType() {
        return authType;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserHead() {
        return userHead;
    }


    public long getUserId() {
        return userId;
    }

    public boolean isFollow() {
        return isFollow;
    }
}
