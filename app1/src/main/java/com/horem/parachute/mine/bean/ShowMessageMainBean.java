package com.horem.parachute.mine.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yuanyukun on 2016/6/7.
 */
public class ShowMessageMainBean implements Serializable {

    private List<ShowMessageSubBean> list;
    private int totalCount;

    public List<ShowMessageSubBean> getList() {
        return list;
    }

    public int getTotalCount() {
        return totalCount;
    }
}
