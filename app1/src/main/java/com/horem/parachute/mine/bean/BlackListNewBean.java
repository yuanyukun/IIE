package com.horem.parachute.mine.bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/6/14.
 */
public class BlackListNewBean implements Serializable {
    private String message;
    private int statusCode;
    private BlackListBean result;

    public BlackListBean getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
