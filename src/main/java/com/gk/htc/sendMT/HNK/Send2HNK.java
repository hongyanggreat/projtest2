/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.HNK;

import com.gk.htc.ahp.brand.common.Md5;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.entity.BrandLabel;
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
public class Send2HNK {

    static final Logger logger = Logger.getLogger(Send2HNK.class);
    private static final String username = "htc";
    private static final String password = "kuLqnV6ggf7dVef";
    private static final String privateKey = "5dbe5a2cf69b982f01953bbd25c97897";
    private static final String URL_SEND_SMS = "http://api.hnkcorp.vn:6868/sms/sendMtBrand";
    private static final String QC = "1";
    private static final String CSKH = "2";

    public static HNKResponse sendOneQueue(SmsBrandQueue queue) {
        //  STATUS - REQID
        String data = buildData(queue);
        String strResult = excutePost(data);
        HNKResponse resp = HNKResponse.json2Object(strResult);
        return resp;
    }

    private static String buildData(SmsBrandQueue queue) {
        HNKRequest one = new HNKRequest();
        String signature = Md5.encryptMD5(username + password + queue.getPhone() + queue.getLabel() + queue.getMessage() + privateKey);
        one.setMsisdn(queue.getPhone());
        one.setBrandname(queue.getLabel());
        one.setMessage(queue.getMessage());
        if (queue.getType() == BrandLabel.TYPE.QC.val) {
            one.setTypemsg(QC);
        } else {
            one.setTypemsg(CSKH);
        }
        one.setUsername(username);
        one.setPassword(password);
        one.setSignature(signature);
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
            urlConnection.setUseCaches(false);
            urlConnection.setDoOutput(true);

            try ( //Send request
                    DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream())) {
                wr.writeBytes(urlParameters);
            }

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
