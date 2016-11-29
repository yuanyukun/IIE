package com.horem.parachute.mine.bean;

import com.horem.parachute.main.bean.attListBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/4/15.
 */
public class MineSendTaskSubBean implements Serializable{
        public void setTaskState(int taskState) {
                this.taskState = taskState;
        }

        private int     allOrderNum;     //订单总数
        private  ArrayList<attListBean> attList;      //子任务Id
        private int  authType;    //
        private balloonDetailBean balloonDetail;
        private String balloonId;
        private float change_fee;
        private String closeReason;
        private int     completeOrderNum;//采纳订单数
        private String completeTime;
        private String confirmOverTime;
        private long createPersonId;
        private String  createTime;      //创建时间
        private int     credit;          //信用卡的张数
        private int     currencyId;       //34
        private int     deliveryOrderNum;//执行订单数
        private double  distance;        //距离
        private  ArrayList<PhotoListBean> imgList;
        private boolean isEnterprise;   //是否企业账号32
        private boolean isFollow;
        private boolean isPrivate;
        private boolean isReceive;
        private int     mediaType;      //媒体类型
        private double  longitude;      //子任务经度
        private double  latitude;       //子任务纬度
        private String  no;             //编号
        private String  otherRequirements;//其他要求
        private int     receiveNum;     //采用个数
        private String  payOverTime;    //付款过期时间
        private String  place;          //子任务地点
        private int     remuneration;   //报酬金额

        private int     taskState;       //子任务状态码
        private String  taskStateName;   //状态中文描述
        private String subTaskId;
        private String subTaskName;
        private String  symbol;           //30;
        private int taskType;
        private String timeOutStr;
        private float total_fee;
        private String updateTime;

        public void setFollow(boolean follow) {
                isFollow = follow;
        }

        public int getAllOrderNum() {
                return allOrderNum;
        }

        public int getAuthType() {
                return authType;
        }

        public balloonDetailBean getBalloonDetail() {
                return balloonDetail;
        }

        public String getBalloonId() {
                return balloonId;
        }

        public float getChange_fee() {
                return change_fee;
        }

        public String getCloseReason() {
                return closeReason;
        }

        public int getCompleteOrderNum() {
                return completeOrderNum;
        }

        public String getCompleteTime() {
                return completeTime;
        }

        public String getConfirmOverTime() {
                return confirmOverTime;
        }

        public long getCreatePersonId() {
                return createPersonId;
        }

        public String getCreateTime() {
                return createTime;
        }

        public int getCredit() {
                return credit;
        }

        public int getCurrencyId() {
                return currencyId;
        }

        public int getDeliveryOrderNum() {
                return deliveryOrderNum;
        }

        public double getDistance() {
                return distance;
        }

        public ArrayList<attListBean> getAttList() {
                return attList;
        }

        public ArrayList<PhotoListBean> getImgList() {
                return imgList;
        }

        public boolean isEnterprise() {
                return isEnterprise;
        }

        public boolean isFollow() {
                return isFollow;
        }

        public boolean isPrivate() {
                return isPrivate;
        }

        public boolean isReceive() {
                return isReceive;
        }

        public int getMediaType() {
                return mediaType;
        }

        public double getLongitude() {
                return longitude;
        }

        public double getLatitude() {
                return latitude;
        }

        public String getNo() {
                return no;
        }

        public String getOtherRequirements() {
                return otherRequirements;
        }

        public int getReceiveNum() {
                return receiveNum;
        }

        public String getPayOverTime() {
                return payOverTime;
        }

        public String getPlace() {
                return place;
        }

        public int getRemuneration() {
                return remuneration;
        }

        public int getTaskState() {
                return taskState;
        }

        public String getTaskStateName() {
                return taskStateName;
        }

        public String getSubTaskId() {
                return subTaskId;
        }

        public String getSubTaskName() {
                return subTaskName;
        }

        public String getSymbol() {
                return symbol;
        }

        public int getTaskType() {
                return taskType;
        }

        public String getTimeOutStr() {
                return timeOutStr;
        }

        public float getTotal_fee() {
                return total_fee;
        }

        public String getUpdateTime() {
                return updateTime;
        }
}
