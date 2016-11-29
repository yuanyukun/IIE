package com.horem.parachute.mine.bean;

import java.io.Serializable;

/**
 * Created by user on 2016/4/18.
 */
public class MoneyDetailSubBean implements Serializable{

    private String id;              //任务Id
    private int s_payWay;           //支付支出
    private String payWayName;      //名字
    private int s_paymentType;      //支付方式
    private String depict;          //描述
    private double s_totalFee;      //费用总和
    private String createTime;      //任务创建时间
    private String symbol;          //金额符号
    private String totalFeeStr;     //总的金额
    private String sign;            //签名

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getS_payWay() {
        return s_payWay;
    }

    public void setS_payWay(int s_payWay) {
        this.s_payWay = s_payWay;
    }

    public String getPayWayName() {
        return payWayName;
    }

    public void setPayWayName(String payWayName) {
        this.payWayName = payWayName;
    }

    public int getS_paymentType() {
        return s_paymentType;
    }

    public void setS_paymentType(int s_paymentType) {
        this.s_paymentType = s_paymentType;
    }

    public String getDepict() {
        return depict;
    }

    public void setDepict(String depict) {
        this.depict = depict;
    }

    public double getS_totalFee() {
        return s_totalFee;
    }

    public void setS_totalFee(double s_totalFee) {
        this.s_totalFee = s_totalFee;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getTotalFeeStr() {
        return totalFeeStr;
    }

    public void setTotalFeeStr(String totalFeeStr) {
        this.totalFeeStr = totalFeeStr;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
