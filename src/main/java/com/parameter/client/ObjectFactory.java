
package com.parameter.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the parameterClient package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _UploadFileResultResponse_QNAME = new QName("http://service.parameter.com/", "uploadFileResultResponse");
    private final static QName _DownloadFileResultResponse_QNAME = new QName("http://service.parameter.com/", "downloadFileResultResponse");
    private final static QName _GetNewFileVersion_QNAME = new QName("http://service.parameter.com/", "getNewFileVersion");
    private final static QName _GetAppointFileVersion_QNAME = new QName("http://service.parameter.com/", "getAppointFileVersion");
    private final static QName _GetNewFileVersionResponse_QNAME = new QName("http://service.parameter.com/", "getNewFileVersionResponse");
    private final static QName _UploadFileResult_QNAME = new QName("http://service.parameter.com/", "uploadFileResult");
    private final static QName _DownloadFileResult_QNAME = new QName("http://service.parameter.com/", "downloadFileResult");
    private final static QName _GetAppointFileVersionResponse_QNAME = new QName("http://service.parameter.com/", "getAppointFileVersionResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: parameterClient
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UploadFileResultResponse }
     * 
     */
    public UploadFileResultResponse createUploadFileResultResponse() {
        return new UploadFileResultResponse();
    }

    /**
     * Create an instance of {@link GetAppointFileVersion }
     * 
     */
    public GetAppointFileVersion createGetAppointFileVersion() {
        return new GetAppointFileVersion();
    }

    /**
     * Create an instance of {@link GetNewFileVersionResponse }
     * 
     */
    public GetNewFileVersionResponse createGetNewFileVersionResponse() {
        return new GetNewFileVersionResponse();
    }

    /**
     * Create an instance of {@link UploadFileResult }
     * 
     */
    public UploadFileResult createUploadFileResult() {
        return new UploadFileResult();
    }

    /**
     * Create an instance of {@link DownloadFileResultResponse }
     * 
     */
    public DownloadFileResultResponse createDownloadFileResultResponse() {
        return new DownloadFileResultResponse();
    }

    /**
     * Create an instance of {@link GetNewFileVersion }
     * 
     */
    public GetNewFileVersion createGetNewFileVersion() {
        return new GetNewFileVersion();
    }

    /**
     * Create an instance of {@link DownloadFileResult }
     * 
     */
    public DownloadFileResult createDownloadFileResult() {
        return new DownloadFileResult();
    }

    /**
     * Create an instance of {@link GetAppointFileVersionResponse }
     * 
     */
    public GetAppointFileVersionResponse createGetAppointFileVersionResponse() {
        return new GetAppointFileVersionResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UploadFileResultResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.parameter.com/", name = "uploadFileResultResponse")
    public JAXBElement<UploadFileResultResponse> createUploadFileResultResponse(UploadFileResultResponse value) {
        return new JAXBElement<UploadFileResultResponse>(_UploadFileResultResponse_QNAME, UploadFileResultResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DownloadFileResultResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.parameter.com/", name = "downloadFileResultResponse")
    public JAXBElement<DownloadFileResultResponse> createDownloadFileResultResponse(DownloadFileResultResponse value) {
        return new JAXBElement<DownloadFileResultResponse>(_DownloadFileResultResponse_QNAME, DownloadFileResultResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetNewFileVersion }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.parameter.com/", name = "getNewFileVersion")
    public JAXBElement<GetNewFileVersion> createGetNewFileVersion(GetNewFileVersion value) {
        return new JAXBElement<GetNewFileVersion>(_GetNewFileVersion_QNAME, GetNewFileVersion.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAppointFileVersion }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.parameter.com/", name = "getAppointFileVersion")
    public JAXBElement<GetAppointFileVersion> createGetAppointFileVersion(GetAppointFileVersion value) {
        return new JAXBElement<GetAppointFileVersion>(_GetAppointFileVersion_QNAME, GetAppointFileVersion.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetNewFileVersionResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.parameter.com/", name = "getNewFileVersionResponse")
    public JAXBElement<GetNewFileVersionResponse> createGetNewFileVersionResponse(GetNewFileVersionResponse value) {
        return new JAXBElement<GetNewFileVersionResponse>(_GetNewFileVersionResponse_QNAME, GetNewFileVersionResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UploadFileResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.parameter.com/", name = "uploadFileResult")
    public JAXBElement<UploadFileResult> createUploadFileResult(UploadFileResult value) {
        return new JAXBElement<UploadFileResult>(_UploadFileResult_QNAME, UploadFileResult.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DownloadFileResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.parameter.com/", name = "downloadFileResult")
    public JAXBElement<DownloadFileResult> createDownloadFileResult(DownloadFileResult value) {
        return new JAXBElement<DownloadFileResult>(_DownloadFileResult_QNAME, DownloadFileResult.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAppointFileVersionResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.parameter.com/", name = "getAppointFileVersionResponse")
    public JAXBElement<GetAppointFileVersionResponse> createGetAppointFileVersionResponse(GetAppointFileVersionResponse value) {
        return new JAXBElement<GetAppointFileVersionResponse>(_GetAppointFileVersionResponse_QNAME, GetAppointFileVersionResponse.class, null, value);
    }

}
