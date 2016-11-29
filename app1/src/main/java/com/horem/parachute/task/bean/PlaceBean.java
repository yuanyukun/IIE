package com.horem.parachute.task.bean;

import java.io.Serializable;

/**
 * Created by user on 2016/4/8.
 */
public class PlaceBean implements Serializable{

    private long addressId;  //地址Id
    private String place;    //投伞地点
    private double longitude;//经度
    private double latitude; //纬度
    private double radius;   //半径

    public PlaceBean() {
    }

    public PlaceBean(long addressId, String place, double longitude, double latitude, double radius) {
        this.addressId = addressId;
        this.place = place;
        this.longitude = longitude;
        this.latitude = latitude;
        this.radius = radius;
    }

    public long getAddressId() {
        return addressId;
    }

    public void setAddressId(long addressId) {
        this.addressId = addressId;
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

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
