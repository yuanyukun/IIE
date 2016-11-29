package com.horem.parachute.mine.bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/6/13.
 */
public class MinePhotoNewBean implements Serializable {
    private String message;
    private int statusCode;
    private MinePhotoBean result;

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public MinePhotoBean getResult() {
        return result;
    }
}
