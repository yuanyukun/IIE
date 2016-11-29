package com.horem.parachute.login.bean;

import com.horem.parachute.balloon.Bean.BaseResultBean;

import java.io.Serializable;

/**
 * Created by user on 2016/4/7.
 */
public class UuidKey extends BaseResultBean{

    private String key;//加密key

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
