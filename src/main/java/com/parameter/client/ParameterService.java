
package com.parameter.client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebService(name = "ParameterService", targetNamespace = "http://service.parameter.com/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface ParameterService {


    /**
     * 
     * @param arg0
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "downloadFileResult", targetNamespace = "http://service.parameter.com/", className = "service3.DownloadFileResult")
    @ResponseWrapper(localName = "downloadFileResultResponse", targetNamespace = "http://service.parameter.com/", className = "service3.DownloadFileResultResponse")
    public boolean downloadFileResult(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0);

    /**
     * 
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getNewFileVersion", targetNamespace = "http://service.parameter.com/", className = "service3.GetNewFileVersion")
    @ResponseWrapper(localName = "getNewFileVersionResponse", targetNamespace = "http://service.parameter.com/", className = "service3.GetNewFileVersionResponse")
    public String getNewFileVersion();

    /**
     * 
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getAppointFileVersion", targetNamespace = "http://service.parameter.com/", className = "service3.GetAppointFileVersion")
    @ResponseWrapper(localName = "getAppointFileVersionResponse", targetNamespace = "http://service.parameter.com/", className = "service3.GetAppointFileVersionResponse")
    public String getAppointFileVersion(
        @WebParam(name = "arg0", targetNamespace = "")
        int arg0);

    /**
     * 
     * @param arg0
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "uploadFileResult", targetNamespace = "http://service.parameter.com/", className = "service3.UploadFileResult")
    @ResponseWrapper(localName = "uploadFileResultResponse", targetNamespace = "http://service.parameter.com/", className = "service3.UploadFileResultResponse")
    public boolean uploadFileResult(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0);

    /**
     * 
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getFileURL", targetNamespace = "http://service.parameter.com/", className = "service3.GetFileURL")
    @ResponseWrapper(localName = "getFileURLResponse", targetNamespace = "http://service.parameter.com/", className = "service3.GetFileURLResponse")
    public String getFileURL(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0);

}
