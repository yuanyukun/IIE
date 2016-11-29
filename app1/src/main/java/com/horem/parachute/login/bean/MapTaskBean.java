package com.horem.parachute.login.bean;

import java.io.Serializable;

/**
 * Created by user on 2016/4/12.
 */
public class MapTaskBean implements Serializable{
    private float scope;
    private SubTaskBean[] subTaskList;

    public float getScope() {
        return scope;
    }

    public void setScope(float scope) {
        this.scope = scope;
    }

    public SubTaskBean[] getSubTaskList() {
        return subTaskList;
    }

    public void setSubTaskList(SubTaskBean[] subTaskList) {
        this.subTaskList = subTaskList;
    }
}
