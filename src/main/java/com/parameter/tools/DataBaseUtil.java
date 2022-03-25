package com.parameter.tools;

import com.parameter.entity.Database;

import java.sql.*;
import java.util.List;

/**
 * @author xiaodong
 * @version 1.0.0
 * @ClassName DataBaseUtil.java
 * @Description 数据库操作
 * @createTime 2022年03月18日 14:03:00
 */
public class DataBaseUtil {

    //获取配置文件里数据库信息
    public static Database getDataBase(int num){
        ReadXML readXML =new ReadXML();
        List<Database> databases = readXML.getDatabaseInfo();
        Database database = databases.get(num);
        return database;
    }

    //获取配置文件里联网数据库信息
    public static Database getDataBaseLW(){
        ReadXML readXML =new ReadXML();
        Database database = readXML.getDatabaseLw();
        return database;
    }

    //连接数据库
    public static Connection getConn(Database database){
        Connection connection = null;
        SQLDBUtil sqldbUtil = null;
        MySQLDBUtil mySQLDBUtil = null;
        OracleDBUtil oracleDBUtil = null;
        try {
            switch (database.getDataBaseType()) {
                case "1":
                    sqldbUtil = new SQLDBUtil(database);
                    connection = sqldbUtil.getConnection();
                    break;
                case "2":
                    oracleDBUtil = new OracleDBUtil(database);
                    connection = oracleDBUtil.getConnection();
                    break;
                case "3":
                    break;
                case "4":
                    mySQLDBUtil = new MySQLDBUtil(database);
                    connection = mySQLDBUtil.getConnection();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("数据库类型错误","error");
        }
        return connection;
    }

    //关闭数据库
    public static void dataBaseCloseS(Connection conn, Statement ps, ResultSet rs, int type){
        try {
            switch (type){
                case 1:
                    SQLDBUtil.closeAllS(rs,ps,conn);
                    break;
                case 2:
                    OracleDBUtil.closeAllS(rs,ps,conn);
                    break;
                case 3:
                    break;
                case 4:
                    MySQLDBUtil.closeAllS(rs,ps,conn);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
