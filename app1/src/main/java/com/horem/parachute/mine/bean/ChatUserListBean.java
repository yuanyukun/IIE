package com.horem.parachute.mine.bean;

import com.horem.parachute.balloon.Bean.BaseResultBean;

import java.util.ArrayList;

/**
 * Created by yuanyukun on 2016/7/1.
 */
public class ChatUserListBean extends BaseResultBean {
    private ArrayList<ChatMessagePersonBean> result;

    public ArrayList<ChatMessagePersonBean> getResult() {
        return result;
    }

    public void setResult(ArrayList<ChatMessagePersonBean> result) {
        this.result = result;
    }
}
