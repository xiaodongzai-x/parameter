package com.parameter.thread;

import com.alibaba.fastjson.JSONArray;
import com.parameter.entity.Database;
import com.parameter.entity.FileInfo;
import com.parameter.entity.InitInfo;
import com.parameter.init.GetInit;
import com.parameter.tools.DataBaseUtil;
import com.parameter.tools.LogCommon;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author xiaodong
 * @version 1.0.0
 * @ClassName TaskThread.java
 * @Description �̱߳��������
 * @createTime 2022��03��17�� 08:52:00
 */
public class TaskThread implements Runnable {
    @Override
    public void run() {
        LogCommon.WriteLogNormal(Thread.currentThread().getName()+"���������߳��ѿ���...","Init");
        while (true){

            //1.�������ݿ⣬���������
            //2.ɸѡ�Զ��·����͵�����ƴ�ӳ�json�ַ���
            //3.�ж��¼�����
            //3.1�¼��Ƿ����Ļ�����վ
              //����webservice�·�

            //3.2�¼��ǳ���
              //����socket�·�

            //4.�����ص���Ϣ������Ϣ���뵽��־��

            //5.ָ���·��ɹ���ɾ��������
            try {
                Thread.sleep(10*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //��ȡ������Ϣ��תΪjsonArray
    public static JSONArray getParameterTask() {
        Connection connection = null;
        Statement ps = null;
        ResultSet rs = null;
        Database database = null;
        FileInfo fileInfo = null;
        InitInfo initInfo = null;
        try {
            initInfo = GetInit.getInitInfo();
            String getCreateFileSql;
            database = DataBaseUtil.getDataBase(0);
            if (database.getDataBaseType().equals("1")){
                getCreateFileSql = "select TOP "+initInfo.getGetTaskNum()+" FILEkEY, FILEURL,FILEMD5,FILENAME from FT_UPLOAD_TABLE where ISCREATEFILE = 0";
            }else {
                getCreateFileSql = "select FILEkEY, FILEURL,FILEMD5,FILENAME from FT_UPLOAD_TABLE where ISCREATEFILE = 0 limit "+initInfo.getGetTaskNum();
            }
            connection = DataBaseUtil.getConn(database);
            ps = connection.createStatement();
            rs = ps.executeQuery(getCreateFileSql);
            while (rs.next()){
                //��ȡ�ļ�·����key
                fileInfo = new FileInfo();
                fileInfo.setFileKey(rs.getString("FILEkEY"));
                fileInfo.setFileUrl(rs.getString("FILEURL"));
                fileInfo.setFileMD5(rs.getString("FILEMD5"));
                fileInfo.setFileName(rs.getString("FILENAME"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("�����ļ��߳��쳣��" + e.getMessage(), "TaskThread");
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
        return new JSONArray();
    }
}
