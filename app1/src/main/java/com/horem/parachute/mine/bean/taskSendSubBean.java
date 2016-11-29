package com.horem.parachute.mine.bean;

import com.horem.parachute.main.bean.attListBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yuanyukun on 2016/7/12.
 */
public class taskSendSubBean implements Serializable{
    private String  id;
    private String  no;             //编号3
    private String  subTaskId;      //子任务Id1
    private String  subTaskName;    //2
    private double  longitude;      //子任务经度4
    private double  latitude;       //子任务纬度5
    private String  place;          //子任务地点6
    private double  distance;        //距离21

    private String p_place;         //接伞的地址
    private double p_distance;      //接伞地址与任务之间的距离
    private double p_longitude;
    private double p_latitude;
    private double remuneration;
    private String  deliveryTime;     //接伞时间
    private String  otherRequirements;//其他要求13
    private long    createPersonId;  //用户Id17
    private String  createPersonName;
    private String  createPersonHead;
    private String  createTime;      //创建时间18

    private String receiveTime;
    private String  updateTime;      //更新时间19
    private ArrayList<MineSendTaskSubBeanImgList> imgList; //图片列表27
    private String  timeOutStr;     //超时剩余时间9
    private String  payOverTime;    //付款过期时间10
    private String  receiveOverTime;//过期时间11
    private String  confirmOverTime;//过期时间12

    private boolean isDelivery;      //是否接受28
    private boolean isPrivate;      //是否私有29
    private String picDepict;       //自我推荐描述
    private int     mediaType;      //媒体类型30
    private int     currencyId;       //31
    private String  symbol;           //32
    private String  closeReason;    //33
    private boolean isEnterprise;   //是否企业账号34
    private int taskState;
    private double total_fee;
    private double change_fee;
    private String taskStateName;
    private String completeTime;

    private int allOrderNum;
    private int deliveryOrderNum;
    private int completeOrderNum;
    private boolean isReceive;
    private int authType;
    private ArrayList<attListBean> attList;
    private int taskType;
    private String balloonId;
    private boolean isFollow;
    private balloonDetailBean balloonDetail;

    public balloonDetailBean getBalloonDetail() {
        return balloonDetail;
    }

    public void setBalloonDetail(balloonDetailBean balloonDetail) {
        this.balloonDetail = balloonDetail;
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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getP_place() {
        return p_place;
    }

    public void setP_place(String p_place) {
        this.p_place = p_place;
    }

    public double getP_distance() {
        return p_distance;
    }

    public void setP_distance(double p_distance) {
        this.p_distance = p_distance;
    }

    public double getP_longitude() {
        return p_longitude;
    }

    public void setP_longitude(double p_longitude) {
        this.p_longitude = p_longitude;
    }

    public double getP_latitude() {
        return p_latitude;
    }

    public void setP_latitude(double p_latitude) {
        this.p_latitude = p_latitude;
    }

    public double getRemuneration() {
        return remuneration;
    }

    public void setRemuneration(double remuneration) {
        this.remuneration = remuneration;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getOtherRequirements() {
        return otherRequirements;
    }

    public void setOtherRequirements(String otherRequirements) {
        this.otherRequirements = otherRequirements;
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

    public ArrayList<MineSendTaskSubBeanImgList> getImgList() {
        return imgList;
    }

    public void setImgList(ArrayList<MineSendTaskSubBeanImgList> imgList) {
        this.imgList = imgList;
    }

    public String getTimeOutStr() {
        return timeOutStr;
    }

    public void setTimeOutStr(String timeOutStr) {
        this.timeOutStr = timeOutStr;
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

    public boolean isDelivery() {
        return isDelivery;
    }

    public void setDelivery(boolean delivery) {
        isDelivery = delivery;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
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

    public int getTaskState() {
        return taskState;
    }

    public void setTaskState(int taskState) {
        this.taskState = taskState;
    }

    public double getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(double total_fee) {
        this.total_fee = total_fee;
    }

    public double getChange_fee() {
        return change_fee;
    }

    public void setChange_fee(double change_fee) {
        this.change_fee = change_fee;
    }

    public String getTaskStateName() {
        return taskStateName;
    }

    public void setTaskStateName(String taskStateName) {
        this.taskStateName = taskStateName;
    }

    public String getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(String completeTime) {
        this.completeTime = completeTime;
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

    public boolean isReceive() {
        return isReceive;
    }

    public void setReceive(boolean receive) {
        isReceive = receive;
    }

    public int getAuthType() {
        return authType;
    }

    public void setAuthType(int authType) {
        this.authType = authType;
    }

    public ArrayList<attListBean> getAttList() {
        return attList;
    }

    public void setAttList(ArrayList<attListBean> attList) {
        this.attList = attList;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public String getBalloonId() {
        return balloonId;
    }

    public void setBalloonId(String balloonId) {
        this.balloonId = balloonId;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }
}
