package com.horem.parachute.balloon.Bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yuanyukun on 2016/6/28.
 */
public class FlowersToMeListResultBean implements Serializable {
    private ArrayList<FlowersToMeSubItem> list;
    private int total;

    public ArrayList<FlowersToMeSubItem> getList() {
        return list;
    }

    public void setList(ArrayList<FlowersToMeSubItem> list) {
        this.list = list;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
