package com.horem.parachute.mine.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yuanyukun on 2016/6/7.
 */
public class ShowMessageSubBean implements Serializable {
    private String id;
    private String no;
    private String subTaskId;
    private String subTaskName;
    private double longitude;
    private double latitude;
    private String place;
    private int receiveNum;
    private double remuneration;
    private String p_place;
    private String otherRequirements;
    private int orderState;
    private String orderStateName;
    private long receivePersonId;
    private String receivePersonName;
    private String receivePersonHead;
    private String createTime;
    private String receiveTime;
    private String updateTime;
    private String deliveryTime;
    private List<PhotoListBean> photoList;
    private boolean isDelivery;
    private double p_distance;
    private String picDepict;
    private String symbol;
    private int mediaType;
    private int currencyId;
    private boolean isEnterprise;

    public String getId() {
        return id;
    }

    public String getNo() {
        return no;
    }

    public String getSubTaskId() {
        return subTaskId;
    }

    public String getSubTaskName() {
        return subTaskName;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getPlace() {
        return place;
    }

    public double getReceiveNum() {
        return receiveNum;
    }

    public double getRemuneration() {
        return remuneration;
    }

    public String getP_place() {
        return p_place;
    }

    public String getOtherRequirements() {
        return otherRequirements;
    }

    public int getOrderState() {
        return orderState;
    }

    public String getOrderStateName() {
        return orderStateName;
    }

    public long getReceivePersonId() {
        return receivePersonId;
    }

    public String getReceivePersonName() {
        return receivePersonName;
    }

    public String getReceivePersonHead() {
        return receivePersonHead;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getReceiveTime() {
        return receiveTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public List<PhotoListBean> getPhotoList() {
        return photoList;
    }

    public boolean isDelivery() {
        return isDelivery;
    }

    public double getP_distance() {
        return p_distance;
    }

    public String getPicDepict() {
        return picDepict;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getMediaType() {
        return mediaType;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public boolean isEnterprise() {
        return isEnterprise;
    }
}
