package com.horem.parachute.balloon.Bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yuanyukun on 2016/6/27.
 */
public class SendFlowerResultBean implements Serializable {
    private int price;
    private boolean isMoreFlowersList;
    private ArrayList<FlowersListBean> flowersList;

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isMoreFlowersList() {
        return isMoreFlowersList;
    }

    public void setMoreFlowersList(boolean moreFlowersList) {
        isMoreFlowersList = moreFlowersList;
    }

    public ArrayList<FlowersListBean> getFlowersList() {
        return flowersList;
    }

    public void setFlowersList(ArrayList<FlowersListBean> flowersList) {
        this.flowersList = flowersList;
    }
}
