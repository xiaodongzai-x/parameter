package com.parameter.entity;

/**
 * @author xiaodong
 * @version 1.0.0
 * @ClassName FileInfo.java
 * @Description TODO
 * @createTime 2022Äê03ÔÂ24ÈÕ 14:57:00
 */
public class FileInfo {
    private String fileKey;
    private String fileName;
    private String fileMD5;
    private String fileUrl;
    private int fileType;
    private String fileVersion;
    private String stagbID;
    private long fileSize;

    @Override
    public String toString() {
        return "FileInfo{" +
                "fileKey='" + fileKey + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileMD5='" + fileMD5 + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", fileType=" + fileType +
                ", fileVersion='" + fileVersion + '\'' +
                ", stagbID='" + stagbID + '\'' +
                ", fileSize=" + fileSize +
                '}';
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public String getFileVersion() {
        return fileVersion;
    }

    public void setFileVersion(String fileVersion) {
        this.fileVersion = fileVersion;
    }

    public String getStagbID() {
        return stagbID;
    }

    public void setStagbID(String stagbID) {
        this.stagbID = stagbID;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileMD5() {
        return fileMD5;
    }

    public void setFileMD5(String fileMD5) {
        this.fileMD5 = fileMD5;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
