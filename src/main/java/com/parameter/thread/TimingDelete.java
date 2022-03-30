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
 * @Description ��ʱ��������ļ�
 * @createTime 2022��03��18�� 10:24:00
 */
public class TimingDelete implements Runnable {
    private static String getOverTime(String fileOutTime) {
        int outTime = Integer.parseInt(fileOutTime);
        //��ȡ��ǰ����
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date today = new Date();
        //��ȡ��ʮ��ǰ����
        Calendar theCa = Calendar.getInstance();
        theCa.setTime(today);
        theCa.add(theCa.DATE, -outTime); //���һ������30�ɸģ�30�����˼
        Date start = theCa.getTime();
        String startDate = sdf.format(start); //��ʮ��֮ǰ����
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
            LogCommon.WriteLogNormal("��ȡ�����ļ���Ϣ���쳣��" + e.getMessage(), "TimingDelete");
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
                LogCommon.WriteLogNormal("�ļ�fileKey��" + fileKey + "�Ѵ�FT_UPLOAD_TABLE��ɾ��", "TimingDelete");
            } else {
                LogCommon.WriteLogNormal("�ļ�fileKey��" + fileKey + "��FT_UPLOAD_TABLEɾ��ʧ��", "TimingDelete");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("��ȡ�����ļ���Ϣ���쳣��" + e.getMessage(), "TimingDelete");
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
            LogCommon.WriteLogNormal("ɾ���ļ�ϵͳ���ļ��쳣��" + e.getMessage(), "TimingDelete");
            LogCommon.WriteLogNormal("ɾ��·����" + deleteURL, "TimingDelete");
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
            LogCommon.WriteLogNormal("����ɾ��·���쳣��" + e.getMessage(), "TimingDelete");
        }
        return null;
    }


    @Override
    public void run() {
        LogCommon.WriteLogNormal(Thread.currentThread().getName() + "��ʱ��������ļ��߳��ѿ���...", "TimingDelete");
        InitInfo initInfo = GetInit.getInitInfo();
        CachedRowSetImpl crs = null;
        while (true) {
            try {
                //��ȡ��������
                String overTime = getOverTime(initInfo.getFileOutTime());
                //���ݹ������ڲ�ѯ����
                crs = getOverTimeFile(overTime);
                //�жϼ���
                if (initInfo.getLevel().equals("10")) {
                    while (crs.next()) {
                        String fileKey = crs.getString("FILEKEY");
                        String fileMD5 = crs.getString("FILEMD5");
                        String fileURL = crs.getString("FILEURL");
                        //����ɾ��·��
                        String deleteURL = getDeleteURL(fileURL, fileMD5);
                        boolean flag = deleteFile(deleteURL);
                        if (flag) {
                            LogCommon.WriteLogNormal("�ļ�fileKey��" + fileKey + "�Ѵ��ļ�ϵͳɾ��", "TimingDelete");
                            deleteFileTable(fileKey);
                        } else {
                            LogCommon.WriteLogNormal("���ļ�fileKey��" + fileKey + "�����ڣ�ֱ��ɾ��FT_UPLOAD_TABLE���������", "TimingDelete");
                            deleteFileTable(fileKey);
                        }

                    }
                } else {
                    //�����ĺ�վ�������Ժ���������...
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
