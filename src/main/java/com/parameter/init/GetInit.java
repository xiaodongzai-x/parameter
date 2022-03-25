package com.parameter.init;

import com.parameter.entity.InitInfo;
import com.parameter.tools.LogCommon;
import com.parameter.tools.ReadXML;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author xiaodong
 * @version 1.0.0
 * @ClassName GetInit.java
 * @Description TODO
 * @createTime 2022年03月18日 10:18:00
 */
@Configuration
public class GetInit {
    //初始化加载配置文件
    //1.获取到本位置的级别 10联网中心 20分中心 30站级
    private static InitInfo initInfo;

    static {
        Long begin = System.currentTimeMillis();
        LogCommon.WriteLogNormal("开始获取配置文件...","init");
        ReadXML readXML = new ReadXML();
        initInfo = readXML.getInit();
        Long end = System.currentTimeMillis();
        LogCommon.WriteLogNormal("获取配置文件结束,用时【"+(end-begin)+"】毫秒","init");
    }

    public static InitInfo getInitInfo() {
        return initInfo;
    }

    public static void setInitInfo(InitInfo initInfo) {
        GetInit.initInfo = initInfo;
    }
}
