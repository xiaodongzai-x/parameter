package com.trkf;

import com.alibaba.fastjson.JSONObject;
import com.parameter.entity.SetUp;
import com.parameter.impl.ParameterServiceImpl;
import com.parameter.impl.SocketClientImpl;
import com.parameter.service.ParameterService;
import com.parameter.tools.JsonUtil;
import com.parameter.tools.LogCommon;
import com.parameter.tools.ReadXML;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * @author xiaodong
 * @version 1.0.0
 * @ClassName Test.java
 * @Description TODO
 * @createTime 2022年03月15日 16:09:00
 */
public class Test {

    @org.junit.jupiter.api.Test
    public void getReadXmlPath(){
//        System.out.println(ReadXML.getPath());
//        LogCommon log = new LogCommon();
//        System.out.println(log.getPath());

        try {
            char[] charArray = new char[10];
            //创建Socket服务端，开放端口8088
            ServerSocket serverSocket = new ServerSocket(2001);
            System.out.println("accept begin = " + System.currentTimeMillis());
            //调用accept方法，此时会堵塞
            Socket socket = serverSocket.accept();
            System.out.println("accept end =" + System.currentTimeMillis());
            //获取客户端发送的数据的输入流
            InputStream inputStream = socket.getInputStream();
            //读取输入流
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            System.out.println("read begin =" + System.currentTimeMillis());
            int readLength = inputStreamReader.read(charArray);
            while (readLength != -1) {
                String newString = new String(charArray, 0, readLength);
                System.out.println(new String(newString.getBytes(),"utf-8"));
                readLength = inputStreamReader.read(charArray);
            }
            System.out.println("read end =" + System.currentTimeMillis());
            inputStreamReader.close();
            inputStream.close();
            socket.close();
            serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @org.junit.jupiter.api.Test
    public void getFileInfo(){
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
        sci.sendStringToServer("localhost","来啦老弟");
    }

    @org.junit.jupiter.api.Test
    public void jsonObject(){
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

        String url= "http://192.168.10.210/group1/default13/20220329/15/44/5/联网中心前置机.xlsx";
        String url2 = url.substring(7);
        String md5 = "7894564566";
        String[] strings = url2.split("\\/");
        StringBuffer buffer = new StringBuffer();
        String[] ips = strings[0].split("\\.");
        buffer.append("http://");
        buffer.append(ips[0]+"."+ips[1]+"."+ips[2]+".");
        buffer.append(strings[2].substring(7)+":");
        buffer.append("9022/");
        buffer.append(strings[1]+"/");
        buffer.append("delete?");
        buffer.append("md5="+md5);
        System.out.println(buffer.toString());
    }
}
