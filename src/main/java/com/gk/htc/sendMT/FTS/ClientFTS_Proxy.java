/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.FTS;

import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
import com.gk.htc.sendMT.service.thread.Send2FTS_Proxy_Task;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.apache.log4j.Logger;

/**
 *
 * @author tuanp
 */
public class ClientFTS_Proxy {

    static final Logger logger = Logger.getLogger(ClientFTS_Proxy.class);
    private static final String URL_SEND_SMS = "http://210.211.98.80:9981/service/cp/brand/CSKH";
//    public static final String USER = "fts";
//    public static final String PASS = "ksakj#(8392iw^";
    public static final String USER = "htc";
    public static final String PASS = "ksakj#(8392iw^";

    public static String[] sendOneQueue(SmsBrandQueue queue) throws UnsupportedEncodingException {
        //  STATUS - REQID
        String data = buildData(queue);
        String strresult = excutePost(data);
        String[] result = strresult.split("\\.");
        if (result != null && result.length == 2) {
            return result;
        } else {
            result = new String[2];
            result[0] = Send2FTS_Proxy_Task.STATUS.RESPONSE_NOTVALID.val + "";
            result[1] = Send2FTS_Proxy_Task.STATUS.RESPONSE_NOTVALID.mess;
            return result;
        }
    }

    private static String buildData(SmsBrandQueue queue) throws UnsupportedEncodingException {
        return "user=" + URLEncoder.encode(USER, "UTF-8")
                + "&pass=" + URLEncoder.encode(PASS, "UTF-8")
                + "&phone=" + URLEncoder.encode(queue.getPhone(), "UTF-8")
                + "&brandName=" + URLEncoder.encode(queue.getLabel(), "UTF-8")
                + "&mess=" + URLEncoder.encode(queue.getMessage(), "UTF-8")
                + "&tranId=" + URLEncoder.encode(queue.getTranId(), "UTF-8");
    }

    private static String excutePost(String urlParameters) {
        String result = "";
        HttpURLConnection urlConnection = null;
        try {
            //Create connection
            URL url = new URL(URL_SEND_SMS);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
            urlConnection.setRequestProperty("Content-Language", "en-US");
            urlConnection.setUseCaches(false);
            urlConnection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

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
//            System.out.println("Response from HTC:" + result);
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

}
