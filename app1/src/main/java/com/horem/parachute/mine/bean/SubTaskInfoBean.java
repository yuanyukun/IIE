package com.horem.parachute.mine.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by user on 2016/4/26.
 */
public class SubTaskInfoBean implements Serializable {
    private String subTaskId;
    private String subTaskName;
    private String no;
    private String place;
    private double longitude;
    private double latitude;
    private int    allOrderNum;
    private int    deliveryOrderNum;
    private int    completeOrderNum;
    private float   remuneration;
    private String payOverTime;
    private String receiveOverTime;     //接收时间是否超时
    private String confirmOverTime;
    private String timeOutStr;
    private String otherRequirements;
    private int    taskState;
    private String taskStateName;
    private long   createPersonId;
    private String createPersonName;
    private String createPersonHead;
    private int    credit;
    private String createTime;
    private int authType;

    public int getAuthType() {
        return authType;
    }

    public void setAuthType(int authType) {
        this.authType = authType;
    }

    public ArrayList<PhotoListBean> getAttList() {
        return attList;
    }

    public void setAttList(ArrayList<PhotoListBean> attList) {
        this.attList = attList;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    private double distance;
    private float  total_fee;
    private boolean isReceiveTimeOver;
    private int     receiveNum;
    private int     mediaType;
    private int  currencyId;
    private String symbol;
    private String closeReason;
    private boolean isEnterprise;
    private ArrayList<PhotoListBean> attList;
    private boolean isPrivate;
    private boolean isFollow;
    private boolean timeOut;

    public boolean isTimeOut() {
        return timeOut;
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

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
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

    public int getAllOrderNum() {
        return allOrderNum;
    }

    public void setAllOrderNum(int allOrderNum) {
        this.allOrderNum = allOrderNum;
    }

    public int getDeliveryOrderNum() {
        return deliveryOrderNum;
    }

    public void setDeliveryOrderNum(int deliveryOrderNum) {
        this.deliveryOrderNum = deliveryOrderNum;
    }

    public int getCompleteOrderNum() {
        return completeOrderNum;
    }

    public void setCompleteOrderNum(int completeOrderNum) {
        this.completeOrderNum = completeOrderNum;
    }

    public float getRemuneration() {
        return remuneration;
    }

    public void setRemuneration(int remuneration) {
        this.remuneration = remuneration;
    }

    public String getPayOverTime() {
        return payOverTime;
    }

    public void setPayOverTime(String payOverTime) {
        this.payOverTime = payOverTime;
    }

    public String getReceiveOverTime() {
        return receiveOverTime;
    }

    public void setReceiveOverTime(String receiveOverTime) {
        this.receiveOverTime = receiveOverTime;
    }

    public String getConfirmOverTime() {
        return confirmOverTime;
    }

    public void setConfirmOverTime(String confirmOverTime) {
        this.confirmOverTime = confirmOverTime;
    }

    public String getTimeOutStr() {
        return timeOutStr;
    }

    public void setTimeOutStr(String timeOutStr) {
        this.timeOutStr = timeOutStr;
    }

    public String getOtherRequirements() {
        return otherRequirements;
    }

    public void setOtherRequirements(String otherRequirements) {
        this.otherRequirements = otherRequirements;
    }

    public int getTaskState() {
        return taskState;
    }

    public void setTaskState(int taskState) {
        this.taskState = taskState;
    }

    public String getTaskStateName() {
        return taskStateName;
    }

    public void setTaskStateName(String taskStateName) {
        this.taskStateName = taskStateName;
    }

    public long getCreatePersonId() {
        return createPersonId;
    }

    public void setCreatePersonId(long createPersonId) {
        this.createPersonId = createPersonId;
    }

    public String getCreatePersonName() {
        return createPersonName;
    }

    public void setCreatePersonName(String createPersonName) {
        this.createPersonName = createPersonName;
    }

    public String getCreatePersonHead() {
        return createPersonHead;
    }

    public void setCreatePersonHead(String createPersonHead) {
        this.createPersonHead = createPersonHead;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public float getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(float total_fee) {
        this.total_fee = total_fee;
    }

    public boolean isReceiveTimeOver() {
        return isReceiveTimeOver;
    }

    public void setReceiveTimeOver(boolean receiveTimeOver) {
        isReceiveTimeOver = receiveTimeOver;
    }

    public int getReceiveNum() {
        return receiveNum;
    }

    public void setReceiveNum(int receiveNum) {
        this.receiveNum = receiveNum;
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

    public String getCloseReason() {
        return closeReason;
    }

    public void setCloseReason(String closeReason) {
        this.closeReason = closeReason;
    }

    public boolean isEnterprise() {
        return isEnterprise;
    }

    public void setEnterprise(boolean enterprise) {
        isEnterprise = enterprise;
    }


    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }


}
