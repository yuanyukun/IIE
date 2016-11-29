package com.horem.parachute.mine.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by user on 2016/4/27.
 */
public class OrderConfirmBean implements Serializable {
    private ArrayList<MineSendTaskSubBeanImgList> imgList;
    private String message;
    private int statusCode;

    public ArrayList<MineSendTaskSubBeanImgList> getImgList() {
        return imgList;
    }

    public void setImgList(ArrayList<MineSendTaskSubBeanImgList> imgList) {
        this.imgList = imgList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
