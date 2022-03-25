package com.parameter.entity;

/**
 * @author xiaodong
 * @version 1.0.0
 * @ClassName SubCompany.java
 * @Description 分公司实体
 * @createTime 2022年03月21日 16:42:00
 */
public class SubCompany {
    private int subComNO;
    private String subComName;
    private String srvIP;

    @Override
    public String toString() {
        return "SubCompany{" +
                "subComNO=" + subComNO +
                ", subComName='" + subComName + '\'' +
                ", srvIP='" + srvIP + '\'' +
                '}';
    }

    public int getSubComNO() {
        return subComNO;
    }

    public void setSubComNO(int subComNO) {
        this.subComNO = subComNO;
    }

    public String getSubComName() {
        return subComName;
    }

    public void setSubComName(String subComName) {
        this.subComName = subComName;
    }

    public String getSrvIP() {
        return srvIP;
    }

    public void setSrvIP(String srvIP) {
        this.srvIP = srvIP;
    }
}
