/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.SOUTH;

import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
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
public class Send2South {

    static final Logger logger = Logger.getLogger(Send2South.class);
    private static final String authKey = "aHRjYmFuazpMNzg4VnVjdWJB";
//    private static final String URL_SEND_SMS = "http://api-02.worldsms.vn/webapi/sendSMS";
    private static final String URL_SEND_SMS = "http://125.212.193.103/webapi/sendSMS";
//    private static final String URL_SEND_SMS_BAK = "http://api-01.worldsms.vn/webapi/sendSMS";
    private static final String URL_SEND_SMS_BAK = "http://125.212.193.103/webapi/sendSMS";

    public static SouthResponse sendOneQueue(SmsBrandQueue queue) {
        //  STATUS - REQID
        String data = buildData(queue);
        String strResult = excutePost(data);
        SouthResponse resp = SouthResponse.toObject(strResult);
        return resp;
    }

    private static String buildData(SmsBrandQueue queue) {
        SouthRequest one = new SouthRequest();
        one.setFrom(queue.getLabel());
        one.setTo(queue.getPhone());
        one.setText(queue.getMessage());
        return one.toJson();
    }

    private static String excutePost(String urlParameters) {
        String result = "";
        HttpURLConnection urlConnection = null;
        try {
            //Create connection
            URL url = new URL(URL_SEND_SMS);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
            urlConnection.setRequestProperty("Content-Language", "en-US");
            urlConnection.setRequestProperty("Authorization", "Basic " + authKey);
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
