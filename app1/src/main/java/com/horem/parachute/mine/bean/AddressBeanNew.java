package com.horem.parachute.mine.bean;

import com.horem.parachute.balloon.Bean.BaseResultBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanyukun on 2016/6/15.
 */
public class AddressBeanNew extends BaseResultBean  {
    private ArrayList<AddressSubBean> result;

    public ArrayList<AddressSubBean> getResult() {
        return result;
    }
}
