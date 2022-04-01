package com.parameter.impl;

import com.alibaba.fastjson.JSONObject;
import com.parameter.entity.*;
import com.parameter.init.GetInit;
import com.parameter.service.ParameterService;
import com.parameter.tools.DataBaseUtil;
import com.parameter.tools.JsonUtil;
import com.parameter.tools.LogCommon;
import org.springframework.stereotype.Service;

import javax.jws.WebService;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @author xiaodong
 * @version 1.0.0
 * @ClassName ParameterServiceImpl.java
 * @Description TODO
 * @createTime 2022��03��14�� 14:37:00
 */
@Service
@WebService(serviceName = "ParameterService", // ��ӿ���ָ����nameһ��
        targetNamespace = "http://service.parameter.com/", // ��ӿ��е������ռ�һ��,һ���ǽӿڵİ�����
        endpointInterface = "com.parameter.service.ParameterService"// �ӿڵ�ַ
)
public class ParameterServiceImpl implements ParameterService {

    //��ѯ�ļ��·���ʽ
    public static int getType(int fileType) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database = null;
        int ISAUTOPUSH = 1;
        try {
            String sql = "select ISAUTOPUSH from FT_FILE_TYPE_TABLE where PROTOCOLTYPE = ?";
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            ps = connection.prepareStatement(sql);
            ps.setInt(1, fileType);
            rs = ps.executeQuery();
            while (rs.next()) {
                ISAUTOPUSH = rs.getInt("ISAUTOPUSH");
            }
            return ISAUTOPUSH;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
        return ISAUTOPUSH;
    }

