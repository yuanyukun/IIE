package com.horem.parachute.mine.bean;

import com.horem.parachute.balloon.Bean.BaseResultBean;

import java.util.ArrayList;

/**
 * Created by yuanyukun on 2016/8/10.
 */
public class ConfirmNewResultBean extends BaseResultBean{
    private ArrayList<ConfirmNewResultSubBean> result;

    public ArrayList<ConfirmNewResultSubBean> getResult() {
        return result;
    }

    public void setResult(ArrayList<ConfirmNewResultSubBean> result) {
        this.result = result;
    }
}
