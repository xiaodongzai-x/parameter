package com.parameter.thread;

import com.parameter.client.ParameterService_Service;
import com.parameter.entity.Database;
import com.parameter.entity.FileInfo;
import com.parameter.entity.FileResult;
import com.parameter.entity.InitInfo;
import com.parameter.init.GetInit;
import com.parameter.tools.DataBaseUtil;
import com.parameter.tools.LogCommon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author xiaodong
 * @version 1.0.0
 * @ClassName GenerateFileThread.java
 * @Description 生成文件线程
 * @createTime 2022年03月21日 14:07:00
 */
public class GenerateFileThread implements Runnable{

    //获取文件信息
    public static FileInfo generateFile() {
        Connection connection = null;
        Statement ps = null;
        ResultSet rs = null;
        Database database = null;
        FileInfo fileInfo = null;
        try {
            String getCreateFileSql;
            database = DataBaseUtil.getDataBase(0);
            if (database.getDataBaseType().equals("1")) {
                getCreateFileSql = "select TOP 1 FILEkEY, FILEURL,FILEMD5,FILENAME from FT_UPLOAD_TABLE where ISCREATEFILE=0 and ISCREATETASK=0";
            } else {
                getCreateFileSql = "select FILEkEY, FILEURL,FILEMD5,FILENAME from FT_UPLOAD_TABLE where ISCREATEFILE=0 and ISCREATETASK=0 limit 1";
            }
            connection = DataBaseUtil.getConn(database);
            ps = connection.createStatement();
            rs = ps.executeQuery(getCreateFileSql);
            while (rs.next()) {
                //获取文件路径及key
                fileInfo = new FileInfo();
                fileInfo.setFileKey(rs.getString("FILEkEY"));
                fileInfo.setFileUrl(rs.getString("FILEURL"));
                fileInfo.setFileMD5(rs.getString("FILEMD5"));
                fileInfo.setFileName(rs.getString("FILENAME"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("获取文件信息异常：" + e.getMessage(), "GenerateFileThread");
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
        return fileInfo;
    }

    //修改文件状态
    public static void updateFileInfo(String fileKey, int ISCREATEFILE) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Database database = null;
        try {
            String getCreateFileSql = "update FT_UPLOAD_TABLE set ISCREATEFILE = ? where FILEKEY = ?";
            database = DataBaseUtil.getDataBase(0);
            connection = DataBaseUtil.getConn(database);
            ps = connection.prepareStatement(getCreateFileSql);
            ps.setInt(1, ISCREATEFILE);
            ps.setString(2, fileKey);
            int num = ps.executeUpdate();
            if (num == 1) {
                LogCommon.WriteLogNormal("更新文件下载状态成功", "GenerateFileThread");
            } else {
                LogCommon.WriteLogNormal("更新文件下载状态失败，未找到更新条件", "GenerateFileThread");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("更新文件下载状态异常：" + e.getMessage(), "GenerateFileThread");
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
    }

    //校验文件流生成文件
    private static FileResult getFile(String newPath, FileInfo fileInfo) {
        FileResult fileResult = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Database database = DataBaseUtil.getDataBase(0);
            fileResult = new FileResult();
            fileResult.setFileKey(fileInfo.getFileKey());
            fileResult.setFileName(fileInfo.getFileName());
            fileResult.setServerIP(database.getIP());
            fileResult.setStano(database.getStaNO());
            fileResult.setPortno(database.getPortNO());
            fileResult.setRoadno(database.getRoadNO());
            fileResult.setDownloadDT(sdf.format(new Date()));
            //生成文件
            //校验MD5

            fileResult.setDescribe("文件生成成功");
            fileResult.setResultCode("1");

        } catch (Exception e) {
            e.printStackTrace();
            fileResult.setResultCode("0");
            fileResult.setDescribe("文件生成失败" + e.getMessage());
        }
        return fileResult;
    }

    private static String[] getList(String primaryKey) {
        return primaryKey.split("[|]");
    }

    @Override
    public void run() {
        LogCommon.WriteLogNormal(Thread.currentThread().getName() + "生成文件线程已启动...", "GenerateFileThread");
        try {
            //获取文件信息
            FileInfo fileInfo = generateFile();
            //根据文件信息里的fileUrl获取流并且放入到本地文件存储系统


            //FileResult fileResult = getFile("生成文件路径", fileInfo);
            //对象转json
            //String downloadMessage = JSONObject.toJSONString(fileResult, SerializerFeature.WriteMapNullValue);
            InitInfo initInfo = GetInit.getInitInfo();
            String[] ips = getList(initInfo.getSuperiorIP());
            for (int i = 0; i < ips.length; i++) {
                ParameterService_Service.getIPPort(ips[i] + ":" + initInfo.getWebServicePort());
                ParameterService_Service service = new ParameterService_Service();
                //service.getParameterServiceImplPort().downloadFileResult(downloadMessage);
                LogCommon.WriteLogNormal("已调用回调函数", "GenerateFileThread");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogCommon.WriteLogNormal("调用回调函数异常：" + e.getMessage(), "GenerateFileThread");
        }
    }
}
