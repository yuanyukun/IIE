package com.horem.parachute.balloon.Bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/6/28.
 */
public class FlowersToMeBean implements Serializable {
    private  String message;
    private int statusCode;
    private FlowersToMeListResultBean result;

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

    public FlowersToMeListResultBean getResult() {
        return result;
    }

    public void setResult(FlowersToMeListResultBean result) {
        this.result = result;
    }
}
