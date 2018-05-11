/*
 * Copyright 2016 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.gk.htc.sendMT.VasVTE.MFS;

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
public class ClientMFSVasVTE_bk {

    static final Logger logger = Logger.getLogger(ClientMFSVasVTE_bk.class);

    private static final String USER = "mobifone";
    private static final String PASS = "357a@#369$";
    private static final String CP_CODE = "MOBIFONE";
    private static final String COMMAND_CODE = "bulksms";

    private static final String CONTENT_TYPE_ASCII = "0";
    public static final String URL = "http://125.235.4.202:8998/bulkapi?wsdl";//REAL

//    public static void main(String[] args) {
////        String response = "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns2:wsCpMtResponse xmlns:ns2=\"http://impl.bulkSms.ws/\"><return><message>Authenticate: IP_INVALID</message><result>0</result></return></ns2:wsCpMtResponse></S:Body></S:Envelope>";
////
////        ResultMF result = parserRespone(response);
////        System.out.println(result.getCode());
////        System.out.println(result.getMessage());
//        try {
//
//            SmsBrandQueue oneQueue = new SmsBrandQueue();
//            oneQueue.setId(123123812);
//            oneQueue.setPhone("84986233352");
//            oneQueue.setLabel("UBXA_NAMLOC");
//            oneQueue.setMessage("test tin tu he thong moi port 8998");
//            ResultMBFS result = wsCpMt(oneQueue);
//            System.out.println("Ket qua: " + result.getCode());
//            System.out.println("Ket qua: " + result.getMessage());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static ResultMBFS wsCpMt(SmsBrandQueue oneQueue) {
        ResultMBFS result = new ResultMBFS();
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
                result = parserRespone(response);
            }
//            MyLog.debug("Call Service MBF=>VTE Result:" + response);
//            System.out.println("Call Service MBF=>VTE Result:" + response);
//            if (response != null && !response.equals("")) {
//                if (response.contains("<result>")) {
//                    int start = response.indexOf("<result>") + "<result>".length();
//                    int end = response.lastIndexOf("</result>");
//                    String responseCode = response.substring(start, end);
//                    System.out.println("responseCode:" + responseCode);
//                    if (responseCode.equalsIgnoreCase("1")) {
//                        result = 0; //call success
//                    }
//                }
//            }
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

    private static ResultMBFS parserRespone(String xmlInput) {
        ResultMBFS result = new ResultMBFS();
//        Tool.debug("Response Result:" + xmlInput);
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
