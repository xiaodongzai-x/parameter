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
import java.util.ArrayList;
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
    private static boolean isExist(String fileKey){
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database =null;
        try {

            String sql = "select * from FT_UPLOAD_TABLE where FILEKEY = ?";
            database =DataBaseUtil.getDataBase(0);
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

    //���������Ĵ�������
    public static boolean analysisFile(String fileInfo,String level) {
        try {
            //�����ļ�������Ϣjson�ַ���������
            JSONObject parseObject = JsonUtil.transToUpperObject(fileInfo);
            //��������ȥ�ж��Զ������ֶ�
            String fileType = parseObject.getString("FILETYPE");
            String fileKey = parseObject.getString("FILEKEY");
            if(isExist(fileKey)){
                LogCommon.WriteLogNormal("�ò�����Ϣ�Ѵ���,fileKey:" + fileKey , "taskCenter");
                return true;
            }
            int fileOutPush = getType(Integer.parseInt(fileType));
            parseObject.put("FILEOUTPUSH", fileOutPush);
            parseObject.put("ISCREATEFILE", 1);
            if (fileOutPush == 1) {
                LogCommon.WriteLogNormal("���������Զ��·�����,fileKey:" + fileKey + " fileType:" + fileType, "taskCenter");
                //�Զ��·�����
                String stagbID = parseObject.getString("STAGBID");
                if (stagbID == null || "".equals(stagbID)) {
                    LogCommon.WriteLogNormal("�ò���û��STAGBID��Ϣ��������������", "taskCenter");
                    List<SubCompany> list = getAllCenter();
                    boolean centerFlag = createCenterTask(parseObject.toJSONString(),list,level);
                    boolean stanoFlag =createStanoTask(parseObject.toJSONString(),level);
                    if(centerFlag&&stanoFlag){
                        insertUploadTable(parseObject.toJSONString());
                        return true;
                    }
                } else {
                    boolean stagbidFlag = createStagbidOneTask(parseObject.toJSONString(), stagbID,level);
                    List<SubCompany> list = new ArrayList<>();
                    SubCompany subCompany = getOneCenter(stagbID);
                    list.add(subCompany);
                    boolean centerFlag = createCenterTask(parseObject.toJSONString(),list,level);
                    if(centerFlag&&stagbidFlag){
                        insertUploadTable(parseObject.toJSONString());
                        return true;
                    }
                }
            } else {
                //�ֶ��·�����
                LogCommon.WriteLogNormal("���������ֶ��·�����,fileKey:" + fileKey + " fileType:" + fileType, "taskCenter");
                boolean manualFlag = createManualTask(fileInfo, fileType,level);
                if(manualFlag){
                    insertUploadTable(parseObject.toJSONString());
                    return true;
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("�쳣��Ϣ��" + e.getMessage(), "taskCenter");
        }
        return false;
    }

    //�ڷ����Ĵ�������
    public static boolean analysisCenterFile(String fileInfo,String level) {
        try {
            //�����ļ�������Ϣjson�ַ���������
            JSONObject parseObject = JsonUtil.transToUpperObject(fileInfo);
            String fileType = parseObject.getString("FILETYPE");
            String fileKey = parseObject.getString("FILEKEY");
            if(isExist(fileKey)){
                LogCommon.WriteLogNormal("�ò�����Ϣ�Ѵ���,fileKey:" + fileKey , "taskCenter");
                return true;
            }
            int fileOutPush = Integer.valueOf(parseObject.getString("FILEOUTPUSH"));
            parseObject.put("ISCREATEFILE", 0);
            if (fileOutPush == 1) {
                LogCommon.WriteLogNormal("���������Զ��·�����,fileKey:" + fileKey + " fileType:" + fileType, "taskCenter");
                String stagbID = parseObject.getString("STAGBID");
                if (stagbID == null || "".equals(stagbID)) {
                    //�Զ��·�����
                    boolean stanoFlag = createStanoTask(parseObject.toJSONString(),level);
                    //�·�����������
                    boolean portnoFlag = createPortnoTask(parseObject.toJSONString(),level);
                    //���������
                    if(stanoFlag&&portnoFlag){
                        insertUploadTable(parseObject.toJSONString());
                        return true;
                    }
                }else {
                    //���������
                    LogCommon.WriteLogNormal("�ò����й������,STAGBID:" + stagbID, "taskCenter");
                    insertUploadTable(parseObject.toJSONString());
                    return true;
                }
            } else {
                //�ֶ��·�����
                LogCommon.WriteLogNormal("���������ֶ��·�����,fileKey:" + fileKey + " fileType:" + fileType, "taskCenter");
                boolean manualFlag = createManualTask(fileInfo, fileType,level);
                if(manualFlag){
                    insertUploadTable(parseObject.toJSONString());
                    return true;
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("�쳣��Ϣ��" + e.getMessage(), "taskCenter");
        }
        return false;
    }

    //������������
    private static boolean createPortnoTask(String fileInfo,String level) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database =null;
        try {
            JSONObject parseObject = JsonUtil.transToUpperObject(fileInfo);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            //����վ������
            LogCommon.WriteLogNormal("��ʼ�����·�����������", "taskCenter");
            List<SetUp> listPortno = getAllPort();
            String insertPortno = null;
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            connection.setAutoCommit(false);
            for (int i = 0; i < listPortno.size(); i++) {
                JSONObject objectPortno = parseObject;
                objectPortno.put("SERVERIP", listPortno.get(i).getSrvIP());
                objectPortno.put("STANO", listPortno.get(i).getStaNO());
                objectPortno.put("ROADNO", listPortno.get(i).getRoadNO());
                objectPortno.put("PORTNO", listPortno.get(i).getPortNO());
                objectPortno.put("LOCATION", 3);
                objectPortno.put("TASKSTATUS", 2);
                //��������id
                String nowDate = level + sdf.format(new Date()) + "" + (i + 10);
                objectPortno.put("TASKID", nowDate);
                if (i == 0) {
                    //ƴ�Ӳ���sql
                    StringBuffer sql1 = null;
                    StringBuffer sql2 = null;
                    sql1.append("if not exists ( ");
                    sql1.append("select 1 from FT_TASK_TABLE where FILEKEY = ? )");
                    sql1.append("insert into FT_TASK_TABLE (");
                    sql2.append("values(");
                    //fastjson��������
                    for (Map.Entry<String, Object> entry : objectPortno.entrySet()) {
                        sql1.append(entry.getKey() + ",");
                        sql2.append("?,");
                    }
                    sql1.replace(sql1.lastIndexOf(","), sql1.lastIndexOf(",") + 1, ") ");
                    sql2.replace(sql2.lastIndexOf(","), sql2.lastIndexOf(",") + 1, ")");
                    insertPortno = sql1.append(sql2).toString();
                    ps = connection.prepareStatement(insertPortno);
                }
                //������������
                int index = 0;
                ps.setString(++index, parseObject.getString("FILEKEY"));
                for (Map.Entry<String, Object> entry : objectPortno.entrySet()) {
                    if (entry.getValue() == null || "".equals(entry.getValue())) {
                        ps.setNull(++index, Types.CHAR);
                    } else {
                        ps.setString(++index, entry.getValue().toString());
                    }
                }
                ps.addBatch();
            }
            ps.executeBatch();
            connection.commit();
            ps.clearBatch();
            LogCommon.WriteLogNormal("�·����������񴴽����", "taskCenter");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("�����·������������쳣��" + e.getMessage(), "taskCenter");
            LogCommon.WriteLogNormal("��ʼ�ع�" , "taskCenter");
            try {
                connection.rollback();
                LogCommon.WriteLogNormal("�ع�����" , "taskCenter");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                LogCommon.WriteLogNormal("�ع��쳣�쳣��" + throwables.getMessage(), "taskCenter");
            }
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
        return false;
    }

    //��վ��������
    public static boolean analysisStanoFile(String fileInfo,String level) {
        try {
            //�����ļ�������Ϣjson�ַ���������
            JSONObject parseObject = JsonUtil.transToUpperObject(fileInfo);
            String fileType = parseObject.getString("FILETYPE");
            String fileKey = parseObject.getString("FILEKEY");
            if(isExist(fileKey)){
                LogCommon.WriteLogNormal("�ò�����Ϣ�Ѵ���,fileKey:" + fileKey , "taskCenter");
                return true;
            }
            int fileOutPush = Integer.valueOf(parseObject.getString("FILEOUTPUSH"));
            parseObject.put("ISCREATEFILE", 0);
            if (fileOutPush == 1) {
                LogCommon.WriteLogNormal("���������Զ��·�����,fileKey:" + fileKey + " fileType:" + fileType, "taskCenter");
                //�Զ��·�����
                //�·�����������
                boolean portnoFlag = createPortnoTask(parseObject.toJSONString(),level);
                //���������
                if(portnoFlag){
                    insertUploadTable(parseObject.toJSONString());
                    return true;
                }
            } else {
                //�ֶ��·�����
                LogCommon.WriteLogNormal("���������ֶ��·�����,fileKey:" + fileKey + " fileType:" + fileType, "taskCenter");
                boolean manualFlag = createManualTask(fileInfo, fileType,level);
                if(manualFlag){
                    insertUploadTable(parseObject.toJSONString());
                    return true;
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("�쳣��Ϣ��" + e.getMessage(), "taskCenter");
        }
        return false;
    }

    //���ݼ���ʵ�ֶ�Ӧ�ķ�ʽ
    public static boolean createTask(String level, String fileInfo) {
        try {
            switch (level) {
                case "10":
                    //��ǰ������������
                    return analysisFile(fileInfo,level);
                case "20":
                    //��ǰ���������
                    //������վ�ͳ����·�����
                    return analysisCenterFile(fileInfo,level);
                case "30":
                    //��ǰ����վ
                    //�����������·�����
                    return analysisStanoFile(fileInfo,level);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //����STAGBID��ѯվ����Ϣ��
    public static Name getOneStano(String stagbID) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database = null;
        Name name = null;
        try {
            String sql = "select * from FT_NAME_TABLE where STAGBID = ?";
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            ps = connection.prepareStatement(sql);
            ps.setString(1, stagbID);
            rs = ps.executeQuery();
            while (rs.next()) {
                name = new Name();
                name.setSrvIP(rs.getString("SRVIP"));
                name.setSubComNO(rs.getInt("SUBCOMNO"));
                name.setRoadNO(rs.getInt("ROADNO"));
                name.setStaNO(rs.getInt("STANO"));
                name.setStanoName(rs.getString("STANAME"));
                name.setStagbID(rs.getString("STAGBID"));
            }
            return name;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
        return name;

    }

    //����STAGBID��ѯ��Ӧվ�ķ�������Ϣ
    public static SubCompany getOneCenter(String stagbID) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database = null;
        SubCompany subCompany = null;
        try {
            String sql = "SELECT s.SRVIP FROM ft_subcompany_table s WHERE s.SUBCOMNO = (SELECT SUBCOMNO FROM ft_name_table WHERE STAGBID = ? )";
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            ps = connection.prepareStatement(sql);
            ps.setString(1, stagbID);
            rs = ps.executeQuery();
            while (rs.next()) {
                subCompany = new SubCompany();
                subCompany.setSrvIP(rs.getString("SRVIP"));
            }
            return subCompany;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
        return subCompany;

    }

    //���񴴽��ɹ����������Ϣ��
    public static void insertUploadTable(String fileInfo) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {
            JSONObject parseObject = JsonUtil.transToUpperObject(fileInfo);
            //������Ϣ���������Ϣ��
            LogCommon.WriteLogNormal("���������ɣ���������Ϣ����FT_UPLOAD_TABLE��", "taskCenter");
            StringBuffer sql1 = null;
            StringBuffer sql2 = null;
            sql1.append("if not exists ( ");
            sql1.append("select 1 from FT_UPLOAD_TABLE where FILEKEY = ?)");
            sql1.append("insert into FT_UPLOAD_TABLE (");
            sql2.append("values(");
            //fastjson��������
            for (Map.Entry<String, Object> entry : parseObject.entrySet()) {
                sql1.append(entry.getKey() + ",");
                sql2.append("?,");
            }
            sql1.replace(sql1.lastIndexOf(","), sql1.lastIndexOf(",") + 1, ") ");
            sql2.replace(sql2.lastIndexOf(","), sql2.lastIndexOf(",") + 1, ")");
            String fileInfoSql = sql1.append(sql2).toString();
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
            ps.executeUpdate();
            LogCommon.WriteLogNormal("������Ϣ����FT_UPLOAD_TABLE�����", "taskCenter");
        } catch (Exception e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("������Ϣ����FT_UPLOAD_TABLE���쳣��" + e.getMessage(), "taskCenter");
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
    }

    //��ȡ���з����ı���Ϣ
    public static List<SubCompany> getAllCenter() {
        List<SubCompany> list = null;
        Connection connection = null;
        Statement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {
            String sql = "select * from FT_SUBCOMPANY_TABLE ";
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            ps = connection.createStatement();
            rs = ps.executeQuery(sql);
            while (rs.next()) {
                SubCompany subCompany = new SubCompany();
                subCompany.setSrvIP(rs.getString("SRVIP"));
                list.add(subCompany);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
        return list;
    }

    //��ȡվ����Ϣ
    public static List<Name> getAllStano() {
        List<Name> list = null;
        Connection connection = null;
        Statement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {
            String sql = "select * from FT_NAME_TABLE ";
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            ps = connection.createStatement();
            rs = ps.executeQuery(sql);
            while (rs.next()) {
                Name name = new Name();
                name.setSrvIP(rs.getString("SRVIP"));
                name.setRoadNO(rs.getInt("ROADNO"));
                name.setStaNO(rs.getInt("STANO"));
                list.add(name);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
        return list;
    }

    //�ȸ������ͻ�ȡҪ�ֶ��·���վ
    public static List<Manual> getManualAll(int fileType) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database = null;
        List<Manual> list = null;
        try {
            String sql = "select * from FT_MANUAL_TABLE where STAGBID = ?";
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            ps = connection.prepareStatement(sql);
            ps.setInt(1, fileType);
            rs = ps.executeQuery();
            while (rs.next()) {
                Manual manual = new Manual();
                manual.setFileManualNo(rs.getString("FILEMANUALNO"));
                manual.setServerIP(rs.getString("SERVERIP"));
                manual.setFileType(rs.getInt("FILETYPE"));
                manual.setRoadNo(rs.getInt("ROADNO"));
                manual.setStano(rs.getInt("STANO"));
                manual.setPortNo(rs.getInt("PORTNO"));
                manual.setLocation(rs.getInt("LOCATION"));
                list.add(manual);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
        return list;

    }

    //��ȡ��������Ϣ
    public static List<SetUp> getAllPort() {
        List<SetUp> list = null;
        Connection connection = null;
        Statement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {
            String sql = "select * from FT_SETUP_TABLE ";
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            ps = connection.createStatement();
            rs = ps.executeQuery(sql);
            while (rs.next()) {
                SetUp setUp = new SetUp();
                setUp.setSrvIP(rs.getString("SRVIP"));
                setUp.setRoadNO(rs.getInt("ROADNO"));
                setUp.setStaNO(rs.getInt("STANO"));
                setUp.setPortNO(rs.getInt("PORTNO"));
                list.add(setUp);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
        return list;
    }

    //��������������
    public static boolean createCenterTask(String fileInfo,List<SubCompany> list,String level) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {
            JSONObject parseObject = JsonUtil.transToUpperObject(fileInfo);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            LogCommon.WriteLogNormal("��ʼ�����·�������������", "taskCenter");
            String insertCenter = null;
            //������������
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            connection.setAutoCommit(false);
            for (int i = 0; i < list.size(); i++) {
                JSONObject objectCenter = parseObject;
                objectCenter.put("SERVERIP", list.get(i).getSrvIP());
                objectCenter.put("LOCATION", 1);
                objectCenter.put("TASKSTATUS", 2);
                //��������id
                String nowDate = level + sdf.format(new Date()) + "" + (i + 10);
                objectCenter.put("TASKID", nowDate);
                if (i == 0) {
                    //ƴ�Ӳ���sql
                    StringBuffer sql1 = null;
                    StringBuffer sql2 = null;
                    sql1.append("if not exists ( ");
                    sql1.append("select 1 from FT_TASK_TABLE where FILEKEY = ? ) ");
                    sql1.append("insert into FT_TASK_TABLE (");
                    sql2.append("values(");
                    //fastjson��������
                    for (Map.Entry<String, Object> entry : objectCenter.entrySet()) {
                        sql1.append(entry.getKey() + ",");
                        sql2.append("?,");
                    }
                    sql1.replace(sql1.lastIndexOf(","), sql1.lastIndexOf(",") + 1, ") ");
                    sql2.replace(sql2.lastIndexOf(","), sql2.lastIndexOf(",") + 1, ")");
                    insertCenter = sql1.append(sql2).toString();
                    ps = connection.prepareStatement(insertCenter);
                }
                int index = 0;
                ps.setString(++index, parseObject.getString("FILEKEY"));
                for (Map.Entry<String, Object> entry : objectCenter.entrySet()) {
                    if (entry.getValue() == null || "".equals(entry.getValue())) {
                        ps.setNull(++index, Types.CHAR);
                    } else {
                        ps.setString(++index, entry.getValue().toString());
                    }
                }
                ps.addBatch();
            }
            ps.executeBatch();
            connection.commit();
            ps.clearBatch();
            LogCommon.WriteLogNormal("�·������������񴴽����", "taskCenter");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("�����·��������������쳣��" + e.getMessage(), "taskCenter");
            LogCommon.WriteLogNormal("��ʼ�ع�" , "taskCenter");
            try {
                connection.rollback();
                LogCommon.WriteLogNormal("�ع����" , "taskCenter");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                LogCommon.WriteLogNormal("�ع��쳣��" + throwables.getMessage(), "taskCenter");
            }
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
        return false;
    }

    //�����ֶ��·�����
    public static boolean createManualTask(String fileInfo, String fileType,String level) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {
            JSONObject parseObject = JsonUtil.transToUpperObject(fileInfo);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            //�ȸ������ͻ�ȡҪ�ֶ��·�λ��
            List<Manual> listManual = getManualAll(Integer.parseInt(fileType));
            String insertManual = null;
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            connection.setAutoCommit(false);
            for (int i = 0; i < listManual.size(); i++) {
                JSONObject objectManual = parseObject;
                objectManual.put("SERVERIP", listManual.get(i).getServerIP());
                objectManual.put("STANO", listManual.get(i).getStano());
                objectManual.put("ROADNO", listManual.get(i).getPortNo());
                objectManual.put("PORTNO", listManual.get(i).getPortNo());
                objectManual.put("LOCATION", listManual.get(i).getLocation());
                objectManual.put("TASKSTATUS", 2);
                //��������id
                String nowDate = level + sdf.format(new Date()) + "" + (i + 10);
                objectManual.put("TASKID", nowDate);
                if (i == 0) {
                    //ƴ�Ӳ���sql
                    StringBuffer sql1 = null;
                    StringBuffer sql2 = null;
                    sql1.append("if not exists ( ");
                    sql1.append("select 1 from FT_TASK_TABLE where FILEKEY = ? )");
                    sql1.append("insert into FT_TASK_TABLE (");
                    sql2.append("values(");
                    //fastjson��������
                    for (Map.Entry<String, Object> entry : objectManual.entrySet()) {
                        sql1.append(entry.getKey() + ",");
                        sql2.append("?,");
                    }
                    sql1.replace(sql1.lastIndexOf(","), sql1.lastIndexOf(",") + 1, ") ");
                    sql2.replace(sql2.lastIndexOf(","), sql2.lastIndexOf(",") + 1, ")");
                    insertManual = sql1.append(sql2).toString();
                    ps = connection.prepareStatement(insertManual);
                }
                //������������
                int index = 0;
                ps.setString(++index, parseObject.getString("FILEKEY"));
                for (Map.Entry<String, Object> entry : objectManual.entrySet()) {
                    if (entry.getValue() == null || "".equals(entry.getValue())) {
                        ps.setNull(++index, Types.CHAR);
                    } else {
                        ps.setString(++index, entry.getValue().toString());
                    }
                }
                ps.addBatch();
            }
            ps.executeBatch();
            connection.commit();
            ps.clearBatch();
            LogCommon.WriteLogNormal("�ֶ��·����񴴽����", "taskCenter");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("�����ֶ��·������쳣��" + e.getMessage(), "taskCenter");
            LogCommon.WriteLogNormal("��ʼ�ع���", "taskCenter");
            try {
                connection.rollback();
                LogCommon.WriteLogNormal("�ع�����" , "taskCenter");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                LogCommon.WriteLogNormal("�ع��쳣��" + throwables.getMessage(), "taskCenter");
            }
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
        return false;
    }

    //����վ������
    public static boolean createStanoTask(String fileInfo,String level) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {
            JSONObject parseObject = JsonUtil.transToUpperObject(fileInfo);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            //����վ������
            LogCommon.WriteLogNormal("��ʼ�����·���վ����", "taskCenter");
            List<Name> listStano = getAllStano();
            String insertStano = null;
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            connection.setAutoCommit(false);
            for (int i = 0; i < listStano.size(); i++) {
                JSONObject objectStano = parseObject;
                objectStano.put("SERVERIP", listStano.get(i).getSrvIP());
                objectStano.put("STANO", listStano.get(i).getStaNO());
                objectStano.put("ROADNO", listStano.get(i).getRoadNO());
                objectStano.put("LOCATION", 2);
                objectStano.put("TASKSTATUS", 2);
                //��������id
                String nowDate = level+ sdf.format(new Date()) + "" + (i + 10);
                objectStano.put("TASKID", nowDate);
                if (i == 0) {
                    //ƴ�Ӳ���sql
                    StringBuffer sql1 = null;
                    StringBuffer sql2 = null;
                    sql1.append("if not exists ( ");
                    sql1.append("select 1 from FT_TASK_TABLE where FILEKEY = ? )");
                    sql1.append("insert into FT_TASK_TABLE (");
                    sql2.append("values(");
                    //fastjson��������
                    for (Map.Entry<String, Object> entry : objectStano.entrySet()) {
                        sql1.append(entry.getKey() + ",");
                        sql2.append("?,");
                    }
                    sql1.replace(sql1.lastIndexOf(","), sql1.lastIndexOf(",") + 1, ") ");
                    sql2.replace(sql2.lastIndexOf(","), sql2.lastIndexOf(",") + 1, ")");
                    insertStano = sql1.append(sql2).toString();
                    ps = connection.prepareStatement(insertStano);
                }
                //������������
                int index = 0;
                ps.setString(++index, parseObject.getString("FILEKEY"));
                for (Map.Entry<String, Object> entry : objectStano.entrySet()) {
                    if (entry.getValue() == null || "".equals(entry.getValue())) {
                        ps.setNull(++index, Types.CHAR);
                    } else {
                        ps.setString(++index, entry.getValue().toString());
                    }
                }
                ps.addBatch();
            }
            ps.executeBatch();
            connection.commit();
            ps.clearBatch();
            LogCommon.WriteLogNormal("�·���վ�����񴴽����", "taskCenter");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("�����·���վ�����쳣��" + e.getMessage(), "taskCenter");
            LogCommon.WriteLogNormal("��ʼ�ع�", "taskCenter");
            try {
                connection.rollback();
                LogCommon.WriteLogNormal("�ع�����", "taskCenter");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                LogCommon.WriteLogNormal("�ع��쳣��" + throwables.getMessage(), "taskCenter");
            }
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
        return false;
    }

    //���ݹ��괴������һ��վ������
    public static boolean createStagbidOneTask(String fileInfo, String stagbID,String level) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {
            JSONObject parseObject = JsonUtil.transToUpperObject(fileInfo);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            //��ѯվ����Ϣ��������
            LogCommon.WriteLogNormal("����STAGBID�����·�����Ӧվ����", "taskCenter");
            JSONObject jsonObjectTask = parseObject;
            Name name = getOneStano(stagbID);
            if (name == null) {
                LogCommon.WriteLogNormal("վ����Ϣ���STAGBID������", "taskCenter");
            } else {
                jsonObjectTask.put("SERVERIP", name.getSrvIP());
                jsonObjectTask.put("ROADNO", name.getRoadNO());
                jsonObjectTask.put("STANO", name.getStaNO());
                jsonObjectTask.put("LOCATION", 2);
                jsonObjectTask.put("TASKSTATUS", 2);
                //��������id
                String nowDate = level + sdf.format(new Date()) + "11";
                jsonObjectTask.put("TASKID", nowDate);
                //ƴ�Ӳ���sql
                StringBuffer sql1 = null;
                StringBuffer sql2 = null;
                sql1.append("if not exists ( ");
                sql1.append("select 1 from FT_TASK_TABLE where FILEKEY = ? AND SERVERIP = ? )");
                sql1.append("insert into FT_TASK_TABLE (");
                sql2.append("values(");
                //fastjson��������
                for (Map.Entry<String, Object> entry : jsonObjectTask.entrySet()) {
                    sql1.append(entry.getKey() + ",");
                    sql2.append("?,");
                }
                sql1.replace(sql1.lastIndexOf(","), sql1.lastIndexOf(",") + 1, ") ");
                sql2.replace(sql2.lastIndexOf(","), sql2.lastIndexOf(",") + 1, ")");
                String TaskSql = sql1.append(sql2).toString();
                //������������
                database = DataBaseUtil.getDataBase(0);
                connection = DataBaseUtil.getConn(database);
                ps.clearParameters();
                ps = connection.prepareStatement(TaskSql);
                int index = 0;
                ps.setString(++index, parseObject.getString("FILEKEY"));
                ps.setString(++index, parseObject.getString("SERVERIP"));
                for (Map.Entry<String, Object> entry : jsonObjectTask.entrySet()) {
                    if (entry.getValue() == null || "".equals(entry.getValue())) {
                        ps.setNull(++index, Types.CHAR);
                    } else {
                        ps.setString(++index, entry.getValue().toString());
                    }
                }
                ps.executeUpdate();
            }
            LogCommon.WriteLogNormal("���ݹ����·���վ�����񴴽����", "taskCenter");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("���ݹ��괴���·���վ�����쳣��" + e.getMessage(), "taskCenter");
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
        return false;
    }

    //�����ĵ��ûص�����
    private static void centerDownloadFileResult(String fileKey,String serverIP,int resultCode,String downResult,String downDT){
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {
            String sql = "update FT_TASK_LOG_TABLE set DOWNLOADFILESTATUS=?,DOWNLOADFILERESULT=?,DOWNLOADFILEDT=? where FILEKEY=? and SERVERIP=? and INSTRISSUESTATUS=?";
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            ps = connection.prepareStatement(sql);
            ps.setInt(1,resultCode);
            ps.setString(2,downResult);
            ps.setString(3,downDT);
            ps.setString(4,fileKey);
            ps.setString(5,serverIP);
            ps.setInt(6,1);
            int num = ps.executeUpdate();
            if(num==1){
                LogCommon.WriteLogNormal("������־�쳣","DownloadFileResult");
            }else {
                LogCommon.WriteLogNormal("������־�쳣","DownloadFileResult");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LogCommon.WriteLogNormal("������־�쳣","DownloadFileResult");
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
    }
    //վ�����ûص�����
    private static void stanoDownloadFileResult(String fileKey,int resultCode,String downResult,String downDT,int roadNO,int staNO,int Location){
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {
            String sql = "update FT_TASK_LOG_TABLE set DOWNLOADFILESTATUS=?,DOWNLOADFILERESULT=?,DOWNLOADFILEDT=? where FILEKEY=? and ROADNO=? AND STANO=? AND LOCATION=? and INSTRISSUESTATUS=?";
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            ps = connection.prepareStatement(sql);
            ps.setInt(1,resultCode);
            ps.setString(2,downResult);
            ps.setString(3,downDT);
            ps.setString(4,fileKey);
            ps.setInt(5,roadNO);
            ps.setInt(6,staNO);
            ps.setInt(7,Location);
            ps.setInt(8,1);
            int num = ps.executeUpdate();
            if(num==1){
                LogCommon.WriteLogNormal("������־�쳣","DownloadFileResult");
            }else {
                LogCommon.WriteLogNormal("������־�쳣","DownloadFileResult");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LogCommon.WriteLogNormal("������־�쳣","DownloadFileResult");
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
    }
    //�������ûص�����
    private static void portnoDownloadFileResult(String fileKey,int resultCode,String downResult,String downDT,int roadNO,int staNO,int portNO,int Location){
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {
            String sql = "update FT_TASK_LOG_TABLE set DOWNLOADFILESTATUS=?,DOWNLOADFILERESULT=?,DOWNLOADFILEDT=? where FILEKEY=? and ROADNO=? AND STANO=? and PORTNO = ? AND LOCATION=? and INSTRISSUESTATUS=?";
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            ps = connection.prepareStatement(sql);
            ps.setInt(1,resultCode);
            ps.setString(2,downResult);
            ps.setString(3,downDT);
            ps.setString(4,fileKey);
            ps.setInt(5,roadNO);
            ps.setInt(6,staNO);
            ps.setInt(7,portNO);
            ps.setInt(8,Location);
            ps.setInt(9,1);
            int num = ps.executeUpdate();
            if(num==1){
                LogCommon.WriteLogNormal("������־�쳣","DownloadFileResult");
            }else {
                LogCommon.WriteLogNormal("������־�쳣","DownloadFileResult");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LogCommon.WriteLogNormal("������־�쳣","DownloadFileResult");
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
    }
    @Override
    public boolean uploadFileResult(String fileInfo) {
        boolean flag = false;
        try {
            if (fileInfo == null || "".equals(fileInfo.trim())) {
                return false;
            }
            //���������ļ���ȡ��ǰ������������ 10�������� 20������ 30վ
            String level = GetInit.getInitInfo().getLevel();
            //���ݼ���ִ�г���
            flag = createTask(level, fileInfo);
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
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
            LogCommon.WriteLogNormal("�ص������ص������յ�"+serverIP+"��Ϣ","DownloadFileResult");
            switch (location){
                case 1:
                    centerDownloadFileResult(fileKey,serverIP,resultCode,downResult,downDT);
                    break;
                case 2:
                    stanoDownloadFileResult(fileKey,resultCode,downResult,downDT,roadNO,staNO,location);
                    break;
                case 3:
                    portnoDownloadFileResult(fileKey,resultCode,downResult,downDT,roadNO,staNO,portNO,location);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("�ص������쳣��"+e.getMessage(),"DownloadFileResult");
        }
        //2.�����־
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
            database =DataBaseUtil.getDataBase(0);
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

}
