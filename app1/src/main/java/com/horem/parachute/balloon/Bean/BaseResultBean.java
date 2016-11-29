package com.horem.parachute.balloon.Bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/6/30.
 */
public class BaseResultBean implements Serializable {
    private String message;
    private int statusCode;

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
