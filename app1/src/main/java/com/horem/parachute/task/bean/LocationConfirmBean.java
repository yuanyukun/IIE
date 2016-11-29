package com.horem.parachute.task.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/5/20.
 */
public class LocationConfirmBean implements Serializable {
    private String locationDescribe;
    private String taskFee;
    private String taskOrderFee;

    public String getLocationDescribe() {
        return locationDescribe;
    }

    public void setLocationDescribe(String locationDescribe) {
        this.locationDescribe = locationDescribe;
    }

    public String getTaskFee() {
        return taskFee;
    }

    public void setTaskFee(String taskFee) {
        this.taskFee = taskFee;
    }

    public String getTaskOrderFee() {
        return taskOrderFee;
    }

    public void setTaskOrderFee(String taskOrderFee) {
        this.taskOrderFee = taskOrderFee;
    }
}
