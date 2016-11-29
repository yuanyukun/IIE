package com.horem.parachute.task.bean;

import java.io.Serializable;

/**
 * Created by user on 2016/4/19.
 */
public class UploadVideoBean implements Serializable{
    private String s_subTaskId;         //子任务Id
    private long s_createPersonId;      //用户Id
    private double m_longitude;         //经度
    private double m_latitude;          //纬度
    private String depict;              //描述
    private String sign;                //签名

    public String getS_subTaskId() {
        return s_subTaskId;
    }

    public void setS_subTaskId(String s_subTaskId) {
        this.s_subTaskId = s_subTaskId;
    }

    public long getS_createPersonId() {
        return s_createPersonId;
    }

    public void setS_createPersonId(long s_createPersonId) {
        this.s_createPersonId = s_createPersonId;
    }

    public double getM_longitude() {
        return m_longitude;
    }

    public void setM_longitude(double m_longitude) {
        this.m_longitude = m_longitude;
    }

    public double getM_latitude() {
        return m_latitude;
    }

    public void setM_latitude(double m_latitude) {
        this.m_latitude = m_latitude;
    }

    public String getDepict() {
        return depict;
    }

    public void setDepict(String depict) {
        this.depict = depict;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
