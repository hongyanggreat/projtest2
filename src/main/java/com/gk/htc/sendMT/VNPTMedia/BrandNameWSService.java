
package com.gk.htc.sendMT.VNPTMedia;

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
@WebServiceClient(name = "BrandNameWSService", targetNamespace = "http://sms.mc.vasc.com/", wsdlLocation = "http://123.29.69.74:8889/WSSMSAdminBR/BrandNameWS?wsdl")
public class BrandNameWSService
    extends Service
{

    private final static URL BRANDNAMEWSSERVICE_WSDL_LOCATION;
    private final static WebServiceException BRANDNAMEWSSERVICE_EXCEPTION;
    private final static QName BRANDNAMEWSSERVICE_QNAME = new QName("http://sms.mc.vasc.com/", "BrandNameWSService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://123.29.69.74:8889/WSSMSAdminBR/BrandNameWS?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        BRANDNAMEWSSERVICE_WSDL_LOCATION = url;
        BRANDNAMEWSSERVICE_EXCEPTION = e;
    }

    public BrandNameWSService() {
        super(__getWsdlLocation(), BRANDNAMEWSSERVICE_QNAME);
    }

    public BrandNameWSService(WebServiceFeature... features) {
        super(__getWsdlLocation(), BRANDNAMEWSSERVICE_QNAME, features);
    }

    public BrandNameWSService(URL wsdlLocation) {
        super(wsdlLocation, BRANDNAMEWSSERVICE_QNAME);
    }

    public BrandNameWSService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, BRANDNAMEWSSERVICE_QNAME, features);
    }

    public BrandNameWSService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public BrandNameWSService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns BrandNameWS
     */
    @WebEndpoint(name = "BrandNameWSPort")
    public BrandNameWS getBrandNameWSPort() {
        return super.getPort(new QName("http://sms.mc.vasc.com/", "BrandNameWSPort"), BrandNameWS.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns BrandNameWS
     */
    @WebEndpoint(name = "BrandNameWSPort")
    public BrandNameWS getBrandNameWSPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://sms.mc.vasc.com/", "BrandNameWSPort"), BrandNameWS.class, features);
    }

    private static URL __getWsdlLocation() {
        if (BRANDNAMEWSSERVICE_EXCEPTION!= null) {
            throw BRANDNAMEWSSERVICE_EXCEPTION;
        }
        return BRANDNAMEWSSERVICE_WSDL_LOCATION;
    }

}
