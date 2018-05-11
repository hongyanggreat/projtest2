/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.MFS;

import com.gk.htc.ahp.brand.common.*;
import java.io.IOException;
import javax.xml.soap.*;
import org.apache.log4j.Logger;
import org.w3c.dom.NodeList;

/**
 *
 * @author TUANPLA
 */
public class ClientMOBI_Service {

    static final Logger logger = Logger.getLogger(ClientMOBI_Service.class);
    private static final String USER = "mobifoneservice";
    private static final String PASS = "Qh2ATavhsAg0STkN";

    public static String sendSMS(String smsID, String brandname, String phonenumber, String message) {
        String result = "0";
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();
            // Send SOAP Message to SOAP Server
            String url = "https://brandsms.irismedia.vn/Service.asmx";
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(smsID, brandname, phonenumber, message), url);
            // Process the SOAP Response
            result = getResponse(soapResponse);
            soapConnection.close();
        } catch (Exception e) {
            System.err.println("Error occurred while sending SOAP Request to Server");
            logger.error(Tool.getLogMessage(e));
        }
        return result;
    }

    private static SOAPMessage createSOAPRequest(String smsID, String brandname, String phonenumber, String message) throws SOAPException, IOException {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        String serverURI = "http://tempuri.org/";
        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("tem", serverURI);
        /*
         <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
         <soapenv:Header/>
         <soapenv:Body>
         <tem:SendSMS>
         <!--Optional:-->
         <tem:userid>?</tem:userid>
         <!--Optional:-->
         <tem:password>?</tem:password>
         <!--Optional:-->
         <tem:SMS_ID>?</tem:SMS_ID>
         <!--Optional:-->
         <tem:brandname>?</tem:brandname>
         <!--Optional:-->
         <tem:phonenumber>?</tem:phonenumber>
         <!--Optional:-->
         <tem:message>?</tem:message>
         </tem:SendSMS>
         </soapenv:Body>
         </soapenv:Envelope>
         */
        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElem = soapBody.addChildElement("SendSMS", "tem");
        SOAPElement useridNode = soapBodyElem.addChildElement("userid", "tem");
        useridNode.addTextNode(USER);
        SOAPElement passwordNode = soapBodyElem.addChildElement("password", "tem");
        passwordNode.addTextNode(PASS);
        SOAPElement smsIdNode = soapBodyElem.addChildElement("SMS_ID", "tem");
//        Tool.debug("SMS_ID: " + smsID);
        smsIdNode.addTextNode(smsID);
        SOAPElement brandnameNode = soapBodyElem.addChildElement("brandname", "tem");
        brandnameNode.addTextNode(brandname);
//        Tool.debug("brandname: " + brandname);
        SOAPElement phonenumberNode = soapBodyElem.addChildElement("phonenumber", "tem");
        phonenumberNode.addTextNode(phonenumber);
//        Tool.debug("phonenumber: " + phonenumber);
        SOAPElement messageNode = soapBodyElem.addChildElement("message", "tem");
        messageNode.addTextNode(message);
//        Tool.debug("message: " + message);
        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", serverURI + "SendSMS");
        soapMessage.saveChanges();
        /* Print the request message */
//        System.out.print("Request SOAP Message = ");
//        soapMessage.writeTo(System.out);
//        Tool.debug();
        return soapMessage;
    }

    /**
     * Method used to print the SOAP Response
     */
    private static String getResponse(SOAPMessage soapResponse) throws Exception {
        String result = "0";
        if (soapResponse != null) {
            SOAPBody body = soapResponse.getSOAPBody();
            NodeList returnList = body.getElementsByTagName("SendSMSResponse");
            if (returnList != null) {
                NodeList noteResult = returnList.item(0).getChildNodes();
                if (noteResult != null && noteResult.item(0).getNodeName().equalsIgnoreCase("SendSMSResult")) {
                    result = noteResult.item(0).getTextContent();
                }
            }
        }
        return result;
    }
}
