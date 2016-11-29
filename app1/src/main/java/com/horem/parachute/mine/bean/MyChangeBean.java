package com.horem.parachute.mine.bean;

import java.io.Serializable;

/**
 * Created by user on 2016/4/15.
 */
public class MyChangeBean implements Serializable{
    /// <summary>
    /// 请求人id
    /// </summary>
    public long memberId;

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }
}
