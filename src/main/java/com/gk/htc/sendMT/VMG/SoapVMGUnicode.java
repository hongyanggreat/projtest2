/*
 * Copyright 2016 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.gk.htc.sendMT.VMG;

import com.gk.htc.ahp.brand.common.DateProc;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
import java.io.IOException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author phucvm1 version 1.0 date: Nov 10, 2016
 * @author duongnh version 3.1 date 01-03-2018
 */
public class SoapVMGUnicode {

    static final Logger logger = Logger.getLogger(SoapVMGUnicode.class);

    private static final String USER = "htc";
    private static final String PASS = "htc123456";

    public static final String URL = "http://brandsms.vn:8018/VMGAPIUnicode.asmx?wsdl";

//    public static void main(String[] args) {
//        try {
//
//            SmsBrandQueue oneQueue = new SmsBrandQueue();
//            oneQueue.setId(123123812);
//            oneQueue.setPhone("84917233352a");
//            oneQueue.setLabel("SL THUYHANG");
//            oneQueue.setMessage("test tin tu he thong moi sang VMG");
//            ResultVMG result = BulkSendSms(oneQueue);
//            if (result != null) {
//                System.out.println("Ket qua getError_code: " + result.getError_code());
//                System.out.println("Ket qua getError_detail: " + result.getError_detail());
//                System.out.println("Ket qua getMessageId: " + result.getMessageId());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//    public static ResultVMG BulkSendSms(SmsBrandQueue oneQueue) {
//        System.out.println("CONTENT:"+oneQueue.getMessage());
//        return null;
//    }
    public static ResultVMG BulkSendSms(SmsBrandQueue oneQueue) {
        ResultVMG result = null;
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();
            // Send SOAP Message to SOAP Server
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(oneQueue), URL);
//            System.out.print("Response SOAP Message = ");
//            soapResponse.writeTo(System.out);
            // Process the SOAP Response
            result = getResponse(soapResponse);
            soapConnection.close();
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        }
        return result;
    }

    private static SOAPMessage createSOAPRequest(SmsBrandQueue oneQueue) throws SOAPException, IOException {
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
               <tem:BulkSendSmsWithRequestId>
                  <!--Optional:-->
                  <tem:requestId>1</tem:requestId>
                  <!--Optional:-->
                  <tem:msisdn>84917233352</tem:msisdn>
                  <!--Optional:-->
                  <tem:alias>SL THUYHANG</tem:alias>
                  <!--Optional:-->
                  <tem:message>test tin tu he thong moi sang VMG</tem:message>
                  <!--Optional:-->
                  <tem:sendTime></tem:sendTime>
                  <!--Optional:-->
                  <tem:authenticateUser>htc</tem:authenticateUser>
                  <!--Optional:-->
                  <tem:authenticatePass>htc123456</tem:authenticatePass>
               </tem:BulkSendSmsWithRequestId>
            </soapenv:Body>
         </soapenv:Envelope>
         </soapenv:Envelope>
         */
        // SOAP Body
//        String content = StringEscapeUtils.escapeXml(oneQueue.getMessage());
        String content = oneQueue.getMessage();
//        System.out.println("content:" + content);
        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElem = soapBody.addChildElement("BulkSendSmsWithRequestId", "tem");

        SOAPElement requestId = soapBodyElem.addChildElement("requestId", "tem");
        requestId.addTextNode(oneQueue.getSystemId());

        SOAPElement msisdn = soapBodyElem.addChildElement("msisdn", "tem");
        msisdn.addTextNode(oneQueue.getPhone());

        SOAPElement alias = soapBodyElem.addChildElement("alias", "tem");
        alias.addTextNode(oneQueue.getLabel());

        SOAPElement isUnicode = soapBodyElem.addChildElement("isUnicode", "tem");
        isUnicode.addTextNode(String.valueOf(oneQueue.getDataEncode()));

        SOAPElement message = soapBodyElem.addChildElement("message", "tem");
        message.addTextNode(content);

        SOAPElement sendTime = soapBodyElem.addChildElement("sendTime", "tem");
        sendTime.addTextNode(DateProc.Timestamp2DDMMYYYYHH24Mi(DateProc.createTimestamp()));

        SOAPElement authenticateUser = soapBodyElem.addChildElement("authenticateUser", "tem");
        authenticateUser.addTextNode(USER);

        SOAPElement authenticatePass = soapBodyElem.addChildElement("authenticatePass", "tem");
        authenticatePass.addTextNode(PASS);
//        Tool.debug("message: " + message);
        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", serverURI + "BulkSendSmsWithRequestId");
        soapMessage.saveChanges();
        /* Print the request message */
//        System.out.print("Request SOAP Message = ");
//        soapMessage.writeTo(System.out);
        return soapMessage;
    }

    /**
     * Method used to print the SOAP Response
     */
    private static ResultVMG getResponse(SOAPMessage soapResponse) {
        ResultVMG result = new ResultVMG();
        String stringBody = "soapResponse.toString() Error";
        try {
            if (soapResponse != null) {
                stringBody = soapResponse.toString();
                SOAPBody body = soapResponse.getSOAPBody();
                stringBody = soapResponse.getSOAPBody().getTextContent();
                NodeList returnList = body.getElementsByTagName("BulkSendSmsWithRequestIdResult");
                if (returnList != null && returnList.getLength() > 0) {
                    Element oneNote = (Element) returnList.item(0);
                    if (oneNote.getElementsByTagName("error_code").item(0) != null) {
                        String code = oneNote.getElementsByTagName("error_code").item(0).getTextContent();
                        result.setError_code(Tool.getInt(code));
                    }
                    if (oneNote.getElementsByTagName("error_detail").item(0) != null) {
                        String mess = oneNote.getElementsByTagName("error_detail").item(0).getTextContent();
                        result.setError_detail(mess);
                    }
                    if (oneNote.getElementsByTagName("messageId").item(0) != null) {
                        String messId = oneNote.getElementsByTagName("messageId").item(0).getTextContent();
                        result.setMessageId(messId);
                    }
                }
            }
        } catch (SOAPException | DOMException e) {
            logger.error(Tool.getLogMessage(e));
            logger.error("ResultVMG Response:" + stringBody);
        }
        return result;
    }

}
