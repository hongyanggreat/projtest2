
package com.gk.htc.sendMT.CMC.HTC;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.6-1b01 
 * Generated source version: 2.2
 * 
 */
@WebService(name = "SMSSoap", targetNamespace = "http://w2m.bluezone.vn/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface SMSSoap {


    /**
     * 
     * @param password
     * @param phone
     * @param sender
     * @param sms
     * @param username
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "SendSMSBrandName", action = "http://w2m.bluezone.vn/SendSMSBrandName")
    @WebResult(name = "SendSMSBrandNameResult", targetNamespace = "http://w2m.bluezone.vn/")
    @RequestWrapper(localName = "SendSMSBrandName", targetNamespace = "http://w2m.bluezone.vn/", className = "com.gk.htc.sendMT.CMC.HTC.SendSMSBrandName")
    @ResponseWrapper(localName = "SendSMSBrandNameResponse", targetNamespace = "http://w2m.bluezone.vn/", className = "com.gk.htc.sendMT.CMC.HTC.SendSMSBrandNameResponse")
    public String sendSMSBrandName(
        @WebParam(name = "phone", targetNamespace = "http://w2m.bluezone.vn/")
        String phone,
        @WebParam(name = "sms", targetNamespace = "http://w2m.bluezone.vn/")
        String sms,
        @WebParam(name = "sender", targetNamespace = "http://w2m.bluezone.vn/")
        String sender,
        @WebParam(name = "username", targetNamespace = "http://w2m.bluezone.vn/")
        String username,
        @WebParam(name = "password", targetNamespace = "http://w2m.bluezone.vn/")
        String password);

}
