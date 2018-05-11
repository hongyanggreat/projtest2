/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.VIVAS;

import com.gk.htc.ahp.brand.common.Tool;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
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
public class Login {

    static Logger logger = Logger.getLogger(Login.class);

    public static enum STATUS {

        LOGIN_SUCCESS(0, "Đăng nhập thành công"),
        WRONG_USER(1, "Sai username"),
        WRONG_PASS(2, "Sai password"),
        OVER_REQUEST(21, "Request bị từ chối vì quá số lượng request đồng thời đến hệ thống"),
        ERROR_50(50, "Lỗi xử lý"),
        ERROR_51(51, "Lỗi xử lý"),
        ERROR_52(52, "Lỗi xử lý"),
        WRONG_PROTOCOL(98, "Lỗi sai protocol gọi request"),
        LOSE_PARAM(99, "Lỗi thiếu tham số gọi request"), //==
        PARSE_RESULT_ERROR(100, "Lỗi Parse ket qua tra ve"), //==
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
            return str;
        }
    }

    // TODO Sychony HAM NAY ???
    public static void loginRequest() {
        HashMap<String, String> result = excuteLogin();
        String status = result.get("status");
        if (status != null && status.equals(STATUS.LOGIN_SUCCESS.val + "")) {
            // Dang nhap thanh cong
            VivasConfig.COOKIE = result.get("cookie");
            Tool.debug("Dang nhap thanh cong COOKIE=" + VivasConfig.COOKIE);
        } else {
            Tool.debug("loginRequest [Vivas Fail] :" + result.get("mess"));
            logger.error("loginRequest [Vivas Fail] :" + result.get("mess"));
        }
    }

    private static HashMap<String, String> excuteLogin() {
        HashMap<String, String> result = new HashMap<>();
        HttpURLConnection conn = null;
        try {
            //Create connection
            URL url = new URL(VivasConfig.URL_LOGIN);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            String param = "<RQST>"
                    + "<USERNAME>" + VivasConfig.USER + "</USERNAME>"
                    + "<PASSWORD>" + VivasConfig.SHA1_HASH + "</PASSWORD>"
                    + "</RQST>";
            conn.setRequestProperty("Content-Length", Integer.toString(param.getBytes().length));
            conn.setRequestProperty("Content-Language", "en-US");

            conn.setUseCaches(false);
            conn.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(param);
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
            String strResponse = response.toString();
            int status = getResult(strResponse);
            if (status == STATUS.LOGIN_SUCCESS.val) {
                String cookie = conn.getHeaderField("Set-Cookie");
                result.put("status", status + "");
                result.put("cookie", cookie);
            } else {
                result.put("status", status + "");
                result.put("mess", STATUS.getmessage(status));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(Tool.getLogMessage(e));
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }

    public static int getResult(String xmlInput) {
        int result = Login.STATUS.PARSE_RESULT_ERROR.val;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlInput));
            Document doc = dBuilder.parse(is);
            NodeList nodes = doc.getElementsByTagName("RPLY");
            if (nodes != null) {
                Element oneNote = (Element) nodes.item(0);
                String status = oneNote.getElementsByTagName("STATUS").item(0).getTextContent();
                result = Tool.getInt(status, result);
            }
        } catch (ParserConfigurationException | SAXException | IOException | DOMException e) {
            logger.error("Result XML Parser Error:" + xmlInput);
            logger.error(Tool.getLogMessage(e));
        }
        return result;
    }
}
