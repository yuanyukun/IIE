package com.horem.parachute.mine.bean;

import java.io.Serializable;

/**
 * Created by user on 2016/4/20.
 */
public class MessageBean implements Serializable {
    private String message;
    private int statusCode;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
