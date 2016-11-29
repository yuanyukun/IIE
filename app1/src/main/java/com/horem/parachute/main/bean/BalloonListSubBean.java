package com.horem.parachute.main.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yuanyukun on 2016/6/21.
 */
public class BalloonListSubBean implements Serializable {
    private int total;
    private ArrayList<BalloonListSubBeanItem> list;

    public int getTotal() {
        return total;
    }

    public ArrayList<BalloonListSubBeanItem> getList() {
        return list;
    }
}
