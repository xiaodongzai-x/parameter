package com.parameter.thread;

import com.parameter.tools.LogCommon;

/**
 * @author xiaodong
 * @version 1.0.0
 * @ClassName TimingDelete.java
 * @Description ��ʱ��������ļ�
 * @createTime 2022��03��18�� 10:24:00
 */
public class TimingDelete implements Runnable {
    @Override
    public void run() {
        LogCommon.WriteLogNormal(Thread.currentThread().getName()+"��ʱ��������ļ��߳��ѿ���...","Init");
        while (true){
            //���������ļ���Ϣ

            //���ļ�������30��ɾ��

            //��ɾ���ļ�ϵͳ�����ɾ���������Ϣ
        }
    }
}
