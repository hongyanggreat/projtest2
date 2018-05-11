/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.SOUTH;

import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
import com.gk.htc.ahp.brand.entity.UniqueID;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;

/**
 *
 * @author TUANPLA
 */
public class Send2SouthUnicode {

    static final Logger logger = Logger.getLogger(Send2SouthUnicode.class);
    private static final String authKey = "aHRjYmFuazpMNzg4VnVjdWJB";
    private static final String URL_SEND_SMS = "http://api-02.worldsms.vn/webapi/sendSMS";
    private static final String URL_SEND_SMS_BAK = "http://api-01.worldsms.vn/webapi/sendSMS";

//    public static void main(String[] args) {
//        SmsBrandQueue queue = new SmsBrandQueue();
//        queue.setLabel("CommCredit");
//        queue.setPhone("84986233352");
//        queue.setMessage("Gửi một tin đi bằng tiếng việt " + System.nanoTime());
//        queue.setDataEncode(1);
//        queue.setSystemId(UniqueID.getId("84986233352"));
//        SouthResponse result = Send2SouthUnicode.sendOneQueue(queue);
//    }


    public static SouthResponse sendOneQueue(SmsBrandQueue queue) {
        //  STATUS - REQID
        String data = buildData(queue);
        String strResult = postRest(data);
//        String strResult = excutePost(data);
//        System.out.println("strResult:" + strResult);
        SouthResponse resp = SouthResponse.toObject(strResult);
        return resp;
    }

    private static String buildData(SmsBrandQueue queue) {
        SouthRequest one = new SouthRequest();
        one.setFrom(queue.getLabel());
        one.setTo(queue.getPhone());
//        one.setText(JSONUtil.escape(queue.getMessage()));
        one.setText(queue.getMessage());
        one.setUnicode(queue.getDataEncode());
        one.setSmsid(queue.getSystemId());
        one.setDlr("0");
        one.setCampaignid("");
        return one.toJson();
    }

//    private static String excutePost(String data) {
//        String result = "";
//        HttpURLConnection urlConnection = null;
//        try {
////            data = JSONUtil.escape(data);
//            //Create connection
//            URL url = new URL(URL_SEND_SMS);
//            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setRequestMethod(HttpMethod.POST);
//            urlConnection.setRequestProperty("cache-control", "no-cache");
//            urlConnection.setRequestProperty("Authorization", "Basic " + authKey);
//            urlConnection.setRequestProperty("accept", MediaType.APPLICATION_JSON);
//            urlConnection.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON);
//            urlConnection.setUseCaches(false);
//            urlConnection.setDoOutput(true);
//            //Send request
//            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
//            System.out.println("urlParameters:" + data);
//            wr.writeBytes(data);
//            wr.close();
//
//            //Get Response  
//            InputStream is = urlConnection.getInputStream();
//            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//            StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+ 
//            String line;
//            while ((line = rd.readLine()) != null) {
//                response.append(line);
//                response.append('\r');
//            }
//            rd.close();
//            result = response.toString();
//            System.out.println("result:" + result);
//        } catch (Exception e) {
//            logger.error(Tool.getLogMessage(e));
//            return null;
//        } finally {
//            if (urlConnection != null) {
//                urlConnection.disconnect();
//            }
//        }
//        return result;
//    }

    private static String postRest(String data) {
        Client client = Client.create();

        WebResource webResource = client.resource(URL_SEND_SMS);
        ClientResponse response = webResource
                .accept(MediaType.APPLICATION_JSON)
                .header("cache-control", "no-cache")
                .header("Authorization", "Basic " + authKey)
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, data);

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        }
        String output = response.getEntity(String.class);
        return output;
    }
}
