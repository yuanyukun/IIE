package com.horem.parachute.balloon.Bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/6/24.
 */
public class ThemeListBean implements Serializable{
        private String coverImg;
        private String themeDesc;
        private String themeID;
        private String themeName;

    public String getCoverImg() {
        return coverImg;
    }

    public String getThemeDesc() {
        return themeDesc;
    }

    public String getThemeID() {
        return themeID;
    }

    public String getThemeName() {
        return themeName;
    }
}
