package com.horem.parachute.mine.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by user on 2016/4/15.
 */
public class MineSendTaskBean implements Serializable{

    private ArrayList<taskSendSubBean> list;
    private int totalCount;

    public ArrayList<taskSendSubBean> getList() {
        return list;
    }

    public void setList(ArrayList<taskSendSubBean> list) {
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
