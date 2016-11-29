package com.horem.parachute.mine.bean;

import java.io.Serializable;

/**
 * Created by user on 2016/4/27.
 */
public class ConfirmBean implements Serializable {
    /// <summary>
    /// 子任务id
    /// </summary>
    private  String subTaskId;
    /// <summary>
    /// <summary>
    /// 创建人id
    /// </summary>
    private  long memberId;

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public String getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(String subTaskId) {
        this.subTaskId = subTaskId;
    }
}
