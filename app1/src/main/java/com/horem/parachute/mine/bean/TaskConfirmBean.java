package com.horem.parachute.mine.bean;

import com.horem.parachute.balloon.Bean.BaseResultBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by user on 2016/4/26.
 */
public class TaskConfirmBean extends BaseResultBean {
    private TaskConfirmResultBean result;

    public TaskConfirmResultBean getResult() {
        return result;
    }

    public void setResult(TaskConfirmResultBean result) {
        this.result = result;
    }
}
