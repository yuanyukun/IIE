package com.horem.parachute.mine.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by user on 2016/4/18.
 */
public class MoneyDetailBean implements Serializable {

    private ArrayList<MoneyDetailSubBean> list;
    private int total;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public ArrayList<MoneyDetailSubBean> getList() {
        return list;
    }

    public void setList(ArrayList<MoneyDetailSubBean> list) {
        this.list = list;
    }
}
