package com.parameter.entity;

/**
 * @author xiaodong
 * @version 1.0.0
 * @ClassName Manual.java
 * @Description TODO
 * @createTime 2022Äê03ÔÂ23ÈÕ 10:23:00
 */
public class Manual {
    private String fileManualNo;
    private int fileType;
    private String serverIP;
    private int location;
    private int roadNo;
    private int stano;
    private int portNo;

    @Override
    public String toString() {
        return "Manual{" +
                "fileManualNo='" + fileManualNo + '\'' +
                ", fileType=" + fileType +
                ", serverIP='" + serverIP + '\'' +
                ", location=" + location +
                ", roadNo=" + roadNo +
                ", stano=" + stano +
                ", portNo=" + portNo +
                '}';
    }

    public String getFileManualNo() {
        return fileManualNo;
    }

    public void setFileManualNo(String fileManualNo) {
        this.fileManualNo = fileManualNo;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public int getRoadNo() {
        return roadNo;
    }

    public void setRoadNo(int roadNo) {
        this.roadNo = roadNo;
    }

    public int getStano() {
        return stano;
    }

    public void setStano(int stano) {
        this.stano = stano;
    }

    public int getPortNo() {
        return portNo;
    }

    public void setPortNo(int portNo) {
        this.portNo = portNo;
    }
}
