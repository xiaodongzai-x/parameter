package com.parameter.impl;

import com.parameter.tools.ByteArray;

import java.io.*;
import java.net.Socket;

/**
 * @author xiaodong
 * @version 1.0.0
 * @ClassName SocketClientImpl.java
 * @Description socket客户端，往车道发送指令
 * @createTime 2022年03月23日 13:41:00
 */
public class SocketClientImpl {

    public boolean sendStringToServer(String serverIP,String parameterInfo){
        //新建客户端并请求服务端的ip为localhost 端口为8088
        Socket socket = null;
        OutputStream outputStream = null;
        BufferedReader reader = null;
        InputStream inputStream = null;
        try {
            //绑定服务器端IP与端口并发送消息
            socket = new Socket(serverIP, 2001);
            outputStream = socket.getOutputStream();
            outputStream.write(parameterInfo.getBytes("utf-8"));
            socket.shutdownOutput();
            //输出socket服务器端返回的消息
            inputStream = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            if (reader.readLine().equals("1")){
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            ByteArray.closeStream(outputStream);
            ByteArray.closeStream(reader);
            ByteArray.closeStream(inputStream);
            ByteArray.closeStream(socket);
        }
        return false;
    }
}
