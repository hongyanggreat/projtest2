
package com.gk.htc.sendMT.VNPTMedia;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.6-1b01 
 * Generated source version: 2.2
 * 
 */
@WebService(name = "BrandNameWS", targetNamespace = "http://sms.mc.vasc.com/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface BrandNameWS {


    /**
     * 
     * @param password
     * @param serviceKind
     * @param serviceId
     * @param userId
     * @param contentType
     * @param username
     * @param infor
     * @return
     *     returns int
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "uploadSMS", targetNamespace = "http://sms.mc.vasc.com/", className = "com.gk.htc.sendMT.VNPTMedia.UploadSMS")
    @ResponseWrapper(localName = "uploadSMSResponse", targetNamespace = "http://sms.mc.vasc.com/", className = "com.gk.htc.sendMT.VNPTMedia.UploadSMSResponse")
    @Action(input = "http://sms.mc.vasc.com/BrandNameWS/uploadSMSRequest", output = "http://sms.mc.vasc.com/BrandNameWS/uploadSMSResponse")
    public int uploadSMS(
        @WebParam(name = "username", targetNamespace = "")
        String username,
        @WebParam(name = "password", targetNamespace = "")
        String password,
        @WebParam(name = "serviceId", targetNamespace = "")
        String serviceId,
        @WebParam(name = "userId", targetNamespace = "")
        String userId,
        @WebParam(name = "contentType", targetNamespace = "")
        String contentType,
        @WebParam(name = "serviceKind", targetNamespace = "")
        String serviceKind,
        @WebParam(name = "infor", targetNamespace = "")
        String infor);

}
