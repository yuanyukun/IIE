package com.horem.parachute.mine.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yuanyukun on 2016/6/15.
 */
public class HomeChatNewBean implements Serializable {
    private String message;
    private int statusCode;
    private ArrayList<HomeChatMessageBean> result;

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public ArrayList<HomeChatMessageBean>  getResult() {
        return result;
    }
}
