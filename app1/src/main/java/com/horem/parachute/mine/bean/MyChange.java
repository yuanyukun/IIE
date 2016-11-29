package com.horem.parachute.mine.bean;

import java.io.Serializable;

/**
 * Created by user on 2016/4/15.
 */
public class MyChange implements Serializable {
    private int statusCode;
    private String message;
    private String result;

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String getResult() {
        return result;
    }
}
