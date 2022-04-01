package com.parameter.entity;

import org.springframework.stereotype.Component;

/**
 * @author xiaodong
 * @version 1.0.0
 * @ClassName InitInfo.java
 * @Description 初始化信息实体类
 * @createTime 2022年03月18日 10:33:00
 */
public class InitInfo {
    private String level;//级别 10联网中心 20分中心 30 站级
    private String fileOutTime;//文件过期时间
    private String superiorIP;//
    private String specialStano;
    private String webServicePort;
    private String specialPort;
    private int getTaskNum;
    private String goFastPort;

    @Override
    public String toString() {
        return "InitInfo{" +
                "level='" + level + '\'' +
                ", fileOutTime='" + fileOutTime + '\'' +
                ", superiorIP='" + superiorIP + '\'' +
                ", specialStano='" + specialStano + '\'' +
                ", webServicePort='" + webServicePort + '\'' +
                ", specialPort='" + specialPort + '\'' +
                ", getTaskNum=" + getTaskNum +
                ", goFastPort='" + goFastPort + '\'' +
                '}';
    }

    public String getGoFastPort() {
        return goFastPort;
    }

    public void setGoFastPort(String goFastPort) {
        this.goFastPort = goFastPort;
    }

    public int getGetTaskNum() {
        return getTaskNum;
    }

    public void setGetTaskNum(int getTaskNum) {
        this.getTaskNum = getTaskNum;
    }

    public String getWebServicePort() {
        return webServicePort;
    }

    public void setWebServicePort(String webServicePort) {
        this.webServicePort = webServicePort;
    }

    public String getSpecialPort() {
        return specialPort;
    }

    public void setSpecialPort(String specialPort) {
        this.specialPort = specialPort;
    }

    public String getSpecialStano() {
        return specialStano;
    }

    public void setSpecialStano(String specialStano) {
        this.specialStano = specialStano;
    }

    public String getSuperiorIP() {
        return superiorIP;
    }

    public void setSuperiorIP(String superiorIP) {
        this.superiorIP = superiorIP;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getFileOutTime() {
        return fileOutTime;
    }

    public void setFileOutTime(String fileOutTime) {
        this.fileOutTime = fileOutTime;
    }
}
