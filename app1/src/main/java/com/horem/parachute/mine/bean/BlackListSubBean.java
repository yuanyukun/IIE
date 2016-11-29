package com.horem.parachute.mine.bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/5/30.
 */
public class BlackListSubBean implements Serializable {

    private int personalType;
    private long personalId;
    private String personalName;
    private String personalHead;
    private String createTime;

    public int getPersonalType() {
        return personalType;
    }

    public void setPersonalType(int personalType) {
        this.personalType = personalType;
    }

    public long getPersonalId() {
        return personalId;
    }

    public void setPersonalId(long personalId) {
        this.personalId = personalId;
    }

    public String getPersonalName() {
        return personalName;
    }

    public void setPersonalName(String personalName) {
        this.personalName = personalName;
    }

    public String getPersonalHead() {
        return personalHead;
    }

    public void setPersonalHead(String personalHead) {
        this.personalHead = personalHead;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
