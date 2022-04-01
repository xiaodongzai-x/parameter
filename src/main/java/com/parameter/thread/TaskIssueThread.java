package com.parameter.thread;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.parameter.client.ParameterService_Service;
import com.parameter.entity.Database;
import com.parameter.entity.InitInfo;
import com.parameter.impl.SocketClientImpl;
import com.parameter.init.GetInit;
import com.parameter.tools.DataBaseUtil;
import com.parameter.tools.JsonUtil;
import com.parameter.tools.LogCommon;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author xiaodong
 * @version 1.0.0
 * @ClassName TaskIssueThread.java
 * @Description 线程遍历任务表, 下发任务
 * @createTime 2022年03月17日 08:52:00
 */
public class TaskIssueThread implements Runnable {
    //获取parameterJson
    private static String getParameterJson(JSONObject jsonObject) {
        JSONObject parameterJson = null;
        try {
            parameterJson = new JSONObject();
            parameterJson.put("FILEKEY", jsonObject.getString("FILEKEY"));
            parameterJson.put("FILENAME", jsonObject.getString("FILENAME"));
            parameterJson.put("FILETYPE", jsonObject.getIntValue("FILETYPE"));
            parameterJson.put("FILESIZE", jsonObject.getLongValue("FILESIZE"));
            parameterJson.put("FILEMD5", jsonObject.getString("FILEMD5"));
            parameterJson.put("FILEURL", jsonObject.getString("FILEURL"));
            parameterJson.put("FILEVERSION", jsonObject.getString("FILEVERSION"));
            parameterJson.put("FILEOUTPUSH", jsonObject.getIntValue("FILEOUTPUSH"));
            parameterJson.put("STAGBID", jsonObject.getString("STAGBID"));
        } catch (Exception e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("获取参数信息异常：" + e.getMessage(), "TaskIssueThread");
        }
        return parameterJson.toJSONString();
    }

