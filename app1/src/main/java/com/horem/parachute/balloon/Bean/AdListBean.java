package com.horem.parachute.balloon.Bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/6/24.
 */
public class AdListBean implements Serializable {
    private int adsId;
    private String adsName;
    private String  coverImg;

    public int getAdsId() {
        return adsId;
    }

    public String getAdsName() {
        return adsName;
    }

    public String getCoverImg() {
        return coverImg;
    }
}
