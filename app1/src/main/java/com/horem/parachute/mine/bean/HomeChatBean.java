package com.horem.parachute.mine.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by user on 2016/5/13.
 */
public class HomeChatBean implements Serializable {
    private ArrayList<HomeChatMessageBean> list;

    public ArrayList<HomeChatMessageBean> getList() {
        return list;
    }

    public void setList(ArrayList<HomeChatMessageBean> list) {
        this.list = list;
    }
}
