package com.horem.parachute.task.bean;

import java.io.Serializable;

/**
 * Created by user on 2016/4/14.
 */
public class UploadCompleteBean implements Serializable{
    /// 子任务id
    /// </summary>
    public String subTaskId;
    /// <summary>
    /// 执行人id
    /// </summary>
    public long memberId;

    public String getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(String subTaskId) {
        this.subTaskId = subTaskId;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }
}
