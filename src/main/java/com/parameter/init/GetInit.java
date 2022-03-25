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
 * @createTime 2022��03��18�� 10:18:00
 */
@Configuration
public class GetInit {
    //��ʼ�����������ļ�
    //1.��ȡ����λ�õļ��� 10�������� 20������ 30վ��
    private static InitInfo initInfo;

    static {
        Long begin = System.currentTimeMillis();
        LogCommon.WriteLogNormal("��ʼ��ȡ�����ļ�...","init");
        ReadXML readXML = new ReadXML();
        initInfo = readXML.getInit();
        Long end = System.currentTimeMillis();
        LogCommon.WriteLogNormal("��ȡ�����ļ�����,��ʱ��"+(end-begin)+"������","init");
    }

    public static InitInfo getInitInfo() {
        return initInfo;
    }

    public static void setInitInfo(InitInfo initInfo) {
        GetInit.initInfo = initInfo;
    }
}
