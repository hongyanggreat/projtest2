
package com.gk.htc.sendMT.ANTIT;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.gk.htc.sendMT.ANTIT package. 
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

    private final static QName _SendMTResponse_QNAME = new QName("http://webservice.agentsms/", "SendMTResponse");
    private final static QName _SendMT_QNAME = new QName("http://webservice.agentsms/", "SendMT");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.gk.htc.sendMT.ANTIT
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SendMTResponse }
     * 
     */
    public SendMTResponse createSendMTResponse() {
        return new SendMTResponse();
    }

    /**
     * Create an instance of {@link SendMT }
     * 
     */
    public SendMT createSendMT() {
        return new SendMT();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendMTResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.agentsms/", name = "SendMTResponse")
    public JAXBElement<SendMTResponse> createSendMTResponse(SendMTResponse value) {
        return new JAXBElement<SendMTResponse>(_SendMTResponse_QNAME, SendMTResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendMT }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.agentsms/", name = "SendMT")
    public JAXBElement<SendMT> createSendMT(SendMT value) {
        return new JAXBElement<SendMT>(_SendMT_QNAME, SendMT.class, null, value);
    }

}
