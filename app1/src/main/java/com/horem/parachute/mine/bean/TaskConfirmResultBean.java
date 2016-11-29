package com.horem.parachute.mine.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yuanyukun on 2016/6/15.
 */
public class TaskConfirmResultBean implements Serializable {
    private SubTaskInfoBean subTaskInfo;
    private ArrayList<OrderListBean> orderList;

    public SubTaskInfoBean getSubTaskInfo() {
        return subTaskInfo;
    }
    public ArrayList<OrderListBean> getOrderList() {
        return orderList;
    }
}
