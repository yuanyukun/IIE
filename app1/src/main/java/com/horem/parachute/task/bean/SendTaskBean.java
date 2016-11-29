package com.horem.parachute.task.bean;

/**
 * Created by user on 2016/4/8.
 */
public class SendTaskBean {

    /// <summary>
    /// 接收人数量1
    /// </summary>
    public int receiveNum;
    /// <summary>
    /// 任务地点2
    /// </summary>
    public long memberId;
    /// <summary>
    /// 创建人经度3
    /// </summary>
    public int limitDays;

    public int getReceiveNum() {
        return receiveNum;
    }

    public void setReceiveNum(int receiveNum) {
        this.receiveNum = receiveNum;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public int getLimitDays() {
        return limitDays;
    }

    public void setLimitDays(int limitDays) {
        this.limitDays = limitDays;
    }
}
