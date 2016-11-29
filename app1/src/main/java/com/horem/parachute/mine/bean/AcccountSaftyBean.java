package com.horem.parachute.mine.bean;

import android.provider.ContactsContract;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yuanyukun on 2016/5/25.
 */
public class AcccountSaftyBean implements Serializable {

            private long memberId;
            private String nickName;
            private String email;
            private String phone;
            private String headImg;
            private String certificationName;
            private String certificationTime;
            private boolean isBindWeChat;
            private boolean isBindAlipay;
            private boolean isEnterprise;

    public int getMyGiveLikeNum() {
        return myGiveLikeNum;
    }

    private int myGiveLikeNum;

            private int beFollowNum;
            private int followNum;
            private int flowersNum;
            private int authType;
            private int sex;
            private int hometownCityId;
            private String hometownCity;
            private String introduction;
            private int professionId;
            private String profession;
            private int workUnitId;
            private String workUnit;
            private int SchoolId;
            private String school;
            private int siteCityId;
            private String siteCity;
//            private ArrayList<AddressListBean> myAddressList;
            private int myAddressCount;

    public int getMyAddressCount() {
        return myAddressCount;
    }

    public String getProfession() {
        return profession;
    }

    public int getBeFollowNum() {
        return beFollowNum;
    }

    public int getFollowNum() {
        return followNum;
    }

    public int getFlowersNum() {
        return flowersNum;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getCertificationName() {
        return certificationName;
    }

    public void setCertificationName(String certificationName) {
        this.certificationName = certificationName;
    }

    public String getCertificationTime() {
        return certificationTime;
    }

    public void setCertificationTime(String certificationTime) {
        this.certificationTime = certificationTime;
    }

    public boolean isBindWeChat() {
        return isBindWeChat;
    }

    public void setBindWeChat(boolean bindWeChat) {
        isBindWeChat = bindWeChat;
    }

    public boolean isBindAlipay() {
        return isBindAlipay;
    }

    public void setBindAlipay(boolean bindAlipay) {
        isBindAlipay = bindAlipay;
    }

    public boolean isEnterprise() {
        return isEnterprise;
    }

    public void setEnterprise(boolean enterprise) {
        isEnterprise = enterprise;
    }

    public int getAuthType() {
        return authType;
    }

    public int getSex() {
        return sex;
    }

    public int getHometownCityId() {
        return hometownCityId;
    }

    public String getHometownCity() {
        return hometownCity;
    }

    public String getIntroduction() {
        return introduction;
    }

    public int getProfessionId() {
        return professionId;
    }

    public int getWorkUnitId() {
        return workUnitId;
    }

    public String getWorkUnit() {
        return workUnit;
    }

    public int getSchoolId() {
        return SchoolId;
    }

    public String getSchool() {
        return school;
    }

    public int getSiteCityId() {
        return siteCityId;
    }

    public String getSiteCity() {
        return siteCity;
    }
}
