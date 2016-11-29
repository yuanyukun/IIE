package com.horem.parachute.task.bean;

import java.io.Serializable;

/**
 * Created by yuanyukun on 2016/6/3.
 */
public class SendTaskPhotoBean implements Serializable {


    private int fileType;
    private String photoName;
    private String srcFileName;
    private String filePath;
    private String tempFilePath;
    private long fileSize;
    private String fileExtension;
    private int sortNo;

    public String getTempFilePath() {
        return tempFilePath;
    }

    public int getSortNo() {
        return sortNo;
    }

    public int getFileType() {
        return fileType;
    }

    public String getPhotoName() {
        return photoName;
    }

    public String getSrcFileName() {
        return srcFileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getTemFilePath() {
        return tempFilePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}
