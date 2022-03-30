package com.parameter.thread;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.parameter.entity.Database;
import com.parameter.entity.InitInfo;
import com.parameter.init.GetInit;
import com.parameter.tools.DataBaseUtil;
import com.parameter.tools.LogCommon;
import com.sun.rowset.CachedRowSetImpl;
import okhttp3.*;

import java.sql.Connection;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author xiaodong
 * @version 1.0.0
 * @ClassName TimingDelete.java
 * @Description 定时清理过期文件
 * @createTime 2022年03月18日 10:24:00
 */
public class TimingDelete implements Runnable {
    private static String getOverTime(String fileOutTime) {
        int outTime = Integer.parseInt(fileOutTime);
        //获取当前日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date today = new Date();
        //获取三十天前日期
        Calendar theCa = Calendar.getInstance();
        theCa.setTime(today);
        theCa.add(theCa.DATE, -outTime); //最后一个数字30可改，30天的意思
        Date start = theCa.getTime();
        String startDate = sdf.format(start); //三十天之前日期
        return startDate;
    }


    private static CachedRowSetImpl getOverTimeFile(String overTime) {
        Connection connection = null;
        Statement ps = null;
        ResultSet rs = null;
        Database database = null;
        CachedRowSetImpl crs = null;
        try {
            String getCreateFileSql = "select FILEKEY,FILEURL,FILEMD5 FROM FT_UPLOAD_TABLE WHERE FILEDT < " + overTime;
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            ps = connection.createStatement();
            rs = ps.executeQuery(getCreateFileSql);
            crs = new CachedRowSetImpl();
            crs.populate(rs);
        } catch (Exception e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("获取参数文件信息表异常：" + e.getMessage(), "TimingDelete");
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
        return crs;
    }


    private static void deleteFileTable(String fileKey) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {
            String getCreateFileSql = "delete from FT_UPLOAD_TABLE where FILEKEY=" + fileKey;
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            ps = connection.prepareStatement(getCreateFileSql);
            int num = ps.executeUpdate(getCreateFileSql);
            if (num == 1) {
                LogCommon.WriteLogNormal("文件fileKey：" + fileKey + "已从FT_UPLOAD_TABLE表删除", "TimingDelete");
            } else {
                LogCommon.WriteLogNormal("文件fileKey：" + fileKey + "从FT_UPLOAD_TABLE删除失败", "TimingDelete");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("获取参数文件信息表异常：" + e.getMessage(), "TimingDelete");
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
    }

    public static boolean deleteFile(String deleteURL) {
        String result = "";
        try {
            OkHttpClient httpClient = new OkHttpClient();
            MultipartBody multipartBody = new MultipartBody.Builder().
                    setType(MultipartBody.FORM)
                    .addFormDataPart("output", "json")
                    .build();

            Request request = new Request.Builder()
                    .url(deleteURL)
                    .post(multipartBody)
                    .build();

            Response response = httpClient.newCall(request).execute();
            ResponseBody body = response.body();
            if (response.isSuccessful()) {
                if (body != null) {
                    result = body.string();
                    JSONObject object = JSON.parseObject(result, Feature.OrderedField);
                    if (object.getString("status").equals("ok")) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("删除文件系统里文件异常：" + e.getMessage(), "TimingDelete");
            LogCommon.WriteLogNormal("删除路径：" + deleteURL, "TimingDelete");
        }
        return false;
    }

    private static String getDeleteURL(String fileURL, String fileMD5) {
        try {
            String url2 = fileURL.substring(7);
            String[] strings = url2.split("\\/");
            StringBuffer buffer = new StringBuffer();
            String[] ips = strings[0].split("\\.");
            buffer.append("http://");
            buffer.append(ips[0] + "." + ips[1] + "." + ips[2] + ".");
            buffer.append(strings[2].substring(7) + ":");
            buffer.append("9022/");
            buffer.append(strings[1] + "/");
            buffer.append("delete?");
            buffer.append("md5=" + fileMD5);
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("解析删除路径异常：" + e.getMessage(), "TimingDelete");
        }
        return null;
    }


    @Override
    public void run() {
        LogCommon.WriteLogNormal(Thread.currentThread().getName() + "定时清理过期文件线程已开启...", "TimingDelete");
        InitInfo initInfo = GetInit.getInitInfo();
        CachedRowSetImpl crs = null;
        while (true) {
            try {
                //获取过期日期
                String overTime = getOverTime(initInfo.getFileOutTime());
                //根据过期日期查询数据
                crs = getOverTimeFile(overTime);
                //判断级别
                if (initInfo.getLevel().equals("10")) {
                    while (crs.next()) {
                        String fileKey = crs.getString("FILEKEY");
                        String fileMD5 = crs.getString("FILEMD5");
                        String fileURL = crs.getString("FILEURL");
                        //构建删除路径
                        String deleteURL = getDeleteURL(fileURL, fileMD5);
                        boolean flag = deleteFile(deleteURL);
                        if (flag) {
                            LogCommon.WriteLogNormal("文件fileKey：" + fileKey + "已从文件系统删除", "TimingDelete");
                            deleteFileTable(fileKey);
                        } else {
                            LogCommon.WriteLogNormal("该文件fileKey：" + fileKey + "不存在，直接删除FT_UPLOAD_TABLE表里的数据", "TimingDelete");
                            deleteFileTable(fileKey);
                        }

                    }
                } else {
                    //分中心和站的数据稍后再做处理...
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } finally {
                if (crs != null) {
                    try {
                        crs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                Thread.sleep(24 * 60 * 60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
