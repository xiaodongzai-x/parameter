package com.parameter.tools;

import com.parameter.entity.Database;
import com.parameter.entity.InitInfo;
import com.trkf.PasswordEncryption.PassWordUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/*
读取配置文件
 */
public class ReadXML {

    public static String getPath() {
        String path = ReadXML.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (System.getProperty("os.name").contains("dows")) {
            path = path.substring(1, path.length());
        }
        if (path.contains("jar")) {
            path = path.substring(0, path.lastIndexOf("."));
            return path.substring(0, path.lastIndexOf("/"));
        }
        return path.replace("target/classes/", "");
    }

    private static Element readTables(String configPath) {
        //使用dom4j解析scores2.xml,生成dom树
        SAXReader reader = new SAXReader();
        Document doc = null;
        try {
            doc = reader.read(new File(getPath() + "/xml/" + configPath));
        } catch (DocumentException e) {
            LogCommon.WriteLogNormal(configPath + "路径错误", "error");
            LogCommon.WriteLogNormal(e.getMessage(), "error");
            e.printStackTrace();
        }
        Element root = doc.getRootElement();
        return root;
    }

    //获取数据库信息
    public List<Database> getDatabaseInfo() {
        List<Database> dataBaseList = new ArrayList<>();
        try {
            Element root = readTables("Database.xml");
            int num = root.selectNodes("/Config/JDBC").size();
            for (int i = 0; i < num; i++) {
                Node node = (Node) root.selectNodes("/Config/JDBC").get(i);
                Database dataBase = new Database();
                String ipAddress = node.selectSingleNode("IP").getStringValue();
                String db = node.selectSingleNode("dataBaseName").getStringValue();
                String dBType = node.selectSingleNode("dataBaseType").getStringValue();
                String connstr = null;
                if (dBType.equals("1")) {
                    connstr = String.format("jdbc:jtds:sqlserver://%s:1433;DatabaseName=%s", ipAddress, db);
                } else if (dBType.equals("2")) {
                    connstr = String.format("jdbc:oracle:thin:@%s:1521:%s", ipAddress, db);
                } else if (dBType.equals("4")) {
                    connstr = String.format("jdbc:mysql://%s:3306/%s?useSSL=false&serverTimezone=CTT", ipAddress, db);
                } else {
                    LogCommon.WriteLogNormal("数据库类型错误", "receive");
                }
                dataBase.setDataBaseType(dBType);
                dataBase.setStationName(node.selectSingleNode("stationName").getStringValue());
                dataBase.setUserName(node.selectSingleNode("userName").getStringValue());
                dataBase.setPasswd(PassWordUtils.decrypt(node.selectSingleNode("passwd").getStringValue()));
                dataBase.setRoadNO(node.selectSingleNode("roadNO").getStringValue());
                dataBase.setStaNO(node.selectSingleNode("staNO").getStringValue());
                dataBase.setPortNO(node.selectSingleNode("portNO").getStringValue());
                dataBase.setIP(ipAddress);
                dataBase.setConstr(connstr);
                dataBaseList.add(dataBase);
            }
        } catch (Exception e) {
            LogCommon.WriteLogNormal("获取数据库配置文件异常", "error");
            LogCommon.WriteLogNormal(e.getMessage(), "error");
            e.printStackTrace();
        }
        return dataBaseList;
    }

    //获取数据库信息
    public Database getDatabaseLw() {
        Database dataBase = new Database();
        try {
            Element root = readTables("Database.xml");
            Node node = (Node) root.selectNodes("/Config/JDBCLW").get(0);
            String ipAddress = node.selectSingleNode("IP").getStringValue();
            String db = node.selectSingleNode("dataBaseName").getStringValue();
            String dBType = node.selectSingleNode("dataBaseType").getStringValue();
            String connstr = null;
            if (dBType.equals("1")) {
                connstr = String.format("jdbc:jtds:sqlserver://%s:1433;DatabaseName=%s", ipAddress, db);
            } else if (dBType.equals("2")) {
                connstr = String.format("jdbc:oracle:thin:@%s:1521:%s", ipAddress, db);
            } else if (dBType.equals("4")) {
                connstr = String.format("jdbc:mysql://%s:3306/%s?useSSL=false&serverTimezone=CTT", ipAddress, db);
            } else {
                LogCommon.WriteLogNormal("数据库类型错误", "receive");
            }
            dataBase.setDataBaseType(dBType);
            dataBase.setStationName(node.selectSingleNode("stationName").getStringValue());
            dataBase.setUserName(node.selectSingleNode("userName").getStringValue());
            dataBase.setPasswd(PassWordUtils.decrypt(node.selectSingleNode("passwd").getStringValue()));
            dataBase.setIP(ipAddress);
            dataBase.setConstr(connstr);
        } catch (Exception e) {
            LogCommon.WriteLogNormal("获取数据库配置文件异常", "error");
            LogCommon.WriteLogNormal(e.getMessage(), "error");
            e.printStackTrace();
        }
        return dataBase;
    }

    //获取初始化的信息
    public InitInfo getInit() {
        InitInfo initInfo = new InitInfo();
        try {
            Element root = readTables("Database.xml");
            Node node = (Node) root.selectNodes("/Config/INIT").get(0);
            initInfo.setLevel(node.selectSingleNode("level").getStringValue());
            initInfo.setFileOutTime(node.selectSingleNode("fileOutTime").getStringValue());
            initInfo.setSuperiorIP(node.selectSingleNode("superiorIP").getStringValue());
            initInfo.setSpecialStano(node.selectSingleNode("specialStano").getStringValue());
            initInfo.setWebServicePort(node.selectSingleNode("webServicePort").getStringValue());
            initInfo.setSpecialPort(node.selectSingleNode("specialPort").getStringValue());
            initInfo.setGoFastPort(node.selectSingleNode("goFastPort").getStringValue());
            initInfo.setGetTaskNum(Integer.valueOf(node.selectSingleNode("getTaskNum").getStringValue()));
        } catch (Exception e) {
            LogCommon.WriteLogNormal("获取初始化配置文件异常", "error");
            LogCommon.WriteLogNormal(e.getMessage(), "error");
            e.printStackTrace();
        }
        return initInfo;
    }
}
