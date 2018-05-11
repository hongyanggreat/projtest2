/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.MBF.NETVIET;

import com.gk.htc.ahp.brand.common.Tool;
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
public class MBFLogin {

    static final Logger logger = Logger.getLogger(MBFLogin.class);
    private static final String USER = "netviet";
    private static final String PASS = "netviet@123";
    private static final String URL_LOGIN = "http://smsbrandname.mobifone.vn/smsg/login.jsp";

    public static String login() {
        String sid = "";
        try {
            String result = execLogin();
            Response obj = Response.buildOBject(result);
            if (obj != null && obj.getStatus().equals("200")) {
                sid = obj.getSid();
            } else {
                logger.error("login Failt:" + result);
            }
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        }
        return sid;
    }

    private static String execLogin() {
        String result = "";
        try {
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("userName", USER);
            params.put("password", PASS);
            params.put("bindMode", "T");

            StringBuilder getData = new StringBuilder();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (getData.length() != 0) {
                    getData.append('&');
                }
                getData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                getData.append('=');
                getData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            try {
                URL url = new URL(URL_LOGIN + "?" + getData);
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
                    System.out.println("result Login:" + result);
                } else {
                    logger.error("Login to MBF Not Success conn.getResponseMessage():" + conn.getResponseMessage() + "|responseCode=" + respCode);
                    Reader in = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    for (int c; (c = in.read()) >= 0;) {
                        sb.append((char) c);
                    }
                    result = sb.toString();
                }
                conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            logger.error(Tool.getLogMessage(ex));
        }
        return result;
    }

}
