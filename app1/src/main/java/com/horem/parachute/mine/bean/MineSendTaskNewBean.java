package com.horem.parachute.mine.bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/6/15.
 */
public class MineSendTaskNewBean implements Serializable {
    private String message;
    private int statusCode;
    private MineSendTaskBean result;

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public MineSendTaskBean getResult() {
        return result;
    }
}
