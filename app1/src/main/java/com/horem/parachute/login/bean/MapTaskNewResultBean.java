package com.horem.parachute.login.bean;

import com.horem.parachute.task.bean.BalloonBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yuanyukun on 2016/6/14.
 */
public class MapTaskNewResultBean implements Serializable {
        private double scope;
        private SubTaskBean[] subTaskList;
        private ArrayList<BalloonBean> balloonList;

        public double getScope() {
            return scope;
        }

        public SubTaskBean[] getSubTaskList() {
            return subTaskList;
        }

        public ArrayList<BalloonBean> getBalloonList() {
            return balloonList;
        }
}
