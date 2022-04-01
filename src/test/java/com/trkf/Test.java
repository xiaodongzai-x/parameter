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
 * @createTime 2022��03��15�� 16:09:00
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
            //���socket�ͻ��˷��ͽ�������Ϣ
            System.out.println(new String(msg.getBytes(),"utf-8"));
            clientSocket.shutdownInput();
            //������Ϣ��socket�ͻ���
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
        sci.sendStringToServer("localhost", "�����ϵ�");
    }

    //��ȡվ����Ϣ
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

        String url = "http://192.168.10.210:9002/group1/default13/20220329/15/44/5/��������ǰ�û�.xlsx";
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
     * @description �ر�������
     */
    public static void closeStream(Closeable oStream) {
        if (null != oStream) {
            try {
                oStream.close();
            } catch (IOException e) {
                oStream = null;//��ֵΪnull,�ȴ���������
                e.printStackTrace();
            }
        }
    }

    @org.junit.jupiter.api.Test
    public void getFile(){
        String url = "http://localhost:8080/group1/path5/huyghji/�ѻ���loadJar.txt";
        Request request = new Request.Builder().url(url).build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                System.out.println("��С:"+arg1.body().contentLength()/1024+"kb");
                InputStream is = arg1.body().byteStream();

                byte[] buf = new byte[2048];

                File file = new File("E:\\�ѻ���loadJar.txt");
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
                System.out.println("�������");
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {

            }
        });
    }

}
