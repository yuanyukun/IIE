package com.horem.parachute.mine.bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/6/15.
 */
public class ChatMessageListNewBean implements Serializable{
    private String message;
    private int statusCode;
    private ChatMessageListBean result;

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public ChatMessageListBean getResult() {
        return result;
    }
}
