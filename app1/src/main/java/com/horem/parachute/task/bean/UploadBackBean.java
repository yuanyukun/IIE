package com.horem.parachute.task.bean;

import java.io.Serializable;

/**
 * Created by user on 2016/4/14.
 */
public class UploadBackBean implements Serializable{

    /// <summary>
    /// 文件类型
    /// </summary>
    public long fileType;
    /// <summary>
    /// 文件名称
    /// </summary>
    public String photoName;
    /// <summary>
    /// 文件原名
    /// </summary>
    public String srcFileName;
    /// <summary>
    /// 文件路径
    /// </summary>
    public String filePath;
    /// <summary>
    /// 临时文件路径
    /// </summary>
    public String tempFilePath;
    /// <summary>
    /// 文件大小
    /// </summary>
    public long fileSize ;
    /// <summary>
    /// 文件后缀
    /// </summary>
    public String fileExtension;
    /// <summary>
    /// 序号
    /// </summary>
    public int sortNo;

    public long getFileType() {
        return fileType;
    }

    public void setFileType(long fileType) {
        this.fileType = fileType;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getSrcFileName() {
        return srcFileName;
    }

    public void setSrcFileName(String srcFileName) {
        this.srcFileName = srcFileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getTempFilePath() {
        return tempFilePath;
    }

    public void setTempFilePath(String tempFilePath) {
        this.tempFilePath = tempFilePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public int getSortNo() {
        return sortNo;
    }

    public void setSortNo(int sortNo) {
        this.sortNo = sortNo;
    }
}
