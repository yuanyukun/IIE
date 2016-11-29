package com.horem.parachute.task.bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/5/24.
 */
public class GetTaskFeeBean implements Serializable {

    private String address;
    private  String  symbol;
    private float   benchmark;
    private float   poundageBenchmark;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public float getBenchmark() {
        return benchmark;
    }

    public void setBenchmark(float benchmark) {
        this.benchmark = benchmark;
    }

    public float getPoundageBenchmark() {
        return poundageBenchmark;
    }

    public void setPoundageBenchmark(float poundageBenchmark) {
        this.poundageBenchmark = poundageBenchmark;
    }
}
