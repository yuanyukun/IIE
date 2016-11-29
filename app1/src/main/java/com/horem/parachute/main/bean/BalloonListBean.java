package com.horem.parachute.main.bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/6/21.
 */
public class BalloonListBean implements Serializable {
    private String message;
    private int statusCode;
    private BalloonListSubBean result;

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public BalloonListSubBean getResult() {
        return result;
    }
}
