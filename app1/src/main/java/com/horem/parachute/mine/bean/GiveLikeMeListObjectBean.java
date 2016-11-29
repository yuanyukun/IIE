package com.horem.parachute.mine.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yuanyukun on 2016/8/8.
 */
public class GiveLikeMeListObjectBean implements Serializable{
    private int total;
    private ArrayList<GiveLikeMeListItemBean> list;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public ArrayList<GiveLikeMeListItemBean> getList() {
        return list;
    }

    public void setList(ArrayList<GiveLikeMeListItemBean> list) {
        this.list = list;
    }
}
