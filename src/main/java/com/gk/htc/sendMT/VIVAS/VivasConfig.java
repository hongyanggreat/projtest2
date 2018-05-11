/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.VIVAS;

import com.gk.htc.ahp.brand.common.Tool;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.log4j.Logger;

/**
 *
 * @author TUANPLA
 */
public class VivasConfig {

    static Logger logger = Logger.getLogger(VivasConfig.class);
    public static final String URL_LOGIN = "http://mkt.vivas.vn:9080/SMSBNAPI/login";
    public static final String URL_SEND_SMS = "http://mkt.vivas.vn:9080/SMSBNAPI/send_sms";
    public static final String URL_SEND_SMS_EXT = "http://mkt.vivas.vn:9080/SMSBNAPI/send_sms_ext";
    public static final String URL_VERYFY = "http://mkt.vivas.vn:9080/SMSBNAPI/verify";
    public static final String URL_LOG_OUT = "http://mkt.vivas.vn:9080/SMSBNAPI/logout";
    //--Login Info
    public static final String USER = "htc123";                             //  vivastest107
    public static final String PASS = "smshtc@1";                           //  123456
    public static final String SHARE_KEY = "123456";                        //  123456
    public static final String SHA1_HASH = "32lAUHeRpbRbD/zBPa3sLc1aDB0=";  //  fEqNCco3Yq9h5ZUglD3CZJT4lBs=
    //--
    public static String COOKIE = "";

    public static enum TYPE {

        CSKH(1, "Tin chăm sóc khách hàng"),
        QC(2, "Tin nhắn Quảng cáo"),;
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

    public static String excutePost(String targetURL, String urlParameters) {
        String result = "";
        HttpURLConnection conn = null;
        try {
            //Create connection
            URL url = new URL(targetURL);
            conn = (HttpURLConnection) url.openConnection();
            // Check Login
            if (!Tool.checkNull(COOKIE)) {
//                Tool.debug("Da co Cookie set vao request");
                conn.setRequestProperty("Cookie", COOKIE);
            } else {
                Tool.debug("excutePost Chua co Cookie set vao request");
                // TODO Dang nhap lai
                Login.loginRequest();
                return excutePost(targetURL, urlParameters);
            }
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
            logger.error(Tool.getLogMessage(e) + "\nData:" + urlParameters);
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }

}
