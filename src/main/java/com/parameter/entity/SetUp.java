package com.parameter.entity;

/**
 * @author xiaodong
 * @version 1.0.0
 * @ClassName SetUp.java
 * @Description 车道实体
 * @createTime 2022年03月21日 16:43:00
 */
public class SetUp {
    private String srvIP;
    private int roadNO;
    private int staNO;
    private int portNO;

    @Override
    public String toString() {
        return "SetUp{" +
                "srvIP='" + srvIP + '\'' +
                ", roadNO=" + roadNO +
                ", staNO=" + staNO +
                ", portNO=" + portNO +
                '}';
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

    public int getPortNO() {
        return portNO;
    }

    public void setPortNO(int portNO) {
        this.portNO = portNO;
    }
}
