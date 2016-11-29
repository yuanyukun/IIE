package com.horem.parachute.mine.bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/5/27.
 */
public class AppUserInfoBean implements Serializable {

    private long memberId;          //用户Id
    private String nickName;        //用户昵称
    private String headImg;         //用户头像
    private String lastLoginTime;   //  最后一次登录时间
    private int throwNum;           //  投伞次数
    private String throwFee;        //  投伞总花费
    private int usingNum;           //  采用数目
    private String usingFee;        //  支出
    private String usingPercentage; //  百分比
    private int receiveNum;         //  接收数量
    private String receiveFee;      //  收入
    private boolean isEnterprise;   //  是否企业账号
    private String adoptedRate;     //  被采用率
    private int authType;           //用户类型
    private String authName;
    private int balloonNum;         //气球
    private int beFollowNum;        //粉丝
    private int followNum;          //关注
    private boolean isFollow;       //是否关注
    private int flowersNum;         //已收到鲜花
    private boolean isCanChat;      //是否可以聊天
    private String introduction;    //个人说明

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public String getAuthName() {
        return authName;
    }

    public int getAuthType() {
        return authType;
    }

    public int getBalloonNum() {
        return balloonNum;
    }

    public int getBeFollowNum() {
        return beFollowNum;
    }

    public int getFollowNum() {
        return followNum;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public int getFlowersNum() {
        return flowersNum;
    }

    public boolean isCanChat() {
        return isCanChat;
    }

    public String getIntroduction() {
        return introduction;
    }

    public String getReceiveFee() {
        return receiveFee;
    }

    public void setReceiveFee(String receiveFee) {
        this.receiveFee = receiveFee;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public int getThrowNum() {
        return throwNum;
    }

    public void setThrowNum(int throwNum) {
        this.throwNum = throwNum;
    }

    public String getThrowFee() {
        return throwFee;
    }

    public void setThrowFee(String throwFee) {
        this.throwFee = throwFee;
    }

    public int getUsingNum() {
        return usingNum;
    }

    public void setUsingNum(int usingNum) {
        this.usingNum = usingNum;
    }

    public String getUsingFee() {
        return usingFee;
    }

    public void setUsingFee(String usingFee) {
        this.usingFee = usingFee;
    }

    public String getUsingPercentage() {
        return usingPercentage;
    }

    public void setUsingPercentage(String usingPercentage) {
        this.usingPercentage = usingPercentage;
    }

    public int getReceiveNum() {
        return receiveNum;
    }

    public void setReceiveNum(int receiveNum) {
        this.receiveNum = receiveNum;
    }

    public boolean isEnterprise() {
        return isEnterprise;
    }

    public void setEnterprise(boolean enterprise) {
        isEnterprise = enterprise;
    }

    public String getAdoptedRate() {
        return adoptedRate;
    }

    public void setAdoptedRate(String adoptedRate) {
        this.adoptedRate = adoptedRate;
    }
}
