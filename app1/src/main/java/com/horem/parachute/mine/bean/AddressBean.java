package com.horem.parachute.mine.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yuanyukun on 2016/6/3.
 */
public class AddressBean implements Serializable {
    private List<AddressSubBean> list;

    public List<AddressSubBean> getList() {
        return list;
    }

    public void setList(List<AddressSubBean> list) {
        this.list = list;
    }
}
