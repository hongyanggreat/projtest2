/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.HTC;

import com.gk.htc.ahp.brand.common.Md5;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Calendar;
import org.apache.log4j.Logger;

/**
 *
 * @author Private
 */
public class Send_SMS_2HTC_Client {

    static final Logger logger = Logger.getLogger(Send_SMS_2HTC_Client.class);
    private static final String URL_SEND_SMS = "http://content.zonesms.vn/index.php";
    private static final String USER = "htcbrand@htcjsc.vn";
    private static final String PASS = "adt@mbf_htc04072017)$";
    private static final String TOKEN = "913923a6cb4bd16f12a55a00e0f16564"; // 913923a6cb4bd16f12a55a00e0f16564
    private static final String SUB_TOKEN = "4bd16f12a55a00e0f16564"; // 913923a6cb4bd16f12a55a00e0f16564

//    public static void main(String[] args) throws UnsupportedEncodingException {
//        SmsBrandQueue queue = new SmsBrandQueue();
//        queue.setTranId(Tool.generateRandomPassword(16));
//        queue.setPhone("0901737828");
//        queue.setLabel("0901800288"); // 0901800288 -> VMS2
//        queue.setMessage("VCCBan da su dung so dien thoai nay de dang ky tai khoan VietID. Day la ma so xac thuc : 347145");
//        String[] result = sendOneQueue(queue);
//        System.out.println("result [0]: " + result[0]);
//        System.out.println("result [0]: " + result[1]);
//    }

    public static String[] sendOneQueue(SmsBrandQueue queue) throws UnsupportedEncodingException {
        //  STATUS - REQID
        String data = buildData(queue);
        String strresult = excuteGET(data);
        SmsResponse jsonData = SmsResponse.json2Objec(strresult);
        String[] result = new String[2];
        if (jsonData != null) {
            result[0] = jsonData.getCode();
            result[1] = jsonData.getStatus();
            return result;
        } else {
            result[0] = STATUS.RESPONSE_NOTVALID.val + "";
            result[1] = STATUS.RESPONSE_NOTVALID.mess;
            return result;
        }

    }

    private static String buildData(SmsBrandQueue queue) throws UnsupportedEncodingException {
        String requestTime = createYYYYMMDDHH24MiSS();
        String security = Md5.encryptMD5(USER + requestTime + SUB_TOKEN);
//        return "r=" + URLEncoder.encode("Api/default/SmsStatus", "UTF-8")
        return "r=Api/default/SendSMS"
                + "&account=" + USER
                + "&token=" + URLEncoder.encode(TOKEN, "UTF-8")
                + "&requesttime=" + URLEncoder.encode(requestTime, "UTF-8")
                + "&security=" + URLEncoder.encode(security, "UTF-8")
                + "&messageid=" + URLEncoder.encode(queue.getTranId(), "UTF-8")
                + "&reciver=" + URLEncoder.encode(queue.getPhone(), "UTF-8")
                + "&sender=" + URLEncoder.encode(queue.getLabel(), "UTF-8")
                + "&content=" + URLEncoder.encode(queue.getMessage(), "UTF-8")
                + "&from=" + URLEncoder.encode("APP", "UTF-8");
    }

    private static String excuteGET(String urlParameters) {
        String result = "";
        HttpURLConnection urlConnection = null;
        try {
            //Create connection
            URL url = new URL(URL_SEND_SMS + "?" + urlParameters);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "text/html");
            urlConnection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
            urlConnection.setRequestProperty("Content-Language", "en-US");
            urlConnection.setUseCaches(false);
            urlConnection.setDoOutput(true);

            //Send request
//            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
//            wr.writeBytes(urlParameters);
//            wr.close();
            //Get Response  
            InputStream is = urlConnection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+ 
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            result = response.toString();
//            System.out.println("Response from HTC:\n" + result);
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

    public static enum STATUS {

        SUCCESS("00", "Success"), // Tra ve ket qua cho KH khong duoc thay doi
        USER_IS_NULL("01", "User is null"), // Sai IP
        TOCKEN_NULL("02", "Token is null"), // Sai user
        TIME_NULL("03", "Request time is null"),
        SECIRIRY_NULL("04", "Security is null"),
        SECURITY_INVALID("05", "Security invalid"),
        TOCK_KEN_INVALID("06", "Token invalid"),
        USER_NOT_EXIT("07", "User not exist"),
        MESSAGEID_NULL("08", "Message ID is null"),
        SENDER_NULL("09", "Sender is null"),
        RECEIVER_NULL("10", "Reciver is null"),
        CONTENT_NULL("11", "Content is null"),
        SYSTEM_ERROR("99", "System error"),
        RESPONSE_NOTVALID("100", "Response not valid"),;
        public String val;
        public String mess;
        private String result;

        public String getResult() {
            return result;
        }

        private void setResult(String val, String mess) {
            result = val + "." + mess;
        }

        public static String getMess(String val) {
            String _result = "Unknow";
            for (STATUS one : STATUS.values()) {
                if (one.val.equals(val)) {
                    _result = one.mess;
                    break;
                }
            }
            return _result;
        }

        private STATUS(String val, String mess) {
            this.val = val;
            this.mess = mess;
            setResult(val, mess);
        }
    }

    public static String createYYYYMMDDHH24MiSS() {
        Timestamp ts = new Timestamp(System.currentTimeMillis());

        Calendar cal = Calendar.getInstance();
        cal.setTime(new java.util.Date(ts.getTime()));
        String strTemp = cal.get(Calendar.YEAR) + "";
        int month = cal.get(Calendar.MONTH) + 1;
        if (month < 10) {
            strTemp += "-0" + month;
        } else {
            strTemp += "-" + month;
        }
        int day = cal.get(Calendar.DAY_OF_MONTH);
        if (day < 10) {
            strTemp += "-0" + day;
        } else {
            strTemp += "-" + day;
        }
        //--
        if (cal.get(Calendar.HOUR_OF_DAY) < 10) {
            strTemp += " 0" + cal.get(Calendar.HOUR_OF_DAY);
        } else {
            strTemp += " " + cal.get(Calendar.HOUR_OF_DAY);
        }
        if (cal.get(Calendar.MINUTE) < 10) {
            strTemp += ":0" + cal.get(Calendar.MINUTE);
        } else {
            strTemp += ":" + cal.get(Calendar.MINUTE);
        }
        if (cal.get(Calendar.SECOND) < 10) {
            strTemp += ":0" + cal.get(Calendar.SECOND);
        } else {
            strTemp += ":" + cal.get(Calendar.SECOND);
        }
        return strTemp;

    }
}
