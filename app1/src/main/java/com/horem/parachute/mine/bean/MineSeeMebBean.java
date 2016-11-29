package com.horem.parachute.mine.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yuanyukun on 2016/5/27.
 */
public class MineSeeMebBean implements Serializable {
    private ArrayList<MineSeeMeSubBean> list;
    private int totalCount;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public ArrayList<MineSeeMeSubBean> getList() {
        return list;
    }

    public void setList(ArrayList<MineSeeMeSubBean> list) {
        this.list = list;
    }
}
