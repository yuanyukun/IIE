package com.horem.parachute.mine.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yuanyukun on 2016/5/29.
 */
public class BlackListBean implements Serializable {
    ArrayList<BlackListSubBean> list;
    private int totalCount;
    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public ArrayList<BlackListSubBean> getList() {
        return list;
    }

    public void setList(ArrayList<BlackListSubBean> list) {
        this.list = list;
    }


}
