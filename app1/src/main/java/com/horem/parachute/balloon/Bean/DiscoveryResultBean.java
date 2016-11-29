package com.horem.parachute.balloon.Bean;

import com.horem.parachute.main.bean.BalloonListSubBeanItem;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yuanyukun on 2016/6/23.
 */
public class DiscoveryResultBean implements Serializable {
    private ArrayList<AdListBean> adsList;
    private ArrayList<BalloonListSubBeanItem> hotBalloonList;
    private ArrayList<BalloonListSubBeanItem> recommendBalloonList;
    private ArrayList<ThemeListBean> recommendThemeList;

    private boolean isMoreHotBalloon;
    private boolean isMoreRecommendBalloon;
    private boolean isMoreRecommendTheme;
    private boolean isMoreRecommendUser;
//    private recommendUserList;

    public ArrayList<AdListBean> getAdsList() {
        return adsList;
    }

    public ArrayList<BalloonListSubBeanItem> getHotBalloonList() {
        return hotBalloonList;
    }

    public ArrayList<BalloonListSubBeanItem> getRecommendBalloonList() {
        return recommendBalloonList;
    }

    public ArrayList<ThemeListBean> getRecommendThemeList() {
        return recommendThemeList;
    }

    public boolean isMoreHotBalloon() {
        return isMoreHotBalloon;
    }

    public boolean isMoreRecommendBalloon() {
        return isMoreRecommendBalloon;
    }

    public boolean isMoreRecommendTheme() {
        return isMoreRecommendTheme;
    }

    public boolean isMoreRecommendUser() {
        return isMoreRecommendUser;
    }
}
