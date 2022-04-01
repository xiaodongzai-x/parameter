package com.parameter.service;

import org.springframework.stereotype.Service;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * @author xiaodong
 * @version 1.0.0
 * @ClassName ParameterService.java
 * @Description TODO
 * @createTime 2022年03月14日 14:33:00
 */
@WebService(name = "ParameterService", // 暴露服务名称
        targetNamespace = "http://service.parameter.com/"// 命名空间,一般是接口的包名倒序
)
public interface ParameterService {
    @WebMethod
    boolean uploadFileResult(String fileInfo);

    @WebMethod
    boolean downloadFileResult(String downloadMessage);

    @WebMethod
    String getNewFileVersion();

    @WebMethod
    String getAppointFileVersion(int fileType);

    @WebMethod
    String getFileURL(String fileKey);
}
