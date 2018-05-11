/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.common;

import org.apache.log4j.Logger;

public class MyLog {

    //-- A4
    private static final Logger DEBUG_LOG = Logger.getLogger("DEBUG_LOG");

    public static void debug(String message) {
        message = StringUtils.str2OneLine(message);
        DEBUG_LOG.info(message);
    }
    //-- A5
    private static final Logger LOG_REQUEST = Logger.getLogger("REQUEST_LOG");

    public static void logIncome(String message) {
        message = StringUtils.str2OneLine(message);
        LOG_REQUEST.info(message);
    }
    //-- A6
    private static final Logger LOG_SUBMIT = Logger.getLogger("SUBMIT_LOG");

    public static void logSubmit(String message) {
        message = StringUtils.str2OneLine(message);
        LOG_SUBMIT.info(message);
    }

}
