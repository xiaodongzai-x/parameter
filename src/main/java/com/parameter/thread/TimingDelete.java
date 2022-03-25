package com.parameter.thread;

import com.parameter.tools.LogCommon;

/**
 * @author xiaodong
 * @version 1.0.0
 * @ClassName TimingDelete.java
 * @Description 定时清理过期文件
 * @createTime 2022年03月18日 10:24:00
 */
public class TimingDelete implements Runnable {
    @Override
    public void run() {
        LogCommon.WriteLogNormal(Thread.currentThread().getName()+"定时清理过期文件线程已开启...","Init");
        while (true){
            //遍历库里文件信息

            //该文件入库大于30天删除

            //先删除文件系统里的在删除库里的信息
        }
    }
}
