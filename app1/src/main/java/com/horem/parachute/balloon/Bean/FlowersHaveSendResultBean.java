package com.horem.parachute.balloon.Bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yuanyukun on 2016/6/30.
 */
public class FlowersHaveSendResultBean implements Serializable {
    private ArrayList<FlowersHaveSendSubItem> list;
    private int total;

    public ArrayList<FlowersHaveSendSubItem> getList() {
        return list;
    }

    public int getTotal() {
        return total;
    }
}
