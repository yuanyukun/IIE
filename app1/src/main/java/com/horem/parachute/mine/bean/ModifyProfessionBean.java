package com.horem.parachute.mine.bean;

import com.horem.parachute.balloon.Bean.BaseResultBean;
import com.horem.parachute.mine.bean.ProfessionBean;

import java.util.ArrayList;

/**
 * Created by yuanyukun on 2016/7/12.
 */
public class ModifyProfessionBean extends BaseResultBean {
    private ArrayList<ProfessionBean> result;

    public ArrayList<ProfessionBean> getResult() {
        return result;
    }

    public void setResult(ArrayList<ProfessionBean> result) {
        this.result = result;
    }
}
