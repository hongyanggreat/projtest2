/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.common;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author TUANPLA
 */
public class RequestTool {

    public static int getInt(HttpServletRequest request, String param) {
        int tem;
        try {
            tem = Integer.parseInt(request.getParameter(param).trim());
        } catch (Exception e) {
            tem = 0;
            Tool.debug("RequestTool: getInt " + e.getMessage() + " | URL" + Tool.getCurrentURL(request));
        }
        return tem;
    }

    public static boolean getBoolean(HttpServletRequest request, String param) {
        boolean tem;
        try {
            String str = request.getParameter(param).trim();
            tem = str != null && (str.equals("1") || str.equals("true"));
        } catch (Exception e) {
            tem = false;
            Tool.debug("RequestTool: getBoolean " + e.getMessage() + " | URL" + Tool.getCurrentURL(request));
        }
        return tem;
    }

    public static int getInt(HttpServletRequest request, String param, int defaultVal) {
        int tem;
        try {
            tem = Integer.parseInt(request.getParameter(param).trim());
        } catch (Exception e) {
            Tool.debug("RequestTool: getInt - defaultVal:" + e.getMessage() + " | URL" + Tool.getCurrentURL(request));
            tem = defaultVal;
        }
        return tem;
    }

    public static long getLong(HttpServletRequest request, String param) {
        long tem;
        try {
            tem = Long.parseLong(request.getParameter(param).trim());
        } catch (Exception e) {
            Tool.debug("RequestTool: getLong:" + e.getMessage() + " | URL " + Tool.getCurrentURL(request));
            tem = 0;
        }
        return tem;
    }

    public static double getDouble(HttpServletRequest request, String param) {
        double tem;
        try {
            tem = Double.parseDouble(request.getParameter(param).trim());
        } catch (Exception e) {
            Tool.debug("RequestTool: getDouble:" + e.getMessage());
            tem = 0;
        }
        return tem;
    }

    public static String getString(HttpServletRequest request, String param) {
        String str;
        try {
            str = request.getParameter(param).trim();
        } catch (Exception e) {
            Tool.debug("RequestTool: getstring:" + e.getMessage());
            str = "";
        }
        return str;
    }

    public static String getString(HttpServletRequest request, String param, String defaultVal) {
        String str;
        try {
            str = request.getParameter(param).trim();
        } catch (Exception e) {
            Tool.debug("RequestTool: getstring:" + e.getMessage());
            str = defaultVal;
        }
        return str;
    }
}
