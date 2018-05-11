/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.MFS.MBSVerify;

import com.gk.htc.sendMT.MFS.MS.*;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
public class SendVerify2MBSViaMFS {

    static final Logger logger = Logger.getLogger(SendVerify2MBSViaMFS.class);
    private static final String USER = "agent_mfs";
    private static final String PASS = "acf87c9394113b633b0b257cbf309a58";
    private static final String URL_SEND_SMS = "http://sms.mobiservices.vn/smsapi/sendmt";

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
        WRONG_NUMBER(101, "Sai số điện thoại"),//WRONG_NUMBER 
        BRAND_NOT_EXIST(102, "Brandname không tồn tạ"),//BRAND_NOT_EXIST 
        UNDEFINED_NUMBER(103, "Không xác định được số điện thoại"),//UNDEFINED_NUMBER 
        MESSAGE_TOO_LONG(104, "Tin nhắn vượt quá số kí tự cho phép"),//MESSAGE_TOO_LONG 
        BRAND_NOT_ACTIVE(105, "Brandname chưa được kích hoạt trên hệ thốn"),//BRAND_NOT_ACTIVE 
        SYSTEM_MAINTAIN(106, "Bảo trì hệ thống"),//SYSTEM_MAINTAIN  
        //        SYSTEM_MAINTAIN(110, "Bảo trì hệ thống"),//SYSTEM_MAINTAIN    
        //        SYSTEM_MAINTAIN(120, "Bảo trì hệ thống"),//SYSTEM_MAINTAIN    
        USER_NOT_EXIST(107, "Tài khoản không tồn tại trên hệ thống"),//USER_NOT_EXIST   
        WRONG_USER_PASSWORD(108, "Sai mã tài khoản kết nối"),//WRONG_USER_PASSWORD   
        BLACK_LIST_IP(109, "IP khách bị chặn bởi hệ thống"),//BLACK_LIST_IP   
        NULL_REQUEST(111, "Không đầy đủ thông tin tham số kết nối"),//NULL_REQUEST    
        UNICODE_CONTENT(112, "Nội dung tin nhắn có chứa kí tự unicode"),//UNICODE_CONTENT     
        NOT_ENOUGH_QUOTA(113, "Tài khoản hết quota gửi tin"),//NOT_ENOUGH_QUOTA     
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

//    public static void main(String[] args) {
//        System.out.println("xxx");
//        SmsBrandQueue oneQueue = new SmsBrandQueue();
//        oneQueue.setId(2123123812);
//        oneQueue.setPhone("84966878997");
//        oneQueue.setLabel("Verify");
//        oneQueue.setMessage("test tin tu sv moi :  MS via MFS");
//        String result[] = sendOneQueue(oneQueue);
//        System.out.println("result:" + result.toString());
//    }

    public static String[] sendOneQueue(SmsBrandQueue queue) {
        //  STATUS - REQID
        String data = buildRequest(queue);
        String response = doPostHttp(data);
        String[] result = getResult(response);  // Da Login lai torng nay
        return result;
    }

    private static String buildRequest(SmsBrandQueue queue) {

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("user", USER);
        params.put("password", PASS);
        params.put("brandname", "VERIFY3"); // MFS YEU CAU CHUYEN Verify => Verify3 
        params.put("phone", queue.getPhone());
        params.put("content", queue.getMessage());
//        System.out.println("params:" + params);

        StringBuilder urlParameters = new StringBuilder();

        try {
            for (Map.Entry<String, Object> param : params.entrySet()) {

                if (urlParameters.length() != 0) {
                    urlParameters.append('&');
                }
//                System.out.println("param.getKey():" + param.getValue());
                urlParameters.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                urlParameters.append('=');
                urlParameters.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
        } catch (Exception e) {
        }
//            System.out.println("data:" + urlParameters);
        String data = urlParameters.toString();
//        System.out.println("======================data:" + data);
        return data;
    }

    private static String doPostHttp(String urlParameters) {
        String result = "Error";
        try {
//            System.out.println("seqid:"+seqid);

            HttpURLConnection conn = null;
            try {
                //Create connection
                URL url = new URL(URL_SEND_SMS);
//                System.out.println("url:" + url);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//
                conn.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
                conn.setRequestProperty( "charset", "utf-8");
                conn.setRequestProperty("Content-Language", "en-US");
                conn.setUseCaches(false);
                conn.setDoOutput(true);
//                //Send request
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.close();
//                //Get Response  
                InputStream is = conn.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+ 
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
//                System.out.println("line:" + line);
                rd.close();
                result = response.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    private static String[] getResult(String xmlInput) {
//        System.out.println("vao day chua");
        String[] result = {STATUS.ERR_PASER.val + "", "Parse Error" + xmlInput};
//        System.out.println("==========");
//        System.out.println("xmlInput:" + xmlInput);
//        System.out.println("==========<");
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlInput));
            Document doc = dBuilder.parse(is);
            NodeList nodes = doc.getElementsByTagName("ROOT");
            if (nodes != null && nodes.getLength() > 0) {
                Element oneNote = (Element) nodes.item(0);
                String err_code = STATUS.ERR_PASER.val + "";
                String desc = "Parse Error: " + xmlInput;
                if (oneNote.getElementsByTagName("CODE").item(0) != null) {
                    err_code = oneNote.getElementsByTagName("CODE").item(0).getTextContent();
                }
//                System.out.println("err_code:" + err_code);
                if (oneNote.getElementsByTagName("DES") != null && oneNote.getElementsByTagName("DES").getLength() > 0) {
                    desc = oneNote.getElementsByTagName("DES").item(0).getTextContent();
                }
//                System.out.println("desc:" + desc);
//                System.out.println("err_code:"+err_code);
//                System.out.println("desc:"+desc);
                result[0] = err_code;
                result[1] = desc;
            }
        } catch (ParserConfigurationException | SAXException | IOException | DOMException | NullPointerException e) {
            logger.error("xmlInput:" + xmlInput);
            logger.error(Tool.getLogMessage(e));
        }
//        System.out.println("result : " + result);
        return result;
    }
}
