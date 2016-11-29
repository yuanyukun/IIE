package com.horem.parachute.mine.bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/6/14.
 */
public class MineSeeMeBeanNew implements Serializable {
    private String message;
    private int statusCode;
    private MineSeeMebBean result;

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public MineSeeMebBean getResult() {
        return result;
    }
}
