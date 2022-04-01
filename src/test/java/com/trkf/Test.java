package com.trkf;

import com.parameter.entity.Database;
import com.parameter.impl.SocketClientImpl;
import com.parameter.tools.DataBaseUtil;
import okhttp3.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * @author xiaodong
 * @version 1.0.0
 * @ClassName Test.java
 * @Description TODO
 * @createTime 2022年03月15日 16:09:00
 */
public class Test {

    @org.junit.jupiter.api.Test
    public void getReadXmlPath() {
//        System.out.println(ReadXML.getPath());
//        LogCommon log = new LogCommon();
//        System.out.println(log.getPath());
        ServerSocket server = null;
        Socket clientSocket = null;
        BufferedReader br = null;
        PrintWriter writer = null;
        try {
            server = new ServerSocket(2001);
            clientSocket = server.accept();
            br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String msg = br.readLine();
            //输出socket客户端发送进来的消息
            System.out.println(new String(msg.getBytes(),"utf-8"));
            clientSocket.shutdownInput();
            //返回消息给socket客户端
            writer = new PrintWriter(clientSocket.getOutputStream());
            writer.println("1");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(br);
            closeStream(writer);
            closeStream(clientSocket);
        }
    }


    @org.junit.jupiter.api.Test
    public void getFileInfo() {
//        ParameterServiceImpl parameterService = new ParameterServiceImpl();
//        parameterService.uploadFileResult("109");
//        SetUp setUp = new SetUp();
//        setUp.setPortNO(100);
//        setUp.setSrvIP("127.0.0.1");
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("xiao",45);
//        jsonObject.put("DONG",56);
//        System.out.println(jsonObject.toString());
//        System.out.println(jsonObject.toJSONString());

        SocketClientImpl sci = new SocketClientImpl();
        sci.sendStringToServer("localhost", "来啦老弟");
    }

    //获取站表信息
    @org.junit.jupiter.api.Test
    public void getOneStano() {
        int roadno = 85;
        int stano = 8504;
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
                System.out.println(rs.getString("SRVIP"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            DataBaseUtil.dataBaseCloseS(connection, ps, rs, Integer.parseInt(database.getDataBaseType()));
        }
    }

    @org.junit.jupiter.api.Test
    public void jsonObject() {
//        String fileInfo = "{\"fileKey\":\"11101\",\"fileName\":\"文件名称\",\"fileType\":111,\"fileMD5\":\"667c581002ee573ff7921dec715d5ce2\",\"fileVersion\":\"1.0\",\"fileURL\":\"https://blog.csdn.net/qq_39457143?type=blog2\",\"stagbID\":\"参数文件所属站点国标编码\",\"fileSize\":\"1000\",\"fileDT\":\"2022-03-18 10:15:18\"}";
//        //接收文件基本信息json字符串并解析
//        JSONObject parseObject = JsonUtil.transToUpperObject(fileInfo);
//        //根据类型去判断自动还是手动
//        String fileType = parseObject.getString("FILETYPE");
//        String fileKey = parseObject.getString("FILEKEY");
//        System.out.println(fileKey+"---------"+fileType);
//        System.out.println(parseObject.toString());

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date today = new Date();
//        //获取三十天前日期
//        Calendar theCa = Calendar.getInstance();
//        theCa.setTime(today);
//        theCa.add(theCa.DATE, -30); //最后一个数字30可改，30天的意思
//        Date start = theCa.getTime();
//        String startDate = sdf.format(start); //三十天之前日期
//        System.out.println(startDate);

        String url = "http://192.168.10.210:9002/group1/default13/20220329/15/44/5/联网中心前置机.xlsx";
        String url2 = url.substring(7);
        String md5 = "7894564566";
        String[] strings = url2.split("\\/");
        StringBuffer buffer = new StringBuffer();
        //String[] ips = strings[0].split("\\.");
        buffer.append("http://");
        //buffer.append(ips[0] + "." + ips[1] + "." + ips[2] + ".");
        //buffer.append(strings[2].substring(7) + ":");
        buffer.append(strings[0]+"/"+strings[1]+"/");
        //buffer.append("9022/");
        //buffer.append(strings[1] + "/");
        buffer.append("delete?");
        buffer.append("md5=" + md5);
        System.out.println(buffer.toString());
    }

    /**
     * @param oStream
     * @description 关闭数据流
     */
    public static void closeStream(Closeable oStream) {
        if (null != oStream) {
            try {
                oStream.close();
            } catch (IOException e) {
                oStream = null;//赋值为null,等待垃圾回收
                e.printStackTrace();
            }
        }
    }

    @org.junit.jupiter.api.Test
    public void getFile(){
        String url = "http://localhost:8080/group1/path5/huyghji/已换新loadJar.txt";
        Request request = new Request.Builder().url(url).build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                System.out.println("大小:"+arg1.body().contentLength()/1024+"kb");
                InputStream is = arg1.body().byteStream();

                byte[] buf = new byte[2048];

                File file = new File("E:\\已换新loadJar.txt");
                if(file.exists()) file.createNewFile();
                FileOutputStream fo = new FileOutputStream(file);

                int len = 0;
                while((len = is.read(buf)) != -1) {
                    fo.write(buf,0,len);
                }
                fo.flush();
                if(is != null)
                    is.close();
                if(fo != null)
                    fo.close();
                System.out.println("下载完成");
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {

            }
        });
    }

}
