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
 * @Description 线程遍历任务表
 * @createTime 2022年03月17日 08:52:00
 */
public class TaskThread implements Runnable {
    @Override
    public void run() {
        LogCommon.WriteLogNormal(Thread.currentThread().getName()+"遍历任务线程已开启...","Init");
        while (true){

            //1.连接数据库，遍历任务表
            //2.筛选自动下发类型的数据拼接成json字符串
            //3.判断下级类型
            //3.1下级是分中心或者是站
              //调用webservice下发

            //3.2下级是车道
              //调用socket下发

            //4.解析回调信息，将信息插入到日志表

            //5.指令下发成功则删除该任务
            try {
                Thread.sleep(10*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //获取参数信息并转为jsonArray
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
                //获取文件路径及key
                fileInfo = new FileInfo();
                fileInfo.setFileKey(rs.getString("FILEkEY"));
                fileInfo.setFileUrl(rs.getString("FILEURL"));
                fileInfo.setFileMD5(rs.getString("FILEMD5"));
                fileInfo.setFileName(rs.getString("FILENAME"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("生成文件线程异常：" + e.getMessage(), "TaskThread");
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
        return new JSONArray();
    }
}
