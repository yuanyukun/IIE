package com.horem.parachute.mine.bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/5/18.
 */
public class MessageContractBean implements Serializable {
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
