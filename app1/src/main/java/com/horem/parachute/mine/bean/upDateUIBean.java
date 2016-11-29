package com.horem.parachute.mine.bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/5/17.
 */
public class upDateUIBean implements Serializable{
    private boolean mySubTaskList;      //子任务列表，主页面
    private boolean myTaskOrderList;    //订单列表，我投的伞
    private boolean viewUserList;       //用户
    private boolean messageList;        //我的消息，聊聊
    private boolean flowersToMeList;    //送花给我的
    private boolean beFollowList;        //粉丝

    public boolean isGiveLikeMeList() {
        return giveLikeMeList;
    }

    public void setGiveLikeMeList(boolean giveLikeMeList) {
        this.giveLikeMeList = giveLikeMeList;
    }

    private boolean giveLikeMeList;        //粉丝

    public boolean isFlowersToMeList() {
        return flowersToMeList;
    }

    public void setFlowersToMeList(boolean flowersToMeList) {
        this.flowersToMeList = flowersToMeList;
    }

    public boolean isBeFollowList() {
        return beFollowList;
    }

    public void setBeFollowList(boolean beFollowList) {
        this.beFollowList = beFollowList;
    }

    public boolean isMySubTaskList() {
        return mySubTaskList;
    }

    public void setMySubTaskList(boolean mySubTaskList) {
        this.mySubTaskList = mySubTaskList;
    }

    public boolean isMyTaskOrderList() {
        return myTaskOrderList;
    }

    public void setMyTaskOrderList(boolean myTaskOrderList) {
        this.myTaskOrderList = myTaskOrderList;
    }

    public boolean isViewUserList() {
        return viewUserList;
    }

    public void setViewUserList(boolean viewUserList) {
        this.viewUserList = viewUserList;
    }

    public boolean isMessageList() {
        return messageList;
    }

    public void setMessageList(boolean messageList) {
        this.messageList = messageList;
    }
}
