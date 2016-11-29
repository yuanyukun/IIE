package com.horem.parachute.balloon.Bean;

import com.horem.parachute.main.bean.attListBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yuanyukun on 2016/6/30.
 */
public class FlowersHaveSendSubItem implements Serializable {
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

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public String getFlowersId() {
        return flowersId;
    }

    public String getBalloonId() {
        return balloonId;
    }

    public String getBalloonCreateTime() {
        return balloonCreateTime;
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

    public ArrayList<attListBean> getAttList() {
        return attList;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public String getCreateTime() {
        return createTime;
    }

    public int getFlowersNum() {
        return flowersNum;
    }

    public long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserHead() {
        return userHead;
    }
}
