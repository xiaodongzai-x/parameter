package com.parameter.service;

import org.springframework.stereotype.Service;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * @author xiaodong
 * @version 1.0.0
 * @ClassName ParameterService.java
 * @Description TODO
 * @createTime 2022��03��14�� 14:33:00
 */
@WebService(name = "ParameterService", // ��¶��������
        targetNamespace = "http://service.parameter.com/"// �����ռ�,һ���ǽӿڵİ�������
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
