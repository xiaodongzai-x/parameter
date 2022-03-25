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
 * @createTime 2022年03月16日 16:22:00
 */
@Controller
@RequestMapping("/parameter")
public class ParameterController {

    //日志展示
    //手动下发
    //在联网中心这级操作后面的都自动下发




    public Result getNewFileVersion(){
        String json = "";
        return new Result(StatusCode.OK,"获取最新版本成功",json);
    }

    public Result getAppointFileVersion(){
        String json = "";
        return new Result(StatusCode.OK,"获取最新版本成功",json);
    }
}
