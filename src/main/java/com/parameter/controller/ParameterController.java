package com.parameter.controller;

import com.parameter.constants.StatusCode;
import com.parameter.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author xiaodong
 * @version 1.0.0
 * @ClassName ParameterController.java
 * @Description TODO
 * @createTime 2022��03��16�� 16:22:00
 */
@Controller
@RequestMapping("/parameter")
public class ParameterController {

    //��־չʾ
    //�ֶ��·�
    //�����������⼶��������Ķ��Զ��·�




    public Result getNewFileVersion(){
        String json = "";
        return new Result(StatusCode.OK,"��ȡ���°汾�ɹ�",json);
    }

    public Result getAppointFileVersion(){
        String json = "";
        return new Result(StatusCode.OK,"��ȡ���°汾�ɹ�",json);
    }
}
