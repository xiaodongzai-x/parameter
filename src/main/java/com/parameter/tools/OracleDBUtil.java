package com.parameter.tools;


import com.parameter.entity.Database;

import java.sql.*;

import static java.lang.Class.forName;

public class OracleDBUtil {

    //设置数据库名称
    private String URL;
    private String USER;
    private String PASSWORD;
    private Connection conn = null;

    public OracleDBUtil(Database database) {
        this.URL = database.getConstr();
        this.USER = database.getUserName();
        this.PASSWORD = database.getPasswd();
    }

    //静态代码块加载类时执行一次，加载数据库信息文件
    static {
        //用来加载properties文件的数据， （文件时键值对， 名字要完整匹配）
        try {
            forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
    }

    //对外提供一个方法来获取数据库连接
    public Connection getConnection() {
        try {
            //2.获得数据库的连接
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("数据库连接异常"+e.getMessage(), "error");
        }
        return conn;
    }

    //关闭资源
    public static void closeAll(ResultSet rs, PreparedStatement pre, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (pre != null) {
            try {
                pre.close();
            } catch (SQLException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
        }
    }

    //关闭资源
    public static void closeAllS(ResultSet rs, Statement pre, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (pre != null) {
            try {
                pre.close();
            } catch (SQLException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
        }
    }
}
