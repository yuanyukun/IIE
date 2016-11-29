package com.horem.parachute.balloon.Bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/6/23.
 */
public class FragmentDiscoveryBean implements Serializable{
    private String message;
    private int statusCode;
    private DiscoveryResultBean result;

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public DiscoveryResultBean getResult() {
        return result;
    }
}
