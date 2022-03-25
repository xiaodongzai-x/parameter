package com.parameter.entity;

/**
 * @author xiaodong
 * @version 1.0.0
 * @ClassName Database.java
 * @Description ���ݿ�ʵ����
 * @createTime 2022��03��15�� 15:32:00
 */
public class Database {
    //����
    private String stationName;
    //�û���
    private String userName;
    //����
    private String passwd;
    //���ݿ�ip��ַ
    private String IP;
    //���ݿ����
    private String dataBaseName;
    //������Ϣ
    private String constr;
    //���ݿ�����
    private String dataBaseType;
    private String roadNO;
    private String staNO;
    private String portNO;

    @Override
    public String toString() {
        return "Database{" +
                "stationName='" + stationName + '\'' +
                ", userName='" + userName + '\'' +
                ", passwd='" + passwd + '\'' +
                ", IP='" + IP + '\'' +
                ", dataBaseName='" + dataBaseName + '\'' +
                ", constr='" + constr + '\'' +
                ", dataBaseType='" + dataBaseType + '\'' +
                ", roadNO='" + roadNO + '\'' +
                ", staNO='" + staNO + '\'' +
                ", portNO='" + portNO + '\'' +
                '}';
    }

    public String getRoadNO() {
        return roadNO;
    }

    public void setRoadNO(String roadNO) {
        this.roadNO = roadNO;
    }

    public String getStaNO() {
        return staNO;
    }

    public void setStaNO(String staNO) {
        this.staNO = staNO;
    }

    public String getPortNO() {
        return portNO;
    }

    public void setPortNO(String portNO) {
        this.portNO = portNO;
    }

    public String getDataBaseType() {
        return dataBaseType;
    }

    public void setDataBaseType(String dataBaseType) {
        this.dataBaseType = dataBaseType;
    }

    public String getConstr() {
        return constr;
    }

    public void setConstr(String constr) {
        this.constr = constr;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getDataBaseName() {
        return dataBaseName;
    }

    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }
}
