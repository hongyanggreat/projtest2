/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.MBF.NETVIET;

import com.gk.htc.ahp.brand.common.DateProc;
import com.gk.htc.ahp.brand.common.MyConfig;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.dao.SmsQueueDao;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author Private
 */
public class MBFSender {

    static final Logger logger = Logger.getLogger(MBFLogin.class);
    public static String SID = "NONONO";
    private static final String URL_SENDER = "http://smsbrandname.mobifone.vn/smsg/send.jsp";
    private static final String URL_SENDER_2 = "http://smsbrandname.mobifone.vn/smsg/send_2.jsp";

    public static void main(String[] args) {
        SmsBrandQueue oneQueue = new SmsBrandQueue();
        oneQueue.setLabel("nvcskh-test");
        oneQueue.setPhone("84902112788");
        oneQueue.setMessage("Gửi tin Tiếng Việt:" + DateProc.createTimestamp());
        Response a = doSend_UCS2(oneQueue);
        if (a != null) {
            System.out.println("a.getSid():" + a.getSid());
            System.out.println("a.getMessage():" + a.getMessage());
            System.out.println("a.getStatus():" + a.getStatus());
        }
    }

    public static Response doSend_ASCII(SmsBrandQueue oneQueueBr) {
        Response resp = new Response();
        try {
            String result = sendSMS_ASCII(oneQueueBr);
            resp = Response.buildOBject(result);
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        }
        return resp;
    }

    public static Response doSend_UCS2(SmsBrandQueue oneQueueBr) {
        Response resp = new Response();
        try {
            String result = sendSMS_UCS2(oneQueueBr);
            resp = Response.buildOBject(result);
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        }
        return resp;
    }

    private static String sendSMS_ASCII(SmsBrandQueue oneQueueBr) {
        String result = "";
        try {
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("sid", SID);
            params.put("sender", oneQueueBr.getLabel());
            params.put("recipient", oneQueueBr.getPhone());     // Phone
            params.put("content", oneQueueBr.getMessage());

            StringBuilder getData = new StringBuilder();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (getData.length() != 0) {
                    getData.append('&');
                }
                getData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                getData.append('=');
                getData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            URL url = new URL(URL_SENDER + "?" + getData);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(20000);
            int respCode = conn.getResponseCode();
            if (respCode == HttpURLConnection.HTTP_OK) {
                Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                for (int c; (c = in.read()) >= 0;) {
                    sb.append((char) c);
                }
                result = sb.toString();
            } else {
                logger.error("sendSMS_UCS2 to MBF Not Success conn.getResponseMessage():" + conn.getResponseMessage() + "|responseCode=" + respCode);
                Reader in = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                for (int c; (c = in.read()) >= 0;) {
                    sb.append((char) c);
                }
                result = sb.toString();
                System.out.println("Vao http Status:" + respCode + "-" + result);
                if (result != null && ((result.equalsIgnoreCase("SessionExpired") && respCode == HttpURLConnection.HTTP_UNAUTHORIZED)
                        || (result.equalsIgnoreCase("SidNotPassed") && respCode == HttpURLConnection.HTTP_UNAUTHORIZED))) {
                    SID = MBFLogin.login();
                    if (oneQueueBr.getRetry() == 0) {
                        // Chua gui duoc lan nao thi retry
                        oneQueueBr.setRetry();
                        result = sendSMS_ASCII(oneQueueBr);
                    } else {
//                            Log xuong DB
                        oneQueueBr.setErrorInfo("Cache From sendSMS_ASCII()");
                        SmsQueueDao.writeBrandQueue(oneQueueBr, MyConfig.PATH_CACHE_BRAND_SEND, ".brSend");
                    }
                }
            }
            conn.disconnect();
        } catch (Exception ex) {
            logger.error(Tool.getLogMessage(ex));
        }
        return result;
    }

    private static String sendSMS_UCS2(SmsBrandQueue oneBr) {
        String result = "";
        try {

            Map<String, Object> params = new LinkedHashMap<>();
            params.put("enCoding", "ALPHA_UCS2");
            params.put("sid", SID);
            params.put("sender", oneBr.getLabel());
            params.put("recipient", oneBr.getPhone());     // Phone
            params.put("content", oneBr.getMessage());

            StringBuilder getData = new StringBuilder();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (getData.length() != 0) {
                    getData.append('&');
                }
                getData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                getData.append('=');
                getData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            URL url = new URL(URL_SENDER_2 + "?" + getData);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(20000);
            int respCode = conn.getResponseCode();
            if (respCode == HttpURLConnection.HTTP_OK) {
                Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                for (int c; (c = in.read()) >= 0;) {
                    sb.append((char) c);
                }
                result = sb.toString();
            } else {
                logger.error("sendSMS_UCS2 to MBF Not Success conn.getResponseMessage():" + conn.getResponseMessage() + "|responseCode=" + respCode);
                Reader in = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                for (int c; (c = in.read()) >= 0;) {
                    sb.append((char) c);
                }
                result = sb.toString();
                if (result != null && (result.equalsIgnoreCase("SessionExpired")
                        || result.equalsIgnoreCase("SidNotPassed")
                        || respCode == HttpURLConnection.HTTP_UNAUTHORIZED)) {
                    SID = MBFLogin.login();
                    if (oneBr.getRetry() == 0) {
                        oneBr.setRetry();
                        result = sendSMS_UCS2(oneBr);
                    } else {
                        // Log xuong DB
                    }
                }
            }
            conn.disconnect();
        } catch (Exception ex) {
            logger.error(Tool.getLogMessage(ex));
        }
        return result;
    }
}
