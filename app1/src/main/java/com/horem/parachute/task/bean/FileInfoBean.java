package com.horem.parachute.task.bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/6/14.
 */
public class FileInfoBean implements Serializable {

        private String id;
        private String name ;
        private float fileSize;
        private int mediaType;
        private int timeLength;
        private String previewImg;

    public int getTimeLength() {
        return timeLength;
    }

    public String getPreviewImg() {
        return previewImg;
    }

    public String getId() {
            return id;
        }


        public String getName() {
            return name;
        }


        public float getFileSize() {
            return fileSize;
        }


        public int getMediaType() {
            return mediaType;
        }

}
