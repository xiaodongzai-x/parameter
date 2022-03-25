package com.parameter.tools;


import com.parameter.entity.Database;

import java.sql.*;

import static java.lang.Class.forName;

public class OracleDBUtil {

    //�������ݿ�����
    private String URL;
    private String USER;
    private String PASSWORD;
    private Connection conn = null;

    public OracleDBUtil(Database database) {
        this.URL = database.getConstr();
        this.USER = database.getUserName();
        this.PASSWORD = database.getPasswd();
    }

    //��̬����������ʱִ��һ�Σ��������ݿ���Ϣ�ļ�
    static {
        //��������properties�ļ������ݣ� ���ļ�ʱ��ֵ�ԣ� ����Ҫ����ƥ�䣩
        try {
            forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            // TODO �Զ����ɵ� catch ��
            e.printStackTrace();
        }
    }

    //�����ṩһ����������ȡ���ݿ�����
    public Connection getConnection() {
        try {
            //2.������ݿ������
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("���ݿ������쳣"+e.getMessage(), "error");
        }
        return conn;
    }

    //�ر���Դ
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
                // TODO �Զ����ɵ� catch ��
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // TODO �Զ����ɵ� catch ��
                e.printStackTrace();
            }
        }
    }

    //�ر���Դ
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
                // TODO �Զ����ɵ� catch ��
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // TODO �Զ����ɵ� catch ��
                e.printStackTrace();
            }
        }
    }
}
