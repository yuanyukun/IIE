package com.horem.parachute.mine.bean;

import com.horem.parachute.main.bean.attListBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yuanyukun on 2016/7/8.
 */
public class balloonDetailBean implements Serializable {
    private ArrayList<attListBean> attList;
    private String balloonDesc;
    private String balloonId;
    private String createTime;
    private boolean isTimeOut;
    private int mediaType;
    private String no;
    private String place;

    public ArrayList<attListBean> getAttList() {
        return attList;
    }

    public String getBalloonDesc() {
        return balloonDesc;
    }

    public String getBalloonId() {
        return balloonId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public boolean isTimeOut() {
        return isTimeOut;
    }

    public int getMediaType() {
        return mediaType;
    }

    public String getNo() {
        return no;
    }

    public String getPlace() {
        return place;
    }
}
