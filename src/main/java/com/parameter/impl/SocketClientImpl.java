package com.parameter.impl;

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
        try {
            socket = new Socket(serverIP,2001);
            //获取客户端的输出流 即向服务端发送的数据
            outputStream = socket.getOutputStream();
            //向输出流中写入要发送的字符串数据
            outputStream.write(parameterInfo.getBytes("utf-8"));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            closeStream(outputStream);
            closeStream(socket);
        }
        return false;
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

}
