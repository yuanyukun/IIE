package com.horem.parachute.balloon.Bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/6/27.
 */
public class SendFlowerBean implements Serializable {
    private String message;
    private int statusCode;
    private SendFlowerResultBean result;

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

    public SendFlowerResultBean getResult() {
        return result;
    }

    public void setResult(SendFlowerResultBean result) {
        this.result = result;
    }
}
