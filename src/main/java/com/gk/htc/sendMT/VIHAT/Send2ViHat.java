/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.VIHAT;

import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.entity.BrandLabel;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author TUANPLA
 */
public class Send2ViHat {

    static final Logger logger = Logger.getLogger(Send2ViHat.class);
    private static final String apikey = "70CEE1F0DF01F730F3C6AF84CD9718";
    private static final String secretkey = "E6D851E9659B6A5688EB978E161F8C";
    private static final String URL_SEND_SMS = "http://api.esms.vn/MainService.svc/xml/SendMultipleMessage_V4/";

    public static enum TYPE {

        QC(1, "Tin nhắn Quảng cáo"),
        CSKH(2, "Tin chăm sóc khách hàng"),;
        public int val;
        public String desc;

        private TYPE(int val, String desc) {
            this.val = val;
            this.desc = desc;
        }

        public static String getDesc(int val) {
            String str = "Unknow";
            for (TYPE one : TYPE.values()) {
                if (one.val == val) {
                    str = one.desc;
                    break;
                }
            }
            return str;
        }
    }

    public static enum STATUS {

        SUCCESS(100, "Request thành công "),
        UNKNOW_ERROR(99, "Lỗi không xác định , thử lại sau "),
        LOGIN_ERROR(101, "Đăng nhập thất bại (api key hoặc secrect key không đúng)"),
        ACCOUNT_LOCK(102, "Tài khoản đã bị khóa"),
        BLANCER_LIMIT(103, "Số dư tài khoản không đủ dể gửi tin"),
        BRAND_NOT_REGISTER(104, "Brandname chưa được đăng ký"),
        TYPE_NOT_VALID(104, "SmsType không đúng"),
        ERR_PASER(-99, "HTC Paser result Error"),;
        public int val;
        public String mess;

        private STATUS(int val, String mess) {
            this.val = val;
            this.mess = mess;
        }

        public static String getmessage(int val) {
            String str = "";
            STATUS[] tmp = STATUS.values();
            for (STATUS one : tmp) {
                if (one.val == val) {
                    str = one.mess;
                }
            }
            if (Tool.checkNull(str)) {
                str = "Unknow VAL:" + val;
            }
            return str;
        }
    }

    public static String[] sendOneQueue(SmsBrandQueue queue) {
        //  STATUS - REQID
        String data = buildRequest(queue);
        String response = excutePost(URL_SEND_SMS, data);
        String[] result = getResult(response);  // Da Login lai torng nay
        return result;
    }

    private static String buildRequest(SmsBrandQueue queue) {
        int type = TYPE.CSKH.val;
        if (queue.getType() == BrandLabel.TYPE.QC.val) {
            type = TYPE.QC.val;
        }
        // TODO
        String message = StringEscapeUtils.escapeXml(queue.getMessage());
//        message = validMessage(message);                            // Valid ky tu thay the cho XML vi khi request ve no chi la ky tu dac biet
        String result = "<RQST>"
                + "<APIKEY>" + apikey + "</APIKEY>"
                + "<SECRETKEY>" + secretkey + "</SECRETKEY>"
                + "<CONTENT>" + message + "</CONTENT>"
                + "<SMSTYPE>" + type + "</SMSTYPE>"
                + "<BRANDNAME>" + queue.getLabel() + "</BRANDNAME>"
                + "<CONTACTS>"
                + "<CUSTOMER>"
                + "<PHONE>" + queue.getPhone() + "</PHONE>"
                + "</CUSTOMER>"
                + "</CONTACTS>"
                //                + "<REQUESTID>" + queue.getSystemId() + "</REQUESTID>"
                + "</RQST>";
        // Set vao day de cho lay ket qua ve
        return result;
    }

//    private static String validMessage(String msg) {
////        msg = msg.replaceAll("&", "&amp;");
////        msg = msg.replaceAll("<", "&lt;");
////        msg = msg.replaceAll(">", "&gt;");
////        msg = msg.replaceAll("'", "&apos;");
////        msg = msg.replaceAll("\"", "&quot;");
////        msg = msg.replaceAll("\r", "&#13;");
////        msg = msg.replaceAll("\n", "&#10;");
//        return msg;
//    }

    private static String[] getResult(String xmlInput) {
        String[] result = {STATUS.UNKNOW_ERROR.val + "", STATUS.UNKNOW_ERROR.mess, "0"};
//        Tool.debug("Response Result:" + xmlInput);
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlInput));
            Document doc = dBuilder.parse(is);
            NodeList nodes = doc.getElementsByTagName("SmsResultModel");
            if (nodes != null && nodes.getLength() > 0) {
                Element oneNote = (Element) nodes.item(0);
                String status = STATUS.UNKNOW_ERROR.val + "";
                String message = "Unknow";
                String reqId = "0";
                if (oneNote.getElementsByTagName("CodeResult").item(0) != null) {
                    status = oneNote.getElementsByTagName("CodeResult").item(0).getTextContent();
                }
                if (oneNote.getElementsByTagName("SMSID").item(0) != null) {
                    reqId = oneNote.getElementsByTagName("SMSID").item(0).getTextContent();
                }
                if (oneNote.getElementsByTagName("ErrorMessage").item(0) != null) {
                    message = oneNote.getElementsByTagName("ErrorMessage").item(0).getTextContent();
                }
                result[0] = status;
                result[1] = message;
                result[2] = reqId;
            }
        } catch (ParserConfigurationException | SAXException | IOException | DOMException | NullPointerException e) {
            logger.error("xmlInput:" + xmlInput);
            logger.error(Tool.getLogMessage(e));
        }
        return result;
    }

    private static String excutePost(String targetURL, String urlParameters) {
        String result = "";
        HttpURLConnection conn = null;
        try {
            //Create connection
            URL url = new URL(targetURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            conn.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
            conn.setRequestProperty("Content-Language", "en-US");

            conn.setUseCaches(false);
            conn.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            //Get Response  
            InputStream is = conn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+ 
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            result = response.toString();
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }
}
