package com.horem.parachute.login.bean;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by user on 2016/4/12.
 */
public class SubTaskBean implements Serializable{
            private String subTaskId;       //子任务Id
            private String SsubTaskName;    //子任务名称
            private String no;              //序列号
            private double longitude;       //经度
            private double latitude;        //纬度
            private String place;           //任务地点
            private int    useOrderNum;     //用户发布成功的任务数量
            private int     allOrderNum;

            private int deliveryOrderNum;
            private int remuneration;       //订单价格
            private int receiveNum;         //采用数量
            private String payOverTime;
            private String receiveOverTime;//订单接收过期时间
            private String confirmOverTime;//确认超时日期
            private String timeOutStr;     //订单超期时间

            private String confirmTimeOutStr;//确认订单超时时间
            private String otherRequirements;//订单描述
            private int taskState;           //订单状态
            private String taskStateName;    //订单状态简称
            private long createPersonId;      //用户Id(0代表所有用户）
            private String createPersonName; //用户昵称
            private String createPersonHead; //头像图片（下个接口获取）
            private int credit;              //信用卡数量
            private String createTime;       //订单创建时间
            private String completeTime;     //订单完成时间
            private double distance;         //任务距离
            private float total_fee;         //总的订单费用
            private int sortNo;              //排序
            private boolean isReceive;
            private boolean isPrivate;
            private int mapScope;           //地图范围
            private int mediaType;          //媒体类型（10照片，30视频）
            private int currencyId;         //货币种类
            private String symbol;          //货币字符
            private String closeReason;     //订单关闭原因
            private boolean isEnterprise;   //是否为企业用户
            private ArrayList<File> attList;//文件，视频或者图片
//            private int authType;
            private boolean isFollow;

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public String getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(String subTaskId) {
        this.subTaskId = subTaskId;
    }

    public String getSsubTaskName() {
        return SsubTaskName;
    }

    public void setSsubTaskName(String ssubTaskName) {
        SsubTaskName = ssubTaskName;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
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

    public int getUseOrderNum() {
        return useOrderNum;
    }

    public void setUseOrderNum(int useOrderNum) {
        this.useOrderNum = useOrderNum;
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

    public int getRemuneration() {
        return remuneration;
    }

    public void setRemuneration(int remuneration) {
        this.remuneration = remuneration;
    }

    public int getReceiveNum() {
        return receiveNum;
    }

    public void setReceiveNum(int receiveNum) {
        this.receiveNum = receiveNum;
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

    public String getConfirmTimeOutStr() {
        return confirmTimeOutStr;
    }

    public void setConfirmTimeOutStr(String confirmTimeOutStr) {
        this.confirmTimeOutStr = confirmTimeOutStr;
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

    public String getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(String completeTime) {
        this.completeTime = completeTime;
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

    public int getSortNo() {
        return sortNo;
    }

    public void setSortNo(int sortNo) {
        this.sortNo = sortNo;
    }

    public boolean isReceive() {
        return isReceive;
    }

    public void setReceive(boolean receive) {
        isReceive = receive;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public int getMapScope() {
        return mapScope;
    }

    public void setMapScope(int mapScope) {
        this.mapScope = mapScope;
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

    public ArrayList<File> getAttList() {
        return attList;
    }

    public void setAttList(ArrayList<File> attList) {
        this.attList = attList;
    }
}
