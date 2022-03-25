package com.parameter.entity;

/**
 * @author xiaodong
 * @version 1.0.0
 * @ClassName Name.java
 * @Description 站实体
 * @createTime 2022年03月21日 16:42:00
 */
public class Name {
    private int subComNO;
    private String srvIP;
    private int roadNO;
    private int staNO;
    private String stanoName;
    private String stagbID;
    private String srvName;

    @Override
    public String toString() {
        return "Name{" +
                "subComNO=" + subComNO +
                ", srvIP='" + srvIP + '\'' +
                ", roadNO=" + roadNO +
                ", staNO=" + staNO +
                ", stanoName='" + stanoName + '\'' +
                ", stagbID='" + stagbID + '\'' +
                ", srvName='" + srvName + '\'' +
                '}';
    }

    public String getSrvName() {
        return srvName;
    }

    public void setSrvName(String srvName) {
        this.srvName = srvName;
    }

    public int getSubComNO() {
        return subComNO;
    }

    public void setSubComNO(int subComNO) {
        this.subComNO = subComNO;
    }

    public String getSrvIP() {
        return srvIP;
    }

    public void setSrvIP(String srvIP) {
        this.srvIP = srvIP;
    }

    public int getRoadNO() {
        return roadNO;
    }

    public void setRoadNO(int roadNO) {
        this.roadNO = roadNO;
    }

    public int getStaNO() {
        return staNO;
    }

    public void setStaNO(int staNO) {
        this.staNO = staNO;
    }

    public String getStanoName() {
        return stanoName;
    }

    public void setStanoName(String stanoName) {
        this.stanoName = stanoName;
    }

    public String getStagbID() {
        return stagbID;
    }

    public void setStagbID(String stagbID) {
        this.stagbID = stagbID;
    }
}
