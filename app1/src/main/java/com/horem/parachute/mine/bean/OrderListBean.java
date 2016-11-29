package com.horem.parachute.mine.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by user on 2016/4/26.
 */
public class OrderListBean implements Serializable {
    private int authType;
    private boolean isFollow;

    public int getAuthType() {
        return authType;
    }

    public void setAuthType(int authType) {
        this.authType = authType;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    private String id;
    private String no;
    private String subTaskId;
    private String subTaskName;private double longitude;
    private double latitude;
    private String place;
    private int    receiveNum;
    private float    remuneration;
    private String p_place;
    private String otherRequirements;
    private int    orderState;
    private String orderStateName;
    private long   receivePersonId;
    private String receivePersonName;
    private String receivePersonHead;
    private String createTime;
    private String receiveTime;
    private String updateTime;
    private ArrayList<PhotoListBean> photoList;
    private String deliveryTime;
    private boolean isDelivery;
    private double  p_distance;
    private String  picDepict;
    private int    mediaType;
    private int    currencyId;
    private String symbol;

    public boolean isEnterprise() {
        return isEnterprise;
    }

    public void setEnterprise(boolean enterprise) {
        isEnterprise = enterprise;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(String subTaskId) {
        this.subTaskId = subTaskId;
    }

    public String getSubTaskName() {
        return subTaskName;
    }

    public void setSubTaskName(String subTaskName) {
        this.subTaskName = subTaskName;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public int getReceiveNum() {
        return receiveNum;
    }

    public void setReceiveNum(int receiveNum) {
        this.receiveNum = receiveNum;
    }

    public float getRemuneration() {
        return remuneration;
    }

    public void setRemuneration(int remuneration) {
        this.remuneration = remuneration;
    }

    public String getP_place() {
        return p_place;
    }

    public void setP_place(String p_place) {
        this.p_place = p_place;
    }

    public String getOtherRequirements() {
        return otherRequirements;
    }

    public void setOtherRequirements(String otherRequirements) {
        this.otherRequirements = otherRequirements;
    }

    public int getOrderState() {
        return orderState;
    }

    public void setOrderState(int orderState) {
        this.orderState = orderState;
    }

    public String getOrderStateName() {
        return orderStateName;
    }

    public void setOrderStateName(String orderStateName) {
        this.orderStateName = orderStateName;
    }

    public long getReceivePersonId() {
        return receivePersonId;
    }

    public void setReceivePersonId(long receivePersonId) {
        this.receivePersonId = receivePersonId;
    }

    public String getReceivePersonName() {
        return receivePersonName;
    }

    public void setReceivePersonName(String receivePersonName) {
        this.receivePersonName = receivePersonName;
    }

    public String getReceivePersonHead() {
        return receivePersonHead;
    }

    public void setReceivePersonHead(String receivePersonHead) {
        this.receivePersonHead = receivePersonHead;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public ArrayList<PhotoListBean> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(ArrayList<PhotoListBean> photoList) {
        this.photoList = photoList;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public boolean isDelivery() {
        return isDelivery;
    }

    public void setDelivery(boolean delivery) {
        isDelivery = delivery;
    }

    public double getP_distance() {
        return p_distance;
    }

    public void setP_distance(double p_distance) {
        this.p_distance = p_distance;
    }

    public String getPicDepict() {
        return picDepict;
    }

    public void setPicDepict(String picDepict) {
        this.picDepict = picDepict;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    private boolean isEnterprise;
}