    //�жϸò����Ƿ����
    private static boolean isExist(String fileKey) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {

            String sql = "select 1 from FT_UPLOAD_TABLE where FILEKEY = ?";
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            ps = connection.prepareStatement(sql);
            ps.setString(1, fileKey);
            rs = ps.executeQuery();
            while (rs.next()) {
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
        return false;
    }

    //ƴ�Ӳ���sql
    private static String getInsertSql(JSONObject jsonObject, String tableName) {
        try {
            StringBuffer sql1 = null;
            StringBuffer sql2 = null;
            sql1.append("if not exists ( ");
            sql1.append("select 1 from " + tableName + " where FILEKEY = ? ) ");
            sql1.append("insert into " + tableName + " (");
            sql2.append("values(");
            //fastjson��������
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                sql1.append(entry.getKey() + ",");
                sql2.append("?,");
            }
            sql1.replace(sql1.lastIndexOf(","), sql1.lastIndexOf(",") + 1, ") ");
            sql2.replace(sql2.lastIndexOf(","), sql2.lastIndexOf(",") + 1, ")");
            return sql1.append(sql2).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //���������Ϣ��
    private static boolean insertUploadTable(JSONObject parseObject) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            parseObject.put("FILEDT", sdf.format(new java.util.Date()));
            //������Ϣ���������Ϣ��
            LogCommon.WriteLogNormal("��������Ϣ����FT_UPLOAD_TABLE��", "taskCenter");
            String fileInfoSql = getInsertSql(parseObject, "FT_UPLOAD_TABLE");
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            ps.clearParameters();
            ps = connection.prepareStatement(fileInfoSql);
            int indexTask = 0;
            ps.setString(++indexTask, parseObject.getString("FILEKEY"));
            for (Map.Entry<String, Object> entry : parseObject.entrySet()) {
                if (entry.getValue() == null || "".equals(entry.getValue())) {
                    ps.setNull(++indexTask, Types.CHAR);
                } else {
                    ps.setString(++indexTask, entry.getValue().toString());
                }
            }
            int num = ps.executeUpdate();
            if (num==1){
                LogCommon.WriteLogNormal("������Ϣ����FT_UPLOAD_TABLE�����", "taskCenter");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("������Ϣ����FT_UPLOAD_TABLE���쳣��" + e.getMessage(), "taskCenter");
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
        return false;
    }

    //�����ĵ��ûص�����
    private static void centerDownloadFileResult(String fileKey, String serverIP, int resultCode, String downResult, String downDT) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {
            String sql = "update FT_TASK_LOG_TABLE set DOWNLOADFILESTATUS=?,DOWNLOADFILERESULT=?,DOWNLOADFILEDT=? where FILEKEY=? and SERVERIP=? and INSTRISSUESTATUS=?";
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            ps = connection.prepareStatement(sql);
            ps.setInt(1, resultCode);
            ps.setString(2, downResult);
            ps.setString(3, downDT);
            ps.setString(4, fileKey);
            ps.setString(5, serverIP);
            ps.setInt(6, 1);
            int num = ps.executeUpdate();
            if (num == 1) {
                LogCommon.WriteLogNormal("������־�쳣", "DownloadFileResult");
            } else {
                LogCommon.WriteLogNormal("������־�쳣", "DownloadFileResult");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LogCommon.WriteLogNormal("������־�쳣", "DownloadFileResult");
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
    }

    //վ�����ûص�����
    private static void stanoDownloadFileResult(String fileKey, int resultCode, String downResult, String downDT, int roadNO, int staNO, int Location) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {
            String sql = "update FT_TASK_LOG_TABLE set DOWNLOADFILESTATUS=?,DOWNLOADFILERESULT=?,DOWNLOADFILEDT=? where FILEKEY=? and ROADNO=? AND STANO=? AND LOCATION=? and INSTRISSUESTATUS=?";
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            ps = connection.prepareStatement(sql);
            ps.setInt(1, resultCode);
            ps.setString(2, downResult);
            ps.setString(3, downDT);
            ps.setString(4, fileKey);
            ps.setInt(5, roadNO);
            ps.setInt(6, staNO);
            ps.setInt(7, Location);
            ps.setInt(8, 1);
            int num = ps.executeUpdate();
            if (num == 1) {
                LogCommon.WriteLogNormal("������־�쳣", "DownloadFileResult");
            } else {
                LogCommon.WriteLogNormal("������־�쳣", "DownloadFileResult");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LogCommon.WriteLogNormal("������־�쳣", "DownloadFileResult");
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
    }

    //�������ûص�����
    private static void portnoDownloadFileResult(String fileKey, int resultCode, String downResult, String downDT, int roadNO, int staNO, int portNO, int Location) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {
            String sql = "update FT_TASK_LOG_TABLE set DOWNLOADFILESTATUS=?,DOWNLOADFILERESULT=?,DOWNLOADFILEDT=? where FILEKEY=? and ROADNO=? AND STANO=? and PORTNO = ? AND LOCATION=? and INSTRISSUESTATUS=?";
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            ps = connection.prepareStatement(sql);
            ps.setInt(1, resultCode);
            ps.setString(2, downResult);
            ps.setString(3, downDT);
            ps.setString(4, fileKey);
            ps.setInt(5, roadNO);
            ps.setInt(6, staNO);
            ps.setInt(7, portNO);
            ps.setInt(8, Location);
            ps.setInt(9, 1);
            int num = ps.executeUpdate();
            if (num == 1) {
                LogCommon.WriteLogNormal("������־�쳣", "DownloadFileResult");
            } else {
                LogCommon.WriteLogNormal("������־�쳣", "DownloadFileResult");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LogCommon.WriteLogNormal("������־�쳣", "DownloadFileResult");
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
    }

    @Override
    public boolean uploadFileResult(String fileInfo) {
        try {
            if (fileInfo == null || "".equals(fileInfo.trim())) {
                LogCommon.WriteLogNormal("������ϢΪ��", "taskCenter");
                return false;
            }
            //�����ļ�������Ϣjson�ַ���������
            JSONObject parseObject = JsonUtil.transToUpperObject(fileInfo);
            String fileKey = parseObject.getString("FILEKEY");
            if (isExist(fileKey)) {
                LogCommon.WriteLogNormal("�ò�����Ϣ�Ѵ���,fileKey:" + fileKey, "taskCenter");
                return true;
            }
            String level = GetInit.getInitInfo().getLevel();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (level.equals("10")){
                String fileType = parseObject.getString("FILETYPE");
                int fileOutPush = getType(Integer.parseInt(fileType));
                parseObject.put("FILEDT", sdf.format(new java.util.Date()));
                parseObject.put("FILEOUTPUSH", fileOutPush);
                parseObject.put("ISCREATEFILE", 1);
                parseObject.put("ISCREATETASK", 0);
                return insertUploadTable(parseObject);
            }else {
                parseObject.put("FILEDT", sdf.format(new java.util.Date()));
                parseObject.put("ISCREATEFILE", 0);
                parseObject.put("ISCREATETASK", 0);
                return insertUploadTable(parseObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("������Ϣ�쳣��"+e.getMessage(), "taskCenter");
            LogCommon.WriteLogNormal("������Ϣ==>"+fileInfo, "taskCenter");
        }
        return false;
    }

    @Override
    public boolean downloadFileResult(String downloadMessage) {
        //�ص�����
        //1.�������ĸ��ط�������
        try {
            JSONObject jsonObject = JsonUtil.transToUpperObject(downloadMessage);
            int location = Integer.valueOf(jsonObject.getString("LOCATION"));
            String fileKey = jsonObject.getString("FILEKEY");
            String serverIP = jsonObject.getString("SERVERIP");
            int resultCode = Integer.valueOf(jsonObject.getString("RESULTCODE"));
            String downResult = jsonObject.getString("describe".toLowerCase());
            String downDT = jsonObject.getString("downloadDT".toLowerCase());
            int roadNO = Integer.valueOf(jsonObject.getString("ROADNNO"));
            int staNO = Integer.valueOf(jsonObject.getString("STANO"));
            int portNO = Integer.valueOf(jsonObject.getString("PORTNO"));
            LogCommon.WriteLogNormal("�ص������ص������յ�" + serverIP + "��Ϣ", "DownloadFileResult");
            switch (location) {
                case 1:
                    centerDownloadFileResult(fileKey, serverIP, resultCode, downResult, downDT);
                    break;
                case 2:
                    stanoDownloadFileResult(fileKey, resultCode, downResult, downDT, roadNO, staNO, location);
                    break;
                case 3:
                    portnoDownloadFileResult(fileKey, resultCode, downResult, downDT, roadNO, staNO, portNO, location);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("�ص������쳣��" + e.getMessage(), "DownloadFileResult");
        }
        return true;
    }

    @Override
    public String getNewFileVersion() {
        //���°汾��ѯ
        Connection connection = null;
        Statement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {
            String sql = "select * from FT_UPLOAD_TABLE_CENTER";
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            ps = connection.createStatement();
            rs = ps.executeQuery(sql);
            return JsonUtil.formatRsToJsonArray(rs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
        return null;
    }

    @Override
    public String getAppointFileVersion(int fileType) {
        //��������ָ���汾��ѯ
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {
            String sql = "select * from FT_UPLOAD_TABLE_CENTER where fileType = ?";
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            ps = connection.prepareStatement(sql);
            ps.setInt(1, fileType);
            rs = ps.executeQuery();
            return JsonUtil.formatRsToJsonArray(rs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
        return null;
    }

    @Override
    public String getFileURL(String fileKey) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {

            String sql = "select FILEURL from FT_UPLOAD_TABLE where FILEKEY = ? and ISCREATEFILE =1";
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            ps = connection.prepareStatement(sql);
            ps.setString(1, fileKey);
            rs = ps.executeQuery();
            while (rs.next()) {
                return rs.getString("FILEURL");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
        return null;
    }

}
