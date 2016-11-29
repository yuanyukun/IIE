package com.horem.parachute.task.bean;

/**
 * Created by user on 2016/4/11.
 */
public class TaskPayBean {
    /// <summary>
    /// 任务id
    /// </summary>
    private  String taskId;
    /// <summary>
    /// 付款人id
    /// </summary>
    private  long memberId;

    public String getTaskId() {
        return taskId;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }
}
