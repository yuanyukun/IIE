package com.horem.parachute.balloon.Bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/6/28.
 */
public class SendFlowersGetPayIdBean implements Serializable {
    private String message;
    private int statusCode;
    private String result;

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResult() {
        return result;
    }
}
