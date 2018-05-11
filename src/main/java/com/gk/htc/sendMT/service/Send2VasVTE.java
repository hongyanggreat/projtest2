/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.sendMT.service;

import com.gk.htc.ahp.brand.app.AppStart;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
import com.gk.htc.sendMT.VasVTE.HTC.CpBalance;
import org.apache.log4j.Logger;

/**
 *
 * @author TUANPLA
 */
public class Send2VasVTE implements ServiceMT {

    static Logger logger = Logger.getLogger(Send2VasVTE.class);
    private static final String USER = "bulk_htc";
    private static final String PASS = "123456a@";
    private static final String CP_CODE = "HTC";

    @Override
    public void doSendBrand(SmsBrandQueue brQueue) {
        AppStart.vasVTE_HTC.addToqueue(brQueue);

    }

    public static CpBalance doCheckBalance() {
        CpBalance result = null;
        try {
            result = checkBalance(USER, PASS, CP_CODE);
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        }
        return result;
    }

    private static CpBalance checkBalance(java.lang.String user, java.lang.String password, java.lang.String cpCode) {
        com.gk.htc.sendMT.VasVTE.HTC.WsBulkSms_Service service = new com.gk.htc.sendMT.VasVTE.HTC.WsBulkSms_Service();
        com.gk.htc.sendMT.VasVTE.HTC.WsBulkSms port = service.getWsBulkSmsPort();
        return port.checkBalance(user, password, cpCode);
    }
}
