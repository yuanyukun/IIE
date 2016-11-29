package com.horem.parachute.mine.bean;

import java.io.Serializable;

/**
 * Created by user on 2016/5/13.
 */
public class HomeChatMessageBean implements Serializable {

            private int personalType;       //用户类型
            private String messageContent;  //最新消息
            private long personalId;        //用户Id
            private String personalName;    //用户姓名
            private String personalHead;    //用户头像
            private String createTime;      //最后一次对话时间
            private boolean hasNewMessage;  //是否有新消息
            private boolean canDelete;      //是否可以删除

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getPersonalType() {
        return personalType;
    }

    public void setPersonalType(int personalType) {
        this.personalType = personalType;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public long getPersonalId() {
        return personalId;
    }

    public void setPersonalId(long personalId) {
        this.personalId = personalId;
    }

    public String getPersonalName() {
        return personalName;
    }

    public void setPersonalName(String personalName) {
        this.personalName = personalName;
    }

    public String getPersonalHead() {
        return personalHead;
    }

    public void setPersonalHead(String personalHead) {
        this.personalHead = personalHead;
    }

    public boolean isHasNewMessage() {
        return hasNewMessage;
    }

    public void setHasNewMessage(boolean hasNewMessage) {
        this.hasNewMessage = hasNewMessage;
    }

    public boolean isCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }
}
