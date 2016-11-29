package com.horem.parachute.mine.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yuanyukun on 2016/5/18.
 */
public class ChatMessageListBean implements Serializable {
    private List<ChatMessageBean> list;

    public List<ChatMessageBean> getList() {
        return list;
    }

    public void setList(List<ChatMessageBean> list) {
        this.list = list;
    }
}
