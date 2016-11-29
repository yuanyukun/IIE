package com.horem.parachute.task.bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/6/28.
 */
public class FlowerBean implements Serializable{
    private String  flowersId;
    private long memberId;

    public String getFlowersId() {
        return flowersId;
    }

    public void setFlowersId(String flowersId) {
        this.flowersId = flowersId;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }
}
