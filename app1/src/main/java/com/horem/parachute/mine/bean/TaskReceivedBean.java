package com.horem.parachute.mine.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yuanyukun on 2016/5/30.
 */
public class TaskReceivedBean implements Serializable {

    private ArrayList<TaskReceivedSubBean> list;
    private int totalCount;

    public ArrayList<TaskReceivedSubBean> getList() {
        return list;
    }

    public void setList(ArrayList<TaskReceivedSubBean> list) {
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
