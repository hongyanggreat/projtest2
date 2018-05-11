
package com.gk.htc.sendMT.CMC.MBF;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.6-1b01 
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "SMS", targetNamespace = "http://w2m.bluezone.vn/", wsdlLocation = "http://124.158.6.45/ADT/Service.asmx?WSDL")
public class SMS
    extends Service
{

    private final static URL SMS_WSDL_LOCATION;
    private final static WebServiceException SMS_EXCEPTION;
    private final static QName SMS_QNAME = new QName("http://w2m.bluezone.vn/", "SMS");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://124.158.6.45/ADT/Service.asmx?WSDL");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        SMS_WSDL_LOCATION = url;
        SMS_EXCEPTION = e;
    }

    public SMS() {
        super(__getWsdlLocation(), SMS_QNAME);
    }

    public SMS(WebServiceFeature... features) {
        super(__getWsdlLocation(), SMS_QNAME, features);
    }

    public SMS(URL wsdlLocation) {
        super(wsdlLocation, SMS_QNAME);
    }

    public SMS(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, SMS_QNAME, features);
    }

    public SMS(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public SMS(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns SMSSoap
     */
    @WebEndpoint(name = "SMSSoap")
    public SMSSoap getSMSSoap() {
        return super.getPort(new QName("http://w2m.bluezone.vn/", "SMSSoap"), SMSSoap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns SMSSoap
     */
    @WebEndpoint(name = "SMSSoap")
    public SMSSoap getSMSSoap(WebServiceFeature... features) {
        return super.getPort(new QName("http://w2m.bluezone.vn/", "SMSSoap"), SMSSoap.class, features);
    }

    /**
     * 
     * @return
     *     returns SMSSoap
     */
    @WebEndpoint(name = "SMSSoap12")
    public SMSSoap getSMSSoap12() {
        return super.getPort(new QName("http://w2m.bluezone.vn/", "SMSSoap12"), SMSSoap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns SMSSoap
     */
    @WebEndpoint(name = "SMSSoap12")
    public SMSSoap getSMSSoap12(WebServiceFeature... features) {
        return super.getPort(new QName("http://w2m.bluezone.vn/", "SMSSoap12"), SMSSoap.class, features);
    }

    private static URL __getWsdlLocation() {
        if (SMS_EXCEPTION!= null) {
            throw SMS_EXCEPTION;
        }
        return SMS_WSDL_LOCATION;
    }

}
