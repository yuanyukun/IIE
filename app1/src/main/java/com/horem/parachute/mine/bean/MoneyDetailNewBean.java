package com.horem.parachute.mine.bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/6/13.
 */
public class MoneyDetailNewBean implements Serializable {
    private String message;
    private int statusCode;
    private MoneyDetailBean result;

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public MoneyDetailBean getResult() {
        return result;
    }
}
