/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.MFS.VNPTHN;

import com.gk.htc.ahp.brand.common.DateProc;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.entity.OptionTelco;
import com.gk.htc.ahp.brand.entity.OptionVina;
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
 * @author tuanp
 */
public class Send2VNPTHNviaMFS {

    static final Logger logger = Logger.getLogger(Send2VNPTHNviaMFS.class);
//    private static final String URL_SEND_SMS = "http://10.58.98.19:8899/smsmarketing/api";    // Chay Qua Proxy cua 6x88
    private static final String URL_SEND_SMS = "http://113.185.0.35:8888/smsmarketing/api";
    
//    private static final String APIUSER = "vnpt_cskh";
//    private static final String APIPASS = "abc123456";
//    private static final String AGENTID = "244";
//    private static final String USERNAME = "VNPT-TEST-CSKH";

    private static final String APIUSER = "mbs";
    private static final String APIPASS = "abc123456";
    private static final String AGENTID = "244";
    private static final String USERNAME = "mobifoneservice_cs";
// CONTRACTTYPEID
    private static final int QC = 2;
    private static final int CSKH = 1;
// CONTRACTID: 2754/IKHDN-TTKHDN
    private static final String CONTRACTID = "7050";

    public static enum STATUS {

        EXCEPTION(-1, "Exception"),
        SUCCESS(0, "Success"),
        INVALID_LOGIN(1, "Username, password, IP, status các API không hợp lệ"),
        WRONG_TIME_SCHEDURE(2, "Thời gian đặt lịch sai định dạng "),
        WRONG_ID_METHOD(3, "ID method không hợp lệ"),
        WRONG_TEMPLATE(7, "Template không hợp lệ hoặc không tồn tại với nhãn và đại lý"),
        WRONG_TIME_QC(8, "Sai thời gian quy định đối với tin nhắn QC"),
        WRONG_CONTENT_TYPE(9, "Contract_type_id không hợp lệ"),
        WRONG_USER(10, "User_name không hợp lệ (user đăng nhập của Agent không đúng)"),
        WRONG_MSG_LENGTH(11, "Độ dài tin nhắn không hợp lệ (quá 640 kí tự)"),
        WRONG_TIME_POLICY(12, "Thời gian không hợp lệ với chính sách của Vinaphone"),
        WRONG_HOPDONG(13, "Hợp đồng không đúng"),
        WRONG_LABEL(14, "Label không hợp lệ"),
        WRONG_SYNTAX(101, "syntax error"),
        PARSE_RESULT_ERROR(-100, "Lỗi Parse ket qua tra ve"), //== SYSTEM DEFINE
        ERROR_CONFIG(-89, "Lỗi khai báo BrandID và TmpID"), //== SYSTEM DEFINE
        ;
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
        
        System.out.println("");
        String[] result = {STATUS.ERROR_CONFIG.val + "", STATUS.ERROR_CONFIG.mess};
        //  STATUS - REQID
        String optStr = queue.getOptString();
        if (Tool.checkNull(optStr)) {
            return result;
        }
        OptionTelco opt = OptionTelco.json2Objec(optStr);
        if (opt == null) {
            return result;
        }
        OptionVina opt_vina = opt.getVinaphone();
        if (opt_vina == null || Tool.checkNull(opt_vina.getLabelId())
                || Tool.checkNull(opt_vina.getTmpId())) {
            result[1] += "\nvinaConfig=" + optStr;
            return result;
        }
        String data = buildData(queue, opt_vina);
        String strResult = excutePost(data);
        result = getResult(strResult);
        return result;
    }

    private static String buildData(SmsBrandQueue queue, OptionVina opt_vina) {
        String schedule_time = DateProc.Timestamp2DDMMYYYYHH24Mi(DateProc.createTimestamp());
        String message = StringEscapeUtils.escapeXml(queue.getMessage());
        String data = "<RQST>"
                + "<name>send_sms_list</name>"
                + "<REQID>" + queue.getSystemId() + "</REQID>"
                + "<LABELID>" + opt_vina.getLabelId() + "</LABELID>"
                + "<CONTRACTID>" + CONTRACTID + "</CONTRACTID>"
                + "<CONTRACTTYPEID>" + CSKH + "</CONTRACTTYPEID>"
                + "<TEMPLATEID>" + opt_vina.getTmpId() + "</TEMPLATEID>" // Van Phai Truyen a ??
                + "<PARAMS>"
                + "<NUM>1</NUM>"
                + "<CONTENT>" + message + "</CONTENT>"
                + "</PARAMS>"
                + "<SCHEDULETIME>" + schedule_time + "</SCHEDULETIME>"
                + "<MOBILELIST>" + queue.getPhone() + "</MOBILELIST>"
                + "<ISTELCOSUB>0</ISTELCOSUB>" // Sử dụng nhóm thuê bao của nhà mạng. Giá trị 0 hoặc 1
                + "<AGENTID>" + AGENTID + "</AGENTID>"
                + "<APIUSER>" + APIUSER + "</APIUSER>"
                + "<APIPASS>" + APIPASS + "</APIPASS>"
                + "<USERNAME>" + USERNAME + "</USERNAME>"
                + "</RQST>";
//        logger.info(data, Level.INFO);
        return data;

    }

    private static String excutePost(String data) {
        String result = "";
        HttpURLConnection urlConnection = null;
        try {
//Create connection
            URL url = new URL(URL_SEND_SMS);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
            urlConnection.setRequestProperty("Content-Length", Integer.toString(data.getBytes().length));
            urlConnection.setRequestProperty("Content-Language", "en-US");
            urlConnection.setUseCaches(false);
            urlConnection.setDoOutput(true);
            try (//Send request
                    DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream())) {
                wr.writeBytes(data);
            }
            //Get Response  
            InputStream is = urlConnection.getInputStream();
            StringBuilder response;
            try (BufferedReader rd = new BufferedReader(new InputStreamReader(is))) {
                response = new StringBuilder(); // or StringBuffer if not Java 5+
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
            } // or StringBuffer if not Java 5+
            result = response.toString();
            logger.info("Send2VNPTNET-Response:" + result);
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }

    private static String[] getResult(String xmlInput) {
        String[] result = {STATUS.PARSE_RESULT_ERROR.val + "", "0"};
//        Tool.debug("Response Result:" + xmlInput);
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlInput));
            Document doc = dBuilder.parse(is);
            NodeList nodes = doc.getElementsByTagName("RPLY");
            if (nodes != null && nodes.getLength() > 0) {
                Element oneNote = (Element) nodes.item(0);
                String err_code = STATUS.PARSE_RESULT_ERROR.val + "";
                String desc = "0";
                if (oneNote.getElementsByTagName("ERROR").item(0) != null) {
                    err_code = oneNote.getElementsByTagName("ERROR").item(0).getTextContent();
                }
                if (oneNote.getElementsByTagName("ERROR_DESC") != null && oneNote.getElementsByTagName("ERROR_DESC").getLength() > 0) {
                    desc = oneNote.getElementsByTagName("ERROR_DESC").item(0).getTextContent();
                }
                result[0] = err_code;
                result[1] = desc;
            }
        } catch (ParserConfigurationException | SAXException | IOException | DOMException | NullPointerException e) {
            logger.error("xmlInput:" + xmlInput);
            logger.error(Tool.getLogMessage(e));
        }
        return result;
    }

}
