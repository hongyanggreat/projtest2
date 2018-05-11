
package com.gk.htc.sendMT.VNPTMediaUnicode;

import com.gk.htc.sendMT.VNPTMediaUnicode.*;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.gk.htc.sendMT.VNPTMedia package. 
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

    private final static QName _UploadSMS_QNAME = new QName("http://sms.mc.vasc.com/", "uploadSMS");
    private final static QName _UploadSMSResponse_QNAME = new QName("http://sms.mc.vasc.com/", "uploadSMSResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.gk.htc.sendMT.VNPTMedia
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UploadSMS }
     * 
     */
    public UploadSMS createUploadSMS() {
        return new UploadSMS();
    }

    /**
     * Create an instance of {@link UploadSMSResponse }
     * 
     */
    public UploadSMSResponse createUploadSMSResponse() {
        return new UploadSMSResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UploadSMS }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sms.mc.vasc.com/", name = "uploadSMS")
    public JAXBElement<UploadSMS> createUploadSMS(UploadSMS value) {
        return new JAXBElement<UploadSMS>(_UploadSMS_QNAME, UploadSMS.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UploadSMSResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sms.mc.vasc.com/", name = "uploadSMSResponse")
    public JAXBElement<UploadSMSResponse> createUploadSMSResponse(UploadSMSResponse value) {
        return new JAXBElement<UploadSMSResponse>(_UploadSMSResponse_QNAME, UploadSMSResponse.class, null, value);
    }

}
