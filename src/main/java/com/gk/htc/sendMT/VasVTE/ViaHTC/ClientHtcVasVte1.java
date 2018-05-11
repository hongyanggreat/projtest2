/*
 * Copyright 2016 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.gk.htc.sendMT.VasVTE.ViaHTC;

import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import utils.Protocol;

/**
 * @author phucvm1 version 1.0 date: Nov 10, 2016
 */
public class ClientHtcVasVte1 {

    static final Logger logger = Logger.getLogger(ClientHtcVasVte1.class);

    private static final String USER = "smsbrand_htc";
    private static final String PASS = "148ab@#369";
    private static final String CP_CODE = "HTC";
    private static final String COMMAND_CODE = "bulksms";

    private static final String CONTENT_TYPE_ASCII = "0";
    public static final String URL = "http://125.235.4.202:8998/bulkapi?wsdl";//BACKUP

    public static ResultHTC wsCpMt(SmsBrandQueue oneQueue) {
        ResultHTC result = new ResultHTC();
        String content = StringEscapeUtils.escapeXml(oneQueue.getMessage());
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:impl=\"http://impl.bulkSms.ws/\">"
                + "<soapenv:Header/>"
                + "<soapenv:Body>"
                + "<impl:wsCpMt>"
                + "<User>" + USER + "</User>"
                + "<Password>" + PASS + "</Password>"
                + "<CPCode>" + CP_CODE + "</CPCode>"
                + "<RequestID>" + "1" + "</RequestID>"
                + "<UserID>" + oneQueue.getPhone() + "</UserID>"
                + "<ReceiverID>" + oneQueue.getPhone() + "</ReceiverID>"
                + "<ServiceID>" + oneQueue.getLabel() + "</ServiceID>"
                + "<CommandCode>" + COMMAND_CODE + "</CommandCode>"
                + "<Content>" + content + "</Content>"
                + "<ContentType>" + CONTENT_TYPE_ASCII + "</ContentType>"
                + "</impl:wsCpMt>"
                + "</soapenv:Body>"
                + "</soapenv:Envelope>";
        PostMethod post = null;
        try {
            Protocol protocol = new Protocol(URL);
            HttpClient httpclient = new HttpClient();
            HttpConnectionManager conMgr = httpclient.getHttpConnectionManager();
            HttpConnectionManagerParams conPars = conMgr.getParams();
            conPars.setConnectionTimeout(20000);
            conPars.setSoTimeout(60000);
            post = new PostMethod(protocol.getUrl());

            RequestEntity entity = new StringRequestEntity(request, "text/xml", "UTF-8");
            post.setRequestEntity(entity);
            post.setRequestHeader("SOAPAction", "");
            httpclient.executeMethod(post);
            InputStream is = post.getResponseBodyAsStream();
            if (is != null) {
                String response = getStringFromInputStream(is);
//                System.out.println(USER + "==> " + response);
                result = parserRespone(response);
            }
        } catch (IOException e) {
//            System.err.println(e);
            logger.error(Tool.getLogMessage(e));
        } finally {
            if (post != null) {
                post.releaseConnection();
            }
        }
        return result;
    }

    public static ResultHTC checkBalance() {
        ResultHTC result = new ResultHTC();
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:impl=\"http://impl.bulkSms.ws/\">"
                + "<soapenv:Header/>"
                + "<soapenv:Body>"
                + "<impl:checkBalance>"
                + " <User>" + USER + "</User>"
                + " <Password>" + PASS + "</Password>"
                + " <CPCode>" + CP_CODE + "</CPCode>"
                + "</impl:checkBalance>"
                + "</soapenv:Body>"
                + "</soapenv:Envelope>";
        PostMethod post = null;
        try {
            Protocol protocol = new Protocol(URL);
            HttpClient httpclient = new HttpClient();
            HttpConnectionManager conMgr = httpclient.getHttpConnectionManager();
            HttpConnectionManagerParams conPars = conMgr.getParams();
            conPars.setConnectionTimeout(20000);
            conPars.setSoTimeout(60000);
            post = new PostMethod(protocol.getUrl());

            RequestEntity entity = new StringRequestEntity(request, "text/xml", "UTF-8");
            post.setRequestEntity(entity);
            post.setRequestHeader("SOAPAction", "");
            httpclient.executeMethod(post);
            InputStream is = post.getResponseBodyAsStream();
            if (is != null) {
                String response = getStringFromInputStream(is);
                System.out.println("==> " + response);
                result = parserRespone(response);
            }
        } catch (IOException e) {
            System.err.println(e);
            logger.error(Tool.getLogMessage(e));
        } finally {
            if (post != null) {
                post.releaseConnection();
            }
        }
        return result;
    }

    private static ResultHTC parserRespone(String xmlInput) {
        ResultHTC result = new ResultHTC();
//        Tool.debug("Response ResultHTC:" + xmlInput);
        String code = "100";
        String message = "Unknow Error";
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlInput));
            Document doc = dBuilder.parse(is);
            NodeList nodes = doc.getElementsByTagName("return");
            if (nodes != null && nodes.getLength() > 0) {
                Element oneNote = (Element) nodes.item(0);
                if (oneNote.getElementsByTagName("result").item(0) != null) {
                    code = oneNote.getElementsByTagName("result").item(0).getTextContent();
                }
                if (oneNote.getElementsByTagName("message").item(0) != null) {
                    message = oneNote.getElementsByTagName("message").item(0).getTextContent();
                }
                result.setMessage(message);
                result.setCode(code);
            }
        } catch (ParserConfigurationException | SAXException | IOException | DOMException | NullPointerException e) {
            logger.error("xmlInput:" + xmlInput);
            logger.error(Tool.getLogMessage(e));
            result.setMessage("ParserConfigurationException:" + e.getMessage());
        }
        return result;
    }

    private static ResultHTC parserBalancer(String xmlInput) {
        ResultHTC result = new ResultHTC();
//        Tool.debug("Response ResultHTC:" + xmlInput);
        String code = "100";
        String message = "Unknow Error";
        double balance;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlInput));
            Document doc = dBuilder.parse(is);
            NodeList nodes = doc.getElementsByTagName("return");
            if (nodes != null && nodes.getLength() > 0) {
                Element oneNote = (Element) nodes.item(0);
                if (oneNote.getElementsByTagName("errCode").item(0) != null) {
                    code = oneNote.getElementsByTagName("errCode").item(0).getTextContent();
                }
                if (oneNote.getElementsByTagName("errDesc").item(0) != null) {
                    message = oneNote.getElementsByTagName("errDesc").item(0).getTextContent();
                }
                if (oneNote.getElementsByTagName("balance").item(0) != null) {
                    String tmp = oneNote.getElementsByTagName("balance").item(0).getTextContent();
                    try {
                        balance = Double.parseDouble(tmp);
                        result.setBlancer(balance);
                    } catch (Exception e) {
                    }
                }
                result.setMessage(message);
                result.setCode(code);
            }
        } catch (ParserConfigurationException | SAXException | IOException | DOMException | NullPointerException e) {
            logger.error("xmlInput:" + xmlInput);
            logger.error(Tool.getLogMessage(e));
            result.setMessage("ParserConfigurationException:" + e.getMessage());
        }
        return result;
    }

    private static String getStringFromInputStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            System.err.println(e);
            logger.error(Tool.getLogMessage(e));
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.err.println(e);
                }
            }
        }

        return sb.toString();
    }
}
