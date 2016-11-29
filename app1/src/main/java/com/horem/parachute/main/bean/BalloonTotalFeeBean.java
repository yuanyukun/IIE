package com.horem.parachute.main.bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/6/22.
 */
public class BalloonTotalFeeBean implements Serializable {
    private String message;
    private int statusCode;
    private BalloonTotalFeeResultBean result;

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

    public BalloonTotalFeeResultBean getResult() {
        return result;
    }

    public void setResult(BalloonTotalFeeResultBean result) {
        this.result = result;
    }
}
