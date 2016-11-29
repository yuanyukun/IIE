package com.horem.parachute.login.bean;

import java.io.Serializable;

/**
 * Created by user on 2016/4/8.
 */
public class User implements Serializable {
    private long uid;
    private String name;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
