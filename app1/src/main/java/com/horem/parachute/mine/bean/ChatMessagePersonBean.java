package com.horem.parachute.mine.bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/7/1.
 */
public class ChatMessagePersonBean implements Serializable {
    private long memberId;
    private String memberHead;
    private String memberName;
    private boolean isEnterprise;
    private int authType;

    public long getMemberId() {
        return memberId;
    }

    public String getMemberHead() {
        return memberHead;
    }

    public String getMemberName() {
        return memberName;
    }

    public boolean isEnterprise() {
        return isEnterprise;
    }

    public int getAuthType() {
        return authType;
    }
}
