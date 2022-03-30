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
 * @createTime 2022��03��15�� 16:09:00
 */
public class Test {

    @org.junit.jupiter.api.Test
    public void getReadXmlPath(){
//        System.out.println(ReadXML.getPath());
//        LogCommon log = new LogCommon();
//        System.out.println(log.getPath());

        try {
            char[] charArray = new char[10];
            //����Socket����ˣ����Ŷ˿�8088
            ServerSocket serverSocket = new ServerSocket(2001);
            System.out.println("accept begin = " + System.currentTimeMillis());
            //����accept��������ʱ�����
            Socket socket = serverSocket.accept();
            System.out.println("accept end =" + System.currentTimeMillis());
            //��ȡ�ͻ��˷��͵����ݵ�������
            InputStream inputStream = socket.getInputStream();
            //��ȡ������
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
        sci.sendStringToServer("localhost","�����ϵ�");
    }

    @org.junit.jupiter.api.Test
    public void jsonObject(){
//        String fileInfo = "{\"fileKey\":\"11101\",\"fileName\":\"�ļ�����\",\"fileType\":111,\"fileMD5\":\"667c581002ee573ff7921dec715d5ce2\",\"fileVersion\":\"1.0\",\"fileURL\":\"https://blog.csdn.net/qq_39457143?type=blog2\",\"stagbID\":\"�����ļ�����վ��������\",\"fileSize\":\"1000\",\"fileDT\":\"2022-03-18 10:15:18\"}";
//        //�����ļ�������Ϣjson�ַ���������
//        JSONObject parseObject = JsonUtil.transToUpperObject(fileInfo);
//        //��������ȥ�ж��Զ������ֶ�
//        String fileType = parseObject.getString("FILETYPE");
//        String fileKey = parseObject.getString("FILEKEY");
//        System.out.println(fileKey+"---------"+fileType);
//        System.out.println(parseObject.toString());

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date today = new Date();
//        //��ȡ��ʮ��ǰ����
//        Calendar theCa = Calendar.getInstance();
//        theCa.setTime(today);
//        theCa.add(theCa.DATE, -30); //���һ������30�ɸģ�30�����˼
//        Date start = theCa.getTime();
//        String startDate = sdf.format(start); //��ʮ��֮ǰ����
//        System.out.println(startDate);

        String url= "http://192.168.10.210/group1/default13/20220329/15/44/5/��������ǰ�û�.xlsx";
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