    //获取任务表信息
    public static JSONArray getParameterTask(InitInfo initInfo) {
        JSONArray array = null;
        Connection connection = null;
        Statement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {
            String getCreateFileSql;
            database = DataBaseUtil.getDataBase(0);
            if (database.getDataBaseType().equals("1")) {
                getCreateFileSql = "select TOP " + initInfo.getGetTaskNum() + "* from FT_TASK_TABLE where FILEOUTPUSH = 1 and TASKSTATUS = 2";
            } else {
                getCreateFileSql = "select * from FT_TASK_TABLE where FILEOUTPUSH = 1 and TASKSTATUS = 2 limit " + initInfo.getGetTaskNum();
            }
            connection = DataBaseUtil.getConn(database);
            ps = connection.createStatement();
            rs = ps.executeQuery(getCreateFileSql);
            array = JsonUtil.formatRsToArray(rs);
        } catch (Exception e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("获取任务表信息异常：" + e.getMessage(), "TaskIssueThread");
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
        return array;
    }

    private static String[] getList(String primaryKey) {
        return primaryKey.split("[|]");
    }

    //拼接下传车道信息
    private static String getParameterInfo(JSONObject jsonObject, String centerIP, String IPport, String level) {
        //参数长度|文件类型|文件名称|是否压缩|分中心WEB地址|下载文件地址|车道回调地址|MD5|版本号|下传日期|序列号|路段号|站编码|车道号|站文件地址
        //序列号怎么生成？
        //如果是分中心下传车道，没有站文件地址怎么办？
        //站文件地址站级文件存放在go-fastdfs地址？
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            StringBuffer buffer = new StringBuffer();
            buffer.append(jsonObject.getLongValue("FILESIZE") + "|");//参数长度
            buffer.append(jsonObject.getIntValue("FILETYPE") + "|");//文件类型
            buffer.append(jsonObject.getString("FILENAME") + "|");//文件名称
            buffer.append("1" + "|");//是否压缩
            buffer.append(centerIP + "|");//分中心web地址
            buffer.append(jsonObject.getString("FILEURL") + "|");
            buffer.append(IPport + "|");//回调地址
            buffer.append(jsonObject.getString("FILEMD5") + "|");
            buffer.append(jsonObject.getString("FILEVERSION") + "|");
            buffer.append(sdf.format(new Date()) + "|");
            buffer.append("序列号" + "|");//序列号暂定
            buffer.append(jsonObject.getString("ROADNO") + "|");
            buffer.append(jsonObject.getString("STANO") + "|");
            buffer.append(jsonObject.getString("PORTNO") + "|");
            if (level.equals("20")) {
                //根据ROADNO和STANO先确定站级已经下载
                String srvip = getOneStano(Integer.parseInt(jsonObject.getString("ROADNO")),Integer.parseInt(jsonObject.getString("ROADNO")));
                String[] strings = jsonObject.getString("FILEURL").split(":");
                //获取站级文件下载路径
                buffer.append("http://");
                buffer.append(srvip);
                buffer.append(strings[1]+"|");
            } else if(level.equals("30")){
                buffer.append(jsonObject.getString("FILEURL") + "|");//站文件地址暂定
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取站表信息
    public static String getOneStano(int roadno,int stano) {
        Connection connection = null;
        Statement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {
            String sql = "select SRVIP from FT_NAME_TABLE where ROADNO="+roadno+" and STANO="+stano;
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            ps = connection.createStatement();
            rs = ps.executeQuery(sql);
            while (rs.next()) {
                return rs.getString("SRVIP");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
        return null;
    }

    private static int getExistNum(String stano, String[] stanos) {
        int isExist = 0;
        for (int j = 0; j < stanos.length; j++) {
            if (stano.equals(stanos[j])) {
                isExist = 1;
                break;
            }
        }
        return isExist;
    }

    //更新任务表
    private void updateTask(String taskID) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {
            String sql = "update FT_TASK_TABLE set TASKSTATUS=1 where TASKID=?";
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            ps = connection.prepareStatement(sql);
            ps.setString(1, taskID);
            int num = ps.executeUpdate();
            if (num == 1) {
                LogCommon.WriteLogNormal("更新任务" + taskID + "成功", "TaskIssueThread");
            } else {
                LogCommon.WriteLogNormal("更新任务" + taskID + "成功", "TaskIssueThread");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LogCommon.WriteLogNormal("更新任务" + taskID + "异常:" + throwables.getMessage(), "TaskIssueThread");
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
    }

    //拼接插入sql
    private String getInsertSql(JSONObject jsonObject, String tableName) {
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

    //插入日志记录
    private void insertTaskLog(JSONObject jsonObject, String taskDate, int number, String level) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {
            String nowDate = level + taskDate + "" + (number + 10);
            jsonObject.put("TASKID", nowDate);
            LogCommon.WriteLogNormal("开始将下发记录插入到日志表", "TaskIssueThread");
            //任务插入任务表
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            String insertCenter = getInsertSql(jsonObject, "FT_TASK_LOG_TABLE");
            ps = connection.prepareStatement(insertCenter);
            int index = 0;
            ps.setString(++index, jsonObject.getString("TASKID"));
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                if (entry.getValue() == null || "".equals(entry.getValue())) {
                    ps.setNull(++index, Types.CHAR);
                } else {
                    ps.setString(++index, entry.getValue().toString());
                }
            }
            int num = ps.executeUpdate();
            if (num == 1) {
                LogCommon.WriteLogNormal("日志记录插入成功", "TaskIssueThread");
            } else {
                LogCommon.WriteLogNormal("日志记录插入失败", "TaskIssueThread");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("日志记录插入异常：" + e.getMessage(), "TaskIssueThread");
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
    }

    @Override
    public void run() {
        LogCommon.WriteLogNormal(Thread.currentThread().getName() + "遍历任务线程已开启...", "TaskIssueThread");
        InitInfo initInfo = GetInit.getInitInfo();
        String[] stanos = getList(initInfo.getSpecialStano());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdfTaskID = new SimpleDateFormat("yyyyMMddHHmmss");
        Database database = DataBaseUtil.getDataBase(0);
        JSONObject jsonObject = null;
        while (true) {
            //1.连接数据库，遍历任务表
            //2.筛选自动下发类型的数据拼接成json字符串
            JSONArray jsonArray = getParameterTask(initInfo);
            if (jsonArray == null || jsonArray.size() == 0) {
                LogCommon.WriteLogNormal("任务表暂无任务，休眠10秒", "TaskIssueThread");
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            String issuDate = null;
            String taskDate = null;
            String level = null;
            //遍历json数组
            for (int i = 0; i < jsonArray.size(); i++) {
                try {
                    //3.判断下级类型
                    boolean flag = false;
                    jsonObject = JsonUtil.transToUpperObject(jsonArray.getJSONObject(i).toJSONString());
                    String taskID = jsonObject.getString("TASKID");
                    int location = jsonObject.getIntValue("LOCATION");
                    String serverIP = jsonObject.getString("SERVERIP");
                    String filekey = jsonObject.getString("FILEKEY");
                    String stano = jsonObject.getString("STANO");
                    String parameterJson = getParameterJson(jsonObject);
                    Date date = new Date();
                    taskDate = sdfTaskID.format(date);
                    issuDate = sdf.format(date);
                    if (location == 1) {
                        //3.1下级是分中心
                        level = "10";
                        ParameterService_Service.getIPPort(serverIP + ":" + initInfo.getWebServicePort());
                        ParameterService_Service service = new ParameterService_Service();
                        flag = service.getParameterServiceImplPort().uploadFileResult(parameterJson);
                    } else if (location == 2) {
                        //3.2下级是站是站
                        //调用webservice下发
                        level = "20";
                        int isExist = getExistNum(stano, stanos);
                        if (isExist == 0) {
                            ParameterService_Service.getIPPort(serverIP + ":" + initInfo.getWebServicePort());
                            ParameterService_Service service = new ParameterService_Service();
                            flag = service.getParameterServiceImplPort().uploadFileResult(parameterJson);
                        } else {
                            ParameterService_Service.getIPPort(serverIP + ":" + initInfo.getSpecialPort());
                            ParameterService_Service service = new ParameterService_Service();
                            flag = service.getParameterServiceImplPort().uploadFileResult(parameterJson);
                        }
                    } else {
                        level = "30";
                        //3.3下级是车道
                        //调用socket下发
                        String centerIP;
                        String IPport;
                        if (initInfo.getLevel().equals("20")) {
                            centerIP = database.getIP();
                            IPport = "http://" + database.getIP() + ":" + initInfo.getWebServicePort() + "/parameter/api?wsdl";
                        } else {
                            String[] strings = getList(initInfo.getSuperiorIP());
                            centerIP = strings[1];
                            int isExist = getExistNum(stano, stanos);
                            if (isExist == 0) {
                                IPport = "http://" + database.getIP() + ":" + initInfo.getWebServicePort() + "/parameter/api?wsdl";
                            } else {
                                IPport = "http://" + database.getIP() + ":" + initInfo.getSpecialPort() + "/parameter/api?wsdl";
                            }

                        }
                        String parameterInfo = getParameterInfo(jsonObject, centerIP, IPport, initInfo.getLevel());
                        SocketClientImpl sci = new SocketClientImpl();
                        flag = sci.sendStringToServer(jsonObject.getString("SERVERIP"), parameterInfo);
                    }
                    //根据flag取更新任务状态
                    if (flag) {
                        //成功  更新任务表
                        LogCommon.WriteLogNormal("参数" + filekey + "下发到" + serverIP + "成功", "TaskIssueThread");
                        updateTask(taskID);
                        jsonObject.put("INSTRISSUESTATUS", 1);
                        jsonObject.put("INSTRISSUERESULT", "成功");
                        jsonObject.put("INSTRISSUEDT", issuDate);
                    }
                    //插入日志表
                    jsonObject.remove("TASKSTATUS");
                    insertTaskLog(jsonObject, taskDate, i, level);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogCommon.WriteLogNormal("参数下发异常：" + e.getMessage(), "TaskIssueThread");
                    jsonObject.put("INSTRISSUESTATUS", 2);
                    jsonObject.put("INSTRISSUERESULT", e.getMessage());
                    jsonObject.put("INSTRISSUEDT", issuDate);
                    jsonObject.remove("TASKSTATUS");
                    insertTaskLog(jsonObject, taskDate, i, level);
                }

            }
        }
    }
}
