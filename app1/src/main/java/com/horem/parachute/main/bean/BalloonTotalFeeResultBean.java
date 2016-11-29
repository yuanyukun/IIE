package com.horem.parachute.main.bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/6/22.
 */
public class BalloonTotalFeeResultBean implements Serializable {
    private int levelNum;
    private int levelNumMin;
    private String feeStr;

    public int getLevelNum() {
        return levelNum;
    }

    public void setLevelNum(int levelNum) {
        this.levelNum = levelNum;
    }

    public int getLevelNumMin() {
        return levelNumMin;
    }

    public void setLevelNumMin(int levelNumMin) {
        this.levelNumMin = levelNumMin;
    }

    public String getFeeStr() {
        return feeStr;
    }

    public void setFeeStr(String feeStr) {
        this.feeStr = feeStr;
    }
}
