package com.horem.parachute.balloon.Bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yuanyukun on 2016/6/30.
 */
public class FansResultBean implements Serializable {
    private ArrayList<FansResultSubItem> list;
    private int statusCode;

    public ArrayList<FansResultSubItem> getList() {
        return list;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
