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
 * @createTime 2022年03月14日 14:37:00
 */
@Service
@WebService(serviceName = "ParameterService", // 与接口中指定的name一致
        targetNamespace = "http://service.parameter.com/", // 与接口中的命名空间一致,一般是接口的包名倒
        endpointInterface = "com.parameter.service.ParameterService"// 接口地址
)
public class ParameterServiceImpl implements ParameterService {

    //查询文件下发方式
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

    //判断该参数是否存在
    private static boolean isExist(String fileKey) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {

            String sql = "select * from FT_UPLOAD_TABLE where FILEKEY = ?";
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

    //在联网中心创建任务
    public static boolean analysisFile(String fileInfo) {
        try {
            //接收文件基本信息json字符串并解析
            JSONObject parseObject = JsonUtil.transToUpperObject(fileInfo);
            //根据类型去判断自动还是手动
            String fileType = parseObject.getString("FILETYPE");
            String fileKey = parseObject.getString("FILEKEY");
            if (isExist(fileKey)) {
                LogCommon.WriteLogNormal("该参数信息已存在,fileKey:" + fileKey, "taskCenter");
                return true;
            }
            int fileOutPush = getType(Integer.parseInt(fileType));
            parseObject.put("FILEOUTPUSH", fileOutPush);
            parseObject.put("ISCREATEFILE", 1);
            if (fileOutPush == 1) {
                LogCommon.WriteLogNormal("该任务是自动下发任务,fileKey:" + fileKey + " fileType:" + fileType, "taskCenter");
                //自动下发任务
                String stagbID = parseObject.getString("STAGBID");
                if (stagbID == null || "".equals(stagbID)) {
                    LogCommon.WriteLogNormal("该参数没有STAGBID信息，创建所有任务", "taskCenter");
                    List<SubCompany> list = getAllCenter();
                    boolean centerFlag = createCenterTask(parseObject, list);
                    boolean stanoFlag = createStanoTask(parseObject);
                    if (centerFlag && stanoFlag) {
                        insertUploadTable(parseObject);
                        return true;
                    }
                } else {
                    boolean stagbidFlag = createStagbidOneTask(parseObject, stagbID);
                    List<SubCompany> list = new ArrayList<>();
                    SubCompany subCompany = getOneCenter(stagbID);
                    list.add(subCompany);
                    boolean centerFlag = createCenterTask(parseObject, list);
                    if (centerFlag && stagbidFlag) {
                        insertUploadTable(parseObject);
                        return true;
                    }
                }
            } else {
                //手动下发任务
                LogCommon.WriteLogNormal("该任务是手动下发任务,fileKey:" + fileKey + " fileType:" + fileType, "taskCenter");
                boolean manualFlag = createManualTask(fileInfo, fileType);
                if (manualFlag) {
                    insertUploadTable(parseObject);
                    return true;
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("异常信息：" + e.getMessage(), "taskCenter");
        }
        return false;
    }

    //在分中心创建任务
    public static boolean analysisCenterFile(String fileInfo) {
        try {
            //接收文件基本信息json字符串并解析
            JSONObject parseObject = JsonUtil.transToUpperObject(fileInfo);
            String fileType = parseObject.getString("FILETYPE");
            String fileKey = parseObject.getString("FILEKEY");
            if (isExist(fileKey)) {
                LogCommon.WriteLogNormal("该参数信息已存在,fileKey:" + fileKey, "taskCenter");
                return true;
            }
            int fileOutPush = Integer.valueOf(parseObject.getString("FILEOUTPUSH"));
            parseObject.put("ISCREATEFILE", 0);
            if (fileOutPush == 1) {
                LogCommon.WriteLogNormal("该任务是自动下发任务,fileKey:" + fileKey + " fileType:" + fileType, "taskCenter");
                String stagbID = parseObject.getString("STAGBID");
                if (stagbID == null || "".equals(stagbID)) {
                    //自动下发任务
                    boolean stanoFlag = createStanoTask(parseObject);
                    //下发到车道任务
                    boolean portnoFlag = createPortnoTask(parseObject);
                    //插入参数表
                    if (stanoFlag && portnoFlag) {
                        insertUploadTable(parseObject);
                        return true;
                    }
                } else {
                    //插入参数表
                    LogCommon.WriteLogNormal("该参数有国标编码,STAGBID:" + stagbID, "taskCenter");
                    insertUploadTable(parseObject);
                    return true;
                }
            } else {
                //手动下发任务
                LogCommon.WriteLogNormal("该任务是手动下发任务,fileKey:" + fileKey + " fileType:" + fileType, "taskCenter");
                boolean manualFlag = createManualTask(fileInfo, fileType);
                if (manualFlag) {
                    insertUploadTable(parseObject);
                    return true;
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("异常信息：" + e.getMessage(), "taskCenter");
        }
        return false;
    }

    //创建车道任务
    private static boolean createPortnoTask(JSONObject objectPortno) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            //创建站级任务
            LogCommon.WriteLogNormal("开始创建下发到车道任务", "taskCenter");
            List<SetUp> listPortno = getAllPort();
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            connection.setAutoCommit(false);
            for (int i = 0; i < listPortno.size(); i++) {
                objectPortno.put("SERVERIP", listPortno.get(i).getSrvIP());
                objectPortno.put("STANO", listPortno.get(i).getStaNO());
                objectPortno.put("ROADNO", listPortno.get(i).getRoadNO());
                objectPortno.put("PORTNO", listPortno.get(i).getPortNO());
                objectPortno.put("LOCATION", 3);
                objectPortno.put("TASKSTATUS", 2);
                //创建任务id
                String nowDate = "30" + sdf.format(new Date()) + "" + (i + 10);
                objectPortno.put("TASKID", nowDate);
                if (i == 0) {
                    String insertPortno = getInsertSql(objectPortno, "FT_TASK_TABLE");
                    ps = connection.prepareStatement(insertPortno);
                }
                //任务插入任务表
                int index = 0;
                ps.setString(++index, objectPortno.getString("FILEKEY"));
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
            LogCommon.WriteLogNormal("下发到车道任务创建完成", "taskCenter");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("创建下发到车道任务异常：" + e.getMessage(), "taskCenter");
            LogCommon.WriteLogNormal("开始回滚", "taskCenter");
            try {
                connection.rollback();
                LogCommon.WriteLogNormal("回滚结束", "taskCenter");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                LogCommon.WriteLogNormal("回滚异常异常：" + throwables.getMessage(), "taskCenter");
            }
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
        return false;
    }

    //在站创建任务
    public static boolean analysisStanoFile(String fileInfo) {
        try {
            //接收文件基本信息json字符串并解析
            JSONObject parseObject = JsonUtil.transToUpperObject(fileInfo);
            String fileType = parseObject.getString("FILETYPE");
            String fileKey = parseObject.getString("FILEKEY");
            if (isExist(fileKey)) {
                LogCommon.WriteLogNormal("该参数信息已存在,fileKey:" + fileKey, "taskCenter");
                return true;
            }
            int fileOutPush = Integer.valueOf(parseObject.getString("FILEOUTPUSH"));
            parseObject.put("ISCREATEFILE", 0);
            if (fileOutPush == 1) {
                LogCommon.WriteLogNormal("该任务是自动下发任务,fileKey:" + fileKey + " fileType:" + fileType, "taskCenter");
                //自动下发任务
                //下发到车道任务
                boolean portnoFlag = createPortnoTask(parseObject);
                //插入参数表
                if (portnoFlag) {
                    insertUploadTable(parseObject);
                    return true;
                }
            } else {
                //手动下发任务
                LogCommon.WriteLogNormal("该任务是手动下发任务,fileKey:" + fileKey + " fileType:" + fileType, "taskCenter");
                boolean manualFlag = createManualTask(fileInfo, fileType);
                if (manualFlag) {
                    insertUploadTable(parseObject);
                    return true;
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("异常信息：" + e.getMessage(), "taskCenter");
        }
        return false;
    }

    //根据级别实现对应的方式
    public static boolean createTask(String level, String fileInfo) {
        try {
            switch (level) {
                case "10":
                    //当前级别联网中心
                    return analysisFile(fileInfo);
                case "20":
                    //当前级别分中心
                    //创建往站和车道下发任务
                    return analysisCenterFile(fileInfo);
                case "30":
                    //当前级别站
                    //创建往车道下发任务
                    return analysisStanoFile(fileInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //根据STAGBID查询站级信息表
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

    //根据STAGBID查询对应站的分中心信息
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

    //任务创建成功插入参数信息表
    private static void insertUploadTable(JSONObject parseObject) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            parseObject.put("FILEDT", sdf.format(new Date()));
            //基础信息插入基础信息表
            LogCommon.WriteLogNormal("任务表创建完成，将参数信息插入FT_UPLOAD_TABLE表", "taskCenter");
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
            ps.executeUpdate();
            LogCommon.WriteLogNormal("参数信息插入FT_UPLOAD_TABLE表完成", "taskCenter");
        } catch (Exception e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("参数信息插入FT_UPLOAD_TABLE表异常：" + e.getMessage(), "taskCenter");
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
    }

    //获取所有分中心表信息
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

    //获取站表信息
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

    //先根据类型获取要手动下发的站
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

    //获取车道表信息
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

    //创建分中心任务
    private static boolean createCenterTask(JSONObject objectCenter, List<SubCompany> list) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            LogCommon.WriteLogNormal("开始创建下发到分中心任务", "taskCenter");
            //任务插入任务表
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            connection.setAutoCommit(false);
            for (int i = 0; i < list.size(); i++) {
                objectCenter.put("SERVERIP", list.get(i).getSrvIP());
                objectCenter.put("LOCATION", 1);
                objectCenter.put("TASKSTATUS", 2);
                //创建任务id
                String nowDate = "10" + sdf.format(new Date()) + "" + (i + 10);
                objectCenter.put("TASKID", nowDate);
                if (i == 0) {
                    String insertCenter = getInsertSql(objectCenter, "FT_TASK_TABLE");
                    ps = connection.prepareStatement(insertCenter);
                }
                int index = 0;
                ps.setString(++index, objectCenter.getString("FILEKEY"));
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
            LogCommon.WriteLogNormal("下发到分中心任务创建完成", "taskCenter");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("创建下发到分中心任务异常：" + e.getMessage(), "taskCenter");
            LogCommon.WriteLogNormal("开始回滚", "taskCenter");
            try {
                connection.rollback();
                LogCommon.WriteLogNormal("回滚完成", "taskCenter");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                LogCommon.WriteLogNormal("回滚异常：" + throwables.getMessage(), "taskCenter");
            }
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
        return false;
    }

    //拼接插入sql
    private static String getInsertSql(JSONObject jsonObject, String tableName) {
        try {
            StringBuffer sql1 = null;
            StringBuffer sql2 = null;
            sql1.append("if not exists ( ");
            sql1.append("select 1 from " + tableName + " where TASKID = ? ) ");
            sql1.append("insert into " + tableName + " (");
            sql2.append("values(");
            //fastjson解析方法
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

    //创建手动下发任务
    public static boolean createManualTask(String fileInfo, String fileType) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {
            JSONObject parseObject = JsonUtil.transToUpperObject(fileInfo);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            //先根据类型获取要手动下发位置
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
                //创建任务id
                String nowDate = "20" + sdf.format(new Date()) + "" + (i + 10);
                objectManual.put("TASKID", nowDate);
                if (i == 0) {
                    insertManual = getInsertSql(objectManual, "FT_TASK_TABLE");
                    ps = connection.prepareStatement(insertManual);
                }
                //任务插入任务表
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
            LogCommon.WriteLogNormal("手动下发任务创建完成", "taskCenter");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("创建手动下发任务异常：" + e.getMessage(), "taskCenter");
            LogCommon.WriteLogNormal("开始回滚：", "taskCenter");
            try {
                connection.rollback();
                LogCommon.WriteLogNormal("回滚结束", "taskCenter");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                LogCommon.WriteLogNormal("回滚异常：" + throwables.getMessage(), "taskCenter");
            }
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
        return false;
    }

    //创建站级任务
    private static boolean createStanoTask(JSONObject objectStano) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            //创建站级任务
            LogCommon.WriteLogNormal("开始创建下发到站任务", "taskCenter");
            List<Name> listStano = getAllStano();
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            connection.setAutoCommit(false);
            for (int i = 0; i < listStano.size(); i++) {
                objectStano.put("SERVERIP", listStano.get(i).getSrvIP());
                objectStano.put("STANO", listStano.get(i).getStaNO());
                objectStano.put("ROADNO", listStano.get(i).getRoadNO());
                objectStano.put("LOCATION", 2);
                objectStano.put("TASKSTATUS", 2);
                //创建任务id
                String nowDate = "20" + sdf.format(new Date()) + "" + (i + 10);
                objectStano.put("TASKID", nowDate);
                if (i == 0) {
                    String insertStano = getInsertSql(objectStano, "FT_TASK_TABLE");
                    ps = connection.prepareStatement(insertStano);
                }
                //任务插入任务表
                int index = 0;
                ps.setString(++index, objectStano.getString("FILEKEY"));
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
            LogCommon.WriteLogNormal("下发到站级任务创建完成", "taskCenter");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("创建下发到站任务异常：" + e.getMessage(), "taskCenter");
            LogCommon.WriteLogNormal("开始回滚", "taskCenter");
            try {
                connection.rollback();
                LogCommon.WriteLogNormal("回滚结束", "taskCenter");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                LogCommon.WriteLogNormal("回滚异常：" + throwables.getMessage(), "taskCenter");
            }
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
        return false;
    }

    //根据国标创建具体一个站级任务
    private static boolean createStagbidOneTask(JSONObject jsonObjectTask, String stagbID) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            //查询站级信息表创建任务
            LogCommon.WriteLogNormal("根据STAGBID创建下发到对应站任务", "taskCenter");
            Name name = getOneStano(stagbID);
            if (name == null) {
                LogCommon.WriteLogNormal("站级信息表该STAGBID不存在", "taskCenter");
            } else {
                jsonObjectTask.put("SERVERIP", name.getSrvIP());
                jsonObjectTask.put("ROADNO", name.getRoadNO());
                jsonObjectTask.put("STANO", name.getStaNO());
                jsonObjectTask.put("LOCATION", 2);
                jsonObjectTask.put("TASKSTATUS", 2);
                //创建任务id
                String nowDate = "20" + sdf.format(new Date()) + "11";
                jsonObjectTask.put("TASKID", nowDate);
                //拼接插入sql
                StringBuffer sql1 = null;
                StringBuffer sql2 = null;
                sql1.append("if not exists ( ");
                sql1.append("select 1 from FT_TASK_TABLE where FILEKEY = ? AND SERVERIP = ? )");
                sql1.append("insert into FT_TASK_TABLE (");
                sql2.append("values(");
                //fastjson解析方法
                for (Map.Entry<String, Object> entry : jsonObjectTask.entrySet()) {
                    sql1.append(entry.getKey() + ",");
                    sql2.append("?,");
                }
                sql1.replace(sql1.lastIndexOf(","), sql1.lastIndexOf(",") + 1, ") ");
                sql2.replace(sql2.lastIndexOf(","), sql2.lastIndexOf(",") + 1, ")");
                String TaskSql = sql1.append(sql2).toString();
                //任务插入任务表
                database = DataBaseUtil.getDataBase(0);
                connection = DataBaseUtil.getConn(database);
                ps.clearParameters();
                ps = connection.prepareStatement(TaskSql);
                int index = 0;
                ps.setString(++index, jsonObjectTask.getString("FILEKEY"));
                ps.setString(++index, jsonObjectTask.getString("SERVERIP"));
                for (Map.Entry<String, Object> entry : jsonObjectTask.entrySet()) {
                    if (entry.getValue() == null || "".equals(entry.getValue())) {
                        ps.setNull(++index, Types.CHAR);
                    } else {
                        ps.setString(++index, entry.getValue().toString());
                    }
                }
                ps.executeUpdate();
            }
            LogCommon.WriteLogNormal("根据国标下发到站级任务创建完成", "taskCenter");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("根据国标创建下发到站任务异常：" + e.getMessage(), "taskCenter");
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
        return false;
    }

    //分中心调用回调函数
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
                LogCommon.WriteLogNormal("更新日志异常", "DownloadFileResult");
            } else {
                LogCommon.WriteLogNormal("更新日志异常", "DownloadFileResult");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LogCommon.WriteLogNormal("更新日志异常", "DownloadFileResult");
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
    }

    //站级调用回调函数
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
                LogCommon.WriteLogNormal("更新日志异常", "DownloadFileResult");
            } else {
                LogCommon.WriteLogNormal("更新日志异常", "DownloadFileResult");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LogCommon.WriteLogNormal("更新日志异常", "DownloadFileResult");
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
    }

    //车道调用回调函数
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
                LogCommon.WriteLogNormal("更新日志异常", "DownloadFileResult");
            } else {
                LogCommon.WriteLogNormal("更新日志异常", "DownloadFileResult");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LogCommon.WriteLogNormal("更新日志异常", "DownloadFileResult");
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
            //根据配置文件获取当前程序所属级别 10联网中心 20分中心 30站
            String level = GetInit.getInitInfo().getLevel();
            //根据级别执行程序
            flag = createTask(level, fileInfo);
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public boolean downloadFileResult(String downloadMessage) {
        //回调函数
        //1.解析是哪个地方回来的
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
            LogCommon.WriteLogNormal("回调函数回调函数收到" + serverIP + "信息", "DownloadFileResult");
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
            LogCommon.WriteLogNormal("回调函数异常：" + e.getMessage(), "DownloadFileResult");
        }
        return true;
    }

    @Override
    public String getNewFileVersion() {
        //最新版本查询
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
        //根据类型指定版本查询
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
