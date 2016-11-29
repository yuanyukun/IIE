package com.horem.parachute.login.bean;

import com.horem.parachute.task.bean.BalloonBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yuanyukun on 2016/6/14.
 */
public class MapTaskNewBean implements Serializable {
    private int statusCode;
    private String message;
    private MapTaskNewResultBean result;


    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public MapTaskNewResultBean getResult() {
        return result;
    }
}
