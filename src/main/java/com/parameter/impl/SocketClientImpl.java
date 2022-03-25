package com.parameter.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author xiaodong
 * @version 1.0.0
 * @ClassName SocketClientImpl.java
 * @Description socket�ͻ��ˣ�����������ָ��
 * @createTime 2022��03��23�� 13:41:00
 */
public class SocketClientImpl {

    public static void SocketClient(){
        try{
            Socket socket=new Socket("127.0.0.1",2001);
            System.out.println("client start ...");
            //�򱾻���52000�˿ڷ����ͻ�����
            BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
            //��ϵͳ��׼�����豸����BufferedReader����
            PrintWriter write=new PrintWriter(socket.getOutputStream());
            //��Socket����õ��������������PrintWriter����
            BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //��Socket����õ�����������������Ӧ��BufferedReader����
            String readline;
            readline=br.readLine(); //��ϵͳ��׼�������һ�ַ���
            while(!readline.equals("end")){
                //���ӱ�׼���������ַ���Ϊ "end"��ֹͣѭ��
                write.println(readline);
                //����ϵͳ��׼���������ַ��������Server
                write.flush();
                //ˢ���������ʹServer�����յ����ַ���
                System.out.println("Client:"+readline);
                //��ϵͳ��׼����ϴ�ӡ������ַ���
                System.out.println("Server:"+in.readLine());
                //��Server����һ�ַ���������ӡ����׼�����
                readline=br.readLine(); //��ϵͳ��׼�������һ�ַ���
            } //����ѭ��
            write.close(); //�ر�Socket�����
            in.close(); //�ر�Socket������
            socket.close(); //�ر�Socket
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
