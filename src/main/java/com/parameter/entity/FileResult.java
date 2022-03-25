package com.parameter.entity;

/**
 * @author xiaodong
 * @version 1.0.0
 * @ClassName FileResult.java
 * @Description 回调函数实体
 * @createTime 2022年03月24日 15:36:00
 */
public class FileResult {
    private String resultCode;
    private String describe;
    private String fileKey;
    private String fileName;
    private String downloadDT;
    private String roadno;
    private String stano;
    private String portno;
    private String location;
    private String serverIP;

    @Override
    public String toString() {
        return "FileResult{" +
                "resultCode='" + resultCode + '\'' +
                ", describe='" + describe + '\'' +
                ", fileKey='" + fileKey + '\'' +
                ", fileName='" + fileName + '\'' +
                ", downloadDT='" + downloadDT + '\'' +
                ", roadno='" + roadno + '\'' +
                ", stano='" + stano + '\'' +
                ", portno='" + portno + '\'' +
                ", location='" + location + '\'' +
                ", serverIP='" + serverIP + '\'' +
                '}';
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
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

    public String getDownloadDT() {
        return downloadDT;
    }

    public void setDownloadDT(String downloadDT) {
        this.downloadDT = downloadDT;
    }

    public String getRoadno() {
        return roadno;
    }

    public void setRoadno(String roadno) {
        this.roadno = roadno;
    }

    public String getStano() {
        return stano;
    }

    public void setStano(String stano) {
        this.stano = stano;
    }

    public String getPortno() {
        return portno;
    }

    public void setPortno(String portno) {
        this.portno = portno;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }
}
