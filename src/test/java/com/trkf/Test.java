package com.trkf;

import com.alibaba.fastjson.JSONObject;
import com.parameter.impl.ParameterServiceImpl;
import com.parameter.service.ParameterService;
import com.parameter.tools.JsonUtil;
import com.parameter.tools.LogCommon;
import com.parameter.tools.ReadXML;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;


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
        System.out.println(ReadXML.getPath());
        LogCommon log = new LogCommon();
        System.out.println(log.getPath());
    }

    @org.junit.jupiter.api.Test
    public void getFileInfo(){
        ParameterServiceImpl parameterService = new ParameterServiceImpl();
        parameterService.uploadFileResult("109");
    }

    @org.junit.jupiter.api.Test
    public void jsonObject(){
        String fileInfo = "{\"fileKey\":\"11101\",\"fileName\":\"�ļ�����\",\"fileType\":111,\"fileMD5\":\"667c581002ee573ff7921dec715d5ce2\",\"fileVersion\":\"1.0\",\"fileURL\":\"https://blog.csdn.net/qq_39457143?type=blog2\",\"stagbID\":\"�����ļ�����վ��������\",\"fileSize\":\"1000\",\"fileDT\":\"2022-03-18 10:15:18\"}";
        //�����ļ�������Ϣjson�ַ���������
        JSONObject parseObject = JsonUtil.transToUpperObject(fileInfo);
        //��������ȥ�ж��Զ������ֶ�
        String fileType = parseObject.getString("FILETYPE");
        String fileKey = parseObject.getString("FILEKEY");
        System.out.println(fileKey+"---------"+fileType);
        System.out.println(parseObject.toString());
    }
}
